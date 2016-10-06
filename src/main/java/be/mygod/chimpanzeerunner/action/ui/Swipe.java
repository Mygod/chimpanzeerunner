package be.mygod.chimpanzeerunner.action.ui;

import io.appium.java_client.MobileElement;
import io.appium.java_client.SwipeElementDirection;

import java.util.Random;

public class Swipe extends UiAction {
    Swipe(MobileElement element) {
        super(element);
    }

    private static final Random random = new Random();

    @Override
    public void perform() {
        element.swipe(random.nextBoolean() ? SwipeElementDirection.DOWN : SwipeElementDirection.UP,
                random.nextInt(1000) + 1);
        element.swipe(random.nextBoolean() ? SwipeElementDirection.LEFT : SwipeElementDirection.RIGHT,
                random.nextInt(1000) + 1);
    }
}
