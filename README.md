# chimpanzeerunner

chimpanzeerunner is a more intelligent runner than monkeyrunner.

## Set up

1. Install [requirements for Appium](http://appium.io/slate/en/master/?java#requirements) and
   [Maven](https://maven.apache.org/);
2. Set `$ANDROID_HOME` to your Android SDK home;
3. If you are going to test on Android 5.0+, set `$JAVA_HOME` to your JDK home;
4. Execute:
```
$ npm install -g appium
$ mvn exec:java -Dexec.mainClass=be.mygod.chimpanzeerunner.App -Dexec.args=/path/to/app/files
```

## Dependency version

* Node.js: 6.8.1 (could be lower)
* npm: 3.10.10 (4.0 isn't compatible with appium yet)
* appium: 1.6.3

## FAQ

* Original error: unknown error: Chrome version must be >= *.*.*.*
  1. You need to downgrade your chromedriver. [Download here](https://chromedriver.storage.googleapis.com/index.html).
     (recommended version: 2.24, which is the last version that works with the latest emulator)
  2. Add this to your command line arguments: `--chromedriver-executable /path/to/chromedriver`
