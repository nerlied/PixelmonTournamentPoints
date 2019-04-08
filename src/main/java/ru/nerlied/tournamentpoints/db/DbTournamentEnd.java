package ru.nerlied.tournamentpoints.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import ru.nerlied.tournamentpoints.TPConfig;
import ru.nerlied.tournamentpoints.TournamentData;
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
		if(TPConfig.INSTANCE.enableLog) System.out.println(this.getClass().getCanonicalName() + " process");
		TPConfig conf = TPConfig.INSTANCE;
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql;
		
    	try {
    		if(tData.tId != 0) {
				sql = "UPDATE `" + conf.dbTblTournaments + "` SET `time_end`='" + Utils.getCurTime() + "' WHERE `id`='" + tData.tId + "'";
				if(TPConfig.INSTANCE.enableLog) System.out.println("SQL > " + sql);
				ps = c.prepareStatement(sql);
    			ps.executeUpdate();
    			
    			/*
    			sql = "SELECT `player` FROM `" + conf.dbTblPlayerStatLog + "` WHERE `tournament_id`='" + tId + "' AND `tournament_match`='1'";
    			if(ATConfig.INSTANCE.enableLog) System.out.println("SQL > " + sql);
    			ps = c.prepareStatement(sql);
        		rs = ps.executeQuery();
        		  
        		*/
    			
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
				System.out.println("[ERROR] tournament end failed: tournamentId = 0");
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
