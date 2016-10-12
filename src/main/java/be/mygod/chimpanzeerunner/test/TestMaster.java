package be.mygod.chimpanzeerunner.test;

import be.mygod.chimpanzeerunner.App;
import be.mygod.chimpanzeerunner.device.Device;
import be.mygod.chimpanzeerunner.device.DeviceManager;
import be.mygod.chimpanzeerunner.strategy.AbstractStrategy;
import io.appium.java_client.remote.AndroidMobileCapabilityType;
import io.appium.java_client.remote.MobileCapabilityType;
import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServiceBuilder;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.File;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Function;

/**
 * The class that does the testing, as well as appium local server management.
 *
 * @author Mygod
 */
public final class TestMaster implements Runnable {
    private static Field streamField, streamsField;
    private static final SimpleDateFormat logFileFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
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

    public TestMaster(HashSet<TestProfile> profiles, Function<TestManager, AbstractStrategy> strategy) {
        this.profiles = profiles;
        this.strategy = strategy;
    }

    private final HashSet<TestProfile> profiles;
    private final Function<TestManager, AbstractStrategy> strategy;
    private final LinkedList<AppiumDriverLocalService> queue = new LinkedList<>();

    AppiumDriverLocalService request() {
        AppiumDriverLocalService result;
        synchronized (queue) {
            result = queue.poll();
        }
        if (result != null) return result;
        DesiredCapabilities capabilities = new DesiredCapabilities();   // default capabilities
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
    void offer(AppiumDriverLocalService service) {
        boolean destroy;
        synchronized (queue) {
            if (queue.size() < profiles.size()) {
                queue.offer(service);
                destroy = false;
            } else destroy = true;
        }
        if (destroy) service.stop();
    }

    private void removeProfile(Iterator<TestProfile> i) {
        AppiumDriverLocalService service = null;
        synchronized (queue) {
            i.remove();
            while (queue.size() > profiles.size()) service = queue.remove();
        }
        if (service != null) service.stop();
    }

    @Override
    public void run() {
        try {
            ArrayList<WeakReference<Thread>> threads = new ArrayList<>();
            System.out.print("Waiting for devices...\n");
            while (!profiles.isEmpty()) {
                Iterator<TestProfile> i = profiles.iterator();
                while (i.hasNext()) {
                    TestProfile profile = i.next();
                    DeviceManager dm = profile.getDeviceManager();
                    if (dm == null) {   // testing disabled
                        removeProfile(i);
                        continue;
                    }
                    Device device = dm.tryGetFreeDevice(profile::isAcceptableDevice);
                    if (device == null) continue;
                    Thread thread = new Thread(dm.startTest(this, profile, device, strategy));
                    thread.start();
                    threads.add(new WeakReference<>(thread));
                    removeProfile(i);
                }
                Thread.sleep(1000);
            }
            while (!threads.isEmpty()) {
                threads.removeIf(wr -> {
                    Thread thread = wr.get();
                    return thread == null || !thread.isAlive();
                });
                Thread.sleep(1000);
            }
        } catch (InterruptedException ignored) { }
    }
}
