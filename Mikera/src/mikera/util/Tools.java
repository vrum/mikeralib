package mikera.util;
import java.util.*;
import java.io.*;
import org.w3c.dom.*;
import org.xml.sax.*;
import javax.xml.parsers.*;
import javax.xml.transform.*; 
import javax.xml.transform.dom.DOMSource; 
import javax.xml.transform.stream.StreamResult;

public class Tools {
	public static int middle(int a, int b, int c) {
		if (a<b) {
			if (b<c) {
				return b;
			}
			return (a<c)?c:a;
		} else {
			if (a<c) {
				return a;
			}
			return (b<c)?c:b;
		}
		
	}
	
	public static void writeXMLToFile(Document doc, String fileName) {
		try {
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.METHOD, "xml");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			Source source = new DOMSource(doc);
			Result result = new StreamResult(new File(fileName));
			transformer.transform(source, result);
		} catch (Throwable t) {
			throw new Error(t);
		}
	}
}
