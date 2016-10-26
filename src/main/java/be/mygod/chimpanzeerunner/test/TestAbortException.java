package be.mygod.chimpanzeerunner.test;

public class TestAbortException extends RuntimeException {
    public TestAbortException() { }
    public TestAbortException(String message) {
        super(message);
    }
}
