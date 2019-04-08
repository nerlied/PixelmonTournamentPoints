package ru.nerlied.tournamentpoints.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import ru.nerlied.tournamentpoints.TPConfig;
import ru.nerlied.tournamentpoints.TournamentData;
import ru.nerlied.tournamentpoints.Utils;

public class DbTournamentStart extends DbTask {
	private TournamentData tData;
	
	public DbTournamentStart(TournamentData tData) {
		this.tData = tData;
	}
	
	@Override
	protected void process(Connection c) throws SQLException {
		if(TPConfig.INSTANCE.enableLog) System.out.println(this.getClass().getCanonicalName() + " process");
		TPConfig conf = TPConfig.INSTANCE;
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql;
		
    	try {
    		sql = "INSERT INTO `" + conf.dbTblTournaments + "`(`time_start`) VALUES ('" + Utils.getCurTime() + "')";
			if(TPConfig.INSTANCE.enableLog) System.out.println("SQL > " + sql);
			ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			int affectedRows = ps.executeUpdate();
			if (affectedRows == 0) {
				throw new SQLException("Creating player failed, no rows affected.");
			}

			try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
				if (generatedKeys.next()) {
					tData.tId = generatedKeys.getInt(1);
					System.out.println("Setting tounament id to " + tData.tId);
				} else {
					throw new SQLException("Creating player failed, no ID obtained.");
				}
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
