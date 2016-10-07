package be.mygod.chimpanzeerunner.action.ui;

import io.appium.java_client.MobileElement;

public class LongClick extends UiAction {
    LongClick(MobileElement element) {
        super(element);
    }

    @Override
    public boolean perform() {
        element.tap(1, 500);
        return true;
    }
}
