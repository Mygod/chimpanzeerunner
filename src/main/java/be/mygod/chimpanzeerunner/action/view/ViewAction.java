package be.mygod.chimpanzeerunner.action.view;

import be.mygod.chimpanzeerunner.App;
import be.mygod.chimpanzeerunner.action.AbstractAction;
import be.mygod.chimpanzeerunner.test.TestManager;
import be.mygod.chimpanzeerunner.view.Activity;
import be.mygod.chimpanzeerunner.view.View;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;
import org.openqa.selenium.By;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.net.URI;
import java.util.LinkedList;
import java.util.Objects;
import java.util.stream.Stream;

public abstract class ViewAction extends AbstractAction {
    public static Stream<ViewAction> getActions(TestManager manager, URI location) {
        AppiumDriver<MobileElement> driver = manager.getDriver();
//        Stream<ViewAction> result = Stream.empty();
        Boolean webViewEnabled = false;
        if (driver.getContextHandles().contains(manager.getWebViewContext())) {
            driver.context(manager.getWebViewContext());
            for (String handle : driver.getWindowHandles()) {
                driver.switchTo().window(handle);
                String url = driver.getCurrentUrl();
                if (App.instance.testUrl.matcher(url).matches()) {
                    // TODO: handle cases for multiple webviews
//                    System.out.printf(String.valueOf(driver.findElementsByXPath("//*[enabled='true' and (scrollable='true' or checkable='true')]").size()));
                    //result = Stream.concat(result, getActions(manager, new WebView(url, driver.getPageSource())));
                    webViewEnabled = true;
                    break;
                }
            }
            driver.context(TestManager.NATIVE_APP);
        }
        if (webViewEnabled) System.out.println("Testing webviews...");
        try {
            return getActions(manager, new Activity(location, driver.getPageSource(), webViewEnabled).root);
//            for (WebElement element : driver.findElementsByTagName("android.webkit.WebView")) {
//                // element.getWindowHandle()?
//            }
//            result = Stream.concat(result, getActions(manager, new Activity(location, driver.getPageSource()).root));
        } catch (IOException | SAXException | ParserConfigurationException e) {
            e.printStackTrace();
        }
        return Stream.empty();
//        return result;
    }

    private static Stream<ViewAction> getActions(TestManager manager, View view) {
        if (!view.enabled) return Stream.empty();
        LinkedList<ViewAction> result = new LinkedList<>();
        if (view.scrollable) result.add(new Swipe(manager, view));
        if (view.isEditable()) result.add(new InputText(manager, view));
        else if (view.checkable || view.clickable) result.add(new Click(manager, view));
        if (view.longClickable) result.add(new LongClick(manager, view));
        return Stream.concat(result.stream(), view.children.stream().flatMap(child -> getActions(manager, child)));
    }

    protected ViewAction(TestManager manager, View view) {
        this.manager = manager;
        this.view = view;
    }

    protected final TestManager manager;
    protected final View view;

    /**
     * TODO: Replace formatting with xpath after this issue is fixed: https://github.com/appium/appium-uiautomator2-server/issues/34
     *
     * @return The element that needs action.
     */
    protected final MobileElement getElement() {
        return manager.getDriver().findElement(By.xpath(view.getXPath()));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!super.equals(o)) return false;
        ViewAction that = (ViewAction) o;
        return Objects.equals(view, that.view);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), view);
    }

    @Override
    public String toString() {
        return super.toString() + ':' + view;
    }
}
