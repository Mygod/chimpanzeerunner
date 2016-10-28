package be.mygod.chimpanzeerunner.action;

import be.mygod.chimpanzeerunner.test.TestManager;

import java.net.URI;
import java.util.WeakHashMap;
import java.util.stream.Stream;

public class NavigateBack extends AbstractAction {
    private static final WeakHashMap<TestManager, URI> initialLocation = new WeakHashMap<>();

    public static Stream<AbstractAction> getActions(TestManager manager, URI location) {
        if (!initialLocation.containsKey(manager)) initialLocation.put(manager, location);
        else if (!initialLocation.get(manager).equals(location)) return Stream.of(new NavigateBack(manager));
        return Stream.empty();
    }

    public NavigateBack(TestManager manager) {
        this.manager = manager;
    }

    private TestManager manager;

    @Override
    public boolean perform() {
        manager.navigateBack();
        return true;
    }
}
