package be.mygod.chimpanzeerunner.android.device;

import be.mygod.chimpanzeerunner.App;
import be.mygod.chimpanzeerunner.android.test.AndroidTestManager;
import be.mygod.chimpanzeerunner.strategy.AbstractStrategy;
import be.mygod.chimpanzeerunner.test.TestManager;
import be.mygod.chimpanzeerunner.test.TestMaster;
import be.mygod.chimpanzeerunner.test.TestProfile;
import com.android.ddmlib.AndroidDebugBridge;
import com.android.ddmlib.IDevice;
import be.mygod.chimpanzeerunner.device.Device;
import be.mygod.chimpanzeerunner.device.DeviceManager;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.function.Function;

public class AndroidDeviceManager extends DeviceManager {
    private AndroidDebugBridge adbBridge;

    @Override
    protected boolean setupCore() {
        if (App.instance.androidHome == null) {
            System.err.println("Please set $ANDROID_HOME! Android testing has been disabled.");
            return false;
        }
        File adbFile = new File(App.instance.androidHome + "/platform-tools/adb");
        if (!adbFile.exists()) {
            System.err.println("adb not found under platform-tools! Android testing has been disabled.");
            return false;
        }
        AndroidDebugBridge.init(false);
        adbBridge = AndroidDebugBridge.createBridge(adbFile.getAbsolutePath(), false);
        return true;
    }

    @Override
    public void terminate() {
        AndroidDebugBridge.terminate();
    }

    private final HashSet<String> busyDevices = new HashSet<>();
    @Override
    public Device tryGetFreeDevice(Function<Device, Boolean> filter) {
        synchronized (busyDevices) {
            for (IDevice device : adbBridge.getDevices()) if (!busyDevices.contains(device.toString())) try {
                Device dev = new AndroidDevice(device);
                if (filter == null || filter.apply(dev)) {
                    busyDevices.add(device.toString());
                    return dev;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
    @Override
    public boolean releaseDevice(Device device) {
        device.release();
        synchronized (busyDevices) {
            return busyDevices.remove(device.toString());
        }
    }

    @Override
    public TestManager startTest(TestMaster master, TestProfile profile, Device device,
                                 Function<TestManager, AbstractStrategy> strategy) {
        return new AndroidTestManager(master, profile, device, strategy);
    }
}
