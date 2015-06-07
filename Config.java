/** 
 * Config.java
 * 
 * Public class to hold global constants
 * 
 **/

public final class Config {
	
	// Number of users allowed
	public static final int 	MAX_CLIENTS 		= 30;
	
	// Host and port info
	public static final String 	HOST_NAME 			= "localhost";
	public static final int 	PORT_NUMBER 		= 5555;
	
	// User name requirements
	public static final int 	MAX_USERNAME_LEN 	= 24;
	public static final int 	MIN_USERNAME_LEN 	= 3;
	
	// Update message types
	public static final String 	UPDATE_USER_LIST 	= "$$_UPDATE_USERLIST_$$";
	public static final String 	USER_CONNECT 		= "$$_U_ADD_USER_$$";
	public static final String 	USER_DISCONNECT 	= "$$_U_RMV_USER_$$";
	
	// Used for update messages
	public static final String 	SPLIT_CHAR 			= 	"»";

}
