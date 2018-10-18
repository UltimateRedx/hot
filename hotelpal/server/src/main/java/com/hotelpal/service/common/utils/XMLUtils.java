package com.hotelpal.service.common.utils;

import com.hotelpal.service.common.exception.ServiceException;
import org.dom4j.*;
import org.dom4j.io.SAXReader;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XMLUtils {

	private XMLUtils(){}
	
	public static Document mapToXML(final Map<String, Object> map) {
			DocumentFactory documentFactory = DocumentFactory.getInstance();
			Element root = documentFactory.createElement("xml");
			Document document = documentFactory.createDocument(root);
			for (Map.Entry<String, Object> entry : map.entrySet()) {
				Element element = documentFactory.createElement(entry.getKey());
				root.add(element);
				element.setText(String.valueOf(entry.getValue()));
			}
			return document;
	}
	
	public static Document parseText(String text) throws DocumentException {
		return DocumentHelper.parseText(text);
	}

	public static Map<String, String> listMapContent(InputStream is) {
		try {
			return listMapContent(getReader().read(is));
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}
	public static Map<String, String> listMapContent(String xml) {
		try {
			return listMapContent(getReader().read(new ByteArrayInputStream(xml.getBytes())));
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}

	private static Map<String, String> listMapContent(Document document) {
		Element root = document.getRootElement();
		List list = root.elements();
		Map<String, String> res = new HashMap<>();
		for (Object e : list) {
			Element element = (Element) e;
			res.put(element.getName(), element.getText());
		}
		return res;
	}

	public static SAXReader getReader() throws SAXException {
		SAXReader reader = new SAXReader();
		reader.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
		return reader;
	}
}
