/** 
 * Server.java
 * 
 * Server class to handle incoming/outgoing messages.
 * Includes ClientThread object class.
 * 
 **/

import java.net.*;
import java.util.HashMap;
import java.util.Set;
import java.io.*;

public class Server 
{
	// List of user names/client objects currently connected
	private HashMap< String, ClientThread > users;
	
	// Is the server running and accepting client connections?
	private boolean running = true;
	
	private int port;
	private static int uniqueId;

	// Constructor
	public Server(int port) 
	{
		this.port = port;
		users = new HashMap<String, ClientThread>();
	}

	// Starts client thread, used when client connects
	public void start() throws Exception {
		
		// Main server socket
		System.out.println("[Server] Binding to port " + port + ", please wait  ...");
		ServerSocket server = new ServerSocket(port);
		
		// Now running
		running = true;

		// Waits for a clients to attempt to connect and accepts them
		while (running) {
			
			System.out.println("\n[Server] Waiting for a client ...\n");
			
			// Main server socket
			Socket socket = server.accept(); // accept connection
			
			// Break the loop if not running
			if (!running) break;
			
			// Make sure there are enough spots open to chat
			if ( users.size() <= Config.MAX_CLIENTS ) {
				ClientThread client = new ClientThread(socket); // make a thread for Client
				client.start();
			}
			else {
				System.out.println("\n[Server] Too many clients connected right now.");
			}
		}
		
		// Server needs to shut down as "running" variable is now false
		try 
		{
			server.close();
			
			for ( ClientThread tc : users.values() ) 
			{
				try 
				{
					tc.streamIn.close();
					tc.streamOut.close();
					tc.socket.close();
				} 
				catch (IOException ioE) { }
			}
		} 
		catch ( Exception e ) {
			System.out.println("[Server] Exception closing the server and clients: " + e);
		}
	}

	
	// Ends server connection, used on error.
	public void stop() 
	{
		running = false;
		
		try 
		{
			new Socket(Config.HOST_NAME, port);
		} 
		catch (Exception e) {
			System.out.println("Error: " + e);
		}
	}

	// Send a message to all users
	public synchronized void handle(String input) {
		
		System.out.println("[Server] Sending message to all users --> " + input );
		
		for ( String user : users.keySet() ) 
		{
			ClientThread ct = users.get(user);
			
			if ( ! ct.writeMsg(input) ) 
			{ 
				users.remove(user); 
			}
		}
	}


	// Terminates their thread and removes them from the client list
	public synchronized void remove(int ID) {
		
		for ( String user : users.keySet() ) 
		{	
			ClientThread ct = users.get(user);
			if ( ct.id == ID ) 
			{
				users.remove(user);
				return;
			}
		}
	}

	// Create server instance
	public static void main(String args[]) throws Exception 
	{
		Server server = new Server(Config.PORT_NUMBER);
		server.start();
	}

	// Converts user list to an update message
	private String userListToString() {
		
		// We will use StringBuilder for this (faster)
		StringBuilder sendString = new StringBuilder();
		sendString.append( Config.UPDATE_USER_LIST );
		
		for ( String user : users.keySet() ) {
			sendString.append(Config.SPLIT_CHAR + user);
		}

		return sendString.toString();
	}

	// Adds client to the server, opens client to accept/send messages through data stream
	// starts client socket.  Updates client count list, ensures the client list is below the maximum. 
	class ClientThread extends Thread 
	{
		// the socket where to listen/talk
		Socket socket;
		ObjectInputStream streamIn;
		ObjectOutputStream streamOut;
		int id;
		String username;
		String cm;
		
		// Constructor
		ClientThread(Socket socket) 
		{
			id = ++uniqueId;
			this.socket = socket;
			try {
				streamOut = new ObjectOutputStream( socket.getOutputStream() );
				streamIn = new ObjectInputStream( socket.getInputStream() );
				username = (String) streamIn.readObject();
				
				Set<String> usernames = users.keySet();
				
				if ( usernames.contains(username) )
				{
					// A user already exists with this name
					
				}
				users.put(username, this);
				
				System.out.println("[Client Thread] '" + username + "' connected and user list updated.");
				
				// Create string representation of the current users
				String userlist = userListToString();
				
				// Update users with user list
				handle( userlist );
				
				// Announce that a new user has been added
				handle ( Config.USER_CONNECT + Config.SPLIT_CHAR + username);
				
			} catch (IOException e) {
				System.out.println("\n[Client Thread] Exception creating new input/output streams: " + e);
				return;
			}
			catch (ClassNotFoundException e) {}
			
		}
		
		// Check if a message is an update message
		public boolean isUpdate(String message) 
		{
			String removeUpdate = Config.USER_DISCONNECT + Config.SPLIT_CHAR; // --> Example: $$_U_RMV_USER_$$»
			
			if ( message.contains( removeUpdate ) )
			{
				String userToRemove = message.replace(removeUpdate, "");
				users.remove(userToRemove);
				
				// Create string representation of the current users
				String userlist = userListToString();
				
				// Update users with user list
				handle( userlist );
				handle( "[Server] " + userToRemove + " has left the chat.");
				
				return true;
			}
			return false;
		}

		public void run() 
		{
			boolean running = true;
			
			while (running) {
				try {
					cm = (String) streamIn.readObject();
				}
				catch (SocketException se) {
					System.out.println("[User: " + username + "] Disconnected.");
					break;
				}
				catch (IOException e) {
					System.out.println("\n[Client Thread: " + username + "] Exception reading Streams: " + e);
					break;
				} catch (ClassNotFoundException e2) { break; }
				
				String message = cm;
				
				if ( ! isUpdate( message ) ) {
					handle(username + ": " + message);
				}
			}
			remove(id);
			close();
		}

		// Ends server connection
		private void close() {
			try {
				if ( streamOut != null ) streamOut.close();
				if ( streamIn != null ) streamIn.close();
				if ( socket != null ) socket.close();
			} catch (Exception e) { }
		}

		private boolean writeMsg(String msg) 
		{
			if ( !socket.isConnected() ) {
				close();
				return false;
			}
			try {
				streamOut.writeObject(msg);
			}
			catch (IOException e) {
				System.out.println("\n[Client Thread: " + username + "] Error sending message --> " + msg);
			}
			return true;
		}
	}
}