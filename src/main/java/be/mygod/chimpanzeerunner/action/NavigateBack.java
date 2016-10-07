package be.mygod.chimpanzeerunner.action;

import be.mygod.chimpanzeerunner.test.TestManager;

import java.util.WeakHashMap;
import java.util.stream.Stream;

public class NavigateBack extends AbstractAction {
    private static final WeakHashMap<TestManager, String> initialLocation = new WeakHashMap<>();
    public static Stream<AbstractAction> getActions(TestManager manager) {
        if (!initialLocation.containsKey(manager)) initialLocation.put(manager, manager.getLocation());
        else if (!initialLocation.get(manager).equals(manager.getLocation()))
            return Stream.of(new NavigateBack(manager));
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
