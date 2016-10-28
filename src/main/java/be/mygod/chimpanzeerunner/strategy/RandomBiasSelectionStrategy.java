package be.mygod.chimpanzeerunner.strategy;

import be.mygod.chimpanzeerunner.action.AbstractAction;
import be.mygod.chimpanzeerunner.test.TestManager;

import java.util.Random;
import java.util.stream.Stream;

public class RandomBiasSelectionStrategy extends CountingStrategy {
    public RandomBiasSelectionStrategy(TestManager manager) {
        super(manager);
    }

    // TODO: ADD BIAS!!!!!
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
        if (selector.action != null) {
            System.out.printf("Performing action #%d: %s\n", count, selector.action);
            selector.action.perform();
        } else System.err.printf("No actions found! Retrying...\n");
    }

    @Override
    public String toString() {
        return "Random Bias Selection Strategy";
    }
}
