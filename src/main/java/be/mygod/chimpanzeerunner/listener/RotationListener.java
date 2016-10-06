package be.mygod.chimpanzeerunner.listener;

import io.appium.java_client.events.api.mobile.RotationEventListener;
import org.openqa.selenium.ScreenOrientation;
import org.openqa.selenium.WebDriver;
import be.mygod.chimpanzeerunner.test.TestManager;

public class RotationListener extends AppiumListener implements RotationEventListener {
    public RotationListener(TestManager manager) {
        super(manager);
    }

    @Override
    public void beforeRotation(WebDriver driver, ScreenOrientation orientation) {

    }

    @Override
    public void afterRotation(WebDriver driver, ScreenOrientation orientation) {

    }
}
