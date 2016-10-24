package be.mygod.chimpanzeerunner.action.ui;

import be.mygod.chimpanzeerunner.action.AbstractAction;
import be.mygod.chimpanzeerunner.action.WrappedMobileElement;
import be.mygod.chimpanzeerunner.test.TestManager;
import io.appium.java_client.MobileElement;
import org.openqa.selenium.By;

import java.util.LinkedList;
import java.util.stream.Stream;

public abstract class UiAction extends AbstractAction {
    public static Stream<AbstractAction> getActions(TestManager manager) {
        return manager.getDriver().findElements(By.xpath("//*[@enabled='true']")).stream()
                .filter(element -> Boolean.parseBoolean(element.getAttribute("displayed"))) // TODO: blocked by https://github.com/appium/appium/issues/6974
                .flatMap(element -> UiAction.getActions(manager, element));
    }

    private static Stream<UiAction> getActions(TestManager manager, MobileElement element) {
        LinkedList<UiAction> result = new LinkedList<>();
        WrappedMobileElement wrapped = new WrappedMobileElement(element);
        if (Boolean.parseBoolean(element.getAttribute("scrollable"))) result.add(new Swipe(manager, wrapped));
        if (Boolean.parseBoolean(element.getAttribute("checkable")) ||
                Boolean.parseBoolean(element.getAttribute("clickable"))) result.add(new Click(manager, wrapped));
        if (Boolean.parseBoolean(element.getAttribute("longClickable")))
            result.add(new LongClick(manager, wrapped));
        return result.stream();
    }

    protected UiAction(TestManager manager, WrappedMobileElement element) {
        this.manager = manager;
        this.element = element;
    }

    protected final TestManager manager;
    protected final WrappedMobileElement element;

    @Override
    public String toString() {
        return super.toString() + ':' + element;
    }
}
