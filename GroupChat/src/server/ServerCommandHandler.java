package server;

import constants.Commands;

import java.io.IOException;
import java.net.Socket;

public class ServerCommandHandler {
    private final ServerThreadStreams serverThreadStreams;
    private final ClientInfo clientInfo;
    public ServerCommandHandler(Socket clientSocket, ClientInfo clientInfo) throws IOException {
        this.clientInfo = clientInfo;
        this.serverThreadStreams = new ServerThreadStreams(clientSocket);
    }

    public void writeToClient(String message) {
        serverThreadStreams.writeToClient(message);
    }

    public String readFromClient() {
        return serverThreadStreams.readFromClient();
    }

    private void sendRejectResponse(String reason) {
        writeToClient(Commands.SERVER_REJECT_COMMAND + Commands.COMMAND_REGEX + reason);
    }

    private void sendOkResponse(String message) {
        writeToClient(Commands.SERVER_OK_COMMAND + Commands.COMMAND_REGEX + message);
    }

    private ClientInfo findClientByUsername(String username) {
        for (ServerThread serverThread: Server.serverThreads) {
            ClientInfo clientInfo = serverThread.getClientInfo();

            if (clientInfo != null && clientInfo.getUsername().equals(username)){
                return clientInfo;
            }
        }

        return null;
    }

    private boolean doesUsernameExist(String username) {
        return findClientByUsername(username) != null;
    }

    private void handleClientLogin(String message) {
        if (message == null) {
            sendRejectResponse("Empty message received from client");
            return;
        }

        if (doesUsernameExist(message)) {
            sendRejectResponse("Username already exists");
        }
        else {
            clientInfo.setUsername(message);
            clientInfo.setHasEnteredChat(true);
            sendOkResponse("Login successful");
            String message1 = message + " has joined the chat!";
            String payload = Commands.CLIENT_JOIN_COMMAND + Commands.COMMAND_REGEX + message1;
            broadCastMessage(message, payload);
        }
    }

    private void broadCastMessageToClientSocket(ServerThread serverThread, String payload) {
        ServerCommandHandler serverCommandHandler1 = serverThread.getServerCommandHandler();

        if (serverCommandHandler1 == null) return;

        serverCommandHandler1.writeToClient(payload);
    }

    public void broadCastMessage (String username, String payload) {
        for (ServerThread thread: Server.serverThreads) {
            ClientInfo clientInfo1 = thread.getClientInfo();

            if(!clientInfo1.getUsername().equals(username) && clientInfo1.hasEnteredChat()) {
                broadCastMessageToClientSocket(thread, payload);
            }
        }
    }

    private void handleClientMessage (String clientMessage) {
        String username = clientInfo.getUsername();
        String message = username + Commands.PAYLOAD_REGEX + clientMessage;
        String payload = Commands.CLIENT_MESSAGE_COMMAND + Commands.COMMAND_REGEX + message;

        broadCastMessage(username, payload);
    }

    public void handleCommand(String message) {
        if (message == null) {
            System.out.println("Empty message is not a valid command");
            return;
        }

        String[] splitMessage = message.split(Commands.COMMAND_REGEX);

        if (splitMessage.length < 2) {
            System.out.println("Error while parsing command. Invalid command");
            return;
        }

        String command = splitMessage[0];
        String clientMessage = splitMessage[1];

        switch (command) {
            case Commands.CLIENT_LOGIN_COMMAND:
                handleClientLogin(clientMessage);
                break;
            case Commands.CLIENT_MESSAGE_COMMAND:
                handleClientMessage(clientMessage);
                break;
            default:
                System.out.println("This command is not supported by server");
        }

    }

    public void closeStreams() throws IOException {
        serverThreadStreams.closeStreams();
    }
}
