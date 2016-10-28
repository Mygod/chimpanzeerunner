package be.mygod.chimpanzeerunner.action.view;

import be.mygod.chimpanzeerunner.test.TestManager;
import be.mygod.chimpanzeerunner.view.View;

public class Click extends ViewAction {
    Click(TestManager manager, View view) {
        super(manager, view);
    }

    @Override
    public boolean perform() {
        getElement().click();
        return true;
    }
}
