package be.mygod.chimpanzeerunner.test;

import be.mygod.chimpanzeerunner.action.AbstractAction;
import be.mygod.chimpanzeerunner.device.Device;
import be.mygod.chimpanzeerunner.listener.AppiumListener;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;
import io.appium.java_client.events.EventFiringWebDriverFactory;
import io.appium.java_client.service.local.AppiumDriverLocalService;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.stream.Stream;

public abstract class TestManager implements Runnable {
    protected TestProfile profile;
    protected Device device;
    private AppiumDriver<MobileElement> driver;

    public TestManager(TestProfile profile, Device device) {
        this.profile = profile;
        this.device = device;
    }

    public AppiumDriver<MobileElement> getDriver() {
        return driver;
    }

    protected abstract AppiumDriver<MobileElement> createDriver(AppiumDriverLocalService service,
                                                                DesiredCapabilities capabilities);
    public abstract String getLocation();
    public abstract void navigateBack();

    @Override
    public final void run() {
        AppiumDriverLocalService service = AppiumServicePool.request();
        System.out.printf("Testing profile %s on device %s with service %s...\n", profile, device, service.getUrl());
        DesiredCapabilities capabilities = new DesiredCapabilities();
        profile.configureCapabilities(capabilities);
        device.configureCapabilities(capabilities);
        driver = EventFiringWebDriverFactory.getEventFiringWebDriver(createDriver(service, capabilities),
                AppiumListener.getListeners(this));
        Stream<AbstractAction> actions = AbstractAction.getActions(this);
        actions.forEach(action -> System.out.printf("%s\n", action));
        driver.quit();
        AppiumServicePool.offer(service);
    }
}
