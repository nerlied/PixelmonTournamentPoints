package ru.nerlied.tournamentpoints.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import ru.nerlied.tournamentpoints.Config;
import ru.nerlied.tournamentpoints.TournamentData;
import ru.nerlied.tournamentpoints.TournamentPoints;

public class DbPlayerWinMatch extends DbTask {
	private TournamentData tData;
	private String username;
	
	public DbPlayerWinMatch(TournamentData tData, String username) {
		this.tData = tData;
		this.username = username;
	}
	
	@Override
	protected void process(Connection c) throws SQLException {
		TournamentPoints.LOG.info(this.getClass().getCanonicalName() + " process");
		PreparedStatement ps = null;
		
    	try {
    		int id = getPlayerStatId(c, username);

    		if(id != -1) {
    			String sql = "UPDATE `" + Config.dbTblPlayers + "` SET `matches_win`=`matches_win` + '1', `matches_total`=`matches_total` + 1, `points`=`points` + '" + Config.pointsAddWinMatch + "' WHERE `id`='" + id + "'";
    			TournamentPoints.LOG.info("SQL > " + sql);
    			ps = c.prepareStatement(sql);
                ps.executeUpdate();
    		}
    		
    		(new DbPlayerStatLog(tData, username, "match_win", "")).process(c);
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
