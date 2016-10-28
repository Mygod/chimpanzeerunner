package be.mygod.chimpanzeerunner.strategy;

import be.mygod.chimpanzeerunner.App;
import be.mygod.chimpanzeerunner.action.AbstractAction;
import be.mygod.chimpanzeerunner.test.TestManager;

import java.util.stream.Stream;

/**
 * The strategy that exits after a number of times.
 *
 * @author Mygod
 */
public abstract class CountingStrategy extends AbstractStrategy {
    public CountingStrategy(TestManager manager) {
        super(manager);
    }

    protected int count = 0;

    protected abstract AbstractAction select(Stream<AbstractAction> actions);
    @Override
    public boolean perform(Stream<AbstractAction> actions) {
        if (count < App.instance.actionsCount) {
            AbstractAction action = select(actions);
            if (action != null) {
                System.out.printf("Performing action #%d: %s\n", count, action);
                action.perform();
                return ++count < App.instance.actionsCount;
            } else System.err.printf("No actions found! Retrying...\n");
        }
        return false;
    }
}
