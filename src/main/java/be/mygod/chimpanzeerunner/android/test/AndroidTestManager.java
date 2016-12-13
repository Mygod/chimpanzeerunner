package be.mygod.chimpanzeerunner.android.test;

import be.mygod.chimpanzeerunner.action.AbstractAction;
import be.mygod.chimpanzeerunner.android.action.AndroidAction;
import be.mygod.chimpanzeerunner.android.action.PressMediaButton;
import be.mygod.chimpanzeerunner.android.device.AndroidDevice;
import be.mygod.chimpanzeerunner.android.os.Activity;
import be.mygod.chimpanzeerunner.device.Device;
import be.mygod.chimpanzeerunner.device.DeviceManager;
import be.mygod.chimpanzeerunner.strategy.AbstractStrategy;
import be.mygod.chimpanzeerunner.test.TestManager;
import be.mygod.chimpanzeerunner.test.TestMaster;
import be.mygod.chimpanzeerunner.test.TestProfile;
import com.android.ddmlib.AdbCommandRejectedException;
import com.android.ddmlib.ShellCommandUnresponsiveException;
import com.android.ddmlib.TimeoutException;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.service.local.AppiumDriverLocalService;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
    public String getPackageName() {
        return ((AndroidTestProfile) profile).packageName;
    }

    private static final Pattern
            PATTERN_CURRENT_FOCUS = Pattern.compile("^  mFocusedApp=AppWindowToken\\{[0-9a-fA-F]+ token=Token\\{[0-9a-fA-F]+ ActivityRecord\\{[0-9a-fA-F]+ u\\d+ (.+/.+) t\\d+}}}$", Pattern.MULTILINE);
    @Override
    public URI getLocation() {
        // TODO: better detection for back availability
        AndroidDevice device = (AndroidDevice) this.device;
        try {
            Matcher matcher = PATTERN_CURRENT_FOCUS.matcher(device.executeShellCommand("dumpsys window windows"));
            if (matcher.find()) return new URI("android://" + matcher.group(1));
        } catch (TimeoutException | AdbCommandRejectedException | IOException | ShellCommandUnresponsiveException | URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void startMainActivity() {
        AndroidTestProfile atp = (AndroidTestProfile) profile;
        driver.startActivity(atp.packageName, atp.mainActivity.name,
                atp.packageName, atp.mainActivity.name,
                Activity.ACTION_MAIN, Activity.CATEGORY_LAUNCHER, null, null);
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
    protected Stream<AbstractAction> getActions(URI location) {
        AndroidDevice device = (AndroidDevice) this.device;
        AndroidTestProfile profile = (AndroidTestProfile) this.profile;
        return Stream.of(
                super.getActions(location),
                AndroidAction.getActionsFromReceivers(device, profile),
                AndroidAction.getActionsFromAudioFocus(device, profile),
                PressMediaButton.getActions(device, profile)
        ).flatMap(x -> x);
    }
}
