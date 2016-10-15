package be.mygod.chimpanzeerunner.action.ui;

import be.mygod.chimpanzeerunner.action.AbstractAction;
import be.mygod.chimpanzeerunner.test.TestManager;
import io.appium.java_client.MobileElement;
import org.openqa.selenium.By;

import java.util.LinkedList;
import java.util.stream.Stream;

public abstract class UiAction extends AbstractAction {
    public static Stream<AbstractAction> getActions(TestManager manager) {
//        manager.getDriver().findElements(By.xpath("//*")).forEach(element -> {
//            try {
//                System.out.println(element.getAttribute("enabled"));
//            } catch (Exception ignored) { }
//        });
        return manager.getDriver().findElements(By.xpath("//*[@enabled='true']")).stream()
                .filter(element -> Boolean.parseBoolean(element.getAttribute("displayed")))
                .flatMap(element -> UiAction.getActions(manager, element));
    }

    private static Stream<UiAction> getActions(TestManager manager, MobileElement element) {
        LinkedList<UiAction> result = new LinkedList<>();
        if (Boolean.parseBoolean(element.getAttribute("scrollable"))) result.add(new Swipe(manager, element));
        if (Boolean.parseBoolean(element.getAttribute("checkable")) ||
                Boolean.parseBoolean(element.getAttribute("clickable"))) result.add(new Click(manager, element));
        if (Boolean.parseBoolean(element.getAttribute("longClickable")))
            result.add(new LongClick(manager, element));
        return result.stream();
    }

    protected UiAction(TestManager manager, MobileElement element) {
        this.manager = manager;
        this.element = element;
    }

    protected final TestManager manager;
    protected final MobileElement element;

    @Override
    public String toString() {
        return super.toString() + ':' + element.getAttribute("className") + ':' + element.getId();
    }
}
