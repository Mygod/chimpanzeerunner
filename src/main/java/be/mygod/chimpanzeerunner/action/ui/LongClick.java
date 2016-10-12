package be.mygod.chimpanzeerunner.action.ui;

import be.mygod.chimpanzeerunner.test.TestManager;
import io.appium.java_client.MobileElement;

public class LongClick extends UiAction {
    LongClick(TestManager manager, MobileElement element) {
        super(manager, element);
    }

    @Override
    public boolean perform() {
        element.tap(1, 500);
        return true;
    }
}
