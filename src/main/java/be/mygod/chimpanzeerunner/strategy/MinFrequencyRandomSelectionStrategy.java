package be.mygod.chimpanzeerunner.strategy;

import be.mygod.chimpanzeerunner.action.AbstractAction;
import be.mygod.chimpanzeerunner.test.TestManager;

import java.util.HashMap;
import java.util.Random;
import java.util.stream.Stream;

public class MinFrequencyRandomSelectionStrategy extends CountingStrategy {
    private static final Random random = new Random();

    public MinFrequencyRandomSelectionStrategy(TestManager manager) {
        super(manager);
    }

    private final HashMap<AbstractAction, Integer> frequencyMap = new HashMap<>();

    private class ActionSelector {
        private AbstractAction action;
        private int count, frequency = Integer.MAX_VALUE;

        private void supply(AbstractAction next) {
            int nextFrequency = frequencyMap.getOrDefault(next, 0);
            if (nextFrequency < frequency) {
                action = next;
                count = 1;
                frequency = nextFrequency;
            } else if (nextFrequency == frequency && random.nextInt(++count) == 0) action = next;
        }
    }

    @Override
    public AbstractAction select(Stream<AbstractAction> actions) {
        ActionSelector selector = new ActionSelector();
        actions.forEach(selector::supply);
        frequencyMap.put(selector.action, frequencyMap.getOrDefault(selector.action, 0) + 1);
        return selector.action;
    }

    @Override
    public String toString() {
        return "Minimum Frequency Random Selection Strategy";
    }
}
