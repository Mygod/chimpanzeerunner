package be.mygod.chimpanzeerunner.android;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.service.local.AppiumDriverLocalService;
import be.mygod.chimpanzeerunner.device.Device;
import be.mygod.chimpanzeerunner.test.TestManager;
import be.mygod.chimpanzeerunner.test.TestProfile;
import org.openqa.selenium.remote.DesiredCapabilities;

public class AndroidTestManager extends TestManager {
    public AndroidTestManager(TestProfile profile, Device device) {
        super(profile, device);
    }
    private AndroidDriver<MobileElement> driver;

    @Override
    protected AppiumDriver<MobileElement> createDriver(AppiumDriverLocalService service,
                                                       DesiredCapabilities capabilities) {
        return driver = new AndroidDriver<>(service, capabilities);
    }

    @Override
    public String getLocation() {
        // TODO: better detection for back availability
        return driver.currentActivity();
    }

    @Override
    public void navigateBack() {
        driver.pressKeyCode(4); // android.view.KeyEvent.KEYCODE_BACK
    }
}
