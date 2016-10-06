package be.mygod.chimpanzeerunner.android;

import be.mygod.chimpanzeerunner.android.os.BroadcastReceiver;
import be.mygod.chimpanzeerunner.devices.Device;
import be.mygod.chimpanzeerunner.devices.DeviceManager;
import be.mygod.chimpanzeerunner.test.TestProfile;
import com.android.ddmlib.IDevice;
import io.appium.java_client.remote.MobileCapabilityType;
import net.dongliu.apk.parser.ApkParser;
import net.dongliu.apk.parser.bean.ApkMeta;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;

public class AndroidTestProfile extends TestProfile {
    private static final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

    public AndroidTestProfile(File file) throws IOException, ParserConfigurationException, SAXException {
        super(file);
        try (ApkParser parser = new ApkParser(appFile)) {
            ApkMeta meta = parser.getApkMeta();
            name = String.format("%s %s (%d)", meta.getName(), meta.getVersionName(), meta.getVersionCode());
            minSdkVersion = Integer.parseInt(meta.getMinSdkVersion());
            Document manifest = dbf.newDocumentBuilder()
                    .parse(new InputSource(new StringReader(parser.getManifestXml())));
            Element application = DomUtils.getChildElementByTagName(manifest.getDocumentElement(), "application");
            receivers = DomUtils.getChildElements(application).stream()
                    .filter(element -> "receiver".equals(element.getNodeName()))
                    .map(BroadcastReceiver::new).toArray(BroadcastReceiver[]::new);
        }
    }

    private String name;
    private int minSdkVersion;
    private BroadcastReceiver[] receivers;

    @Override
    public DeviceManager getDeviceManager() {
        return DeviceManager.getAndroid();
    }

    @Override
    public boolean isAcceptableDevice(Device device) {
        if (!(device instanceof AndroidDevice)) return false;
        IDevice dev = ((AndroidDevice) device).device;
        return dev.isOnline() && dev.getApiLevel() >= minSdkVersion;
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
