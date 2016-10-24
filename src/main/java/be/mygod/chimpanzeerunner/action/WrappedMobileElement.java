package be.mygod.chimpanzeerunner.action;

import be.mygod.chimpanzeerunner.util.Lazy;
import io.appium.java_client.MobileElement;

import java.util.function.Supplier;

/**
 * This is needed so that getAttribute calls can be lazily computed.
 *
 * An example element would look like this:
 *
 * <android.widget.TextView index="1" text="Settings" class="android.widget.TextView" package="com.android.launcher3"
 *                          content-desc="Settings" checkable="false" checked="false" clickable="true" enabled="true"
 *                          focusable="true" focused="false" scrollable="false" long-clickable="true" password="false"
 *                          selected="false" bounds="[384,777][572,974]" resource-id="" instance="1"/>
 *
 * See also: https://github.com/appium/appium-uiautomator2-server/blob/4e99400/app/src/main/java/io/appium/uiautomator2/core/AccessibilityNodeInfoDumper.java#L96
 *
 * @author Mygod
 */
public class WrappedMobileElement {
    public final MobileElement inner;
    public final Supplier<String> className;

    public WrappedMobileElement(MobileElement element) {
        inner = element;
        className = Lazy.wrap(() -> inner.getAttribute("className"));
    }

    @Override
    public String toString() {
        return className.get() + ':' + inner.getId();
    }
}
