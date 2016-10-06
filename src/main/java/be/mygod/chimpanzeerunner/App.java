package be.mygod.chimpanzeerunner;

import be.mygod.chimpanzeerunner.device.DeviceManager;
import be.mygod.chimpanzeerunner.test.TestMaster;
import be.mygod.chimpanzeerunner.test.TestProfile;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

import java.io.File;
import java.util.List;

public class App {
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
        if (instance.help || instance.paths == null || instance.paths.isEmpty()) {
            jcommander.usage();
            return;
        }
        if (!(instance.logDirectory = new File(instance.logDir)).mkdirs()) {
            System.err.printf("Failed to create log directory. Exiting...\n");
            return;
        }
        new TestMaster(TestProfile.createFromPaths(instance.paths)).run();
        DeviceManager.cleanUp();
    }
}
