package be.mygod.chimpanzeerunner.android.action;

import be.mygod.chimpanzeerunner.action.AbstractAction;
import be.mygod.chimpanzeerunner.android.device.AndroidDevice;
import be.mygod.chimpanzeerunner.android.os.BroadcastFilter;
import be.mygod.chimpanzeerunner.android.test.AndroidTestProfile;
import be.mygod.chimpanzeerunner.device.Device;
import com.android.ddmlib.AdbCommandRejectedException;
import com.android.ddmlib.ShellCommandUnresponsiveException;
import com.android.ddmlib.TimeoutException;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public abstract class AndroidAction extends AbstractAction {
    protected final AndroidDevice targetDevice;

    public AndroidAction(Device device) {
        this.targetDevice = (AndroidDevice) device;
    }

    @Override
    public int getBias() {
        return 2;
    }

    private static final Pattern
            PATTERN_APP = Pattern.compile("^    app=(\\d+):(.*?)(:.*)?/(\\d+|u\\d+[asi]\\d+) pid=\\1 uid=(\\d+) user=(\\d+)$"),
            PATTERN_AUDIO_FOCUS = Pattern.compile(" -- pack: (.*) -- ");

    public static Stream<AndroidAction> getActionsFromReceivers(AndroidDevice device, AndroidTestProfile profile) {
        LinkedList<BroadcastFilter> registeredFilters = new LinkedList<>();
        try {
            Scanner scanner = new Scanner(device.executeShellCommand("dumpsys activity broadcasts"));
            int state = 0;
            BroadcastFilter filter = null;
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                // formatting filters
                if (state == 3) if (filter.takeDump(line)) continue; else if (line.startsWith("      ")) {
                    System.err.printf("Unexpected data in dumpsys activity broadcasts: " + line);
                    continue;
                } else {
                    registeredFilters.add(filter);
                    filter = null;
                    state = 2;
                }
                if (state == 2) if (line.startsWith("    Filter #")) {	// awaiting filters
                    filter = new BroadcastFilter();
                    state = 3;
                    continue;
                } else state = 0;
                if (state == 1) {	// check if it's the correct package
                    Matcher matcher = PATTERN_APP.matcher(line);
                    state = matcher.matches() && profile.packageName.equals(matcher.group(2)) ? 2 : 0;
                    continue;
                }
                // state 0: awaiting ReceiverList
                if (line.startsWith("  * ReceiverList{")) state = 1;
                else if (line.length() == 0) break;
            }
        } catch (TimeoutException | AdbCommandRejectedException | ShellCommandUnresponsiveException | IOException e) {
            e.printStackTrace();
        }
        return Stream.concat(
                profile.receivers.stream().flatMap(receiver -> receiver.getActions(device, profile.packageName)),
                registeredFilters.stream().flatMap(filter -> filter.getActions(device, profile.packageName, null))
        );
    }

    public static Stream<PlacePhoneCall> getActionsFromAudioFocus(AndroidDevice device, AndroidTestProfile profile) {
        try {
            Scanner output = new Scanner(device.executeShellCommand("dumpsys audio"));
            boolean started = false;
            while (output.hasNextLine()) {
                String line = output.nextLine();
                if (started) {
                    if (line.isEmpty()) break;
                    Matcher matcher = PATTERN_AUDIO_FOCUS.matcher(line);
                    if (matcher.matches()) {
                        if (profile.packageName.equals(matcher.group(1))) return Stream.of(new PlacePhoneCall(device));
                    } else System.err.printf("Unexpected content in dumpsys audio: %s\n", line);
                } else if ("Audio Focus stack entries (last is top of stack):".equals(line)) started = true;
            }
        } catch (TimeoutException | AdbCommandRejectedException | IOException | ShellCommandUnresponsiveException e) {
            e.printStackTrace();
        }
        return Stream.empty();
    }
}
