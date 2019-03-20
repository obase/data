package com.github.obase.base;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;
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

	public static Set<Class<?>> scanPackClass(Set<String> packs) throws IOException {

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
			try {
				int end = cpath.lastIndexOf(".class");
				if (end == -1) {
					end = cpath.length();
				}
				Class<?> clazz = Class.forName(cpath.substring(0, end).replace('/', '.'));
				if (ClassBase.class.isAssignableFrom(clazz) && ClassBase.class != clazz) { // 必须实现APP及非abstract
					int mfs = clazz.getModifiers();
					if (Modifier.isPublic(mfs) && !Modifier.isAbstract(mfs)) {
						clazzSet.add(clazz);
					}
				}
			} catch (ClassNotFoundException e) {
				// skip the unkonw class
				logger.error("class not found: %s", cpath);
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
			if (file.isFile() && file.getName().endsWith(".class")) {
				ret.add(prefix + "/" + file.getName());
			} else if (file.isDirectory()) {
				scanPackClassInFile(ret, file, prefix);
			}
		}
	}
}
