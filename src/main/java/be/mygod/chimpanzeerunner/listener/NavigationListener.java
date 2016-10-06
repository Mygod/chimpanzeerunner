package be.mygod.chimpanzeerunner.listener;

import io.appium.java_client.events.api.general.NavigationEventListener;
import org.openqa.selenium.WebDriver;
import be.mygod.chimpanzeerunner.test.TestManager;

public class NavigationListener extends AppiumListener implements NavigationEventListener {
    public NavigationListener(TestManager manager) {
        super(manager);
    }

    @Override
    public void beforeNavigateTo(String url, WebDriver driver) {

    }

    @Override
    public void afterNavigateTo(String url, WebDriver driver) {

    }

    @Override
    public void beforeNavigateBack(WebDriver driver) {

    }

    @Override
    public void afterNavigateBack(WebDriver driver) {

    }

    @Override
    public void beforeNavigateForward(WebDriver driver) {

    }

    @Override
    public void afterNavigateForward(WebDriver driver) {

    }

    @Override
    public void beforeNavigateRefresh(WebDriver driver) {

    }

    @Override
    public void afterNavigateRefresh(WebDriver driver) {

    }
}
