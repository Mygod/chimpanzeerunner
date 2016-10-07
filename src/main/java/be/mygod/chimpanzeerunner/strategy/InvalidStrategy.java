package be.mygod.chimpanzeerunner.strategy;

import be.mygod.chimpanzeerunner.action.AbstractAction;
import be.mygod.chimpanzeerunner.test.TestManager;

import java.util.stream.Stream;

public final class InvalidStrategy extends AbstractStrategy {
    public InvalidStrategy(TestManager manager) {
        super(manager);
    }

    @Override
    public boolean perform(Stream<AbstractAction> actions) {
        return false;
    }
}
