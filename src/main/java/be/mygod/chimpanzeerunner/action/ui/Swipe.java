package be.mygod.chimpanzeerunner.action.ui;

import be.mygod.chimpanzeerunner.test.TestManager;
import be.mygod.chimpanzeerunner.view.View;

import java.util.Random;

public class Swipe extends UiAction {
    Swipe(TestManager manager, View view) {
        super(manager, view);
    }

    private static final Random random = new Random();

    @Override
    public boolean perform() {
        int x = (view.bounds.right - view.bounds.left) >> 1, y = (view.bounds.bottom - view.bounds.top) >> 1,
                centerX = (view.bounds.left + view.bounds.right) >> 1,
                centerY = (view.bounds.top + view.bounds.bottom) >> 1;
        if (x < 2 || y < 2) return false;
        if (random.nextBoolean()) x = 1 - x; else --x;
        if (random.nextBoolean()) y = 1 - y; else --y;
        manager.getDriver().swipe(centerX, centerY, centerX + x, centerY + y, random.nextInt(1000) + 1);
        return true;
    }
}
