package ru.nerlied.tournamentpoints.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import ru.nerlied.tournamentpoints.Config;
import ru.nerlied.tournamentpoints.TournamentData;
import ru.nerlied.tournamentpoints.TournamentPoints;

public class DbPlayerLoseTournament extends DbTournamentTask {
	private TournamentData tData;
	private String username;
	
	public DbPlayerLoseTournament(TournamentData tData, String username) {
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
    			String sql = String.format("UPDATE `%s` SET `tournaments_lose`=`tournaments_lose` + '1', `tournaments_total` = `tournaments_total` + 1, `points` = `points` + ? WHERE `id` = ?", Config.dbTblPlayers);
    			ps = c.prepareStatement(sql);
    			ps.setInt(1, Config.pointsAddLoseTournament);
    			ps.setInt(2, id);
    			TournamentPoints.LOG.info("SQL > " + ps);
    			ps.executeUpdate();
    		}
    		
    		(new DbPlayerStatLog(tData, username, "tournament_lose", Config.pointsAddLoseTournament, "")).process(c);
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
