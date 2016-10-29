package be.mygod.chimpanzeerunner.action.view;

import be.mygod.chimpanzeerunner.test.TestManager;
import be.mygod.chimpanzeerunner.view.View;
import io.appium.java_client.MobileElement;

public class InputText extends ViewAction {
    InputText(TestManager manager, View view) {
        super(manager, view);
    }

    @Override
    public boolean perform() {
        MobileElement element = getElement();
        element.clear();
        element.sendKeys("RandomText12.34");
        return true;
    }
}
