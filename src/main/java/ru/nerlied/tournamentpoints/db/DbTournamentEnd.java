package ru.nerlied.tournamentpoints.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import ru.nerlied.tournamentpoints.Config;
import ru.nerlied.tournamentpoints.TournamentData;
import ru.nerlied.tournamentpoints.TournamentPoints;
import ru.nerlied.tournamentpoints.Utils;

public class DbTournamentEnd extends DbTask {
	private TournamentData tData;
	private List<String> winners;
	
	public DbTournamentEnd(TournamentData tData, List<String> winners) {
		this.tData = tData;
		this.winners = winners;
	}
	
	@Override
	protected void process(Connection c) throws SQLException {
		TournamentPoints.LOG.info(this.getClass().getCanonicalName() + " process");
		PreparedStatement ps = null;
		String sql;
		
    	try {
    		if(tData.tId != 0) {
				sql = String.format("UPDATE `%s` SET `time_end` = ? WHERE `id` = ?", Config.dbTblTournaments);
				ps = c.prepareStatement(sql);
				ps.setInt(1, Utils.getCurTime());
				ps.setInt(2, tData.tId);
				TournamentPoints.LOG.info("SQL > " + ps);
    			ps.executeUpdate();
    			
        		List<String> losers = new ArrayList<String>();
        		String player;
        		
        		for(int i = 0; i < tData.tPlayers.size(); i++) {
        			player = tData.tPlayers.get(i);
        			if(!this.winners.contains(player)) {
        				losers.add(player);
        			}
        		}
        		
        		for(String loser : losers) {
        			(new DbPlayerLoseTournament(tData, loser)).process(c);
        		}
			} else {
				TournamentPoints.LOG.info("[ERROR] tournament end failed: tournamentId = 0");
			}
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
