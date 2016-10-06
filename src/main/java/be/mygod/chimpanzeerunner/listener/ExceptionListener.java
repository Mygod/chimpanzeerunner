package be.mygod.chimpanzeerunner.listener;

import io.appium.java_client.events.api.general.ListensToException;
import org.openqa.selenium.WebDriver;
import be.mygod.chimpanzeerunner.test.TestManager;

public class ExceptionListener extends AppiumListener implements ListensToException {
    public ExceptionListener(TestManager manager) {
        super(manager);
    }

    @Override
    public void onException(Throwable throwable, WebDriver driver) {

    }
}
