package client;

import common.exceptions.DeclaredLimitException;
import common.exceptions.MustBeNotEmptyException;
import common.utility.OutputDeliver;

import java.util.NoSuchElementException;
import java.util.Scanner;

public class Authorizer {
    private Scanner userScanner;

    public Authorizer(Scanner scanner) {
        this.userScanner = scanner;
    }

    /**
     * Asks user a login.
     * @return login.
     */
    public String askLogin() {
        String login;
        while (true) {
            try {
                OutputDeliver.print("Введите логин:" + "\n" + ClientApp.PS2);
                login = userScanner.nextLine().trim();
                if (login.equals("")) throw new MustBeNotEmptyException();
                break;
            } catch (MustBeNotEmptyException exception) {
                OutputDeliver.printError("Имя не может быть пустым!");
            } catch (IllegalStateException exception) {
                OutputDeliver.printError("Непредвиденная ошибка!");
            } catch (NoSuchElementException exception) {
                OutputDeliver.printError("Данного логина не существует!");
            }
        }
        return login;
    }

    /**
     * Asks user a login.
     * @return password.
     */
    public String askPassword() {
        String password;
        while (true) {
            try {
                OutputDeliver.print("Введите пароль:" + "\n" + ClientApp.PS2);
                password = userScanner.nextLine().trim();
                break;
            } catch (NoSuchElementException exception) {
                OutputDeliver.printError("Неверный логин или пароль!");
            } catch (IllegalStateException exception) {
                OutputDeliver.printError("Непредвиденная ошибка!");
                System.exit(0);
            }
        }
        return password;
    }


    /**
     * Asks a user a question.
     *
     * @param question A question.
     * @return Answer (true/false).
     */
    public boolean askQuestion(String question) {
        String finalQuestion = question + " (+/-):";
        String answer;
        while (true) {
            try {
                OutputDeliver.println(finalQuestion);
                OutputDeliver.print(ClientApp.PS2);
                answer = userScanner.nextLine().trim();
                if (!answer.equals("+") && !answer.equals("-")) throw new DeclaredLimitException();
                break;
            } catch (NoSuchElementException exception) {
                OutputDeliver.printError("Ответ не распознан!");
            } catch (DeclaredLimitException exception) {
                OutputDeliver.printError("Ответ должен быть представлен знаками '+' или '-'!");
            } catch (IllegalStateException exception) {
                OutputDeliver.printError("Непредвиденная ошибка!");
                System.exit(0);
            }
        }
        return answer.equals("+");
    }
}
