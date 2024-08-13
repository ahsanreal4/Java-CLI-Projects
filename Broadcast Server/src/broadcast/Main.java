package broadcast;

import java.util.Scanner;

public class Main {
	private static final int PORT = 4000;
	
	private static String SERVER_START_COMMAND = "start";
	private static String CLIENT_CONNECT_COMMAND = "connect";
	private static String STOP_COMMAND = "stop";
	private static String HELP_COMMAND = "help";
	
	private static Scanner scanner;
	
	private static void printHelp() {
		System.out.println("");
		System.out.println(SERVER_START_COMMAND + " => starts the server");		
		System.out.println(CLIENT_CONNECT_COMMAND + " => connects the client to the server");		
		System.out.println(STOP_COMMAND + " => exits the program\n");
	}
	
	private static Server createServer(Server server) {
		if (server != null && server.isAlive()) {
			System.err.println("Server already exists");
			return null;
		}
		
		return new Server(PORT);
	}
	
	private static Client createClient(Client client) {
		if (client != null && client.isAlive()) {
			System.out.println("Client already created");
			return null;
		}
		
		return new Client(PORT, scanner);		
	}
	
	private static void showMainMenu() {
		System.out.println("-----------------------------------\n           Main Menu                  \n-----------------------------------");
		System.out.println("Enter " + HELP_COMMAND + " to see all commands\n");
	}
	
	public static void main(String[] args) {
		scanner = new Scanner(System.in);
		Server server = null;
		Client client = null;
		
		
		boolean showMainMenuString = true;
		
		while (true) {
			System.out.println();
			if (showMainMenuString) {
				showMainMenu();
				showMainMenuString = false;
			}
			String userInput = scanner.nextLine().trim();
			
			if (userInput.equalsIgnoreCase(SERVER_START_COMMAND)) {
				server = createServer(server);
				showMainMenuString = true;
			}
			else if (userInput.equalsIgnoreCase(CLIENT_CONNECT_COMMAND)) {
				client = createClient(client);
				showMainMenuString = true;
			}
			else if(userInput.equalsIgnoreCase(STOP_COMMAND)) {
				break;
			}
			else if (userInput.equalsIgnoreCase(HELP_COMMAND)) {
				printHelp();
			}
			else {
				System.out.println("Invalid command\n");
			}
		}
		
		System.out.println("Shutting down...");
	}

}
