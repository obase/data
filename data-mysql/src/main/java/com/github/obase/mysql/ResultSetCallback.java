package com.github.obase.mysql;

import java.sql.ResultSet;
import java.sql.SQLException;

@FunctionalInterface
public interface ResultSetCallback<T> {
	T doInResultSet(ResultSet rs) throws SQLException;
}
