package client;

import common.interaction.Request;
import common.interaction.User;

import java.util.Scanner;

/**
 * Handle user login and password.
 */
public class AuthHandler {
    private final String loginCommand = "login";
    private final String registerCommand = "register";

    private Scanner userScanner;

    public AuthHandler(Scanner userScanner) {
        this.userScanner = userScanner;
    }

    /**
     * Handle user authentication.
     *
     * @return Request of user.
     */
    public Request take() {
        Authorizer authorizer = new Authorizer(userScanner);
        String command = authorizer.askQuestion("У вас уже есть учетная запись?") ? loginCommand : registerCommand;
        User user = new User(authorizer.askLogin(), authorizer.askPassword());
        return new Request(command, "", user);
    }
}
