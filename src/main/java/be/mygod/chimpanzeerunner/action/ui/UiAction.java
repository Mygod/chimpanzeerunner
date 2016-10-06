package be.mygod.chimpanzeerunner.action.ui;

import be.mygod.chimpanzeerunner.action.AbstractAction;
import be.mygod.chimpanzeerunner.test.TestManager;
import io.appium.java_client.MobileElement;
import org.openqa.selenium.By;

import java.util.stream.Stream;

/**
 * These are Android-specific for now. More info:
 * https://github.com/appium/appium-android-bootstrap/blob/e20f5a8/bootstrap/src/io/appium/android/bootstrap/AndroidElement.java
 *
 * Other stuff that may be used in the future:
 *   element.getAttribute("name") = contentDescription || text
 *   element.getAttribute("resourceId")
 *
 * @author Mygod
 */
public abstract class UiAction extends AbstractAction {
    public static Stream<AbstractAction> getActions(TestManager manager) {
        return manager.getDriver().findElements(By.xpath("//*")).stream()
                .filter(element -> element.isEnabled() && element.isDisplayed()).flatMap(UiAction::getActions);
    }

    private static Stream<UiAction> getActions(MobileElement element) {
        Stream<UiAction> result = Stream.empty();
        if (Boolean.parseBoolean(element.getAttribute("scrollable")))
            result = Stream.concat(result, Stream.of(new Swipe(element)));
        if (Boolean.parseBoolean(element.getAttribute("checkable")) ||
                Boolean.parseBoolean(element.getAttribute("clickable")))
            result = Stream.concat(result, Stream.of(new Click(element)));
        if (Boolean.parseBoolean(element.getAttribute("longClickable")))
            result = Stream.concat(result, Stream.of(new LongClick(element)));
        return result;
    }

    protected UiAction(MobileElement element) {
        this.element = element;
    }

    protected MobileElement element;

    @Override
    public String toString() {
        return element.getAttribute("className") + ':' + element.getId() + ':' + super.toString();
    }
}
