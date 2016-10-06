package be.mygod.chimpanzeerunner.android;

import be.mygod.chimpanzeerunner.devices.Device;
import be.mygod.chimpanzeerunner.devices.DeviceManager;
import be.mygod.chimpanzeerunner.test.TestProfile;
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
            Document manifest = dbf.newDocumentBuilder()
                    .parse(new InputSource(new StringReader(parser.getManifestXml())));
            Element application = DomUtils.getChildElementByTagName(manifest.getDocumentElement(), "application");
            for (Element element : DomUtils.getChildElements(application)) switch (element.getNodeName()) {
//                case "activity":
//                    if (DomUtils.getChildElementsByTagName(element, "intent-filter").stream().map(BroadcastFilter::new)
//                            .anyMatch(filter -> filter.actions.contains("android.intent.action.MAIN") &&
//                                    filter.categories.contains("android.intent.category.LAUNCHER")))
//                        mainActivityName = element.getAttribute("android:name");
//                    break;
                case "receiver":
                    // TODO: add receiver list
                    break;
            }
        }
    }

    private String name;

    @Override
    public DeviceManager getDeviceManager() {
        return DeviceManager.getAndroid();
    }

    @Override
    public boolean isAcceptableDevice(Device device) {
        // TODO: add more
        return device instanceof AndroidDevice;
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
