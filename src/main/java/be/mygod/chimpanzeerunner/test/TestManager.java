package be.mygod.chimpanzeerunner.test;

import be.mygod.chimpanzeerunner.action.AbstractAction;
import be.mygod.chimpanzeerunner.action.NavigateBack;
import be.mygod.chimpanzeerunner.action.ui.UiAction;
import be.mygod.chimpanzeerunner.device.Device;
import be.mygod.chimpanzeerunner.listener.AppiumListener;
import be.mygod.chimpanzeerunner.strategy.AbstractStrategy;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;
import io.appium.java_client.events.EventFiringWebDriverFactory;
import io.appium.java_client.remote.MobileCapabilityType;
import io.appium.java_client.service.local.AppiumDriverLocalService;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.function.Function;
import java.util.stream.Stream;

public abstract class TestManager implements Runnable {
    private static final HashSet<URI> URI_WHITE_LIST;

    static {
        HashSet<URI> list;
        try {
            list = new HashSet<>(Arrays.asList(
                    new URI("android://com.android.documentsui/.DocumentsActivity"),
                    new URI("android://com.android.packageinstaller/.permission.ui.GrantPermissionsActivity"),
                    new URI("android://com.android.settings/.Settings$AppWriteSettingsActivity")
            ));
        } catch (URISyntaxException e) {
            e.printStackTrace();
            list = new HashSet<>();
        }
        URI_WHITE_LIST = list;
    }

    private final TestMaster master;
    protected final TestProfile profile;
    protected final Device device;
    private final Function<TestManager, AbstractStrategy> strategyFactory;
    private AppiumDriver<MobileElement> driver;

    public TestManager(TestMaster master, TestProfile profile, Device device,
                       Function<TestManager, AbstractStrategy> strategy) {
        this.master = master;
        this.profile = profile;
        this.device = device;
        strategyFactory = strategy;
    }

    public AppiumDriver<MobileElement> getDriver() {
        return driver;
    }

    protected abstract AppiumDriver<MobileElement> createDriver(AppiumDriverLocalService service,
                                                                DesiredCapabilities capabilities);
    public abstract String getPackageName();
    public abstract URI getLocation();
    public abstract void navigateBack();
    protected abstract void cleanUp();

    protected Stream<AbstractAction> getActions(URI location) {
        return Stream.concat(
                UiAction.getActions(this, location),
                NavigateBack.getActions(this, location)
        );
    }

    @Override
    public final void run() {
        AppiumDriverLocalService service = master.request();
        try {
            System.out.printf("Testing profile %s on %s with service %s...\n", profile, device, service.getUrl());
            DesiredCapabilities capabilities = new DesiredCapabilities();
            profile.configureCapabilities(capabilities);
            device.configureCapabilities(capabilities);
            Automation automation = device.getAutomation();
            capabilities.setCapability(MobileCapabilityType.AUTOMATION_NAME, automation.getName());
            driver = EventFiringWebDriverFactory.getEventFiringWebDriver(createDriver(service, capabilities),
                    AppiumListener.getListeners(this));
            try {
                AbstractStrategy strategy = strategyFactory.apply(this);
                for (;;) try {
                    URI location;
                    for (int i = 0; ; ++i) {
                        if (i >= 10) throw new TestAbortException("Unexpected location.");
                        location = getLocation();
                        if (getPackageName().equals(location.getHost()) || URI_WHITE_LIST.contains(location)) break;
                        else {
                            System.out.printf("Unexpected location: %s. Pressing back in hope of returning to original package...\n", location);
                            navigateBack();
                        }
                    }
                    if (!strategy.perform(getActions(location).distinct())) break;
                } catch (TestAbortException e) {
                    System.err.println(e.getMessage());
                    break;
                } catch (NoSuchElementException e) {
                    System.err.println("Element not found. Retrying later.");
                } catch (StaleElementReferenceException e) {
                    if (e.getMessage().startsWith("android.support.test.uiautomator.StaleObjectException\n"))
                        System.err.println("Element not found. It could be a [known issue](https://github.com/appium/appium-uiautomator2-server/issues/29). Retrying later.");
                    else e.printStackTrace();
                } catch (RuntimeException e) {
                    e.printStackTrace();
                }
            } finally {
                driver.quit();
            }
        } finally {
            cleanUp();
            master.offer(service);
        }
    }
}
