package be.mygod.chimpanzeerunner.action.ui;

import io.appium.java_client.MobileElement;

public class Click extends UiAction {
    Click(MobileElement element) {
        super(element);
    }

    @Override
    public boolean perform() {
        //long start = System.nanoTime();
        element.click();
        //System.out.printf("Clicking took %f seconds.\n", (System.nanoTime() - start) * 1e-9);
        return true;
    }
}
