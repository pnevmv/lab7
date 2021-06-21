package client;

import common.exceptions.DeclaredLimitException;
import common.exceptions.WrongAmountOfElementsException;
import common.utility.OutputDeliver;

import java.util.Scanner;

/**
 * Main client class. Creates all client instances.
 * @author Smirnov Danil.
 */
public class ClientApp {
    public static final String PS1 = ">>> ";
    public static final String PS2 = "=>> ";

    private static final int RECONNECTION_TIMEOUT = 5 * 1000;
    private static final int MAX_RECONNECTION_ATTEMPTS = 5;

    private static String host;
    private static int port;

    public static void main(String[] args) {
        if (!initialize(args)) return;
        Scanner userScanner = new Scanner(System.in);
        AuthHandler authHandler = new AuthHandler(userScanner);
        UserHandler userHandler = new UserHandler(userScanner);
        Client client = new Client(host, port, RECONNECTION_TIMEOUT, MAX_RECONNECTION_ATTEMPTS, userHandler, authHandler);
        client.run();
        userScanner.close();
    }

    /**
     * Controls initialization.
     */
    private static boolean initialize(String[] args) {
        try {
            if (args.length != 2) throw new WrongAmountOfElementsException();
            host = args[0];
            port = Integer.parseInt(args[1]);
            if (port < 0) throw new DeclaredLimitException();
            return true;
        } catch (WrongAmountOfElementsException exception) {
            String jarName = new java.io.File(ClientApp.class.getProtectionDomain()
                    .getCodeSource()
                    .getLocation()
                    .getPath())
                    .getName();
            OutputDeliver.println("Использование: 'java -jar " + jarName + " <host> <port>'");
        } catch (NumberFormatException exception) {
            OutputDeliver.printError("Порт должен быть представлен числом!");
        } catch (DeclaredLimitException exception) {
            OutputDeliver.printError("Порт не может быть отрицательным!");
        }
        return false;
    }
}
