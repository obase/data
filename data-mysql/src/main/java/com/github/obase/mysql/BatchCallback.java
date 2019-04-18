package com.github.obase.mysql;

import java.sql.SQLException;
import java.util.List;

@FunctionalInterface
public interface BatchCallback<T> {
	T batch(List<T> result) throws SQLException;
}
