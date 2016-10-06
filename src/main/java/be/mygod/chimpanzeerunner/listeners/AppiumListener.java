package be.mygod.chimpanzeerunner.listeners;

import io.appium.java_client.events.api.Listener;
import be.mygod.chimpanzeerunner.test.TestManager;

import java.util.Arrays;
import java.util.Collection;

public abstract class AppiumListener implements Listener {
    protected TestManager manager;

    public AppiumListener(TestManager manager) {
        this.manager = manager;
    }

    public static Collection<Listener> getListeners(TestManager manager) {
        return Arrays.asList(
                new AlertListener(manager),
                new WebDriverListener(manager),
                new ContextListener(manager),
                new ElementListener(manager),
                new JavascriptEventListener(manager),
                new ExceptionListener(manager),
                new NavigationListener(manager),
                new RotationListener(manager),
                new SearchEventListener(manager),
                new WindowListener(manager)
        );
    }
}
