package broadcast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {
	private final int MAX_CONNECTIONS = 5;
	
	private static int CLIENT_ID = 1;
	public static String SERVER_MAX_CONNECTION_MESSAGE = "SERVER: Maximum connections limit reached";
	
	private ArrayList<ConnectedClient> connectedClients;
	private ServerSocket serverSocket;

	public Server(int port) {
		connectedClients = new ArrayList<ConnectedClient>();
		this.initializeServer(port);
	}
	
	public boolean isAlive() {
		if (serverSocket == null) return false;
		
		return !this.serverSocket.isClosed();
	}
	
	private boolean isServerFull() {
		return this.connectedClients.size() >= MAX_CONNECTIONS;
	}
	
	private void shutdownServer(String errorMessage) {
		System.out.println(errorMessage);
		System.out.println("Shutting down server...");
	}
	
	private void initializeServer(int port) {
		try {
			serverSocket = new ServerSocket(port);
			System.out.println("Server Initialized");
			startServer();
		}
		catch (IllegalArgumentException e) {
			shutdownServer("Illegal argument passed");
		} 
		catch (IOException e) {
			shutdownServer("Server already exists");
		}
	}
	
	private void writeServerFullMessageToClient(Socket socket) {
		DataOutputStream currOutputStream = null;
		
		try {
			currOutputStream = new DataOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if (currOutputStream == null) return;
		
		try {
			currOutputStream.writeUTF(SERVER_MAX_CONNECTION_MESSAGE);
			currOutputStream.close();
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void startServer() {		
		boolean showServerFullMessage = false;
		
		try {
			while (true) {
				if (isServerFull()) {
					if (showServerFullMessage) {
						System.out.println("Connection limit reached, Server can't accept any more connections");
						showServerFullMessage = false;
					}
				}
				
				System.out.println("Listening for clients");
				
				showServerFullMessage = true;
				
				Socket clientSocket = serverSocket.accept();
				if (isServerFull()) {
					writeServerFullMessageToClient(clientSocket);
				}
				else if (clientSocket.isConnected()) {
					addConnectedClient(clientSocket);
				}
			}
		}
		catch (IOException e) {
			System.out.println("Server error");
			e.printStackTrace();
		}
	
	}
	
	private void writeMessageToClient(ConnectedClient client, String message) {		
		try {
			DataOutputStream outputStream = new DataOutputStream(client.getSocket().getOutputStream());			
			outputStream.writeUTF("SERVER: " + message);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void broadCastMessage(String line, ConnectedClient currClient) {
		connectedClients.stream().forEach(client -> {	
			if (currClient.getId() == client.getId()) return;
			
			writeMessageToClient(client, line);
		});
	}
	
	private void handleClientDisconnect(ConnectedClient client) {
		String messageToBroadCast = "Client " + client.getId() + " disconnected";
		System.out.println(messageToBroadCast);
		broadCastMessage(messageToBroadCast, client);
	}
	
	private void readClientMessages(ConnectedClient client) {
		String line = "";
		DataInputStream inputStream = null;
		try {
			inputStream = new DataInputStream(client.getSocket().getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if (inputStream == null) return;
			
		while (true) {
			try {
				line = inputStream.readUTF();
				
				if (line.equalsIgnoreCase(ConnectedClient.STOP_DATA_SEND_COMMAND)) {
					handleClientDisconnect(client);
					break;
				}
				
				String broadCastMessageString = "Message from client " + client.getId() + " => " + line;
				
				System.out.println(broadCastMessageString);
				broadCastMessage(broadCastMessageString, client);
			}
			catch (IOException e) {
				handleClientDisconnect(client);
				break;
			}
		}
		
		try {
			inputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void performClientActions (ConnectedClient connectedClient) {
		new Thread(() -> {
			this.readClientMessages(connectedClient);
			connectedClient.close();
			removeClientById(connectedClient.getId());
		}).start();
	}
	
	private void addConnectedClient(Socket socketToAdd) {
		if (connectedClients.size() >= MAX_CONNECTIONS) {
			System.out.println("Server doesn't accept connections more than " + MAX_CONNECTIONS);
			return;
		}
				
		ConnectedClient connectedClient = new ConnectedClient(socketToAdd, CLIENT_ID);
		
		String broadCastMesasgeString = "Client " + connectedClient.getId() + " connected";
		System.out.println(broadCastMesasgeString);
		CLIENT_ID++;
		connectedClients.add(connectedClient);
		performClientActions(connectedClient);
		broadCastMessage(broadCastMesasgeString, connectedClient);
	}
	
	private void removeClientById(int id) {
		for (int i = 0; i < connectedClients.size(); i++) {
			ConnectedClient connectedClient = connectedClients.get(i);
			
			if (connectedClient.getId() == id) {
				connectedClients.remove(connectedClient);
				break;
			}
		}
	}
}
