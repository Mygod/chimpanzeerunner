package be.mygod.chimpanzeerunner.android.action;

import be.mygod.chimpanzeerunner.android.device.AndroidDevice;
import be.mygod.chimpanzeerunner.android.test.AndroidTestProfile;
import be.mygod.chimpanzeerunner.device.Device;
import com.android.ddmlib.AdbCommandRejectedException;
import com.android.ddmlib.ShellCommandUnresponsiveException;
import com.android.ddmlib.TimeoutException;

import java.io.IOException;
import java.util.Random;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class PressMediaButton extends AndroidAction {
    private static final int[] MEDIA_BUTTONS = {128, 129, 90, 87, 127, 126, 85, 88, 130, 89, 86};
    private static final Random random = new Random();
    private static final Pattern PATTERN_MEDIA_BUTTON_RECEIVER = Pattern.compile(
            "^  mediaButtonReceiver=PendingIntent\\{[0-9a-f]+: PendingIntentRecord\\{[0-9a-f]+ (.*) broadcastIntent\\}\\}$");

    public PressMediaButton(Device device) {
        super(device);
    }

    @Override
    public void perform() {
        try {
            targetDevice.executeShellCommand("input keyevent " + MEDIA_BUTTONS[random.nextInt(MEDIA_BUTTONS.length)]);
        } catch (TimeoutException | AdbCommandRejectedException | IOException | ShellCommandUnresponsiveException e) {
            e.printStackTrace();
        }
    }

    public static Stream<PressMediaButton> getActions(AndroidDevice device, AndroidTestProfile profile) {
        try {
            Scanner scanner = new Scanner(device.executeShellCommand("dumpsys media_session")); // TODO: only work on 5.0+
            while (scanner.hasNextLine()) {
                Matcher matcher = PATTERN_MEDIA_BUTTON_RECEIVER.matcher(scanner.nextLine());
                if (matcher.matches() && profile.packageName.equals(matcher.group(1)))
                    return Stream.of(new PressMediaButton(device));
            }
        } catch (TimeoutException | AdbCommandRejectedException | IOException | ShellCommandUnresponsiveException e) {
            e.printStackTrace();
        }
        return Stream.empty();
    }
}
