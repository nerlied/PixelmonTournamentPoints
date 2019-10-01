package ru.nerlied.tournamentpoints;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
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
@Plugin(id = TournamentPoints.MOD_ID, name = "TournamentPoints", version = "@VERSION@", 
description = "Player Statistics for Tournaments (Pixelmon sidemod)", 
authors = {"Nerlied"},
dependencies = { 
	@Dependency(id = "pixelmon"),
	@Dependency(id = "tournaments") ,
	@Dependency(id = "placeholderapi", optional = true)
})
public class TournamentPoints {
	public static final String MOD_ID = "tournamentpoints";
	
	public static TournamentPoints INSTANCE;
	
	public static Logger LOG;

	@Listener
	public void onGameInit(GameInitializationEvent event) {
		INSTANCE = this;
		LOG = Logger.getLogger(this.getClass().getName());
		LOG.setLevel(Level.INFO);
		
		Tournaments.EVENT_BUS.register(new TournamentListener());
		
		Config.load();
		
		Sponge.getScheduler().createSyncExecutor(TournamentPoints.INSTANCE).scheduleAtFixedRate(() -> {
			AwardGiver. giveAwards();
        }, (long)5000, (long)Config.periodAwardGivesCheck, TimeUnit.MILLISECONDS);
		
		CommandSpec commandTPoints = CommandSpec.builder()
				.description(Text.of("Pixelmon Tournament Table"))
				.permission("tournamentpoints.command.tpoints")
				.executor((CommandSource src, CommandContext args) -> {
				  	(new DbPlayerStats(src)).start();
				  	return CommandResult.success();
				})
				.build();

		CommandSpec commandTAward = CommandSpec.builder()
				.description(Text.of("Pixelmon Tournament Award"))
				.permission("tournamentpoints.command.taward")
				.arguments(
			    		GenericArguments.integer(Text.of("award_give_id"))
			    )
				.executor(new CommandTAward())
				.build();
		
		Sponge.getCommandManager().register(this, commandTPoints, "tpoints");
		Sponge.getCommandManager().register(this, commandTAward, "taward");
		
		LOG.info("GameInit");
	}
}
