package ru.nerlied.tournamentpoints.db;

import java.sql.Connection;
import java.sql.SQLException;

public class DbUpdateTask extends DbTask {
	private String sql;
	
	public DbUpdateTask(String sql) {
		this.sql = sql;
	}
	
	@Override
	protected void process(Connection c) throws SQLException {
		executeUpdate(c, this.sql);
	}
}
