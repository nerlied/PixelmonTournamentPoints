package ru.nerlied.tournamentpoints;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.Text;

import com.hiroku.tournaments.Tournaments;

/***
 * 
 * Турнирная таблица, за каждую победу в матче начислять очки, за каждую победу в турнире начислять очки
 * @author Nerlied
 *
 */
@Plugin(id = "tournamentpoints", name = "TournamentPoints", version = "0.0.5", 
description = "Player Statistics for Tournaments (Pixelmon sidemod)", 
authors = {"Nerlied"},
dependencies = { 
	@Dependency(id = "pixelmon"),
	@Dependency(id = "tournaments") ,
	@Dependency(id = "placeholderapi", optional = true)
})
public class TournamentPoints {
	public static TournamentPoints INSTANCE;
		
	@Listener
	public void onGameInit(GameInitializationEvent event) {
		INSTANCE = this;
		
		Tournaments.EVENT_BUS.register(new TournamentListener());
		
		TPConfig.load();
		
		CommandSpec commandTPoints = CommandSpec.builder()
			    .description(Text.of("Pixelmon Tournament Table"))
			    .permission("tournamentpoints.command.tpoints")
			    .executor(new CommandTPoints())
			    .build();

		Sponge.getCommandManager().register(this, commandTPoints, "tpoints");
	
		if(TPConfig.INSTANCE.enableLog) System.out.println("GameInit");
	}
}
