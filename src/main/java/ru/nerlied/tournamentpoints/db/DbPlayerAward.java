package ru.nerlied.tournamentpoints.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import ru.nerlied.tournamentpoints.AwardGiver;
import ru.nerlied.tournamentpoints.Config;
import ru.nerlied.tournamentpoints.TournamentAward;
import ru.nerlied.tournamentpoints.TournamentPoints;
import ru.nerlied.tournamentpoints.Utils;

public class DbPlayerAward extends DbTournamentTask {
	private String player;
	private int awardGiveId;
	
	public DbPlayerAward(String player, int awardGiveId) {
		this.player = player;
		this.awardGiveId = awardGiveId;
	}
	
	@Override
	protected void process(Connection c) throws SQLException {
		TournamentPoints.LOG.info(this.getClass().getCanonicalName() + " process");
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			String sql = String.format("SELECT `player`, `itemlist`, `time_given` FROM `%s` WHERE `id` = ?", Config.dbTblPlayerAwardGives);
			
			ps = c.prepareStatement(sql);
			ps.setInt(1, this.awardGiveId);
			
			TournamentPoints.LOG.info("SQL > " + sql);
			rs = ps.executeQuery();

			if(rs.next()) {
				if(rs.getString("player").equals(this.player)) {
					if(rs.getInt("time_given") == 0) {
						String items = rs.getString("itemlist");
						TournamentAward[] awardList = TournamentAward.createFromJson(items);
						
						//Проверка, все ли награды загрузились должным образом
						boolean allAwardsLoaded = true;
						for(int i = 0; i < awardList.length; i++) {
							if(awardList[i] == null) {
								allAwardsLoaded = false;
								TournamentPoints.LOG.warning("Error while giving award #" + this.awardGiveId + " to " + this.player + " (can't load #" + i + " in itemlist)");
								break;
							} else {
								awardList[i].player = this.player;
								
								if(awardList[i].isNull()) {
									allAwardsLoaded = false;
									TournamentPoints.LOG.warning("Error while giving award #" + this.awardGiveId + " to " + this.player + " (can't load #" + i + " in itemlist)");
									break;
								}
							}
						}
						
						if(allAwardsLoaded) {
							sql = String.format("UPDATE `%s` SET `time_given` = ? WHERE `id` = ?", Config.dbTblPlayerAwardGives);
							ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
							ps.setInt(1, Utils.getCurTime());
							ps.setInt(2, this.awardGiveId);
							int affectedRows = ps.executeUpdate();
							
							if(affectedRows != 1) {
								TournamentPoints.LOG.warning("Error while giving award #" + this.awardGiveId + " to " + this.player + " (affected rows " + affectedRows + ")");
							} else {
								for(TournamentAward a : awardList) {
									AwardGiver.awardsToGive.add(a);
								}
							}
						}
					} else {
						TournamentPoints.LOG.info("Player tried to take award that is already taken (" + player + ", " + this.awardGiveId + ")");
					}
				} else {
					TournamentPoints.LOG.info("Player tried to take award that does not belong to him (" + player + ", " + rs.getString("player") + ")");
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
