package server;

import constants.ServerInfo;

public class ServerDriver {
    public static void main(String[] args) {
        new Server(ServerInfo.PORT, ServerInfo.MAX_CONNECTIONS);
    }
}
