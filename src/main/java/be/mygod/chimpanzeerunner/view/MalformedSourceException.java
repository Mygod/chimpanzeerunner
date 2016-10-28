package be.mygod.chimpanzeerunner.view;

import org.w3c.dom.Node;

import java.io.IOException;

public class MalformedSourceException extends IOException {
    public MalformedSourceException(String msg) {
        super(msg);
    }

    public static void checkNodeName(Node node, String expected) throws MalformedSourceException {
        String actual = node.getNodeName();
        if (!expected.equals(actual)) throw new MalformedSourceException(
                String.format("Malformed node name: expected %s, actual %s.", expected, actual));
    }
}
