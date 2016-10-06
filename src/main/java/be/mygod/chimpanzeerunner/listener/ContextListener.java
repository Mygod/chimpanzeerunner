package be.mygod.chimpanzeerunner.listener;

import io.appium.java_client.events.api.mobile.ContextEventListener;
import org.openqa.selenium.WebDriver;
import be.mygod.chimpanzeerunner.test.TestManager;

public class ContextListener extends AppiumListener implements ContextEventListener {
    public ContextListener(TestManager manager) {
        super(manager);
    }

    @Override
    public void beforeSwitchingToContext(WebDriver driver, String context) {

    }

    @Override
    public void afterSwitchingToContext(WebDriver driver, String context) {

    }
}
