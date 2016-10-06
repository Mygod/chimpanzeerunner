package be.mygod.chimpanzeerunner.action.ui;

import io.appium.java_client.MobileElement;

public class Click extends UiAction {
    Click(MobileElement element) {
        super(element);
    }

    @Override
    public void perform() {
        element.click();
    }
}
