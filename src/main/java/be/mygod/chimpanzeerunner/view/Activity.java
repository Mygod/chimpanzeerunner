package be.mygod.chimpanzeerunner.view;

import be.mygod.chimpanzeerunner.util.Xml;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Objects;

public final class Activity {
    public final URI uri;
    public final int rotation;
    public final View root;

    public Activity(URI uri, String source, Boolean webViewEnabled)
            throws IOException, SAXException, ParserConfigurationException {
        this.uri = uri;
        Element hierarchy = Xml.parse(source).getDocumentElement();
        MalformedSourceException.checkNodeName(hierarchy, "hierarchy");
        rotation = Integer.parseInt(hierarchy.getAttribute("rotation"));
        List<Element> children = DomUtils.getChildElements(hierarchy);
        if (children.size() != 1) throw new MalformedSourceException("There isn't only one hierarchy root found!");
        View root = new View(this, null, children.get(0), webViewEnabled);
        this.root = webViewEnabled || !root.isWebView() ? root : null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Activity activity = (Activity) o;
        return Objects.equals(uri, activity.uri);
    }

    @Override
    public int hashCode() {
        return uri.hashCode();
    }

    @Override
    public String toString() {
        return uri.toString();
    }
}
