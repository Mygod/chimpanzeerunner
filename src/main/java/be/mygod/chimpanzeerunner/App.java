package be.mygod.chimpanzeerunner;

import be.mygod.chimpanzeerunner.device.DeviceManager;
import be.mygod.chimpanzeerunner.strategy.AbstractStrategy;
import be.mygod.chimpanzeerunner.strategy.InvalidStrategy;
import be.mygod.chimpanzeerunner.strategy.StrategyParser;
import be.mygod.chimpanzeerunner.test.TestManager;
import be.mygod.chimpanzeerunner.test.TestMaster;
import be.mygod.chimpanzeerunner.test.TestProfile;
import com.beust.jcommander.IDefaultProvider;
import com.beust.jcommander.IStringConverter;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Pattern;

public final class App implements IDefaultProvider {
    public static class PatternParser implements IStringConverter<Pattern> {
        @Override
        public Pattern convert(String value) {
            return Pattern.compile(value, Pattern.CASE_INSENSITIVE);
        }
    }

    @Parameter(names = {"-s", "--strategy"}, converter = StrategyParser.class,
            description = "Strategy to use. Available options: frequency, graph, random, random-bias")
    public Function<TestManager, AbstractStrategy> strategy;

    @Parameter(names = {"-c", "--count"},
            description = "Specify total count of actions to taken. Doesn't apply to graph strategy.")
    public int actionsCount = 100;

    @Parameter(names = {"--android-home"}, description = "Android SDK path, overrides $ANDROID_HOME.")
    public String androidHome;

    @Parameter(names = {"--chromedriver-executable"}, description = "Custom chromedriver executable to override.")
    public String chromedriverExecutable;

    @Parameter(names = {"-u", "--test-url"}, converter = PatternParser.class,
            description = "URL pattern to test in WebViews (ignore case). If not supplied. Default: ^file:///.+$")
    public Pattern testUrl;

    @Parameter(names = {"--log"}, description = "Directory of log files.")
    public String logDir = "log";
    public File logDirectory;

    @Parameter(names = "--help", description = "Display this help message.", help = true)
    public boolean help;

    @Parameter(description = "[path to the apps that need testing or the directories containing apps]")
    public List<String> paths;

    public static App instance = new App();
    public static void main(String[] args) {
        JCommander jcommander = new JCommander(instance);
        jcommander.setDefaultProvider(instance);
        jcommander.parse(args);
        if (instance.help || instance.paths == null || instance.paths.isEmpty() ||
                instance.strategy.apply(null) instanceof InvalidStrategy) {
            jcommander.usage();
            return;
        }
        instance.run();
    }

    private void run() {
        if (androidHome == null) androidHome = System.getenv("ANDROID_HOME");
        logDirectory = new File(logDir);
        if (!logDirectory.isDirectory() && !logDirectory.mkdirs()) {
            System.err.printf("Failed to create log directory. Exiting...\n");
            return;
        }
        new TestMaster(TestProfile.createFromPaths(paths), strategy).run();
        DeviceManager.cleanUp();
    }

    private static final SimpleDateFormat logFileFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
    public File createLogFile(String prefix) {
        return new File(logDirectory, String.format("%s-%s.log", prefix, logFileFormat.format(new Date())));
    }

    @Override
    public String getDefaultValueFor(String optionName) {
        switch (optionName) {
            case "-s": case "--strategy": return "frequency";
            case "-c": case "--count": return "100";
            case "-u": case "--test-url": return "^file:///.+$";
            default: return null;
        }
    }
}
