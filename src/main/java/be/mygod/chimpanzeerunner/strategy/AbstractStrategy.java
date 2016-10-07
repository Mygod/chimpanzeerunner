package be.mygod.chimpanzeerunner.strategy;

import be.mygod.chimpanzeerunner.action.AbstractAction;
import be.mygod.chimpanzeerunner.test.TestManager;

import java.util.stream.Stream;

public abstract class AbstractStrategy {
    protected final TestManager manager;

    public AbstractStrategy(TestManager manager) {
        this.manager = manager;
    }

    public abstract boolean perform(Stream<AbstractAction> actions);
}
