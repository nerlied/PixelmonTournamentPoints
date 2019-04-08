package ru.nerlied.tournamentpoints.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import ru.nerlied.tournamentpoints.TPConfig;
import ru.nerlied.tournamentpoints.TournamentData;

public class DbPlayerWinTournament extends DbTask {
	private TournamentData tData;
	private String username;
	
	public DbPlayerWinTournament(TournamentData tData, String username) {
		this.tData = tData;
		this.username = username;
	}
	
	@Override
	protected void process(Connection c) throws SQLException {
		if(TPConfig.INSTANCE.enableLog) System.out.println(this.getClass().getCanonicalName() + " process");
		TPConfig conf = TPConfig.INSTANCE;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
    	try {
    		String sql = "SELECT `id` FROM `" + conf.dbTblPlayers + "` WHERE `name` = '" + username + "'";
    		if(TPConfig.INSTANCE.enableLog) System.out.println("SQL > " + sql);
    		
    		ps = c.prepareStatement(sql);
    		rs = ps.executeQuery();
        	
    		int id = -1;
    		if(rs.next()) {
    			id = rs.getInt("id");
    		} else {
    			sql = "INSERT INTO `player_stats`(`name`) VALUES ('" + this.username + "')";
    			if(TPConfig.INSTANCE.enableLog) System.out.println("SQL > " + sql);
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
    			sql = "UPDATE `" + conf.dbTblPlayers + "` SET `tournaments_win`=`tournaments_win` + '1', `tournaments_total`=`tournaments_total` + 1, `points`=`points` + '" + conf.pointsAddWinTournament + "' WHERE `id`='" + id + "'";
    			if(TPConfig.INSTANCE.enableLog) System.out.println("SQL > " + sql);
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
