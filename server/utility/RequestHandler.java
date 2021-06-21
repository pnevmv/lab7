package server.utility;

import common.interaction.Request;
import common.interaction.Response;
import common.interaction.ResponseCode;
import common.interaction.User;
import server.commands.Command;

import java.util.List;
import java.util.concurrent.RecursiveTask;

/**
 * Handles requests.
 */
public class RequestHandler extends RecursiveTask<Response> {
    private CommandManager commandManager;
    private Request request;

    public RequestHandler(Request request, CommandManager commandManager) {
        this.request = request;
        this.commandManager = commandManager;
    }

    @Override
    protected Response compute() {
        User hashedUser = new User(
                request.getUser().getUsername(),
                PasswordHash.hashPassword(request.getUser().getPassword())
        );
        ResponseCode responseCode = executeCommand(request.getCommandName(), request.getCommandStringArgument(),
                request.getCommandObjectArgument(), hashedUser);
        return new Response(responseCode, ResponseOutputDeliver.getAndClear());
    }

    /**
     * Executes a command from a request.
     * @param command               Name of command.
     * @param commandStringArgument String argument for command.
     * @param commandObjectArgument Object argument for command.
     * @return Command execute status.
     */
    private ResponseCode executeCommand(String command, String commandStringArgument,
                                        Object commandObjectArgument, User user) {
        List<Command> commandList = commandManager.getCommands();

        if (command.equals("execute_script")) {
            if (!commandManager.executeScript(commandStringArgument, commandObjectArgument, user)) return ResponseCode.OK;
        }
        if (command.equals("help")) {
            commandManager.getAllCommands(commandStringArgument, commandObjectArgument, user);
            return ResponseCode.OK;
        }
        for (int i = 0; i < commandList.size(); i++) {
            String[] nowCommands = commandList.get(i).getName().split(" ");
            String now = nowCommands[0];
            if (now.equals(command)) {
                commandList.get(i).execute(commandStringArgument, commandObjectArgument, user);
                return ResponseCode.OK;
            }
        }
        return ResponseCode.OK;
    }
}