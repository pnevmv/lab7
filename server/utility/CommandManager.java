package server.utility;

import common.interaction.User;
import server.commands.Command;
import server.commands.ExecuteScriptCommand;
import server.commands.HelpCommand;

import java.util.ArrayList;
import java.util.List;

/**
 * Operates the commands.
 */
public class CommandManager {
    private List<Command> commands = new ArrayList<>();
    private ExecuteScriptCommand executeScriptCommand;

    public CommandManager(Command... commands1) {
        for (int i = 0; i < commands1.length; i++) {
            if (commands1[i] instanceof ExecuteScriptCommand) {
                this.executeScriptCommand = (ExecuteScriptCommand) commands1[i];
                continue;
            }
            commands.add(commands1[i]);
        }
    }

    /**
     * Prints that command is not found.
     * @param command Command, which is not found.
     * @return Command exit status.
     */
    public boolean noSuchCommand(String command) {
        ResponseOutputDeliver.appendLn("Command '" + command + "' not found. Type 'help' for help.");
        return false;
    }

    /**
     * Prints info about the all commands.
     * @param argument It's argument.
     * @return Command exit status.
     */
    public boolean getAllCommands(String argument, Object objectArg, User user) {
        HelpCommand helpCommand = new HelpCommand();
        if (helpCommand.execute(argument, objectArg, user)) {
            for (Command command: commands) {
                if (command.getDescription().equals("Inner command")) continue;
                ResponseOutputDeliver.appendable(command.getName(), command.getDescription());
            }
            ResponseOutputDeliver.appendable(executeScriptCommand.getName(), executeScriptCommand.getDescription());
            return true;
        }
        else return false;
    }

    /**
     * Executes needed command.
     * @param argument It's argument.
     * @return Command exit status.
     */
    public boolean executeScript(String argument, Object objectArg, User user) {
        return executeScriptCommand.execute(argument, objectArg, user);
    }

    public List<Command> getCommands() {
        return this.commands;
    }

    @Override
    public String toString() {
        return "CommandManager (helper class for working with commands)";
    }
}