package ru.nerlied.tournamentpoints.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.service.sql.SqlService;

import ru.nerlied.tournamentpoints.TPConfig;

public abstract class DbTask extends Thread {	
	private String dbUrl = TPConfig.INSTANCE.dbUrl;
	
	public DbTask setConf(String dbUrl) {
		this.dbUrl = dbUrl;
		return this;
	}
	
	@Override
    public final void run() {
        Connection c = null;
        try {
            String url = dbUrl + "?useUnicode=true&characterEncoding=utf-8";
            c = getDataSource(url).getConnection();
            process(c);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (c != null) {
                try {
                    c.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    protected abstract void process(Connection c) throws SQLException;

	private static SqlService sql;
	
	public static DataSource getDataSource(String jdbcUrl) throws SQLException {
	    if (sql == null) {
	        sql = Sponge.getServiceManager().provide(SqlService.class).get();
	    }
	    
	    return sql.getDataSource(jdbcUrl);
	}
    
    public static void executeUpdate(Connection c, String sql) throws SQLException {
        PreparedStatement ps = null;
        try {
            ps = c.prepareStatement(sql);
            ps.executeUpdate();
        } finally {
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
