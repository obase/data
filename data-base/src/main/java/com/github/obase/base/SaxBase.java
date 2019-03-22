package com.github.obase.base;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.github.obase.SystemException;

public final class SaxBase {

	static WeakReference<SAXParser> Ref = null;

	public static synchronized SAXParser parser() throws ParserConfigurationException, SAXException {
		if (Ref == null || Ref.get() == null) {
			SAXParserFactory factory = SAXParserFactory.newInstance();
			factory.setNamespaceAware(true);
			factory.setValidating(false);
			SAXParser parser = factory.newSAXParser();

			Ref = new WeakReference<SAXParser>(parser);
		}
		return Ref.get();
	}

	public static boolean parse(InputStream in, DefaultHandler handler) {

		if (in == null || handler == null) {
			return false;
		}

		try {
			SAXParser parser = parser();
			parser.parse(new BufferedInputStream(in), handler);
			return true;
		} catch (Exception e) {
			throw new SystemException("Parse xml with handler failed: " + handler, e);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					throw new SystemException(e);
				}
			}
		}
	}

	public static boolean parse(String uri, DefaultHandler handler) {

		try {
			SAXParser parser = parser();
			parser.parse(uri, handler);
			return true;
		} catch (Exception e) {
			throw new SystemException("Parse xml with handler failed: " + handler, e);
		}

	}

	public static boolean parse(File file, DefaultHandler handler) {

		try {
			SAXParser parser = parser();
			parser.parse(file, handler);
			return true;
		} catch (Exception e) {
			throw new SystemException("Parse xml with handler failed: " + handler, e);
		}

	}

	public static boolean parse(InputSource source, DefaultHandler handler) {

		try {
			SAXParser parser = parser();
			parser.parse(source, handler);
			return true;
		} catch (Exception e) {
			throw new SystemException("Parse xml with handler failed: " + handler, e);
		}

	}

}
