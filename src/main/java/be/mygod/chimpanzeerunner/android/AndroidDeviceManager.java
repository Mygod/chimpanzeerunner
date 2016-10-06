package be.mygod.chimpanzeerunner.android;

import be.mygod.chimpanzeerunner.App;
import be.mygod.chimpanzeerunner.test.TestManager;
import be.mygod.chimpanzeerunner.test.TestProfile;
import com.android.ddmlib.AndroidDebugBridge;
import com.android.ddmlib.IDevice;
import be.mygod.chimpanzeerunner.devices.Device;
import be.mygod.chimpanzeerunner.devices.DeviceManager;

import java.io.File;
import java.util.HashSet;
import java.util.function.Function;

public class AndroidDeviceManager extends DeviceManager {
    private File adbFile;
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
        this.adbFile = adbFile;
        AndroidDebugBridge.init(false);
        adbBridge = AndroidDebugBridge.createBridge(adbFile.getAbsolutePath(), false);
        //New Changes
        //AndroidDebugBridge.addClientChangeListener(new ClientChangeListener());
        /*try{
        ClientData.setMethodProfilingHandler(new MethodProfileHandler(new TextLogger(PropertyParser.baseWorkingDir+"/MethodProfilerLog.txt")));
        } catch(Exception e){
            Logger.logError("Problem occured while setting the profile handler");
            Logger.logException(e);
        }	*/
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
            for (IDevice device : adbBridge.getDevices()) if (!busyDevices.contains(device.toString())) {
                Device dev = new AndroidDevice(device);
                if (filter == null || filter.apply(dev)) {
                    busyDevices.add(device.toString());
                    return dev;
                }
            }
        }
        return null;
    }
    @Override
    public boolean releaseDevice(Device device) {
        synchronized (busyDevices) {
            return busyDevices.remove(device.toString());
        }
    }

    @Override
    public TestManager startTest(TestProfile profile, Device device) {
        return new AndroidTestManager(profile, device);
    }
}
