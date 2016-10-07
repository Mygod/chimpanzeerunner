package be.mygod.chimpanzeerunner.device;

import be.mygod.chimpanzeerunner.android.device.AndroidDeviceManager;
import be.mygod.chimpanzeerunner.strategy.AbstractStrategy;
import be.mygod.chimpanzeerunner.test.TestManager;
import be.mygod.chimpanzeerunner.test.TestMaster;
import be.mygod.chimpanzeerunner.test.TestProfile;

import java.util.function.Function;

public abstract class DeviceManager {
    private static AndroidDeviceManager adm;
    public static DeviceManager getAndroid() {
        if (adm == null) adm = new AndroidDeviceManager();
        return adm.setup() ? adm : null;
    }
    public static void cleanUp() {
        if (adm != null) {
            adm.terminate();
            adm = null;
        }
    }

    private Boolean setupFinished;
    protected synchronized boolean setup() {
        if (setupFinished != null) return setupFinished;
        return setupFinished = setupCore();
    }
    protected abstract boolean setupCore();
    public abstract void terminate();

    public abstract Device tryGetFreeDevice(Function<Device, Boolean> filter);
    public abstract boolean releaseDevice(Device device);

    public abstract TestManager startTest(TestMaster master, TestProfile profile, Device device,
                                          Function<TestManager, AbstractStrategy> strategy);
}
