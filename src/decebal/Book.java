package decebal;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class Book {
	private static final String bookFile = "book/book.xml";
	private static final String positionsFile = "book/positions.xml";

	public static String getBestMove(String shortFen) {
		String parts[] = shortFen.split("\\s");
		if (parts.length == 6) {
			// shorten FEN
			shortFen = parts[0] + " " + parts[1] + " " + parts[2];
		}

		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(bookFile);
			XPathFactory xPathfactory = XPathFactory.newInstance();
			XPath xpath = xPathfactory.newXPath();
			String query = "/book/move[@fen='" + shortFen + "']/text()";
			XPathExpression expr = xpath.compile(query);
			String bestMove = (String) expr
					.evaluate(doc, XPathConstants.STRING);
			if (bestMove == null || bestMove.equals("")) {
				countMissing(shortFen);
				return null;
			} else {
				return bestMove;
			}
		} catch (Exception e) {
			countMissing(shortFen);
			return null;
		}
	}

	private static void countMissing(String shortFen) {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(positionsFile);
			XPathFactory xPathfactory = XPathFactory.newInstance();
			XPath xpath = xPathfactory.newXPath();
			String query = "/positions/position[@fen='" + shortFen + "']";
			XPathExpression expr = xpath.compile(query);
			NodeList nl = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
			if (nl.getLength() == 0) {
				Element newChild = doc.createElement("position");
				newChild.setAttribute("fen", shortFen);
				newChild.setAttribute("num", "0");
				doc.getDocumentElement().appendChild(newChild);
			} else {
				int newNum = 1 + new Integer(nl.item(0).getAttributes()
						.getNamedItem("num").getTextContent());
				((Element) nl.item(0)).setAttribute("num", newNum + "");
			}

			Transformer transformer = TransformerFactory.newInstance()
					.newTransformer();
			Result output = new StreamResult(new File(positionsFile));
			Source input = new DOMSource(doc);
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
			transformer.transform(input, output);

		} catch (Exception e) {
		}
	}
}
