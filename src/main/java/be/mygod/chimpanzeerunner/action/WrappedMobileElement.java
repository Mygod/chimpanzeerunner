package be.mygod.chimpanzeerunner.action;

import be.mygod.chimpanzeerunner.util.Lazy;
import io.appium.java_client.MobileElement;

import java.util.function.Supplier;

/**
 * This is needed so that getAttribute calls can be lazily computed.
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
