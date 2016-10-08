package be.mygod.chimpanzeerunner.action.ui;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;
import io.appium.java_client.SwipeElementDirection;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.Rectangle;

import java.util.Random;

public class Swipe extends UiAction {
    Swipe(MobileElement element) {
        super(element);
    }

    private static final Random random = new Random();

    @Override
    public boolean perform() {
//        Rectangle rect = element.getRect();
//        int x = rect.getWidth() >> 1, y = rect.getHeight() >> 1, centerX = rect.getX() + x, centerY = rect.getY() + y;
        Point point = element.getLocation();
        Dimension size = element.getSize();
        int x = size.getWidth() >> 1, y = size.getHeight() >> 1, centerX = point.getX() + x, centerY = point.getY() + y;
        if (x < 2 || y < 2) return false;
        if (random.nextBoolean()) x = 1 - x; else --x;
        if (random.nextBoolean()) y = 1 - y; else --y;
        ((AppiumDriver) element.getWrappedDriver()).swipe(centerX, centerY, centerX + x, centerY + y,
                random.nextInt(1000) + 1);
        return true;
    }
}
