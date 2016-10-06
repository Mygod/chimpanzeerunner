package be.mygod.chimpanzeerunner.action.ui;

import be.mygod.chimpanzeerunner.test.TestManager;
import io.appium.java_client.AppiumDriver;
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
public final class UiActions {
    private UiActions() { }

    public static Stream<UiAction> getActions(TestManager manager) {
        return manager.getDriver().findElements(By.xpath("//*")).stream()
                .filter(element -> element.isEnabled() && element.isDisplayed()).flatMap(UiActions::getActions);
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
}
