package be.mygod.chimpanzeerunner.listeners;

import io.appium.java_client.events.api.general.JavaScriptEventListener;
import org.openqa.selenium.WebDriver;
import be.mygod.chimpanzeerunner.test.TestManager;

public class JavascriptEventListener extends AppiumListener implements JavaScriptEventListener {
    public JavascriptEventListener(TestManager manager) {
        super(manager);
    }

    @Override
    public void beforeScript(String script, WebDriver driver) {

    }

    @Override
    public void afterScript(String script, WebDriver driver) {

    }
}
