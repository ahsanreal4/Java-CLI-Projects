package client;

import constants.ServerInfo;

public class ClientDriver {
    public static void main(String[] args) {
        new Client(ServerInfo.HOST, ServerInfo.PORT);
    }
}
