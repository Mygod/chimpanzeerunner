package be.mygod.chimpanzeerunner.android.os;

import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;

public class BroadcastReceiver {
	public String name;
	public BroadcastFilter[] filters;

	public BroadcastReceiver(Element element) {
		name = element.getAttribute("android:name");
		filters = DomUtils.getChildElementsByTagName(element, "intent-filter").stream()
				.map(BroadcastFilter::new).toArray(BroadcastFilter[]::new);
	}
}
