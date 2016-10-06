package be.mygod.chimpanzeerunner.listeners;

import io.appium.java_client.events.api.general.ElementEventListener;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import be.mygod.chimpanzeerunner.test.TestManager;

public class ElementListener extends AppiumListener implements ElementEventListener {
    public ElementListener(TestManager manager) {
        super(manager);
    }

    @Override
    public void beforeClickOn(WebElement element, WebDriver driver) {

    }

    @Override
    public void afterClickOn(WebElement element, WebDriver driver) {

    }

    @Override
    public void beforeChangeValueOf(WebElement element, WebDriver driver) {

    }

    @Override
    public void afterChangeValueOf(WebElement element, WebDriver driver) {

    }
}
