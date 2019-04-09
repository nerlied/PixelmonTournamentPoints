package ru.nerlied.tournamentpoints.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import ru.nerlied.tournamentpoints.TPConfig;
import ru.nerlied.tournamentpoints.TournamentData;

public class DbTournamentUpdate extends DbTask {
	private TournamentData tData;
	
	public DbTournamentUpdate(TournamentData tData) {
		this.tData = tData;
	}
	
	@Override
	protected void process(Connection c) throws SQLException {
		if(TPConfig.INSTANCE.enableLog) System.out.println(this.getClass().getCanonicalName() + " process");
		TPConfig conf = TPConfig.INSTANCE;
		PreparedStatement ps = null;
		String sql;
		
    	try {
    		if(tData.tId != 0) {
				sql = "UPDATE `" + conf.dbTblTournaments + "` SET `round_number`='" + tData.tRoundNumber + "',`match_number`='" + tData.tMatchNumber + "' WHERE `id`='" + tData.tId + "'";
				if(TPConfig.INSTANCE.enableLog) System.out.println("SQL > " + sql);
				ps = c.prepareStatement(sql);
    			ps.executeUpdate();
			} else {
				System.out.println("[ERROR] updating tournament failed: tournamentId = 0");
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
