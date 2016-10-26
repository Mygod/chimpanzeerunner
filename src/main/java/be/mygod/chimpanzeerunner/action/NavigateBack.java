package be.mygod.chimpanzeerunner.action;

import be.mygod.chimpanzeerunner.test.TestAbortException;
import be.mygod.chimpanzeerunner.test.TestManager;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.WeakHashMap;
import java.util.stream.Stream;

public class NavigateBack extends AbstractAction {
    private static final HashSet<URI> URI_WHITE_LIST;
    private static final WeakHashMap<TestManager, URI> initialLocation = new WeakHashMap<>();

    static {
        HashSet<URI> list;
        try {
            list = new HashSet<>(Arrays.asList(
                    new URI("android://com.android.packageinstaller/.permission.ui.GrantPermissionsActivity"),
                    new URI("android://com.android.settings/.Settings$AppWriteSettingsActivity")
            ));
        } catch (URISyntaxException e) {
            e.printStackTrace();
            list = new HashSet<>();
        }
        URI_WHITE_LIST = list;
    }

    public static Stream<AbstractAction> getActions(TestManager manager) {
        URI location = null;
        int i;
        for (i = 10; i > 0; --i) {
            location = manager.getLocation();
            if (manager.getPackageName().equals(location.getHost()) || URI_WHITE_LIST.contains(location)) break; else {
                System.out.printf("Unexpected location: %s. Pressing back in hope of returning to original package...\n", location);
                manager.navigateBack();
            }
        }
        if (i <= 0) throw new TestAbortException("Unexpected location.");
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
