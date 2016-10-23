package be.mygod.chimpanzeerunner.action.ui;

import be.mygod.chimpanzeerunner.test.TestManager;
import io.appium.java_client.MobileElement;

public class Click extends UiAction {
    Click(TestManager manager, MobileElement element) {
        super(manager, element);
    }

    @Override
    public boolean perform() {
        element.click();
        return true;
    }
}
