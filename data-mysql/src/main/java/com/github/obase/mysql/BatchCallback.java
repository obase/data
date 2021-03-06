package com.github.obase.mysql;

import java.sql.SQLException;
import java.util.List;

@FunctionalInterface
public interface BatchCallback<T> {
	void batch(List<T> result) throws SQLException;
}
