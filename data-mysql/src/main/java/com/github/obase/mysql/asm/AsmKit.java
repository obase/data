package com.github.obase.mysql.asm;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.asm.ClassReader;
import org.springframework.asm.Type;
import org.springframework.core.io.Resource;

import com.github.obase.SystemException;
import com.github.obase.base.ClassBase;
import com.github.obase.base.StringBase;
import com.github.obase.mysql.JdbcMeta;
import com.github.obase.mysql.MysqlErrno;
import com.github.obase.mysql.annotation.Column;
import com.github.obase.mysql.annotation.ForeignKey;
import com.github.obase.mysql.annotation.Indexes;
import com.github.obase.mysql.annotation.Meta;
import com.github.obase.mysql.annotation.OptimisticLock;
import com.github.obase.mysql.annotation.PrimaryKey;
import com.github.obase.mysql.annotation.Table;
import com.github.obase.mysql.data.ClassMetaInfo;
import com.github.obase.mysql.data.FieldMetaInfo;
import com.github.obase.mysql.data.MethodMetaInfo;

public final class AsmKit {

	static final String JdbcMetaSuffix = "__JdbcMeta";
	static final String Object_INTERNAL_NAME = Type.getInternalName(Object.class);
	static final String SqlAction_INTERNAL_NAME = Type.getInternalName(JdbcMeta.class);
	static final String GETTER_METHOD_PREFIX = "get";
	static final String SETTER_METHOD_PREFIX = "set";
	static final String TABLE_ANNOTATION_DESC = Type.getDescriptor(Table.class);
	static final String META_ANNOTATION_DESC = Type.getDescriptor(Meta.class);
	static final String PRIMARY_KEY_ANNOTATION_DESC = Type.getDescriptor(PrimaryKey.class);
	static final String FOREIGN_KEY_ANNOTATION_DESC = Type.getDescriptor(ForeignKey.class);
	static final String INDEXES_KEY_ANNOTATION_DESC = Type.getDescriptor(Indexes.class);
	static final String COLUMN_ANNOTATION_DESC = Type.getDescriptor(Column.class);
	static final String OPTIMISTIC_LOCK_ANNOTATION_DESC = Type.getDescriptor(OptimisticLock.class);

	static final int CLASS_READER_ACCEPT_FLAGS = ClassReader.SKIP_DEBUG | ClassReader.SKIP_CODE | ClassReader.SKIP_FRAMES;

	public static JdbcMeta newJdbcMeta(ClassMetaInfo classMetaInfo) throws IOException, ReflectiveOperationException {
		String internalName = classMetaInfo.internalName + JdbcMetaSuffix;
		String className = ClassBase.getClassNameFromInternalName(internalName);

		Class<?> c = null;
		try {
			c = ClassBase.loadClass(className);
		} catch (ClassNotFoundException e) {
			byte[] data = JdbcMetaClassWriter.dump(internalName, classMetaInfo);
			synchronized (AsmKit.class) {
				try {
					c = ClassBase.loadClass(className);
				} catch (ClassNotFoundException e2) {
					c = ClassBase.defineClass(className, data);
				}
			}
		}
		return (JdbcMeta) c.newInstance();
	}

	public static JdbcMeta newJdbcMeta(String targetClassName) throws IOException, ReflectiveOperationException {
		String className = targetClassName + JdbcMetaSuffix;
		Class<?> c = null;
		try {
			c = ClassBase.loadClass(className);
		} catch (ClassNotFoundException e) {
			ClassMetaInfo classMetaInfo = getClassMetaInfo(targetClassName);
			String internalName = ClassBase.getInternalNameFromClassName(className);
			byte[] data = JdbcMetaClassWriter.dump(internalName, classMetaInfo);
			synchronized (AsmKit.class) {
				try {
					c = ClassBase.loadClass(className);
				} catch (ClassNotFoundException e2) {
					c = ClassBase.defineClass(className, data);
				}
			}
		}
		return (JdbcMeta) c.newInstance();
	}

	public static ClassMetaInfo getAnnotationClassMetaInfo(Resource rs) throws IOException {
		ClassReader cr = null;
		InputStream is = null;
		try {
			is = rs.getInputStream();
			cr = new ClassReader(is);
		} finally {
			if (is != null) {
				is.close();
			}
		}

		ClassMetaInfo result = new ClassMetaInfo();
		cr.accept(new AnnotationMetaInfoClassVisitor(result), CLASS_READER_ACCEPT_FLAGS);
		return postProcessClassMetaInfo(result);
	}

	public static ClassMetaInfo getAnnotationClassMetaInfo(String className) throws IOException {
		ClassReader cr = new ClassReader(className);
		ClassMetaInfo result = new ClassMetaInfo();
		cr.accept(new AnnotationMetaInfoClassVisitor(result), CLASS_READER_ACCEPT_FLAGS);
		return postProcessClassMetaInfo(result);
	}

	public static ClassMetaInfo getClassMetaInfo(String className) throws IOException {
		ClassReader cr = new ClassReader(ClassBase.getResourceAsStream(ClassBase.getClassPathFromClassName(className)));
		ClassMetaInfo result = new ClassMetaInfo();
		cr.accept(new MetaInfoClassVisitor(result), CLASS_READER_ACCEPT_FLAGS);
		return postProcessClassMetaInfo(result);
	}

	static ClassMetaInfo postProcessClassMetaInfo(ClassMetaInfo classMetaInfo) {
		if (classMetaInfo.tableAnnotation != null) {
			gatherTableMetaData(classMetaInfo);
			if (classMetaInfo.keys == null || classMetaInfo.keys.size() == 0) {
				throw new SystemException(MysqlErrno.META_INFO_EXT_FAILED, "Undefine primary key for table:" + classMetaInfo.internalName);
			}
		}
		changeGetterOrSetterToColumnName(classMetaInfo.getters, classMetaInfo.fields);
		changeGetterOrSetterToColumnName(classMetaInfo.setters, classMetaInfo.fields);
		return classMetaInfo;
	}

	static final int CAP_DIF = ('A' - 'a');

	static void changeGetterOrSetterToColumnName(Map<String, MethodMetaInfo> methods, Map<String, FieldMetaInfo> fields) {
		String name = null;
		FieldMetaInfo field = null;
		StringBuilder sb = new StringBuilder(128);

		Set<String> keys = new HashSet<String>(methods.keySet());
		for (String method : keys) {
			sb.setLength(0);
			sb.append(method).delete(0, 3).setCharAt(0, (char) (sb.charAt(0) - CAP_DIF));
			name = sb.toString();
			field = fields.get(name);
			if (field != null && field.columnAnnotation != null && StringBase.isNotEmpty(field.columnAnnotation.name)) {
				name = field.columnAnnotation.name;
			}
			methods.put(name, methods.remove(method));
		}
	}

	static final Comparator<FieldMetaInfo> FieldMetaInfoComparator = new Comparator<FieldMetaInfo>() {

		@Override
		public int compare(FieldMetaInfo o1, FieldMetaInfo o2) {
			if (o1.order > o2.order) {
				return -1;
			} else if (o1.order < o2.order) {
				return 1;
			} else {
				return 0;
			}
		}
	};

	static void gatherTableMetaData(ClassMetaInfo classMetaInfo) {

		String tableName = null;
		if (classMetaInfo.tableAnnotation != null) {
			tableName = classMetaInfo.tableAnnotation.name;
			if (StringBase.isEmpty(tableName)) {
				int pos = classMetaInfo.internalName.lastIndexOf('/');
				tableName = (pos != -1) ? classMetaInfo.internalName.substring(pos + 1) : classMetaInfo.internalName;
			}
		}
		classMetaInfo.tableName = tableName;

		List<FieldMetaInfo> fields = new LinkedList<FieldMetaInfo>(classMetaInfo.fields.values());
		Collections.sort(fields, FieldMetaInfoComparator);
		classMetaInfo.fields.clear();
		for (FieldMetaInfo field : fields) {
			classMetaInfo.fields.put(field.name, field);
		}

		List<String> keys = new LinkedList<String>();
		List<String> cols = new LinkedList<String>();
		for (FieldMetaInfo field : fields) {
			if (field.columnAnnotation != null) {
				String name = field.columnAnnotation.name;
				if (StringBase.isEmpty(name)) {
					name = field.name;
				}
				cols.add(name);
				if (Boolean.TRUE.equals(field.columnAnnotation.key)) {
					keys.add(name);
				}
			}
		}

		if (classMetaInfo.primaryKeyAnnotation != null) {
			keys.clear();
			keys.addAll(classMetaInfo.primaryKeyAnnotation.columns);
		}

		classMetaInfo.keys = keys;
		classMetaInfo.columns = cols;

	}

}
