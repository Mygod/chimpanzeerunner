package be.mygod.chimpanzeerunner.test;

import io.appium.java_client.remote.AndroidMobileCapabilityType;
import io.appium.java_client.remote.MobileCapabilityType;
import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServiceBuilder;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.LinkedList;

public final class AppiumServicePool {
    private AppiumServicePool() { }

    private static boolean closed;
    private static final LinkedList<AppiumDriverLocalService> queue = new LinkedList<>();

    public static void close() {
        synchronized (queue) {
            closed = true;
        }
        for (AppiumDriverLocalService service : queue) service.stop();
        queue.clear();
    }
    public static AppiumDriverLocalService request() {
        AppiumDriverLocalService result;
        synchronized (queue) {
            if (closed) throw new IllegalStateException("AppiumServicePool has closed. Come back tomorrow.");
            result = queue.poll();
        }
        if (result != null) return result;
        DesiredCapabilities capabilities = new DesiredCapabilities();   // default capabilities
        //todo:capabilities.setCapability(MobileCapabilityType.AUTO_WEBVIEW, true);
        capabilities.setCapability(MobileCapabilityType.NO_RESET, true);
        capabilities.setCapability(MobileCapabilityType.NEW_COMMAND_TIMEOUT, 120);
        capabilities.setCapability(AndroidMobileCapabilityType.UNICODE_KEYBOARD, true);   // hmm why not?
        capabilities.setCapability(AndroidMobileCapabilityType.RESET_KEYBOARD, true);
        // Logging perhaps: withLogFile
        AppiumDriverLocalService service = new AppiumServiceBuilder().usingAnyFreePort()
                .withCapabilities(capabilities).build();
        service.start();
        return service;
    }
    public static void offer(AppiumDriverLocalService service) {
        boolean destroy;
        synchronized (queue) {
            if (!(destroy = closed)) queue.offer(service);
        }
        if (destroy) service.stop();
    }
}
