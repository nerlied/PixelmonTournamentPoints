package ru.nerlied.tournamentpoints.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import ru.nerlied.tournamentpoints.Config;
import ru.nerlied.tournamentpoints.TournamentPoints;

public class DbPlayerStats extends DbTask {
	private CommandSource sender;
	
	public DbPlayerStats(CommandSource src) {
		this.sender = src;
	}
	
	@Override
	protected void process(Connection c) throws SQLException {
		TournamentPoints.LOG.info(this.getClass().getCanonicalName() + " process");
		PreparedStatement ps = null;
		ResultSet rs = null;
		
    	try {
    		String sql = String.format("SELECT * FROM `%s` WHERE `season` = ? ORDER BY `points` DESC LIMIT 10;", Config.dbTblPlayers);
    		ps = c.prepareStatement(sql);
    		ps.setInt(1, Config.season);
    		TournamentPoints.LOG.info("SQL > " + ps);
    		rs = ps.executeQuery();
    		int i = 1;
    		while(rs.next()) {
    			this.sender.sendMessage(Text.of(i + ". " + rs.getString("name") + " - " + rs.getInt("points") + " > tournaments: " + rs.getInt("tournaments_win") + " / " + rs.getInt("tournaments_total") + ", matches: " + rs.getInt("matches_win") + " / " + rs.getInt("matches_total")));
    			i++;
    		}
    		
    		if(sender instanceof Player) {
    			this.sender.sendMessage(Text.of("-------------------"));
        		this.sender.sendMessage(Text.of("Your Position:"));
        		
    			String player = sender.getName();
    			sql = String.format("SELECT * FROM `%s` WHERE `season` = ? AND `name` = ? LIMIT 1;", Config.dbTblPlayers);
        		ps = c.prepareStatement(sql);
        		ps.setInt(1, Config.season);
        		ps.setString(2, player);
        		TournamentPoints.LOG.info("SQL > " + ps);
        		rs = ps.executeQuery();
    			if(rs.next()) {
    				int points = rs.getInt("points");
    				int tWin = rs.getInt("tournaments_win");
    				int tTotal = rs.getInt("tournaments_total");
    				int mWin = rs.getInt("matches_win");
    				int mTotal = rs.getInt("matches_total");
    				int position = 1;
    				
    				sql = String.format("SELECT COUNT(*) AS `pos` FROM `%s` WHERE `season` = ? AND `points` >= ?;", Config.dbTblPlayers);
            		ps = c.prepareStatement(sql);
            		ps.setInt(1, Config.season);
            		ps.setInt(2, points);
            		TournamentPoints.LOG.info("SQL > " + ps);
            		rs = ps.executeQuery();
            		if(rs.next()) {
            			position = rs.getInt("pos");
            		}
            		
        			this.sender.sendMessage(Text.of(position + ". " + player + " - " + points + " > tournaments: " + tWin + " / " + tTotal + ", matches: " + mWin + " / " + mTotal));
    			} else {
    				int position = 1;
    				
    				sql = String.format("SELECT COUNT(*) AS `pos` FROM `%s` WHERE `season` = ?;", Config.dbTblPlayers);
            		ps = c.prepareStatement(sql);
            		ps.setInt(1, Config.season);
            		TournamentPoints.LOG.info("SQL > " + ps);
            		rs = ps.executeQuery();
            		if(rs.next()) {
            			position = rs.getInt("pos") + 1;
            		}
            		
        			this.sender.sendMessage(Text.of(position + ". " + player + " - 0 > tournaments: 0 / 0, matches: 0 / 0"));
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
