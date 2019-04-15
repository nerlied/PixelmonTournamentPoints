package ru.nerlied.tournamentpoints.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import ru.nerlied.tournamentpoints.Config;
import ru.nerlied.tournamentpoints.TournamentData;
import ru.nerlied.tournamentpoints.TournamentPoints;
import ru.nerlied.tournamentpoints.Utils;

public class DbTournamentStart extends DbTask {
	private TournamentData tData;
	
	public DbTournamentStart(TournamentData tData) {
		this.tData = tData;
	}
	
	@Override
	protected void process(Connection c) throws SQLException {
		TournamentPoints.LOG.info(this.getClass().getCanonicalName() + " process");
		PreparedStatement ps = null;
		String sql;
		
    	try {
    		sql = "INSERT INTO `" + Config.dbTblTournaments + "`(`season`, `time_start`) VALUES ('" + Config.season + "', '" + Utils.getCurTime() + "')";
			TournamentPoints.LOG.info("SQL > " + sql);
			ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			int affectedRows = ps.executeUpdate();
			if (affectedRows == 0) {
				throw new SQLException("Creating player failed, no rows affected.");
			}

			try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
				if (generatedKeys.next()) {
					tData.tId = generatedKeys.getInt(1);
					TournamentPoints.LOG.info("Setting tounament id to " + tData.tId);
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
