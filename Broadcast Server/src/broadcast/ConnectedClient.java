package broadcast;

import java.io.IOException;
import java.net.Socket;

public class ConnectedClient {
	public static String STOP_DATA_SEND_COMMAND = "stop";
	
	private Socket socket;
	private int id;
		
	public ConnectedClient(Socket socket, int id) {
		this.socket = socket;
		this.id = id;
	}
	
	public boolean isActive() {
		return !socket.isClosed();
	}
	
	public void close() {
		try {
			this.socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Socket getSocket() {
		return socket;
	}

	public void setSocket(Socket socket) {
		this.socket = socket;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	
}
