package be.mygod.chimpanzeerunner.test;

import be.mygod.chimpanzeerunner.App;
import io.appium.java_client.remote.AndroidMobileCapabilityType;
import io.appium.java_client.remote.MobileCapabilityType;
import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServiceBuilder;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.File;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;

public final class AppiumServicePool {
    private AppiumServicePool() { }

    private static boolean closed;
    private static final LinkedList<AppiumDriverLocalService> queue = new LinkedList<>();
    private static final SimpleDateFormat logFileFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");

    private static Field streamField, streamsField;

    static {
        try {
            streamField = AppiumDriverLocalService.class.getDeclaredField("stream");
            streamField.setAccessible(true);
            streamsField = Class.forName("io.appium.java_client.service.local.ListOutputStream")
                    .getDeclaredField("streams");
            streamsField.setAccessible(true);
        } catch (ClassNotFoundException | NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    public static void close() {
        synchronized (queue) {
            closed = true;
        }
        queue.forEach(AppiumDriverLocalService::stop);
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
        File logFile = new File(App.instance.logDir, String.format("appium-%s.log", logFileFormat.format(new Date())));
        System.out.printf("Creating new appium service with log file: %s\n", logFile);
        AppiumDriverLocalService service = new AppiumServiceBuilder()
                .usingAnyFreePort()
                .withCapabilities(capabilities)
                .withLogFile(logFile)
                .build();
        try {
            ((ArrayList<OutputStream>) streamsField.get(streamField.get(service))).clear(); // remove System.out logging
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
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
