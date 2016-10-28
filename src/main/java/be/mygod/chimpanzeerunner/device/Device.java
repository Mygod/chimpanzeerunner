package be.mygod.chimpanzeerunner.device;

import be.mygod.chimpanzeerunner.test.Automation;
import org.openqa.selenium.remote.DesiredCapabilities;

public abstract class Device {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Device that = (Device) o;
        return toString().equals(that.toString());
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public String toString() {
        return "Device_Unknown";
    }

    public abstract Automation getAutomation();
    public void configureCapabilities(DesiredCapabilities capabilities) { }
    public void release() { }
}
