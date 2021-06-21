package server;

import common.exceptions.DeclaredLimitException;
import common.exceptions.WrongAmountOfElementsException;
import common.utility.OutputDeliver;
import server.utility.*;
import server.commands.*;

/**
 * Main server class. Creates all server instances.
 * @author Smirnov Danil.
 */
public class ServerApp {
    public static int port;
    public static final int MAX_CLIENTS = 1000;
    private static String databaseUsername = "postgres";
    private static String databaseHost;
    private static String databasePassword;
    private static String databaseAddress;

    public static void main(String[] args) {
        if (!initialize(args)) return;
        DatabaseHandler databaseHandler = new DatabaseHandler(databaseAddress, databaseUsername, databasePassword);
        DatabaseUserManager databaseUserManager = new DatabaseUserManager(databaseHandler);
        DatabaseCollectionManager databaseCollectionManager = new DatabaseCollectionManager(databaseHandler, databaseUserManager);
        CollectionManager collectionManager = new CollectionManager(databaseCollectionManager);
        CommandManager commandManager = new CommandManager(
                new HelpCommand(),
                new InfoCommand(collectionManager),
                new ShowCommand(collectionManager),
                new InsertCommand(collectionManager, databaseCollectionManager),
                new UpdateCommand(collectionManager, databaseCollectionManager),
                new RemoveCommand(collectionManager, databaseCollectionManager),
                new ClearCommand(collectionManager, databaseCollectionManager),
                new ExecuteScriptCommand(),
                new ExitCommand(collectionManager),
                new RemoveLowerCommand(collectionManager, databaseCollectionManager),
                new ReplaceIfGreaterCommand(collectionManager, databaseCollectionManager),
                new RemoveLowerKeyCommand(collectionManager, databaseCollectionManager),
                new AverageOfNumberParticipantsCommand(collectionManager, databaseCollectionManager),
                new FilterCommand(collectionManager),
                new FieldsOfDescriptionsCommand(collectionManager),
                new LoginCommand(databaseUserManager),
                new RegisterCommand(databaseUserManager));
        Server server = new Server(port, MAX_CLIENTS, commandManager);
        server.run();
        databaseHandler.closeConnection();
    }

    /**
     * Controls initialization.
     */
    private static boolean initialize(String[] args) {
        try {
            if (args.length != 3) throw new WrongAmountOfElementsException();
            port = Integer.parseInt(args[0]);
            if (port < 0) throw new DeclaredLimitException();
            databaseHost = args[1];
            databasePassword = args[2];
            databaseAddress = "jdbc:postgresql://" + databaseHost + ":5432/postgres";
            return true;
        } catch (WrongAmountOfElementsException exception) {
            String jarName = new java.io.File(ServerApp.class.getProtectionDomain()
                    .getCodeSource()
                    .getLocation()
                    .getPath())
                    .getName();
            OutputDeliver.println("Использование: 'java -jar " + jarName + " <port> <db_host> <db_password>'");
        } catch (NumberFormatException exception) {
            OutputDeliver.printError("Порт должен быть представлен числом!");
        } catch (DeclaredLimitException exception) {
            OutputDeliver.printError("Порт не может быть отрицательным!");
        }
        return false;
    }
}
