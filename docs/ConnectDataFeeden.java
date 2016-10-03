import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 *Class example for the connection to the DataFeed on port 10001.
 *Makes into connection is called a command input,
 *given this command will start the disclosure of the title/s the requested/s for 60 seconds.
 *Past the minutes will be signed on the title/s and closed the connection.
 *
 *Obviously if you enter an invalid command there will be error code and closing.
 */
public class ConnectDataFeeden{
	Socket s; //the socket for connection
	PrintWriter pW; //provides methods print and println, which allow you to write any given Java, converting it automatically in string.
	BufferedReader bfr; //uses a buffer to temporarily store the characters to read/write
	int porta; //port on which to access
	String host; //host on which to access
	//variables for code development
	static String tick;
	static boolean verifica=true;
	
	/**
	 *Constructor,in this case we initialize the host and port to connect
	 */
	public ConnectDataFeeden(int porta,String host){
		this.porta=porta;
		this.host=host;
	}
	
	/**
	 * Connection to the socket
	 * To create the socket PrintWriter and the buffer.
	 */
	private void Connessione(){
		try {
	        s = new Socket(host, porta);
	        pW = new PrintWriter(s.getOutputStream(), true);
	        bfr = new BufferedReader(new InputStreamReader(s.getInputStream()));
	        System.out.println("Connection Successful");
	        System.out.println(RLine());
		} catch (Exception e) {
            System.err.println("Unable to connect to the DataFeed: " + e );
        }
	}
	
	/**
	 *Method for the closing of the connection.
	 */
	private void closeConnection(){
		try {
			System.out.println("Shutting.");
			pW.close();
			bfr.close();
			s.close();
			System.out.println("Close connection successful!");
		} catch (Exception e){
            System.err.println("Error disconnecting from the DataFeed: " + e);
		}
	}
	
	/**
	 *Method to send command given input
	 * @param cmd the command
	 */
	private void sendCommand(String cmd){
		pW.println(cmd);
	}
	
	/**
	 * Method to print the contents of the buffer.
	 * @return string containing the message buffer
	 */
	private String RLine(){
		String line = null;
		try {
			line = bfr.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return line;
	}
	
	/**
	 * method used for the printing of the buffer for a number of seconds taken into input
	 */
	private void sott_nsec()
	{
		//Scanner input=new Scanner(System.in);
		//System.out.println("Enter how long to subscribe (seconds):");
	     //int timer=input.nextInt();
		int timer=120;//seconds
	     boolean continua=true;
	     String line="";
	     long start= System.currentTimeMillis();//variable for the timer
	     System.out.println("Print for -->"+timer+"<-- seconds the subscriptions for "+tick);
	     while(continua){
	    	 line=RLine();//takes the message from the buffer
	    	 if(line.startsWith("ERR")){
	    		 continua=false;
	    		 verifica=false;
	    	 }
	    	 System.out.println(line);//printing the answer
	    	 long now=System.currentTimeMillis();//management of the timer
	    	 long tot= (now-start)/1000;
	    	 //System.out.println("verification time:"+tot);
	    	 if(tot>timer) continua=false;
	     }//test for the timer if beyond the limit away from the cycle	
	}
	
	/**
	 * Method used to derive the ticker of the title by the command input
	 * @param cmd input command
	 * @return ticker of the title
	 */
	private String formatTick(String cmd){
		String formats=cmd.replace(" ", "\t\t\t\t\t\t\t\t");
		String tick=formats.substring(formats.length()-8,formats.length()).trim();
		return tick;
	}
	
	public static void main(String args[]){
		System.out.println("TEST DATAFEED");
					
		ConnectDataFeeden t=new ConnectDataFeeden(10001,"localhost");//object creation
		t.Connessione();//Connection
		
		Scanner scanner = new Scanner(System.in);

        System.out.print("Digitare il comando:");
        String cmd = scanner.nextLine();//inout command
        
        t.sendCommand(cmd);//send command
        
        //Call of the print buffer, with controls
        if(cmd.startsWith("SUB")){
        	tick=t.formatTick(cmd);//take ticker
        	t.sott_nsec();//method for the subscription for n seconds
        	if(verifica==true){
        		System.out.println("\n unscription the ticket---> UNS "+tick);
        		//t.sendCommand("UNS"+tick);//send the unscription title
				}
			}
        else 
        	System.out.println(t.RLine()); 
		
		t.closeConnection();//close connection
		}
}