package ru.nerlied.tournamentpoints;

import java.util.ArrayList;
import java.util.List;

import org.spongepowered.api.entity.living.player.User;

import com.hiroku.tournaments.api.events.match.MatchEndEvent;
import com.hiroku.tournaments.api.events.match.MatchStartEvent;
import com.hiroku.tournaments.api.events.round.RoundStartEvent;
import com.hiroku.tournaments.api.events.tournament.TournamentEndEvent;
import com.hiroku.tournaments.api.events.tournament.TournamentStartEvent;
import com.hiroku.tournaments.obj.Team;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import ru.nerlied.tournamentpoints.db.DbPlayerLoseMatch;
import ru.nerlied.tournamentpoints.db.DbPlayerWinMatch;
import ru.nerlied.tournamentpoints.db.DbPlayerWinTournament;
import ru.nerlied.tournamentpoints.db.DbTournamentEnd;
import ru.nerlied.tournamentpoints.db.DbTournamentStart;
import ru.nerlied.tournamentpoints.db.DbTournamentUpdate;

public class TournamentListener {
	private static final TournamentData tournament = new TournamentData();
	
	@SubscribeEvent
	public void onTournamentStart(TournamentStartEvent event) {
		if(TPConfig.INSTANCE.enableLog) System.out.println(this.getClass().getCanonicalName() + " TournamentStartEvent");

		tournament.clear();
		(new DbTournamentStart(tournament)).start();
	}
	
	@SubscribeEvent
	public void onRoundStart(RoundStartEvent event) {
		if(TPConfig.INSTANCE.enableLog) System.out.println(this.getClass().getCanonicalName() + " RoundStartEvent");
		tournament.tRoundNumber++;
	}
	
	@SubscribeEvent
	public void onMatchStart(MatchStartEvent event) {
		if(TPConfig.INSTANCE.enableLog) System.out.println(this.getClass().getCanonicalName() + " MatchStartEvent");
		tournament.tMatchNumber++;
	}
	
	@SubscribeEvent
	public void onMatchEnd(MatchEndEvent event) {
		if(TPConfig.INSTANCE.enableLog) System.out.println(this.getClass().getCanonicalName() + " MatchEndEvent");
		
		for(Team team : event.winningSide.teams) {
			for(User u : team.users) { 
				String playerName = u.getName();
				
				if(!tournament.tPlayers.contains(playerName)) {
					tournament.tPlayers.add(playerName);
				}
				
				if(TPConfig.INSTANCE.enableLog) System.out.println("Round win : " + playerName);
				
				//инсерт в бд с информацией о победе в раунде игрока playerName
				(new DbPlayerWinMatch(tournament, playerName)).start();				
			}
		}
		
		for(Team team : event.losingSide.teams) {
			for(User u : team.users) { 
				String playerName = u.getName();
				
				if(!tournament.tPlayers.contains(playerName)) {
					tournament.tPlayers.add(playerName);
				}
				
				if(TPConfig.INSTANCE.enableLog) System.out.println("Round lost : " + playerName);
				
				//инсерт в бд с информацией о победе в раунде игрока playerName
				(new DbPlayerLoseMatch(tournament, playerName)).start();				
			}
		}
		
		(new DbTournamentUpdate(tournament)).start();
	}
	
	@SubscribeEvent
	public void onTournamentEnd(TournamentEndEvent event) {
		if(TPConfig.INSTANCE.enableLog) System.out.println(this.getClass().getCanonicalName() + " TournamentEndEvent");

		List<String> winnersNames = new ArrayList<String>();
		for(User u : event.winners) {
			String playerName = u.getName();
			winnersNames.add(playerName);
			
			if(TPConfig.INSTANCE.enableLog) System.out.println("Tournament win : " + playerName);
			
			//инсерт в бд с информацией о победе в турнире игрока playerName
			(new DbPlayerWinTournament(tournament, playerName)).start();			
		}
		
		(new DbTournamentEnd(tournament, winnersNames)).start();
	}
}
