package client;

import constants.Commands;

import java.io.IOException;
import java.net.Socket;
import java.util.*;

public class ClientCommandHandler {
    private final ClientStreams clientStreams;
    private final Scanner scanner;

    public boolean isValidChoiceString(String input, String[] choices) {
        List<String> result = Arrays.stream(choices).filter(choice -> choice.equals(input)).toList();

        return !result.isEmpty();
    }

    public String getNextInput(String displayMessage) {
        System.out.println(displayMessage);
        return scanner.next();
    }

    public String forceUserCorrectCommand (String displayMessage, String[] choices){
        System.out.println(displayMessage);

        while (true) {
            String input = scanner.nextLine();

            if (input.trim().equals(Commands.CLIENT_HELP_FLAG)) {
                System.out.println(displayMessage);
            }
            else if(isValidChoiceString(input.trim(), choices)) {
                return input;
            }
            else {
                System.out.println("Invalid command. Enter --help to see the options again");
            }
        }
    }

    public ClientCommandHandler(Socket socket, Scanner scanner) throws IOException {
        this.clientStreams = new ClientStreams(socket);
        this.scanner = scanner;
    }

    public void writeToServer(String message) {
        clientStreams.writeToServer(message);
    }

    public String readFromServer() {
        return clientStreams.readFromServer();
    }
}
