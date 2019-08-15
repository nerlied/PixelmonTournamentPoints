package ru.nerlied.tournamentpoints.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import ru.nerlied.tournamentpoints.Config;
import ru.nerlied.tournamentpoints.TournamentData;
import ru.nerlied.tournamentpoints.TournamentPoints;
import ru.nerlied.tournamentpoints.Utils;

public class DbPlayerStatLog extends DbTask {
	private TournamentData tData;
	private String player;
	private String action;
	private int points;
	private String data;
	
	public DbPlayerStatLog(TournamentData tData, String player, String action, int points, String data) {
		this.tData = tData;
		this.player = player;
		this.action = action;
		this.points = points;
		this.data = data;
	}

	@Override
	protected void process(Connection c) throws SQLException {
		TournamentPoints.LOG.info(this.getClass().getCanonicalName() + " process");
		PreparedStatement ps = null;
		
    	try {
    		String sql = String.format("INSERT INTO `%s`(`season`, `tournament_id`, `tournament_round`, `tournament_match`, `player`, `action`, `points`, `data`, `time`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)", Config.dbTblPlayerStatLog);
    		ps = c.prepareStatement(sql);
    		ps.setInt(1, Config.season);
    		ps.setInt(2, tData.tId);
    		ps.setInt(3, tData.tRoundNumber);
    		ps.setInt(4, tData.tMatchNumber);
    		ps.setString(5, this.player);
    		ps.setString(6, this.action);
    		ps.setInt(7, this.points);
    		ps.setString(8, this.data);
    		ps.setInt(9, Utils.getCurTime());
    		TournamentPoints.LOG.info("SQL > " + ps);
    		ps.executeUpdate();
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
