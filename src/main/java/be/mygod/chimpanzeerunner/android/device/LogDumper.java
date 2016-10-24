package be.mygod.chimpanzeerunner.android.device;

import be.mygod.chimpanzeerunner.App;
import com.android.ddmlib.*;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

class LogDumper implements AutoCloseable, IShellOutputReceiver, Runnable {
    private static final SimpleDateFormat logcatFormat = new SimpleDateFormat("y-M-d H:mm:ss.SSS");
    private final IDevice device;
    private boolean cancelled;
    private BufferedOutputStream out;

    LogDumper(IDevice device) throws IOException {
        out = new BufferedOutputStream(new FileOutputStream(
                App.instance.createLogFile("android-" + (this.device = device).toString())));
    }

    @Override
    public void run() {
        try {
            device.executeShellCommand(String.format("logcat -T '%s'", logcatFormat.format(new Date())), this);
        } catch (AdbCommandRejectedException | ShellCommandUnresponsiveException | IOException | TimeoutException e) {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized void close() throws Exception {
        cancelled = true;
        out.close();
    }

    @Override
    public void addOutput(byte[] buffer, int start, int count) {
        try {
            out.write(buffer, start, count);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void flush() {
        try {
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized boolean isCancelled() {
        return cancelled;
    }
}
