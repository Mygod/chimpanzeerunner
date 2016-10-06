package be.mygod.chimpanzeerunner.action.ui;

import io.appium.java_client.MobileElement;

public class LongClick extends UiAction {
    LongClick(MobileElement element) {
        super(element);
    }

    @Override
    public void perform() {
        element.tap(1, 500);
    }
}
