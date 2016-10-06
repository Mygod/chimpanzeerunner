package be.mygod.chimpanzeerunner.test;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;
import io.appium.java_client.android.AndroidElement;
import io.appium.java_client.events.EventFiringWebDriverFactory;
import io.appium.java_client.service.local.AppiumDriverLocalService;
import org.openqa.selenium.By;
import org.openqa.selenium.Point;
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
        //element.click();
        driver.findElements(By.xpath("//*")).stream().filter(WebElement::isEnabled).forEach(element -> {
            // These are Android-specific for now. More info: https://github.com/appium/appium-android-bootstrap/blob/master/bootstrap/src/io/appium/android/bootstrap/AndroidElement.java
            //element.getAttribute("name") = contentDescription || text
            //element.getAttribute("text")
            //element.getAttribute("className")
            //element.getAttribute("resourceId")
//            if (Boolean.parseBoolean(element.getAttribute("checkable"))) {
//            }
//            if (Boolean.parseBoolean(element.getAttribute("clickable"))) {
//                //element.click();
//            }
//            if (Boolean.parseBoolean(element.getAttribute("longClickable"))) {
//                //element.tap(1, 500);
//            }
//            if (Boolean.parseBoolean(element.getAttribute("scrollable"))) {
//            }
//            if (Boolean.parseBoolean(element.getAttribute("displayed"))) {
//            }
        });
        // TODO: add more here
        driver.quit();
        service.stop();
    }
}
