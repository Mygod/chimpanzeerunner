package be.mygod.chimpanzeerunner.action.ui;

import be.mygod.chimpanzeerunner.test.TestManager;
import io.appium.java_client.MobileElement;

public class Click extends UiAction {
    Click(TestManager manager, MobileElement element) {
        super(manager, element);
    }

    @Override
    public boolean perform() {
        //long start = System.nanoTime();
        element.click();
        //System.out.printf("Clicking took %f seconds.\n", (System.nanoTime() - start) * 1e-9);
        return true;
    }
}
