package com.github.obase.base;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ClassBase {

	static final Logger logger = LogManager.getLogger(ClassBase.class);

	public static Set<Class<?>> scanPackClass(Set<String> packs, Class<?> parent) throws IOException {

		if (packs.size() == 0) {
			return Collections.emptySet();
		}

		Set<String> cpathSet = new LinkedHashSet<String>();

		String[] prefixs = new String[packs.size()];
		int idx = 0;
		for (String pack : packs) {
			prefixs[idx++] = pack.replace('.', '/');
		}

		String[] clsspaths;
		if (File.separatorChar == '\\') {
			// Windows
			clsspaths = System.getProperty("java.class.path", "").split("\\;");
		} else {
			// Linux/Unix
			clsspaths = System.getProperty("java.class.path", "").split("\\:");
		}

		for (String clsspath : clsspaths) {
			File file = new File(clsspath);
			if (file.isFile()) {
				// jar
				scanPackClassInJar(cpathSet, file, prefixs);
			} else {
				// dir
				for (String prefix : prefixs) {
					File dir = new File(file, prefix);
					if (dir.exists() && dir.isDirectory()) {
						scanPackClassInFile(cpathSet, dir, prefix);
					}
				}
			}
		}

		Set<Class<?>> clazzSet = new HashSet<Class<?>>(cpathSet.size());
		for (String cpath : cpathSet) {
			int end = cpath.lastIndexOf(".class");
			if (end == -1) {
				end = cpath.length();
			}
			String fullname = cpath.substring(0, end).replace('/', '.');
			try {
				Class<?> clazz = ClassBase.forName(fullname);
				if (parent.isAssignableFrom(clazz) && parent != clazz) { // 必须实现APP及非abstract
					int mfs = clazz.getModifiers();
					if (Modifier.isPublic(mfs) && !Modifier.isAbstract(mfs)) {
						clazzSet.add(clazz);
					}
				}
			} catch (ClassNotFoundException e) {
				// skip the unkonw class
				logger.error("class not found: {}", fullname);
			}
		}
		return clazzSet;
	}

	public static void scanPackClassInJar(Set<String> ret, File jarf, String[] prefixs) throws IOException {
		JarFile jfile = null;
		try {
			jfile = new JarFile(jarf);
			for (Enumeration<JarEntry> enums = jfile.entries(); enums.hasMoreElements();) {
				String name = enums.nextElement().getName();
				for (String pre : prefixs) {
					if (name.startsWith(pre) && name.endsWith(".class")) {
						ret.add(name);
						break;
					}
				}
			}
		} finally {
			if (jfile != null) {
				try {
					jfile.close();
				} catch (IOException e) {
					logger.error("close jar file failed", e);
				}
			}
		}
	}

	public static void scanPackClassInFile(Set<String> ret, File dir, String prefix) throws IOException {
		for (File file : dir.listFiles()) {
			String fname = file.getName();
			if (file.isFile() && fname.endsWith(".class")) {
				ret.add(prefix + "/" + fname);
			} else if (file.isDirectory()) {
				scanPackClassInFile(ret, file, prefix + "/" + fname); // FIXBUG: 逐级附加
			}
		}
	}

	private static final DelegateClassLoader ContextClassLoader = new DelegateClassLoader(contextClassLoader());

	public static Class<?> defineClass(String name, byte[] data, ClassLoader loader) {
		return new DelegateClassLoader(loader).defineClass(name, data);
	}

	public static Class<?> defineClass(String name, byte[] data) {
		return ContextClassLoader.defineClass(name, data);
	}

	public static Class<?> loadClass(String name) throws ClassNotFoundException {
		return ContextClassLoader.loadClass(name);
	}

	public static Class<?> forName(String name) throws ClassNotFoundException {
		return ContextClassLoader.loadClass(name);
	}

	public static class DelegateClassLoader extends ClassLoader {

		public DelegateClassLoader(ClassLoader delegate) {
			super(delegate);
		}

		public Class<?> defineClass(String name, byte[] data) {
			return super.defineClass(name, data, 0, data.length);
		}

	}

	public static ClassLoader contextClassLoader() {
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		if (loader == null) {
			loader = ClassBase.class.getClassLoader();
		}
		return loader;
	}

	public static String getResourceAsString(String classpath) throws IOException {
		InputStream in = null;
		try {
			// FIXBUG: not const ContextClassLoader
			in = ContextClassLoader.getResourceAsStream(classpath);
			if (in != null) {
				Reader reader = new BufferedReader(new InputStreamReader(in));
				StringBuilder sb = new StringBuilder(1024);
				int len = 0;
				for (char[] buff = new char[1024]; (len = reader.read(buff)) > 0;) {
					sb.append(buff, 0, len);
				}
				return sb.toString();
			}
			return null;
		} finally {
			if (in != null) {
				in.close();
			}
		}
	}

	public static URL getResource(String classpath) {
		return ContextClassLoader.getResource(classpath);
	}

	public static InputStream getResourceAsStream(String classpath) {
		// FIXBUG: not const ContextClassLoader
		return ContextClassLoader.getResourceAsStream(classpath);
	}

	public static String getClassPathFromClassName(String className) {
		return new StringBuilder(128).append('/').append(className.replace('.', '/')).append(".class").toString();
	}

	public static String getClassPathFromInternalName(String internalName) {
		return new StringBuilder(128).append('/').append(internalName).append(".class").toString();
	}

	public static String getClassNameFromInternalName(String internalName) {
		return internalName.replace('/', '.');
	}

	public static String getInternalNameFromClassName(String className) {
		return className.replace('.', '/');
	}

	public static final String CWD = "CWD";
	public static final Class<?> BaseClass = ClassBase.class;
	public static final String BasePath = "/" + BaseClass.getCanonicalName().replace('.', '/') + ".class";

	/**
	 * 获取当前工作目录
	 */
	public static File cwd() {

		String cwd = System.getProperty(CWD, System.getProperty(CWD.toLowerCase(), System.getenv(CWD)));
		if (StringBase.isNotEmpty(cwd)) {
			return new File(cwd);
		}

		URL url = ContextClassLoader.getResource(BasePath);
		String jurl = url.getFile();
		String prot = url.getProtocol();
		if ("jar".equalsIgnoreCase(prot)) {
			String furl = jurl.substring(0, jurl.length() - BasePath.length() - 1); // jar:xxx.jar!/xxxx
			try {
				return new File(new URL(furl).getFile()).getParentFile();
			} catch (MalformedURLException e) {
				// ignore
			}
		} else if ("file".equalsIgnoreCase(prot)) {
			String furl = jurl.substring(0, jurl.length() - BasePath.length());
			return new File(furl);
		}
		return new File("./");
	}
}
