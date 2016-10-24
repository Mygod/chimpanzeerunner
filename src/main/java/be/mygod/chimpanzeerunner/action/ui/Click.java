package be.mygod.chimpanzeerunner.action.ui;

import be.mygod.chimpanzeerunner.action.WrappedMobileElement;
import be.mygod.chimpanzeerunner.test.TestManager;

public class Click extends UiAction {
    Click(TestManager manager, WrappedMobileElement element) {
        super(manager, element);
    }

    @Override
    public boolean perform() {
        element.inner.click();
        return true;
    }
}
