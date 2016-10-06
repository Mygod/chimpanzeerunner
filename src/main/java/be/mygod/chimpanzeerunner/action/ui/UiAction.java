package be.mygod.chimpanzeerunner.action.ui;

import be.mygod.chimpanzeerunner.action.AbstractAction;
import io.appium.java_client.MobileElement;

public abstract class UiAction extends AbstractAction {
    protected UiAction(MobileElement element) {
        this.element = element;
    }

    protected MobileElement element;

    @Override
    public String toString() {
        return element.getAttribute("className") + ':' + element.getId() + ':' + super.toString();
    }
}
