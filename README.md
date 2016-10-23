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
* npm: 3.10.9 (could be lower)
* appium: 1.6.0
* appium-uiautomator2-driver: 0.0.6
