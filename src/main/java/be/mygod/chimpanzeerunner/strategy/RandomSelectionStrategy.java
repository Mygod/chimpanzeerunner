package be.mygod.chimpanzeerunner.strategy;

import be.mygod.chimpanzeerunner.action.AbstractAction;
import be.mygod.chimpanzeerunner.test.TestManager;
import org.openqa.selenium.NoSuchElementException;

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
    public boolean performCore(Stream<AbstractAction> actions) {
        try {
            ActionSelector selector = new ActionSelector();
            actions.forEach(selector::supply);
            if (selector.action != null) {
                System.out.printf("Performing action #%d: %s\n", count, selector.action);
                selector.action.perform();
                return true;
            } else System.err.printf("No actions found! Retrying...\n");
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public String toString() {
        return "Random Selection Strategy";
    }
}
