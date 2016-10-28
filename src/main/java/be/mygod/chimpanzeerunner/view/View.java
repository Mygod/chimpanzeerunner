package be.mygod.chimpanzeerunner.view;

import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.Objects;

public class View extends ArrayList<View> {
    public final Activity activity;
    public final View parent;
    public final String className, text, contentDesc, resourceId;
    public final int index, instance;
    public final boolean checkable, checked, clickable, enabled, focusable, focused, scrollable, longClickable,
            password, selected;
    public final Bounds bounds;

    public View(Activity activity, View parent, Element element) throws MalformedSourceException {
        this.activity = activity;
        this.parent = parent;
        MalformedSourceException.checkNodeName(element, className = element.getAttribute("class"));
        text = element.getAttribute("text");
        // ignore package for now
        contentDesc = element.getAttribute("content-desc");
        resourceId = element.getAttribute("resource-id");
        index = Integer.parseInt(element.getAttribute("index"));
        instance = Integer.parseInt(element.getAttribute("instance"));
        checkable = Boolean.parseBoolean(element.getAttribute("checkable"));
        checked = Boolean.parseBoolean(element.getAttribute("checked"));
        clickable = Boolean.parseBoolean(element.getAttribute("clickable"));
        enabled = Boolean.parseBoolean(element.getAttribute("enabled"));
        focusable = Boolean.parseBoolean(element.getAttribute("focusable"));
        focused = Boolean.parseBoolean(element.getAttribute("focused"));
        scrollable = Boolean.parseBoolean(element.getAttribute("scrollable"));
        longClickable = Boolean.parseBoolean(element.getAttribute("long-clickable"));
        password = Boolean.parseBoolean(element.getAttribute("password"));
        selected = Boolean.parseBoolean(element.getAttribute("selected"));
        bounds = new Bounds(element.getAttribute("bounds"));
        for (Element child : DomUtils.getChildElements(element)) add(new View(activity, this, child));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        View view = (View) o;
        return checkable == view.checkable &&
                clickable == view.clickable &&
                focusable == view.focusable &&
                scrollable == view.scrollable &&
                longClickable == view.longClickable &&
                password == view.password &&
                Objects.equals(activity, view.activity) &&
                Objects.equals(parent, view.parent) &&
                Objects.equals(className, view.className) &&
                Objects.equals(text, view.text) &&
                Objects.equals(contentDesc, view.contentDesc) &&
                Objects.equals(resourceId, view.resourceId) &&
                Objects.equals(bounds, view.bounds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), activity, parent, className, text, contentDesc, resourceId, checkable,
                clickable, focusable, scrollable, longClickable, password, bounds);
    }

    @Override
    public String toString() {
        return className + '{' +
                "text='" + text + '\'' +
                '}';
    }

    private int findIndex(String className, View view) {
        int result = 1;
        for (View child : this) if (view == child) return result; else if (className.equals(child.className)) ++result;
        throw new IndexOutOfBoundsException();
    }
    public String getXPath() {
        return parent == null ? String.format("/%s[1]", className)
                : String.format("%s/%s[%d]", parent.getXPath(), className, parent.findIndex(className, this));
    }
}
