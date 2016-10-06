package be.mygod.chimpanzeerunner.test;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;
import io.appium.java_client.events.EventFiringWebDriverFactory;
import io.appium.java_client.service.local.AppiumDriverLocalService;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import be.mygod.chimpanzeerunner.devices.Device;
import be.mygod.chimpanzeerunner.listeners.AppiumListener;

public abstract class TestManager implements Runnable {
    protected TestProfile profile;
    protected Device device;

    public TestManager(TestProfile profile, Device device) {
        this.profile = profile;
        this.device = device;
    }

    protected abstract AppiumDriver<MobileElement> createDriver(AppiumDriverLocalService service,
                                                                DesiredCapabilities capabilities);

    @Override
    public final void run() {
        AppiumDriverLocalService service = AppiumServicePool.request();
        System.out.printf("Testing profile %s on device %s with service %s...\n", profile, device, service.getUrl());
        DesiredCapabilities capabilities = new DesiredCapabilities();
        profile.configureCapabilities(capabilities);
        device.configureCapabilities(capabilities);
        AppiumDriver<MobileElement> driver = EventFiringWebDriverFactory.getEventFiringWebDriver(
                createDriver(service, capabilities), AppiumListener.getListeners(this));
        for (WebElement element : driver.findElements(By.xpath("//*"))) {
            System.out.printf("%s, %d, %d\n", element.getTagName(),
                    element.getLocation().getX(), element.getLocation().getY());
        }
        // TODO: add more here
        driver.quit();
        service.stop();
    }
}
