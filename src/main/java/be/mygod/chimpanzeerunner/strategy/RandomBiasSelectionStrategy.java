package be.mygod.chimpanzeerunner.strategy;

import be.mygod.chimpanzeerunner.action.AbstractAction;
import be.mygod.chimpanzeerunner.test.TestManager;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Stream;

public class RandomBiasSelectionStrategy extends CountingStrategy {
    public RandomBiasSelectionStrategy(TestManager manager) {
        super(manager);
    }

    private static final Random random = new Random();
    private HashMap<ImmutablePair<AbstractAction, AbstractAction[]>, Integer> G = new HashMap<>();
    private HashMap<AbstractAction, Integer> L = new HashMap<>();

    @Override
    public AbstractAction select(Stream<AbstractAction> actionStream) {
        AbstractAction[] actions = actionStream.toArray(AbstractAction[]::new);
        for (;;) {
            AbstractAction action = actions[random.nextInt(actions.length)];
            ImmutablePair<AbstractAction, AbstractAction[]> gkey = new ImmutablePair<>(action, actions);
            int l = L.getOrDefault(action, 0), g = G.getOrDefault(gkey, 0);
            if (l == g) {
                G.put(gkey, g + 1);
                return action;
            } else L.put(action, l + 1);
        }
    }

    @Override
    public String toString() {
        return "Random Bias Selection Strategy";
    }
}
