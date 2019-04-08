package ru.nerlied.tournamentpoints;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;

import ru.nerlied.tournamentpoints.db.DbPlayerStats;

public class CommandTPoints implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        (new DbPlayerStats(src)).start();
        return CommandResult.success();
    }
}
