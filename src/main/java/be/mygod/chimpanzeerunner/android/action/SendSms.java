package be.mygod.chimpanzeerunner.android.action;

import be.mygod.chimpanzeerunner.device.Device;

import java.io.IOException;

public class SendSms extends AndroidAction {
    public SendSms(Device device) {
        super(device);
    }

    @Override
    public void perform() {
        try {
            targetDevice.executeDeviceCommand("sms send 6789077112 Come over! Let's have a coffee.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
