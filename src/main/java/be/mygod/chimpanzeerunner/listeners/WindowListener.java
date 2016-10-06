package be.mygod.chimpanzeerunner.listeners;

import io.appium.java_client.events.api.general.WindowEventListener;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import be.mygod.chimpanzeerunner.test.TestManager;

public class WindowListener extends AppiumListener implements WindowEventListener {
    public WindowListener(TestManager manager) {
        super(manager);
    }

    @Override
    public void beforeWindowChangeSize(WebDriver driver, WebDriver.Window window, Dimension targetSize) {

    }

    @Override
    public void afterWindowChangeSize(WebDriver driver, WebDriver.Window window, Dimension targetSize) {

    }

    @Override
    public void beforeWindowIsMoved(WebDriver driver, WebDriver.Window window, Point targetPoint) {

    }

    @Override
    public void afterWindowIsMoved(WebDriver driver, WebDriver.Window window, Point targetPoint) {

    }

    @Override
    public void beforeWindowIsMaximized(WebDriver driver, WebDriver.Window window) {

    }

    @Override
    public void afterWindowIsMaximized(WebDriver driver, WebDriver.Window window) {

    }
}
