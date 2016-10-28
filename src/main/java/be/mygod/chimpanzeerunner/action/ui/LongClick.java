package be.mygod.chimpanzeerunner.action.ui;

import be.mygod.chimpanzeerunner.test.TestManager;
import be.mygod.chimpanzeerunner.view.View;

public class LongClick extends UiAction {
    LongClick(TestManager manager, View view) {
        super(manager, view);
    }

    @Override
    public boolean perform() {
        getElement().tap(1, 500);
        return true;
    }
}
