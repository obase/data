package com.github.obase.mysql.xml;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;

import org.springframework.core.io.Resource;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.github.obase.app.SystemException;
import com.github.obase.base.StringBase;
import com.github.obase.mysql.MysqlErrno;
import com.github.obase.mysql.sql.Sql;
import com.github.obase.mysql.sql.SqlDqlKit;
import com.github.obase.mysql.sql.SqlKit;

public final class ObaseMysqlParser {

	final DocumentBuilderFactory factory;

	public ObaseMysqlParser() {
		factory = DocumentBuilderFactory.newInstance();
		factory.setValidating(false);
		factory.setNamespaceAware(false);
		factory.setIgnoringElementContentWhitespace(true);
		factory.setIgnoringComments(true);
		factory.setCoalescing(true); // 必需! 将CDATA转为TEXT.
	}

	public ObaseMysqlObject parse(Resource rs) throws Exception {

		InputStream is = null;
		try {
			is = rs.getInputStream();
			if (is == null) {
				return null;
			}
			ObaseMysqlObject obj = new ObaseMysqlObject();

			Element root = factory.newDocumentBuilder().parse(new BufferedInputStream(is)).getDocumentElement();
			obj.namespace = root.getAttribute(ATTR_NAMESPACE);

			for (Node node = root.getFirstChild(); node != null; node = node.getNextSibling()) {
				if (node.getNodeType() != Node.ELEMENT_NODE) {
					continue;
				}
				String tag = node.getNodeName();
				if (ELEM_TABLE.equals(tag)) {
					parseTable(obj, (Element) node);
				} else if (ELEM_META.equals(tag)) {
					parseTable(obj, (Element) node);
				} else if (ELEM_STMT.equals(tag)) {
					parseStatement(obj, (Element) node);
				}
			}

			return obj;
		} finally {
			if (is != null) {
				is.close();
			}
		}

	}

	private List<Part> optimize(List<Part> parts) {

		LinkedList<Part> ret = new LinkedList<Part>();
		StringBuilder psql = new StringBuilder(4096);
		LinkedList<String> params = new LinkedList<String>();
		// 合并优化静态,如果无动态则psqls与params存的是静态
		for (Part f : parts) {
			if (f.isDynamic()) {
				if (psql.length() > 0) {
					ret.add(new Static(psql.toString(), params.toArray(new String[params.size()])));
					psql.setLength(0);
					params.clear();
				}
				ret.add(f);
			} else {
				String s = f.getPsql();
				if (StringBase.isNotBlank(s)) { // 空元素去除
					psql.append(s);
					Collections.addAll(params, f.getParams());
				}
			}
		}
		if (psql.length() > 0) {
			ret.add(new Static(psql.toString(), params.toArray(new String[params.size()])));
		}

		return ret;
	}

	void parseTable(ObaseMysqlObject obj, Element node) throws ClassNotFoundException {
		String className = node.getTextContent().trim();
		if (StringBase.isNotEmpty(className)) {
			obj.tableClassList.add(Class.forName(className));
		}
	}

	void parseMeta(ObaseMysqlObject obj, Element node) throws ClassNotFoundException {
		String className = node.getTextContent().trim();
		if (StringBase.isNotEmpty(className)) {
			obj.metaClassList.add(Class.forName(className));
		}
	}

	void parseStatement(ObaseMysqlObject obj, Element root) {
		String id = root.getAttribute(ATTR_ID);
		String nop = root.getAttribute(ATTR_NOP);
		List<Part> parts = parseChildPart(root);
		if (parts.size() > 0) {
			obj.statementList.add(new Statement(id, "true".equalsIgnoreCase(nop), parts.toArray(new Part[parts.size()])));
		}
	}

	private List<Part> parseChildPart(Element root) {
		List<Part> ret = new LinkedList<Part>();

		for (Node node = root.getFirstChild(); node != null; node = node.getNextSibling()) {
			short nt = node.getNodeType();
			Part f = null;
			if (nt == Node.TEXT_NODE) {
				f = parseStatic(node);
			} else if (nt == Node.ELEMENT_NODE) {
				String tag = node.getNodeName();
				if (ELEM_WHERE.equals(tag)) {
					f = parseX((Element) node, new WHERE());
				} else if (ELEM_AND.equals(tag)) {
					f = parseX((Element) node, new AND());
				} else if (ELEM_OR.equals(tag)) {
					f = parseX((Element) node, new OR());
				} else if (ELEM_X.equals(tag)) {
					f = parseX((Element) node, new X());
				}
			}
			if (f != null) {
				ret.add(f);
			}
		}
		if (ret.size() > 1) { // 多个才需要优化
			ret = optimize(ret);
		}
		return ret;
	}

	private Part parseX(Element root, X x) {
		String s = root.getAttribute(ATTR_SEP);
		List<Part> parts = parseChildPart(root);
		int size = parts.size();
		if (size == 0) {
			return null;
		} else if (size == 1) {
			// 不或只包含一个子元素
			Part p = parts.get(0);
			String[] ph = p.getParams();

			if (ph.length == 0) {
				// 无参数退化为静态
				return new Static(p.getPsql(), null);
			} else if (ph.length == 1) {
				// 嵌套简化
				return x.reset(p.isDynamic() ? p.getSeparator() : s, p.getPsql(), ph[0]);
			} else {
				// 动态标签不支持多参数
				throw new SystemException(MysqlErrno.SQL_CONFIG_EXCEED_PARAMS, "Statement subtag has more than 1 params: " + traceXPathPath(root) + ", " + Arrays.toString(ph));
			}

		} else {
			// 包含多个子标签
			return x.reset(s, parts.toArray(new Part[size]));
		}

	}

	private Part parseStatic(Node node) {
		String val = SqlKit.filterWhiteSpaces(node.getTextContent());
		if (StringBase.isEmpty(val)) {
			return null;
		}
		Sql sql = SqlDqlKit.parseSql(val);
		return Static.getInstance(sql.content, sql.params);
	}

	private static String traceXPathPath(Element elem) {
		LinkedList<Element> stack = new LinkedList<Element>();
		for (Node n = elem; n instanceof Element; n = n.getParentNode()) {
			stack.addFirst((Element) n);
		}
		StringBuilder sb = new StringBuilder(128);
		String at;
		for (Element e : stack) {
			sb.append('/').append(e.getNodeName());
			if (StringBase.isNotEmpty(at = e.getAttribute(ATTR_ID))) {
				sb.append("[@").append(ATTR_ID).append("=").append(at).append(']');
			} else if (StringBase.isNotEmpty(at = e.getAttribute(ATTR_NAMESPACE))) {
				sb.append("[@").append(ATTR_NAMESPACE).append("=").append(at).append(']');
			}
		}
		return sb.toString();
	}

	/* Lv1 */
	static final String ELEM_MYSQL = "obase-mysql";
	static final String ELEM_TABLE = "table-class";
	static final String ELEM_META = "meta-class";
	static final String ELEM_STMT = "statement";
	/* Lv2 */
	static final String ELEM_WHERE = "where";
	static final String ELEM_AND = "and";
	static final String ELEM_OR = "or";
	static final String ELEM_X = "x";

	static final String ATTR_SEP = "s";
	static final String ATTR_NAMESPACE = "namespace";
	static final String ATTR_ID = "id";
	static final String ATTR_NOP = "nop";
}
