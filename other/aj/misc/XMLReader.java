/*
 * Created on Apr 10, 2006
 * 
 */
package aj.misc;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * 
 * @author marble
 * 
 */
public class XMLReader {

	/**
	 * Reads in a DOM object from an XML file
	 * 
	 * @param file
	 * @return
	 */
	public static Document readDocumentFromFile(File file) {
		// Initialize default return value
		Document dataSourceDocument = null;

		try {
			// Build an XML document builder and InputStream to read in a file
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder docBuilder = factory.newDocumentBuilder();
			FileInputStream stream = new FileInputStream(file);
			dataSourceDocument = docBuilder.parse(stream);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return dataSourceDocument;
	}

	/**
	 * Reads in a DOM object from an XML file
	 * 
	 * @param stream
	 * @return
	 */
	public static Document readDocumentFromStream(InputStream stream) {
		// Initialize default return value
		Document dataSourceDocument = null;

		try {
			// Build an XML document builder and InputStream to read in a file
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder docBuilder = factory.newDocumentBuilder();
			dataSourceDocument = docBuilder.parse(stream);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return dataSourceDocument;
	}

	/**
	 * Reads in a DOM object from a URL reference
	 * 
	 * @param u
	 *            the URL
	 * @return the document
	 */
	public static Document readDocumentFromURL(URL u) {
		InputStream stream = null;
		try {
			stream = u.openStream();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return XMLReader.readDocumentFromStream(stream);
	}

	/**
	 * Recursively prints out the given node and its children
	 * 
	 * @param n
	 */
	public static void displayNodeAndChildren(Node n) {
		if (n.getNodeType() == Node.TEXT_NODE) {
			if (!n.getNodeValue().trim().equals(""))
				System.out.println("  " + n.getNodeValue().trim());
		} else {
			System.out.println(n.getNodeName());
		}

		if (n.hasAttributes()) {
			displayNodeAttributes(n);
		}
		if (n.hasChildNodes()) {
			Node currentChild = n.getFirstChild();
			while (currentChild != null) {
				displayNodeAndChildren(currentChild);
				currentChild = currentChild.getNextSibling();
			}
		}
	}

	/**
	 * Prints out the attributes of the given node
	 * 
	 * @param n
	 */
	public static void displayNodeAttributes(Node n) {
		NamedNodeMap attrs = n.getAttributes();
		for (int i = 0; i < attrs.getLength(); i++) {
			Node currentAttr = attrs.item(i);
			System.out.println(" " + currentAttr.getNodeName() + " -> "
					+ currentAttr.getNodeValue());
		}
	}

	/**
	 * 
	 * @param nodeName
	 *            the Name of a node child in the node root
	 * 
	 * @param root
	 *            the parent to search for a child node within
	 * @return the value of the text node of the child, null if not found
	 */
	public static Vector getAllChildValueByNodeName(String nodeName, Node root) {
		Vector res=new Vector();
		NodeList nl = root.getChildNodes();
		for (int i = 0; i < nl.getLength(); i++) {
			Node n = nl.item(i);
			if (n.getNodeName().equalsIgnoreCase(nodeName)) {
				res.addElement(getChildTextNode(n));
			}
		}
		return res;
	}

	/**
	 * 
	 * @param nodeName
	 *            the Name of a node child in the node root
	 * 
	 * @param root
	 *            the parent to search for a child node within
	 * @return the value of the text node of the child, null if not found
	 */
	public static String getChildValueByNodeName(String nodeName, Node root) {
		NodeList nl = root.getChildNodes();
		for (int i = 0; i < nl.getLength(); i++) {
			Node n = nl.item(i);
			if (n.getNodeName().equalsIgnoreCase(nodeName)) {
				return getChildTextNode(n);
			}
		}
		return null;
	}
	
	/**
	 * 
	 * @param nodeName
	 * @param root
	 * @return
	 */
	public static Node getChildNodeNameByName(String nodeName, Node root) {
		NodeList nl = root.getChildNodes();
		for (int i = 0; i < nl.getLength(); i++) {
			Node n = nl.item(i);
			if (n.getNodeName().equalsIgnoreCase(nodeName)) {
				return n;
			}
		}
		return null;
	}

	/**
	 * 
	 * @param nodeName
	 * @param root
	 * @return
	 */
	public static Vector getAllChildNodeByName(String nodeName, Node root) {
		Vector res=new Vector();
		NodeList nl = root.getChildNodes();
		for (int i = 0; i < nl.getLength(); i++) {
			Node n = nl.item(i);
//			System.out.println("node name="+n.getNodeName());
			if (n.getNodeName().equalsIgnoreCase(nodeName)) {
				res.addElement(n);
			}
		}
		return res;
	}

	
	/**
	 * Gets the text node value of an xml node
	 * 
	 * @param n
	 *            the node to with a text node child
	 * @return the text node value, null if not found
	 */
	static public String getChildTextNode(Node n) {
		NodeList nl = n.getChildNodes();
		for (int a = 0; a < nl.getLength(); a++) {
			Node nn = nl.item(a);
			if (nn.getNodeName().equals("#text")) {
				return nn.getNodeValue();
			}
		}
		// System.out.println("cannot find text in node");
		return null;
	}
}


