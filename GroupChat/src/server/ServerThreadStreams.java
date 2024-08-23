package server;

import java.io.*;
import java.net.Socket;

public class ServerThreadStreams {
    private final PrintWriter printWriter;
    private final BufferedReader reader;

    public ServerThreadStreams(Socket clientSocket) throws IOException {
        printWriter = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()), true);
        reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    }

    public void writeToClient(String message) {
        printWriter.println(message);
    }
    public String readFromClient() {
        try {
            return reader.readLine();
        }
        catch (IOException e) {
            return null;
        }
    }

    public void closeStreams() throws IOException {
        printWriter.close();
        reader.close();
    }

}
