package be.mygod.chimpanzeerunner.android.os;

import be.mygod.chimpanzeerunner.android.action.AndroidAction;
import be.mygod.chimpanzeerunner.device.Device;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;

import java.util.Arrays;
import java.util.stream.Stream;

public class BroadcastReceiver {
    public String componentName;
    public BroadcastFilter[] filters;

    public BroadcastReceiver(Element element) {
        componentName = element.getAttribute("android:componentName");
        filters = DomUtils.getChildElementsByTagName(element, "intent-filter").stream()
                .map(BroadcastFilter::new).toArray(BroadcastFilter[]::new);
    }

    public Stream<AndroidAction> getActions(Device device, String packageName) {
        return Arrays.stream(filters).flatMap(filter -> filter.getActions(device, packageName, componentName));
    }
}
