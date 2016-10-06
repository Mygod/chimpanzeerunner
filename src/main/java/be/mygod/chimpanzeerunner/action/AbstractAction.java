package be.mygod.chimpanzeerunner.action;

import be.mygod.chimpanzeerunner.action.ui.UiActions;
import be.mygod.chimpanzeerunner.test.TestManager;

import java.util.stream.Stream;

public abstract class AbstractAction {
    public static Stream<AbstractAction> getActions(TestManager manager) {
        return Stream.concat(
                UiActions.getActions(manager),
                NavigateBack.getActions(manager)
        );
    }

    public abstract void perform();

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}
