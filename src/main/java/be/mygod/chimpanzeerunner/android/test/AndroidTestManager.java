package be.mygod.chimpanzeerunner.android.test;

import be.mygod.chimpanzeerunner.action.AbstractAction;
import be.mygod.chimpanzeerunner.android.action.AndroidAction;
import be.mygod.chimpanzeerunner.android.action.PressMediaButton;
import be.mygod.chimpanzeerunner.android.device.AndroidDevice;
import be.mygod.chimpanzeerunner.device.Device;
import be.mygod.chimpanzeerunner.device.DeviceManager;
import be.mygod.chimpanzeerunner.strategy.AbstractStrategy;
import be.mygod.chimpanzeerunner.test.TestManager;
import be.mygod.chimpanzeerunner.test.TestMaster;
import be.mygod.chimpanzeerunner.test.TestProfile;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.service.local.AppiumDriverLocalService;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.function.Function;
import java.util.stream.Stream;

public class AndroidTestManager extends TestManager {
    public AndroidTestManager(TestMaster master, TestProfile profile, Device device,
                              Function<TestManager, AbstractStrategy> strategy) {
        super(master, profile, device, strategy);
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

    @Override
    protected void cleanUp() {
        //noinspection ConstantConditions
        DeviceManager.getAndroid().releaseDevice(device);
    }

    @Override
    protected Stream<AbstractAction> getActions() {
        AndroidDevice device = (AndroidDevice) this.device;
        AndroidTestProfile profile = (AndroidTestProfile) this.profile;
        return Stream.of(
                super.getActions(),
                AndroidAction.getActionsFromReceivers(device, profile),
                AndroidAction.getActionsFromAudioFocus(device, profile),
                PressMediaButton.getActions(device, profile)
        ).flatMap(x -> x);
    }
}
