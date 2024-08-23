package client;

import constants.Commands;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private ClientCommandHandler commandHandler;
    private Scanner scanner;

    public Client(String host, int port) {
        connectClient(host, port);
    }

    private void closeConnection() {
        System.out.println("Connection closed");
        System.exit(0);
    }

    private String createCommand (String command, String message) {
        return command + Commands.COMMAND_REGEX + message;
    }

    private boolean isConnectedToServer() {
        String message = commandHandler.readFromServer();

        return message != null && message.equals(Commands.PING_COMMAND);
    }

    private void readMessagesFromOtherClients () {
        while (true) {
            String message = commandHandler.readFromServer();

            if(message == null) {
                closeConnection();
            }

            String[] messageSplit = message.split(Commands.COMMAND_REGEX);

            if (messageSplit.length < 2) {
                System.out.println("Invalid command from server");
                System.out.println(message);
                return;
            }

            String command = messageSplit[0];

            if (command.equals(Commands.CLIENT_JOIN_COMMAND) || command.equals(Commands.CLIENT_LEAVE_COMMAND)) {
                System.out.println(messageSplit[1]);
                continue;
            }

            String[] serverMessageSplit = messageSplit[1].split(Commands.PAYLOAD_REGEX);
            if (serverMessageSplit.length < 2) {
                System.out.println("Invalid payload from server");
                return;
            }

            String username = serverMessageSplit[0];
            String clientMessage = serverMessageSplit[1];
            System.out.println(username + " : " + clientMessage);
        }
    }

    private void chat() {
        scanner.nextLine();
        new Thread(this::readMessagesFromOtherClients).start();
        System.out.println("Start sending messages by typing and pressing enter!");
        System.out.println("To exit the chat enter " + Commands.STOP_COMMAND);

        while (true) {
            String line = scanner.nextLine();

            if (line.equalsIgnoreCase(Commands.STOP_COMMAND)) {
                closeConnection();
                break;
            }

            if (line.trim().isEmpty()) {
                System.out.println("Cannot send empty message");
                continue;
            }

            String command = Commands.CLIENT_MESSAGE_COMMAND + Commands.COMMAND_REGEX + line.trim();
            commandHandler.writeToServer(command);
        }
    }

    private String createAuthenticationPayload () {
        return commandHandler.getNextInput("Enter username");
    }

    private void loginUser() {
        while (true) {
            String payload = createAuthenticationPayload();

            commandHandler.writeToServer(createCommand(Commands.CLIENT_LOGIN_COMMAND, payload));

            String serverResponse = commandHandler.readFromServer();

            if (serverResponse == null) {
                closeConnection();
                return;
            }

            String[] parsedMessage = serverResponse.split(Commands.COMMAND_REGEX);

            if (parsedMessage.length < 2) return;

            String command = parsedMessage[0];
            String message = parsedMessage[1];

            if (command.equals(Commands.SERVER_REJECT_COMMAND)) {
                System.out.println("Server rejected the login request.");
                System.out.println("Reason: " + message);

                String displayMessage = "1-Try again\n2-Go back to main menu";
                String[] choices = {"1", "2"};

                scanner.nextLine();
                String input = commandHandler.forceUserCorrectCommand(displayMessage, choices);

                if (input.equals("2")) {
                    break;
                }
            }
            else {
                System.out.println("You are logged in the chat");
                chat();
                break;
            }

        }
    }

    private void authenticateUser() {
        while (true) {
            String displayMessage = "1-Login\n2-Exit Program";
            String[] choices = {"1", "2"};
            String input = commandHandler.forceUserCorrectCommand(displayMessage, choices);

            if (input.equals("1")) {
                loginUser();
            }
            else {
                closeConnection();
            }
        }
    }

    private void connectClient(String host, int port) {
        try {
            Socket socket = new Socket(host, port);
            scanner = new Scanner(System.in);
            commandHandler = new ClientCommandHandler(socket, scanner);

            if (isConnectedToServer()) {
                System.out.println("Client Connected");

                commandHandler.writeToServer(Commands.PING_COMMAND);
                authenticateUser();
            }
            else {
                System.out.println("Client could not connect");
            }
        } catch (IOException | IllegalArgumentException e) {
            System.out.println("Error when connecting client");
            System.out.println(e.getMessage());
        }
    }
}
