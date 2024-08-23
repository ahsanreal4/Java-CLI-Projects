package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

public class Server {
    private ServerSocket serverSocket;
    public static ArrayList<ServerThread> serverThreads;
    public static int serverId;
    private final int maxConnections;

    static {
        serverThreads = new ArrayList<>();
        serverId = 1;
    }

    public Server(int port, int maxConnections) {
        this.maxConnections = maxConnections;
        initializeServer(port);
    }

    private void initializeServer(int port) {
        try {
            serverSocket = new ServerSocket(port);
            this.createServerThreads();
        } catch (IOException | IllegalArgumentException e) {
            System.out.println("Error when initializing server");
            System.out.println(e.getMessage());
        }
    }

    @SuppressWarnings("InfiniteLoopStatement")
    private void createServerThreads() {
        int SLEEP_TIME = 2000;

        while (true) {
            ArrayList<ServerThread> freeThreads = getFreeServerThreads();
            int freeServerThreads = freeThreads.size();

            // If there are no free server threads then create one
            // We don't need to keep multiple free server threads waiting, keep only one free
            if (!isServerFull() && freeServerThreads == 0) {
                ServerThread serverThread = new ServerThread(serverSocket, serverId);
                addServerThread(serverThread);
                serverThread.start();
                serverId++;
            }
            else if (freeServerThreads > 0) {
                restartTerminatedThreads(freeThreads);
            }

            try {
                Thread.sleep(SLEEP_TIME);
            } catch (InterruptedException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private void addServerThread(ServerThread serverThread) {
        if(isServerFull()) {
            System.out.println("Max connection limit reached!");
            return;
        }

        serverThreads.add(serverThread);
    }

    private void reAssignThreadById(int threadId) {
        for (int i = 0; i < serverThreads.size(); i++) {
            ServerThread thread = serverThreads.get(i);

            if (thread.getServerId() == threadId) {
                thread = null;
                serverThreads.set(i, null);
                System.gc();

                thread = new ServerThread(serverSocket, threadId);
                serverThreads.set(i, thread);
                thread.start();
                break;
            }
        }
    }

    private void restartTerminatedThreads(ArrayList<ServerThread> threads) {
        for (ServerThread thread: threads) {
            if (thread.getState().equals(Thread.State.TERMINATED)) {
                reAssignThreadById(thread.getServerId());
            }
        }
    }

    private boolean isServerFull() {
        return getServerThreadsSize() >= maxConnections;
    }

    private int getServerThreadsSize() {
        return serverThreads.size();
    }

    // Returns instances of server threads listening for clients
    private ArrayList<ServerThread> getFreeServerThreads() {
        ArrayList<ServerThread> threads = new ArrayList<>();

        for (ServerThread thread : serverThreads) {
            // If client is not connected then it means that serverThread is free
            if (!thread.isClientConnected()) {
                threads.add(thread);
            }
        }

        return threads;
    }

}
