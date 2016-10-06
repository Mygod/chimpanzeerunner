package be.mygod.chimpanzeerunner.test;

import be.mygod.chimpanzeerunner.devices.Device;
import be.mygod.chimpanzeerunner.devices.DeviceManager;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashSet;

public final class TestMaster implements Runnable {
    private final HashSet<TestProfile> profiles;
    private final ArrayList<WeakReference<Thread>> threads = new ArrayList<>();

    public TestMaster(HashSet<TestProfile> profiles) {
        this.profiles = profiles;
    }

    @Override
    public void run() {
        try {
            System.out.print("Waiting for devices...\n");
            while (!profiles.isEmpty()) {
                profiles.removeIf(profile -> {
                    DeviceManager dm = profile.getDeviceManager();
                    if (dm == null) return true;    // testing disabled
                    Device device = dm.tryGetFreeDevice(profile::isAcceptableDevice);
                    if (device == null) return false;
                    Thread thread = new Thread(dm.startTest(profile, device));
                    thread.start();
                    threads.add(new WeakReference<>(thread));
                    return true;
                });
                Thread.sleep(1000);
            }
            AppiumServicePool.close();  // destroy existing services from now on
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
