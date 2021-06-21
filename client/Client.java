package client;

import common.exceptions.ConnectionErrorException;
import common.exceptions.DeclaredLimitException;
import common.interaction.Request;
import common.interaction.Response;
import common.interaction.ResponseCode;
import common.interaction.User;
import common.utility.OutputDeliver;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

/**
 * Run the client.
 */
public class Client {
    private String host;
    private int port;
    private int reconnectionTimeout;
    private int reconnectionAttempts;
    private int maxReconnectionAttempts;
    private UserHandler userHandler;
    private SocketChannel socketChannel;
    private ObjectOutputStream serverWriter;
    private ObjectInputStream serverReader;
    private AuthHandler authHandler;
    private User user;

    public Client(String host, int port, int reconnectionTimeOut, int maxReconnectionAttempts, UserHandler userHandler, AuthHandler authHandler) {
        this.host = host;
        this.port = port;
        this.reconnectionTimeout = reconnectionTimeOut;
        this.maxReconnectionAttempts = maxReconnectionAttempts;
        this.userHandler = userHandler;
        this.authHandler = authHandler;
    }
    /**
     * Starts client operation.
     */
    public void run() {
        try {
            while (true) {
                try {
                    connectToServer();
                    processAuthentication();
                    processRequestToServer();
                    break;
                } catch (ConnectionErrorException exception) {
                    if (reconnectionAttempts >= maxReconnectionAttempts) {
                        OutputDeliver.printError("Превышено количество попыток подключения!");
                        break;
                    }
                    try {
                        Thread.sleep(reconnectionTimeout);
                    } catch (IllegalArgumentException timeoutException) {
                        OutputDeliver.printError("Время ожидания подключения '" + reconnectionTimeout +
                                "' находится за пределами возможных значений!");
                        OutputDeliver.println("Повторное подключение будет произведено немедленно.");
                    } catch (Exception timeoutException) {
                        OutputDeliver.printError("Произошла ошибка при попытке ожидания подключения!");
                        OutputDeliver.println("Повторное подключение будет произведено немедленно.");
                    }
                }
                reconnectionAttempts++;
            }
            if (socketChannel != null) socketChannel.close();
            OutputDeliver.println("Работа клиента завершена.");
        } catch (DeclaredLimitException exception) {
            OutputDeliver.printError("Клиент не может быть запущен!");
        } catch (IOException exception) {
            OutputDeliver.printError("Произошла ошибка при попытке завершить соединение с сервером!");
        }
    }

    /**
     * Connecting to server.
     */
    private void connectToServer() throws ConnectionErrorException, DeclaredLimitException {
        try {
            if (reconnectionAttempts >= 1) OutputDeliver.println("Повторное соединение с сервером...");
            socketChannel = SocketChannel.open(new InetSocketAddress(host, port));
            OutputDeliver.println("Соединение с сервером успешно установлено.");
            OutputDeliver.println("Ожидание разрешения на обмен данными...");
            serverWriter = new ObjectOutputStream(socketChannel.socket().getOutputStream());
            serverReader = new ObjectInputStream(socketChannel.socket().getInputStream());
            OutputDeliver.println("Разрешение на обмен данными получено.");
        } catch (IllegalArgumentException exception) {
            OutputDeliver.printError("Адрес сервера введен некорректно!");
            throw new DeclaredLimitException();
        } catch (IOException exception) {
            OutputDeliver.printError("Произошла ошибка при соединении с сервером!");
            throw new ConnectionErrorException();
        }
    }

    /**
     * Server request process.
     */
    private boolean processRequestToServer() {
        Request requestToServer = null;
        Response serverResponse = null;
        do {
            try {
                requestToServer = serverResponse != null ? userHandler.handle(serverResponse.getResponseCode(), user) :
                        userHandler.handle(null, user);
                if (requestToServer.isEmpty()) continue;
                serverWriter.writeObject(requestToServer);
                serverResponse = (Response) serverReader.readObject();
                OutputDeliver.print(serverResponse.getResponseBody());
            } catch (InvalidClassException exception) {
                OutputDeliver.printError("Произошла ошибка при отправке данных на сервер!");
            } catch (NotSerializableException exception) {
                System.out.println("Не удалось сериализовать");
            } catch (ClassNotFoundException exception) {
                OutputDeliver.printError("Произошла ошибка при чтении полученных данных!");
            } catch (IOException exception) {
                OutputDeliver.printError("Соединение с сервером разорвано!");
                exception.printStackTrace();
                try {
                    reconnectionAttempts++;
                    connectToServer();
                } catch (ConnectionErrorException | DeclaredLimitException reconnectionException) {
                    if (requestToServer.getCommandName().equals("exit"))
                        OutputDeliver.println("Команда не будет зарегистрирована на сервере.");
                    else OutputDeliver.println("Попробуйте повторить команду позднее.");
                }
            }
        } while (!requestToServer.getCommandName().equals("exit"));
        return false;
    }


    /**
     * Handle process authentication.
     */
    private void processAuthentication() {
        Request requestToServer = null;
        Response serverResponse = null;
        do {
            try {
                requestToServer = authHandler.take();
                if (requestToServer.isEmpty()) continue;
                serverWriter.writeObject(requestToServer);
                serverResponse = (Response) serverReader.readObject();
                OutputDeliver.print(serverResponse.getResponseBody());
            } catch (InvalidClassException | NotSerializableException exception) {
                OutputDeliver.printError("Произошла ошибка при отправке данных на сервер!");
            } catch (ClassNotFoundException exception) {
                OutputDeliver.printError("Произошла ошибка при чтении полученных данных!");
            } catch (IOException exception) {
                OutputDeliver.printError("Соединение с сервером разорвано!");
                try {
                    connectToServer();
                } catch (ConnectionErrorException | DeclaredLimitException reconnectionException) {
                    OutputDeliver.println("Попробуйте повторить авторизацию позднее.");
                }
            }
        } while (serverResponse == null || !serverResponse.getResponseCode().equals(ResponseCode.OK));
        user = requestToServer.getUser();
    }
}
