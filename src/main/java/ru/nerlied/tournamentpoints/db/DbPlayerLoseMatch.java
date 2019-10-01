package ru.nerlied.tournamentpoints.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import ru.nerlied.tournamentpoints.Config;
import ru.nerlied.tournamentpoints.TournamentData;
import ru.nerlied.tournamentpoints.TournamentPoints;

public class DbPlayerLoseMatch extends DbTournamentTask {
	private TournamentData tData;
	private String username;
	
	public DbPlayerLoseMatch(TournamentData tData, String username) {
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
    			String sql = String.format("UPDATE `%s` SET `matches_lose`=`matches_lose` + '1', `matches_total` = `matches_total` + 1, `points` = `points` + ? WHERE `id` = ?", Config.dbTblPlayers);
    			ps = c.prepareStatement(sql);
    			ps.setInt(1, Config.pointsAddLoseMatch);
    			ps.setInt(2, id);
    			TournamentPoints.LOG.info("SQL > " + ps);
                ps.executeUpdate();
    		}
    		
    		(new DbPlayerStatLog(tData, username, "match_lose", Config.pointsAddLoseMatch, "")).process(c);
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
