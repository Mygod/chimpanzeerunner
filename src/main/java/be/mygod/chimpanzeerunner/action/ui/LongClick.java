package be.mygod.chimpanzeerunner.action.ui;

import be.mygod.chimpanzeerunner.action.WrappedMobileElement;
import be.mygod.chimpanzeerunner.test.TestManager;

public class LongClick extends UiAction {
    LongClick(TestManager manager, WrappedMobileElement element) {
        super(manager, element);
    }

    @Override
    public boolean perform() {
        element.inner.tap(1, 500);
        return true;
    }
}
