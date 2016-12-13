package be.mygod.chimpanzeerunner.android.os;

import be.mygod.chimpanzeerunner.android.action.AndroidAction;
import be.mygod.chimpanzeerunner.device.Device;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;

import java.util.Arrays;
import java.util.stream.Stream;

public abstract class Component {
    public String name;
    public boolean exported;
    public BroadcastFilter[] filters;

    public Component(Element element) {
        name = element.getAttribute("android:name");
        filters = DomUtils.getChildElementsByTagName(element, "intent-filter").stream()
                .map(BroadcastFilter::new).toArray(BroadcastFilter[]::new);
        Boolean exported = BooleanUtils.toBooleanObject(element.getAttribute("android:exported"));
        this.exported = exported == null ? filters.length > 0 : exported;
    }

    public Stream<AndroidAction> getActions(Device device, String packageName) {
        return exported
                ? Arrays.stream(filters).flatMap(filter -> filter.getActions(device, packageName, name))
                : Stream.empty();
    }
}
