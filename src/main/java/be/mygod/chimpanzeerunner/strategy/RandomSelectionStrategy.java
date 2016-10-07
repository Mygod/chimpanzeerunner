package be.mygod.chimpanzeerunner.strategy;

import be.mygod.chimpanzeerunner.action.AbstractAction;
import be.mygod.chimpanzeerunner.test.TestManager;

import java.util.Random;
import java.util.stream.Stream;

public class RandomSelectionStrategy extends CountingStrategy {
    public RandomSelectionStrategy(TestManager manager) {
        super(manager);
    }

    private static class ActionSelector {
        private static final Random random = new Random();

        private AbstractAction action;
        private int count;

        private void supply(AbstractAction next) {
            if (random.nextInt(++count) == 0) action = next;
        }
    }

    @Override
    public void performCore(Stream<AbstractAction> actions) {
        ActionSelector selector = new ActionSelector();
        actions.forEach(selector::supply);
        selector.action.perform();
    }
}
