package be.mygod.chimpanzeerunner.android.test;

import be.mygod.chimpanzeerunner.android.device.AndroidDevice;
import be.mygod.chimpanzeerunner.android.os.Activity;
import be.mygod.chimpanzeerunner.android.os.BroadcastReceiver;
import be.mygod.chimpanzeerunner.device.Device;
import be.mygod.chimpanzeerunner.device.DeviceManager;
import be.mygod.chimpanzeerunner.test.TestProfile;
import be.mygod.chimpanzeerunner.util.Xml;
import com.android.ddmlib.IDevice;
import io.appium.java_client.remote.MobileCapabilityType;
import net.dongliu.apk.parser.ApkParser;
import net.dongliu.apk.parser.bean.ApkMeta;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class AndroidTestProfile extends TestProfile {
    public AndroidTestProfile(File file) throws IOException, ParserConfigurationException, SAXException {
        super(file);
        try (ApkParser parser = new ApkParser(appFile)) {
            ApkMeta meta = parser.getApkMeta();
            packageName = meta.getPackageName();
            name = String.format("%s %s (%d)", meta.getName(), meta.getVersionName(), meta.getVersionCode());
            minSdkVersion = Integer.parseInt(meta.getMinSdkVersion());
            Document manifest = Xml.parse(parser.getManifestXml());
            Element application = DomUtils.getChildElementByTagName(manifest.getDocumentElement(), "application");
            for (Element element : DomUtils.getChildElements(application)) switch (element.getNodeName()) {
                case "activity":
                case "activity-alias":  // ignore the differences for now
                    if (mainActivity == null) {
                        Activity activity = new Activity(element);
                        if (activity.isMainActivity()) mainActivity = activity;
                    }
                    break;
                case "receiver":
                    receivers.add(new BroadcastReceiver(element));
                    break;
            }
        }
    }

    public final String packageName;
    public Activity mainActivity;
    public final ArrayList<BroadcastReceiver> receivers = new ArrayList<>();

    private final String name;
    private final int minSdkVersion;

    @Override
    public DeviceManager getDeviceManager() {
        return DeviceManager.getAndroid();
    }

    @Override
    public boolean isAcceptableDevice(Device device) {
        if (!(device instanceof AndroidDevice)) return false;
        IDevice dev = ((AndroidDevice) device).device;
        try {
            return dev.isOnline() && dev.getApiLevel() >= minSdkVersion;
        } catch (IllegalStateException e) {
            System.err.printf("%s not ready.\n", device);
            return false;
        }
    }

    @Override
    public void configureCapabilities(DesiredCapabilities capabilities) {
        super.configureCapabilities(capabilities);
        capabilities.setCapability(MobileCapabilityType.PLATFORM_NAME, "Android");
    }

    @Override
    public String toString() {
        return name;
    }
}
