package be.mygod.chimpanzeerunner.action.ui;

import be.mygod.chimpanzeerunner.action.WrappedMobileElement;
import be.mygod.chimpanzeerunner.test.TestManager;
import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;

import java.util.Random;

public class Swipe extends UiAction {
    Swipe(TestManager manager, WrappedMobileElement element) {
        super(manager, element);
    }

    private static final Random random = new Random();

    @Override
    public boolean perform() {
        Point point = element.inner.getLocation();
        Dimension size = element.inner.getSize();
        int x = size.getWidth() >> 1, y = size.getHeight() >> 1, centerX = point.getX() + x, centerY = point.getY() + y;
        if (x < 2 || y < 2) return false;
        if (random.nextBoolean()) x = 1 - x; else --x;
        if (random.nextBoolean()) y = 1 - y; else --y;
        ((AppiumDriver) element.inner.getWrappedDriver()).swipe(centerX, centerY, centerX + x, centerY + y,
                random.nextInt(1000) + 1);
        return true;
    }
}
