package be.mygod.chimpanzeerunner;

import be.mygod.chimpanzeerunner.device.Device;
import be.mygod.chimpanzeerunner.device.DeviceManager;
import be.mygod.chimpanzeerunner.strategy.AbstractStrategy;
import be.mygod.chimpanzeerunner.strategy.StrategyParser;
import be.mygod.chimpanzeerunner.test.AppiumServicePool;
import be.mygod.chimpanzeerunner.test.TestManager;
import be.mygod.chimpanzeerunner.test.TestProfile;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.function.Function;

public final class App {
    @Parameter(names = {"-s", "--strategy"}, description = "Strategy to use. Default: random", converter = StrategyParser.class)
    public Function<TestManager, AbstractStrategy> strategy;

    @Parameter(names = {"-c", "--count"}, description = "Specify total count of actions to taken. Only apply to the following strategies: random. Default: 100")
    public int actionsCount = 100;

    @Parameter(names = {"--android-home"}, description = "Android SDK path, default: $ANDROID_HOME")
    public String androidHome = System.getenv("ANDROID_HOME");

    @Parameter(names = {"--log"}, description = "Directory of log files, default: log")
    public String logDir = "log";
    public File logDirectory;

    @Parameter(names = "--help", description = "Display this help message.", help = true)
    public boolean help;

    @Parameter(description = "[path to the apps that need testing or the directories containing apps]")
    public List<String> paths;

    public static App instance = new App();
    public static void main(String[] args) {
        JCommander jcommander = new JCommander(instance, args);
        if (instance.help || instance.paths == null || instance.paths.isEmpty() || instance.strategy == null) {
            jcommander.usage();
            return;
        }
        instance.run();
    }

    public void run() {
        logDirectory = new File(logDir);
        if (!logDirectory.isDirectory() && !logDirectory.mkdirs()) {
            System.err.printf("Failed to create log directory. Exiting...\n");
            return;
        }
        try {
            HashSet<TestProfile> profiles = TestProfile.createFromPaths(paths);
            ArrayList<WeakReference<Thread>> threads = new ArrayList<>();
            System.out.print("Waiting for devices...\n");
            while (!profiles.isEmpty()) {
                profiles.removeIf(profile -> {
                    DeviceManager dm = profile.getDeviceManager();
                    if (dm == null) return true;    // testing disabled
                    Device device = dm.tryGetFreeDevice(profile::isAcceptableDevice);
                    if (device == null) return false;
                    Thread thread = new Thread(dm.startTest(profile, device, strategy));
                    thread.start();
                    threads.add(new WeakReference<>(thread));
                    return true;
                });
                Thread.sleep(1000);
            }
            AppiumServicePool.close();  // destroy existing services from now on
            while (!threads.isEmpty()) {
                threads.removeIf(wr -> {
                    Thread thread = wr.get();
                    return thread == null || !thread.isAlive();
                });
                Thread.sleep(1000);
            }
        } catch (InterruptedException ignored) { }
        DeviceManager.cleanUp();
    }
}
