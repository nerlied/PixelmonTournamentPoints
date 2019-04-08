package ru.nerlied.tournamentpoints.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import ru.nerlied.tournamentpoints.TPConfig;
import ru.nerlied.tournamentpoints.TournamentData;
import ru.nerlied.tournamentpoints.Utils;

public class DbPlayerStatLog extends DbTask {
	private TournamentData tData;
	private String player;
	private String action;
	private String data;
	
	public DbPlayerStatLog(TournamentData tData, String player, String action, String data) {
		this.tData = tData;
		this.player = player;
		this.action = action;
		this.data = data;
	}

	@Override
	protected void process(Connection c) throws SQLException {
		if(TPConfig.INSTANCE.enableLog) System.out.println(this.getClass().getCanonicalName() + " process");
		TPConfig conf = TPConfig.INSTANCE;
		PreparedStatement ps = null;
		
    	try {
    		String sql = "INSERT INTO `" + conf.dbTblPlayerStatLog + "`(`tournament_id`, `tournament_round`, `tournament_match`, `player`, `action`, `data`, `time`) VALUES ('" + tData.tId + "', '" + tData.tRoundNumber + "', '" + tData.tMatchNumber + "', '" + this.player + "', '" + this.action + "', '" + this.data + "', '" + Utils.getCurTime() + "')";
    		if(TPConfig.INSTANCE.enableLog) System.out.println("SQL > " + sql);
    		ps = c.prepareStatement(sql);
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