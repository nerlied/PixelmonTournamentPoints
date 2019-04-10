package ru.nerlied.tournamentpoints.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import ru.nerlied.tournamentpoints.Config;
import ru.nerlied.tournamentpoints.TournamentData;
import ru.nerlied.tournamentpoints.TournamentPoints;

public class DbPlayerWinTournament extends DbTask {
	private TournamentData tData;
	private String username;
	
	public DbPlayerWinTournament(TournamentData tData, String username) {
		this.tData = tData;
		this.username = username;
	}
	
	@Override
	protected void process(Connection c) throws SQLException {
		TournamentPoints.LOG.info(this.getClass().getCanonicalName() + " process");
		PreparedStatement ps = null;
		ResultSet rs = null;
		
    	try {
    		String sql = "SELECT `id` FROM `" + Config.dbTblPlayers + "` WHERE `name` = '" + username + "'";
    		TournamentPoints.LOG.info("SQL > " + sql);
    		
    		ps = c.prepareStatement(sql);
    		rs = ps.executeQuery();
        	
    		int id = -1;
    		if(rs.next()) {
    			id = rs.getInt("id");
    		} else {
    			sql = "INSERT INTO `player_stats`(`name`) VALUES ('" + this.username + "')";
    			TournamentPoints.LOG.info("SQL > " + sql);
    			ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
				int affectedRows = ps.executeUpdate();
    			if (affectedRows == 0) {
					throw new SQLException("Creating player failed, no rows affected.");
				}

				try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
					if (generatedKeys.next()) {
						id = generatedKeys.getInt(1);
					} else {
						throw new SQLException("Creating player failed, no ID obtained.");
					}
				}
    		}
    		
    		if(id != -1) {
    			sql = "UPDATE `" + Config.dbTblPlayers + "` SET `tournaments_win`=`tournaments_win` + '1', `tournaments_total`=`tournaments_total` + 1, `points`=`points` + '" + Config.pointsAddWinTournament + "' WHERE `id`='" + id + "'";
    			TournamentPoints.LOG.info("SQL > " + sql);
    			ps = c.prepareStatement(sql);
    			ps.executeUpdate();
    		}
    		
    		(new DbPlayerStatLog(tData, username, "tournament_win", "")).process(c);
    	} catch(Exception e) {
    		e.printStackTrace();
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
