package be.mygod.chimpanzeerunner.android;

import com.android.ddmlib.IDevice;
import be.mygod.chimpanzeerunner.devices.Device;
import io.appium.java_client.remote.MobileCapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

public class AndroidDevice extends Device {
    public IDevice device;

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
    }
}
