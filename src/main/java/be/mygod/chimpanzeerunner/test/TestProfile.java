package be.mygod.chimpanzeerunner.test;

import be.mygod.chimpanzeerunner.devices.DeviceManager;
import io.appium.java_client.remote.MobileCapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import be.mygod.chimpanzeerunner.android.AndroidTestProfile;
import be.mygod.chimpanzeerunner.devices.Device;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;

public abstract class TestProfile {
    public static HashSet<TestProfile> createFromPaths(List<String> paths) {
        HashSet<TestProfile> result = new HashSet<>();
        for (String path : paths) {
            File file = new File(path);
            if (file.exists()) addToResult(file, result);
            else System.err.printf("Warning: File %s doesn't exist.\n", path);
        }
        return result;
    }

    /**
     * A recursive search for test profiles.
     * @param file The directory/file to search.
     */
    private static void addToResult(File file, HashSet<TestProfile> result) {
        if (file.isDirectory())
            //noinspection ConstantConditions
            for (File sub : file.listFiles()) addToResult(sub, result);
        else {
            TestProfile profile = fromFile(file);
            if (profile != null) result.add(profile);
        }
    }

    public static TestProfile fromFile(File file) {
        String name = file.getName().toLowerCase();
        if (name.endsWith(".apk")) try {
            return new AndroidTestProfile(file);
        } catch (IOException | ParserConfigurationException | SAXException e) {
            e.printStackTrace();
        }
        return null;
    }

    protected File appFile;

    protected TestProfile(File file) {
        appFile = file;
    }

    public abstract DeviceManager getDeviceManager();

    public boolean isAcceptableDevice(Device device) {
        return true;
    }

    public void configureCapabilities(DesiredCapabilities capabilities) {
        capabilities.setCapability(MobileCapabilityType.APP, appFile.getAbsolutePath());
    }

    @Override
    public String toString() {
        return appFile.getName();
    }
}
