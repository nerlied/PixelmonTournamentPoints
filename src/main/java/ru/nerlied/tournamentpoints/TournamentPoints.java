package ru.nerlied.tournamentpoints;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.Text;

import com.hiroku.tournaments.Tournaments;

import ru.nerlied.tournamentpoints.db.DbPlayerStats;

/***
 * 
 * Турнирная таблица, за каждую победу в матче начислять очки, за каждую победу в турнире начислять очки
 * @author Nerlied
 *
 */
@Plugin(id = "tournamentpoints", name = "TournamentPoints", version = "@VERSION@", 
description = "Player Statistics for Tournaments (Pixelmon sidemod)", 
authors = {"Nerlied"},
dependencies = { 
	@Dependency(id = "pixelmon"),
	@Dependency(id = "tournaments") ,
	@Dependency(id = "placeholderapi", optional = true)
})
public class TournamentPoints {
	public static TournamentPoints INSTANCE;
	
	public static Logger LOG;

	@Listener
	public void onGameInit(GameInitializationEvent event) {
		INSTANCE = this;
		LOG = Logger.getLogger(this.getClass().getName());
		LOG.setLevel(Level.INFO);
		
		Tournaments.EVENT_BUS.register(new TournamentListener());
		
		Config.load();
		
		CommandSpec commandTPoints = CommandSpec.builder()
				.description(Text.of("Pixelmon Tournament Table"))
				.permission("tournamentpoints.command.tpoints")
				.executor((CommandSource src, CommandContext args) -> {
				  	(new DbPlayerStats(src)).start();
				  	return CommandResult.success();
				})
				.build();

		Sponge.getCommandManager().register(this, commandTPoints, "tpoints");
	
		LOG.info("GameInit");
	}
}
