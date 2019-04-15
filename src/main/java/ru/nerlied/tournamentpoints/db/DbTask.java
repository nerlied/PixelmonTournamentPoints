package ru.nerlied.tournamentpoints.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.service.sql.SqlService;

import ru.nerlied.tournamentpoints.Config;
import ru.nerlied.tournamentpoints.TournamentPoints;

public abstract class DbTask extends Thread {	
	private static SqlService sqlService;
	
	private String dbUrl = Config.dbUrl;
	
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

	public static DataSource getDataSource(String jdbcUrl) throws SQLException {
	    if (sqlService == null) {
	        sqlService = Sponge.getServiceManager().provide(SqlService.class).get();
	    }
	    
	    return sqlService.getDataSource(jdbcUrl);
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
    
    protected static int getPlayerStatId(Connection c, String username) throws SQLException {
    	String sql = "SELECT `id` FROM `" + Config.dbTblPlayers + "` WHERE `name` = '" + username + "' AND `season`='" + Config.season + "'";
		TournamentPoints.LOG.info("SQL > " + sql);
		
		PreparedStatement ps = c.prepareStatement(sql);
		ResultSet rs = ps.executeQuery();

		if(rs.next()) {
			return rs.getInt("id");
		} else {
			return insertPlayerStat(c, username);
		}
    }
    
    protected static int insertPlayerStat(Connection c, String username) throws SQLException {
    	String sql = "INSERT INTO `player_stats`(`name`, `season`) VALUES ('" + username + "', '" + Config.season + "')";
		TournamentPoints.LOG.info("SQL > " + sql);
		PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
		int affectedRows = ps.executeUpdate();
		
		if (affectedRows == 0) {
			throw new SQLException("Creating player failed, no rows affected.");
		}

		try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
			if (generatedKeys.next()) {
				return generatedKeys.getInt(1);
			} else {
				throw new SQLException("Creating player failed, no ID obtained.");
			}
		}
    }
}
