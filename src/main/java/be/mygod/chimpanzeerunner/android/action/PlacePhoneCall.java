package be.mygod.chimpanzeerunner.android.action;

import be.mygod.chimpanzeerunner.device.Device;

import java.io.IOException;

public class PlacePhoneCall extends AndroidAction {
    public PlacePhoneCall(Device device) {
        super(device);
    }

    @Override
    public void perform() {
        try {
            targetDevice.executeDeviceCommand("gsm call 6789077112");
            Thread.sleep(6000);
            targetDevice.executeDeviceCommand("gsm cancel 6789077112");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
