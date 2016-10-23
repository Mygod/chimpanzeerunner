package be.mygod.chimpanzeerunner.android.device;

import be.mygod.chimpanzeerunner.Automation;
import be.mygod.chimpanzeerunner.device.Device;
import com.android.ddmlib.*;
import com.google.common.base.Charsets;
import io.appium.java_client.remote.AutomationName;
import io.appium.java_client.remote.MobileCapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class AndroidDevice extends Device {
    public final IDevice device;

    public AndroidDevice(IDevice device) {
        this.device = device;
    }

    @Override
    public String toString() {
        return "AndroidDevice_" + device.toString();
    }

    @Override
    public void configureCapabilities(DesiredCapabilities capabilities) {
        super.configureCapabilities(capabilities);
        capabilities.setCapability(MobileCapabilityType.DEVICE_NAME, device.getName());
        try {
            capabilities.setCapability(MobileCapabilityType.AUTOMATION_NAME, device.getApiLevel() < 19
                    ? AutomationName.SELENDROID : Automation.NAME_ANDROID_UIAUTOMATOR2);
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    public void executeDeviceCommand(String command) throws IOException {
        String name = device.toString();
        // TODO: This only works on emulators
        try (Socket socket = new Socket("127.0.0.1", Integer.parseInt(name.substring(name.indexOf('-') + 1)))) {
            try (OutputStreamWriter writer = new OutputStreamWriter(socket.getOutputStream())) {
                writer.write(command);
                writer.write("\n");
            }
        }
    }

    public String executeShellCommand(String command) throws TimeoutException, AdbCommandRejectedException,
            ShellCommandUnresponsiveException, IOException {
        StringBuilder result = new StringBuilder();
        device.executeShellCommand(command, new IShellOutputReceiver() {
            @Override
            public void addOutput(byte[] buffer, int start, int count) {
                result.append(new String(buffer, start, count, Charsets.UTF_8));
            }

            @Override
            public void flush() { }

            @Override
            public boolean isCancelled() {
                return false;
            }
        });
        return result.toString();
    }
}
