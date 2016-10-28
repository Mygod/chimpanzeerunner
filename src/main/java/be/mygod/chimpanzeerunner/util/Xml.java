package be.mygod.chimpanzeerunner.util;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;

public abstract class Xml {
    private Xml() { }
    private static final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

    public static Document parse(String content) throws ParserConfigurationException, IOException, SAXException {
        return dbf.newDocumentBuilder().parse(new InputSource(new StringReader(content)));
    }
}
