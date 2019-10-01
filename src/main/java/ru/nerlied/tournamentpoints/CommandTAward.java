package ru.nerlied.tournamentpoints;

import java.util.Optional;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import ru.nerlied.tournamentpoints.db.DbPlayerAward;

public class CommandTAward implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if(!(src instanceof Player)) {
			throw new CommandException(Text.of("Команда доступна только игрокам"));
		}
		
		Player p = (Player)src;
		
		Optional<Integer> awardGiveIdOpt = args.getOne("award_give_id");
		if(awardGiveIdOpt.isPresent()) {
			int awardGiveId = awardGiveIdOpt.get();
			(new DbPlayerAward(p.getName(), awardGiveId)).start();
		}
		
		return CommandResult.success();
	}
}
