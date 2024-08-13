package broadcast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {
	private final String HOST = "127.0.0.1";
	
	private Socket socket;
	private DataInputStream inputStream;
	private DataOutputStream outputStream;
	private Scanner scanner;
	private Thread readThread;
	
	private volatile boolean isConnectedToServer = true;
	
	public Client(int port, Scanner scanner) {		
		this.scanner = scanner;
		connectToServer(port);
	}
	
	public boolean isAlive() {
		if (socket == null) return false;

		return !socket.isClosed();
	}
	
	private void closeClient(String errorMessage) {
		System.out.println(errorMessage);
		System.out.println("Closing client...");
	}
	
	private void handleDisconnect() {
		try {
			isConnectedToServer = false;
			inputStream.close();
			outputStream.close();
			socket.close();
			readThread = null;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void readMessages() {				
		while (this.isAlive()) {
			try {
				String lineString = inputStream.readUTF();
				System.out.println(lineString);
				
				if (lineString.trim().equalsIgnoreCase(Server.SERVER_MAX_CONNECTION_MESSAGE)) {
					handleDisconnect();
					break;
				}
			} catch (IOException e) {
				// Do nothing	
			}
		}
	}
	
	private void writeMessages() {		
		final int TEST_CONNECTION_SLEEP_TIME = 200;
		
		try {
			Thread.sleep(TEST_CONNECTION_SLEEP_TIME);
			if (isConnectedToServer == false) {
				connectionRefusedMessage();
				return;
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		
		String line = "";
		
		System.out.println("Type your message");
		while (!line.equalsIgnoreCase(ConnectedClient.STOP_DATA_SEND_COMMAND)) {
			line = scanner.nextLine();
			
			if (line.trim().length() == 0) {
				System.out.println("Cannot send empty messages\n");
				continue;
			}
			
			try {
				outputStream.writeUTF(line.trim());
			} catch (IOException e) {
				System.out.println("Server went offline");
				break;
			}
		}
		
		System.out.println("Disconnected from server");
		
		handleDisconnect();
	}
	
	private void connectionRefusedMessage() {
		System.out.println("Server refused the connection\nMessage: Server can't handle more connections at the moment");
	}
	
	private void connectToServer(int port) {		
		try {
			socket = new Socket(HOST, port);

			if (!socket.isClosed()) {
				outputStream = new DataOutputStream(socket.getOutputStream());
				inputStream = new DataInputStream(socket.getInputStream());
				System.out.println("Connected!");
				
				readThread = new Thread(() -> {
					readMessages();
				});
				readThread.start();
				writeMessages();
			}
			else {
				connectionRefusedMessage();
			}
		}
		catch (UnknownHostException e) {
			closeClient(e.getMessage());
		} catch (IOException e) {
			closeClient(e.getMessage());
		}
	}
}
