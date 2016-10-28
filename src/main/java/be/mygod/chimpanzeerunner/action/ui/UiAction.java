package be.mygod.chimpanzeerunner.action.ui;

import be.mygod.chimpanzeerunner.action.AbstractAction;
import be.mygod.chimpanzeerunner.test.TestManager;
import be.mygod.chimpanzeerunner.view.Activity;
import be.mygod.chimpanzeerunner.view.View;
import io.appium.java_client.MobileElement;
import org.openqa.selenium.By;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.net.URI;
import java.util.LinkedList;
import java.util.stream.Stream;

public abstract class UiAction extends AbstractAction {
    public static Stream<UiAction> getActions(TestManager manager, URI location) {
        try {
            return getActions(manager, new Activity(location, manager.getDriver().getPageSource()).root);
        } catch (IOException | SAXException | ParserConfigurationException e) {
            e.printStackTrace();
        }
        return Stream.empty();
    }

    private static Stream<UiAction> getActions(TestManager manager, View view) {
        if (!view.enabled) return Stream.empty();
        LinkedList<UiAction> result = new LinkedList<>();
        if (view.scrollable) result.add(new Swipe(manager, view));
        if (view.checkable || view.clickable) result.add(new Click(manager, view));
        if (view.longClickable) result.add(new LongClick(manager, view));
        return Stream.concat(result.stream(), view.stream().flatMap(child -> getActions(manager, child)));
    }

    protected UiAction(TestManager manager, View view) {
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
        String xpath = view.getXPath();
        return manager.getDriver().findElement(By.xpath(String.format("%s|/hierarchy%s", xpath, xpath)));
    }

    @Override
    public String toString() {
        return super.toString() + ':' + view;
    }
}
