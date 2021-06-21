package server.commands;

import common.interaction.User;

/**
 * Interface for all commands.
 */
public interface Command {
    String getDescription();
    String getName();
    boolean execute(String stringArgument, Object objectArgument, User user);
}