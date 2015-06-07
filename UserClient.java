/** 
 * UserClient.java
 * 
 * Client class to handle messages from users.
 * Includes Listener object class (listens for messages from server).
 * 
 **/

import java.net.*;
import java.io.*;

public class UserClient
{  
	private	Socket		socket;
	public	boolean 	running	= true;
	private GUI 		gui;
	private String 		server, username;
	private int 		port;
		
	private ObjectInputStream 	streamIn;		// to read from the socket
	private ObjectOutputStream 	streamOut;		// to write on the socket

	public UserClient(String server, int port, String username, GUI gui){  
		this.server = server;
		this.port = port;
		this.username = username;
		this.gui = gui;
	}

	// Client (user) receives message
	public void handle(String msg)
	{  
		// Update message types
		String refreshList = Config.UPDATE_USER_LIST;
		String newUser = Config.USER_CONNECT + Config.SPLIT_CHAR;
		
		if ( msg.contains( refreshList ) )
		{	
			// This is an update message, not a chat message
			
			// Update client user list using update message
			gui.refreshUserList( msg, refreshList );
			
		}
		else if ( msg.contains( newUser ) ) 
		{
			// This is an update message, not a chat message
			
			// Remove update header
			String user = msg.replace(newUser, "");
			
			// Announce to client if they are not the new user
			if ( ! username.equals(user) ) {
				gui.append("[Server] " + user + " has joined the chat.");
			}
		}
		else 
		{	
			// Regular chat message
			gui.append(msg);	
		}
	}

	// Starts the clients connection to server/port
	public boolean start() throws IOException
	{
		// try to connect to the server
		try { socket = new Socket(server, port); } 
		
		// if it failed not much I can so
		catch(Exception ec) {
			System.out.println("[Client: " + username + "] Error connectiong to server:" + ec);
			return false;
		}
		
		// Connection successful (user friendly message)
		handle("[Welcome] You are now connect to the chat.");
	
		// Creating both data streams
		try
		{
			streamIn  = new ObjectInputStream(socket.getInputStream());
			streamOut = new ObjectOutputStream(socket.getOutputStream());
		}
		catch (IOException eIO) {
			System.out.println("\n[Client: " + username + "] Exception creating new Input/output Streams: " + eIO);
			return false;
		}

		// Creates the thread to listen from the server 
		new Listener().start();
		
		// Send our user name to the server this is the only message that we
		// will send as a String. All other messages will be ChatMessage objects
		try {
			streamOut.writeObject(username);
		}
		catch (IOException eIO) {
			System.out.println("\n[Client: " + username + "] Exception doing login : " + eIO);
			stop();
			return false;
		}
		
		// Success --> Inform the caller that it worked
		return true;
	}

	// Closes socket/input/output stream
	public void stop() 
	{  
		try { if(streamIn != null) streamIn.close(); }
		catch(Exception e) {} // not much else I can do
		
		try { if(streamOut != null) streamOut.close(); }
		catch(Exception e) {} // not much else I can do
        
		try { if(socket != null) socket.close(); }
		catch(Exception e) {} // not much else I can do
	}

	// Send a message to the server
	void sendMessage(String msg) {
		try {
			streamOut.writeObject(msg);
		}
		catch(IOException e) {
			System.out.println("\n[Client: " + username + "] Exception writing to server: " + e);
		}
	}
	
	// Disconnect from server
	public boolean disconnect(String user) {
		
		try { 
			String sendString = Config.USER_DISCONNECT + Config.SPLIT_CHAR + user;
			sendMessage( sendString );
			this.stop();
			
			return true;
		}
		catch ( Exception e ) { }
		
		return false;
	}

	// Listens for messages sent from server
	class Listener extends Thread {
	
		public void run() {
			while(true) {
				try {
					String msg = (String) streamIn.readObject();
					// if console mode print the message and add back the prompt
					if(gui == null) {
						System.out.println(msg);
						System.out.print("> ");
					}
					else {
						handle(msg);
					}
				}
				catch(IOException e) {
					System.out.println("\n[Client Listener: " + username + "] Server has closed the connection: " + e);
					break;
				}
				// can't happen with a String object but need the catch anyhow
				catch(ClassNotFoundException e2) {
				}
			}
		}
	}
}



