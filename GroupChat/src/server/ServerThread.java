package server;

import constants.Commands;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerThread extends Thread {
    private Socket clientSocket;
    private final ServerSocket serverSocket;
    private final int id;
    private volatile boolean isConnected;
    private ServerCommandHandler serverCommandHandler;
    private final ClientInfo clientInfo;

    public ServerThread(ServerSocket serverSocket, int id) {
        this.serverSocket = serverSocket;
        this.id = id;
        this.isConnected = false;
        clientInfo = new ClientInfo();
    }

    public int getServerId() {
        return id;
    }

    private void printServerMessage (String message) {
        System.out.println("SERVER " + this.getServerId() + ": " + message);
    }

    private void closeClientConnection() {
        try {
            printServerMessage("Closing server thread");
            serverCommandHandler.closeStreams();
            clientSocket.close();
            this.isConnected = false;

            if (clientInfo == null) return;

            clientInfo.setHasEnteredChat(false);
            String message = clientInfo.getUsername() + " has left the chat!";
            String payload = Commands.CLIENT_LEAVE_COMMAND + Commands.COMMAND_REGEX + message;
            serverCommandHandler.broadCastMessage(clientInfo.getUsername(), payload);

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private boolean isConnectedToClient() {
        String message = serverCommandHandler.readFromClient();

        return message != null && message.equals(Commands.PING_COMMAND);
    }

    private void readClientMessages() {
        while (true) {
                if (!this.isConnected) break;

                String message = serverCommandHandler.readFromClient();

                if (message == null) {
                    printServerMessage("Connection closed with client");
                    closeClientConnection();
                    break;
                }

                serverCommandHandler.handleCommand(message);
        }
    }

    private void listenForClients() {
        printServerMessage("Listening for clients");

        try {
            clientSocket = serverSocket.accept();
            this.serverCommandHandler = new ServerCommandHandler(clientSocket, clientInfo);

            serverCommandHandler.writeToClient(Commands.PING_COMMAND);
            if (isConnectedToClient()) {
                this.isConnected = true;
                printServerMessage("Connected to client successfully");
                readClientMessages();
            }
            else {
                printServerMessage("Connection failure from Client side");
                closeClientConnection();
            }

        } catch (IOException e) {
            printServerMessage("Error when listening for client");
            System.out.println(e.getMessage());
            closeClientConnection();
        }
    }

    public ClientInfo getClientInfo() {
        return this.clientInfo;
    }

    @Override
    public void run() {
        listenForClients();
    }

    public boolean isClientConnected() {
        return isConnected;
    }

    public ServerCommandHandler getServerCommandHandler() {
        return serverCommandHandler;
    }

}
