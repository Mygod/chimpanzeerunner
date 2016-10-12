package be.mygod.chimpanzeerunner.test;

import be.mygod.chimpanzeerunner.Automation;
import be.mygod.chimpanzeerunner.action.AbstractAction;
import be.mygod.chimpanzeerunner.action.NavigateBack;
import be.mygod.chimpanzeerunner.action.ui.UiAction;
import be.mygod.chimpanzeerunner.device.Device;
import be.mygod.chimpanzeerunner.listener.AppiumListener;
import be.mygod.chimpanzeerunner.strategy.AbstractStrategy;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;
import io.appium.java_client.events.EventFiringWebDriverFactory;
import io.appium.java_client.remote.AutomationName;
import io.appium.java_client.remote.MobileCapabilityType;
import io.appium.java_client.service.local.AppiumDriverLocalService;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.Stream;

public abstract class TestManager implements Runnable {
    private static final String NATIVE_CONTEXT = "NATIVE_APP";

    private final TestMaster master;
    protected final TestProfile profile;
    protected final Device device;
    private final Function<TestManager, AbstractStrategy> strategyFactory;
    private AppiumDriver<MobileElement> driver;

    public TestManager(TestMaster master, TestProfile profile, Device device,
                       Function<TestManager, AbstractStrategy> strategy) {
        this.master = master;
        this.profile = profile;
        this.device = device;
        strategyFactory = strategy;
    }

    public AppiumDriver<MobileElement> getDriver() {
        return driver;
    }

    protected abstract AppiumDriver<MobileElement> createDriver(AppiumDriverLocalService service,
                                                                DesiredCapabilities capabilities);
    public abstract String getLocation();
    public abstract void navigateBack();

    protected Stream<AbstractAction> getActions() {
        AbstractAction[] actions = driver.getContextHandles().stream()
                .filter(context -> !NATIVE_CONTEXT.equals(context)).flatMap(context -> {
            driver.context(context);
            return UiAction.getActions(this);
        }).toArray(AbstractAction[]::new);
        driver.context(NATIVE_CONTEXT);
        return Stream.of(
                Arrays.stream(actions),
                UiAction.getActions(this),
                NavigateBack.getActions(this)
        ).flatMap(x -> x);
    }

    @Override
    public final void run() {
        AppiumDriverLocalService service = master.request();
        System.out.printf("Testing profile %s on %s with service %s...\n", profile, device, service.getUrl());
        DesiredCapabilities capabilities = new DesiredCapabilities();
        profile.configureCapabilities(capabilities);
        device.configureCapabilities(capabilities);
        driver = EventFiringWebDriverFactory.getEventFiringWebDriver(createDriver(service, capabilities),
                AppiumListener.getListeners(this));
        AbstractStrategy strategy = strategyFactory.apply(this);
        while (strategy.perform(getActions().distinct())) { }
        driver.quit();
        master.offer(service);
    }
}
