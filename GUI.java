/** 
 * GUI.java
 * 
 * Creates and initializes GUI.
 * Displays messages and handles click events/user name validations.
 * 
 **/

import java.awt.event.*;
import java.io.IOException;
import java.util.ArrayList;

public class GUI extends javax.swing.JFrame 
{	
	private static final long serialVersionUID = 1L;
	
	private ArrayList<String> userList = new ArrayList<String>();  // List of user names
	private String thisUser;  // Saved user name after a user is connected
	private boolean thisUserConnected = false;  // Is there a user connected in this GUI?
	private UserClient client; // the Client object

	// Constructor: Initialize GUI
	GUI() { initComponents(); }

	
	// Displays a message in GUI
	public void append( String str ) { 
		activeMessagesArea.append(str + "\n\n"); 
	}
	
	
	// Called when a user closes the chat window
	private void onWindowClose() 
	{ 
		// If user connected, then disconnect them
		if ( thisUserConnected ) { client.disconnect(thisUser); } 
	}
	
    
    // Validate user name
    public boolean validateUsername( String username ) 
    {
    	if ( username.length() >= Config.MIN_USERNAME_LEN ) 
    	{
    		if ( username.length() <= Config.MAX_USERNAME_LEN ) 
    		{
    			return true; // All checks passed --> Valid user name
    		}
    		else {
    			append("[System] Your username must not exceed " + Config.MAX_USERNAME_LEN + " characters.");
    			usernameField.setText("");
    		}
    	}
    	else 
        {
    		// User name length is not long enough
        	if ( username.length() == 0 ) 
        	{ 
        		append("[System] You must enter a username..."); 
        		usernameField.setText("");
        	}
            else 
            { 
            	append("[System] Username must be at least " + Config.MIN_USERNAME_LEN + " characters."); 
            	usernameField.setText("");
            }
        }
    	return false;
    }
    
    // Called when user clicks "Send" button
    private void sendAction(java.awt.event.ActionEvent evt) 
    {	
    	// Send button clicked
    	
    	// Message cannot be blank
    	if ( ! userMessageArea.getText().equals("") ) 
    	{
    		// Send message and reset text area
    		client.sendMessage(userMessageArea.getText());
    		userMessageArea.setText("");
    	}
    }
    
    // Called when user clicks "Connect" button
    private void connectAction(java.awt.event.ActionEvent evt) 
    {
        // Make sure user isn't already connected
        if ( !thisUserConnected ) 
        {   
        	// Remove all whitespace
        	String username = usernameField.getText().replaceAll(" ", "");
        	
        	boolean usernameIsValid = validateUsername( username );
        	
            // Check that user name length is greater than Config.MIN_USERNAME_LEN
            if ( usernameIsValid )
            {	
               // Create client
               client = new UserClient(Config.HOST_NAME, Config.PORT_NUMBER, username, this);
               
               // Try starting client
               try 
               { 
            	   // Clear any error messages that may have been printed from user name validation
               		activeMessagesArea.setText("");
               		
            	   if ( !client.start() ) { 
            		   return; 
            	   } 
               } 
               catch (IOException e1) { e1.printStackTrace(); }
               
                // Check for successful client run
                if (client.running) 
                {   
                    // Save user name and connection
                    thisUser = username;
                    thisUserConnected = true;
                }
            }
        }
        else {
        	// User already connected
            append("[System] You are already connected...");
        }
    }
    
    
    // Called when user clicks "Disconnect" button
    private void disconnectAction(java.awt.event.ActionEvent evt) {
    	
    	// User must be connected in order to disconnect
        if( thisUserConnected ) {
            
            // Disconnect user
        	if ( client.disconnect(thisUser) ) {
        		
        		// Successfully disconnected --> Update connection state and GUI
                thisUserConnected = false;
                resetGUI();
        	}
        }
    }
    
    // Clears all text areas and text fields
    private void resetGUI() 
    {
    	usernameField.setText("");
    	activeUsersArea.setText("");
    	activeMessagesArea.setText("");
    	userMessageArea.setText("");
    }
    
    // Updates user list display based on update message/header
    public void refreshUserList( String listMessage, String header ) {
    	
    	// An update message may look like this:   $$_UPDATE_USERLIST_$$»Nic»Andrew»Rob»Kevin
    	
    	// Create String array from split items
		String[] items = listMessage.split("»");
		
		// Create new ArrayList and add items
		userList = new ArrayList<String>();
		
		for ( String user : items ) {
			if ( ! user.equals( header ) ) {
				userList.add(user);
			}
		}
    	
		// Update display
    	updateUserList();
    }
    
    // Update user list display in GUI
    private void updateUserList() {
    	
    	// Reset
    	activeUsersArea.setText("");
    	
    	// Populate
    	for (String username : userList ) {
    		activeUsersArea.append(username + "\n");
    	}	
    }
	
	
    // Create GUI instance
	public static void main(String[] args) 
	{
		new GUI().setVisible(true);
	}
	
	
	
	
	

	/**** Declare and initialize all GUI elements ****/
	
	// Variables declaration - do not modify//GEN-BEGIN:variables
    private static javax.swing.JTextArea activeMessagesArea;
    private javax.swing.JScrollPane activeMessagesPane;
    private javax.swing.JTextArea activeUsersArea;
    private javax.swing.JLabel activeUsersLabel;
    private javax.swing.JScrollPane activeUsersPane;
    private javax.swing.JButton connectButton;
    private javax.swing.JButton disconnectButton;
    private javax.swing.JButton sendButton;
    private javax.swing.JTextArea userMessageArea;
    private javax.swing.JScrollPane userMessagePane;
    private javax.swing.JTextField usernameField;
    private javax.swing.JLabel usernameLabel;
	
    private void initComponents() {
		
		addWindowListener( 
			new WindowAdapter() { 
				public void windowClosing(WindowEvent e) { 
					onWindowClose(); 
				} 
			}
		);

        usernameLabel = new javax.swing.JLabel();
        usernameField = new javax.swing.JTextField();
        connectButton = new javax.swing.JButton();
        disconnectButton = new javax.swing.JButton();
        activeUsersLabel = new javax.swing.JLabel();
        activeUsersPane = new javax.swing.JScrollPane();
        activeUsersArea = new javax.swing.JTextArea();
        activeMessagesPane = new javax.swing.JScrollPane();
        activeMessagesArea = new javax.swing.JTextArea();
        userMessagePane = new javax.swing.JScrollPane();
        userMessageArea = new javax.swing.JTextArea();
        sendButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        usernameLabel.setText("Username");

        connectButton.setText("Connect");
        connectButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                connectAction(evt);
            }
        });

        disconnectButton.setText("Disconnect");
        disconnectButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                disconnectAction(evt);
            }
        });

        activeUsersLabel.setText("Active Users");

        activeUsersArea.setEditable(false);
        activeUsersArea.setColumns(15);
        activeUsersArea.setRows(5);
        activeUsersArea.setAutoscrolls(false);
        activeUsersPane.setViewportView(activeUsersArea);

        activeMessagesArea.setEditable(false);
        activeMessagesArea.setColumns(20);
        activeMessagesArea.setLineWrap(true);
        activeMessagesArea.setRows(5);
        activeMessagesPane.setViewportView(activeMessagesArea);

        userMessageArea.setColumns(20);
        userMessageArea.setRows(5);
        userMessagePane.setViewportView(userMessageArea);

        sendButton.setText("Send");
        sendButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sendAction(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(activeUsersPane, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(activeUsersLabel))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(activeMessagesPane, javax.swing.GroupLayout.PREFERRED_SIZE, 524, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(userMessagePane, javax.swing.GroupLayout.PREFERRED_SIZE, 392, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(sendButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(usernameLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(usernameField, javax.swing.GroupLayout.PREFERRED_SIZE, 221, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(connectButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(disconnectButton)))
                .addContainerGap(20, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(activeUsersLabel)
                    .addComponent(usernameLabel)
                    .addComponent(usernameField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(connectButton)
                    .addComponent(disconnectButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(activeMessagesPane, javax.swing.GroupLayout.PREFERRED_SIZE, 370, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(userMessagePane, javax.swing.GroupLayout.DEFAULT_SIZE, 137, Short.MAX_VALUE)
                            .addComponent(sendButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addComponent(activeUsersPane, javax.swing.GroupLayout.PREFERRED_SIZE, 527, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(16, Short.MAX_VALUE))
        );

        pack();
    }
}





