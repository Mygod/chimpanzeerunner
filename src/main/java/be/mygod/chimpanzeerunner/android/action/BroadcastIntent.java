package be.mygod.chimpanzeerunner.android.action;

import be.mygod.chimpanzeerunner.device.Device;
import com.android.ddmlib.AdbCommandRejectedException;
import com.android.ddmlib.ShellCommandUnresponsiveException;
import com.android.ddmlib.TimeoutException;

import java.io.IOException;
import java.util.HashSet;

public class BroadcastIntent extends AndroidAction {
    public final String packageName, componentName, action, dataUri, mimeType;
    public final HashSet<String> categories;
    public final boolean registeredOnly;

    public BroadcastIntent(Device device, String packageName, String componentName, String action, String dataUri,
                           String mimeType, HashSet<String> categories) {
        super(device);
        this.packageName = packageName;
        this.componentName = componentName;
        this.action = action;
        this.categories = categories;
        this.dataUri = dataUri;
        this.mimeType = mimeType;
        registeredOnly = componentName == null;
    }

    @Override
    public boolean perform() {
        // TODO: --user uid?
        StringBuilder command = new StringBuilder("am broadcast -a ");
        command.append(action);
        if (dataUri != null) {
            command.append(" -d ");
            command.append(dataUri);
        }
        if (mimeType != null) {
            command.append(" -t ");
            command.append(mimeType);
        }
        if (categories != null) for (String category : categories) {
            command.append(" -c ");
            command.append(category);
        }
        if (registeredOnly) command.append(" --receiver-registered-only");
        if (componentName == null) {
            command.append(' ');
            command.append(packageName);
        } else {
            command.append(" -n ");
            command.append(packageName);
            command.append('/');
            command.append(componentName);
        }
        try {
            String result = targetDevice.executeShellCommand(command.toString());
            if (result.contains("Broadcast completed")) return true;
            System.err.printf("Unknown error when broadcasting:\n%s\n", result);
        } catch (TimeoutException | AdbCommandRejectedException | ShellCommandUnresponsiveException | IOException e) {
            System.err.printf("Broadcast failed.\n");
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder(super.toString());
        result.append(':');
        result.append(packageName);
        if (registeredOnly) result.append(":registered"); else {
            result.append('/');
            result.append(componentName);
        }
        result.append(':');
        result.append(action);
        if (dataUri != null) {
            result.append(":d:");
            result.append(dataUri);
        }
        if (dataUri != null) {
            result.append(":t:");
            result.append(mimeType);
        }
        if (categories != null) for (String category : categories) {
            result.append(":c:");
            result.append(category);
        }
        return result.toString();
    }
}
