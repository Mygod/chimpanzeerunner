package be.mygod.chimpanzeerunner.android;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.service.local.AppiumDriverLocalService;
import be.mygod.chimpanzeerunner.devices.Device;
import be.mygod.chimpanzeerunner.test.TestManager;
import be.mygod.chimpanzeerunner.test.TestProfile;
import org.openqa.selenium.remote.DesiredCapabilities;

public class AndroidTestManager extends TestManager {
    public AndroidTestManager(TestProfile profile, Device device) {
        super(profile, device);
    }

    @Override
    protected AppiumDriver<MobileElement> createDriver(AppiumDriverLocalService service,
                                                       DesiredCapabilities capabilities) {
        return new AndroidDriver<>(service, capabilities);
    }
}
