package be.mygod.chimpanzeerunner.strategy;

import be.mygod.chimpanzeerunner.action.AbstractAction;
import be.mygod.chimpanzeerunner.action.NavigateBack;
import be.mygod.chimpanzeerunner.test.TestManager;

import java.net.URI;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

public class GraphTraversalStrategy extends AbstractStrategy {
    public GraphTraversalStrategy(TestManager manager) {
        super(manager);
    }

    private final HashMap<URI, HashSet<AbstractAction>> traversalMap = new HashMap<>();
    private URI root;
    private int backCount;

    private HashSet<AbstractAction> getPerformedActions(URI location) {
        HashSet<AbstractAction> result = traversalMap.get(location);
        if (result == null) traversalMap.put(location, result = new HashSet<>());
        return result;
    }

    @Override
    public boolean perform(Stream<AbstractAction> actions) {
        URI location = manager.getLocation();
        if (root == null) root = location;
        final HashSet<AbstractAction> performedActions = getPerformedActions(location);
        Optional<AbstractAction> option = actions
                .filter(a -> !(a instanceof NavigateBack || performedActions.contains(a))).findFirst();
        if (option.isPresent()) {
            backCount = 0;
            AbstractAction action = option.get();
            performedActions.add(action);
            System.out.printf("Performing action: %s\n", action);
            action.perform();
            return true;
        }
        if (Objects.equals(location, root)) return false;
        if (++backCount >= 10) {
            System.err.println("Unable to return to root. Terminating.");
            return false;
        }
        System.out.printf("No more actions. Pressing back.\n");
        manager.navigateBack();
        return true;
    }

    @Override
    public String toString() {
        return "Graph Traversal Strategy";
    }
}
