package be.mygod.chimpanzeerunner.listeners;

import io.appium.java_client.events.api.general.SearchingEventListener;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import be.mygod.chimpanzeerunner.test.TestManager;

public class SearchEventListener extends AppiumListener implements SearchingEventListener {
    public SearchEventListener(TestManager manager) {
        super(manager);
    }

    @Override
    public void beforeFindBy(By by, WebElement element, WebDriver driver) {

    }

    @Override
    public void afterFindBy(By by, WebElement element, WebDriver driver) {

    }
}
