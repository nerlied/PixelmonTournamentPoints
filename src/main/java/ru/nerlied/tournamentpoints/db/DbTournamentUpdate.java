package ru.nerlied.tournamentpoints.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import ru.nerlied.tournamentpoints.Config;
import ru.nerlied.tournamentpoints.TournamentData;
import ru.nerlied.tournamentpoints.TournamentPoints;

public class DbTournamentUpdate extends DbTask {
	private TournamentData tData;
	
	public DbTournamentUpdate(TournamentData tData) {
		this.tData = tData;
	}
	
	@Override
	protected void process(Connection c) throws SQLException {
		TournamentPoints.LOG.info(this.getClass().getCanonicalName() + " process");
		PreparedStatement ps = null;
		String sql;
		
    	try {
    		if(tData.tId != 0) {
				sql = "UPDATE `" + Config.dbTblTournaments + "` SET `round_number`='" + tData.tRoundNumber + "',`match_number`='" + tData.tMatchNumber + "' WHERE `id`='" + tData.tId + "'";
				TournamentPoints.LOG.info("SQL > " + sql);
				ps = c.prepareStatement(sql);
    			ps.executeUpdate();
			} else {
				TournamentPoints.LOG.info("[ERROR] updating tournament failed: tournamentId = 0");
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
