package client;

import java.io.*;
import java.net.Socket;

public class ClientStreams {
    private final PrintWriter printWriter;
    private final BufferedReader reader;

    public ClientStreams(Socket serverSocket) throws IOException {
        printWriter = new PrintWriter(new OutputStreamWriter(serverSocket.getOutputStream()), true);
        reader = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));
    }

    public void writeToServer(String message) {
        printWriter.println(message);
    }
    public String readFromServer() {
        try {
            return reader.readLine();
        } catch (IOException e) {
            return null;
        }
    }

}
