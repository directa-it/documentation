import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Vector;
/**
 *Class of example for connecting to the Trading through port 10002.
 *This example connects the situation and printing portfolio and the list of orders.
 *Then he makes an example of a command to send trading, for security and 'entered a command that does NOTHING.
 *After sending the command you will get the answer to video and will close the connection and Terminara program.
 *
 *NB:having put a command NULL obviously the answer is NOTHING, screen will display an H (HeartBeat),
 *	 but this is not the answer, but what goes on at that time, that is NOTHING.
 */
public class TradingConnecten{
	Socket s; //To use the socket for connection
	PrintWriter pW; //provides methods print and println, which allow you to write any given Java, automatically converting it to a string.
	BufferedReader bfr; //uses a buffer to temporarily store the characters to read / write
	int porta; //port on which to access
	String host; //host on which to access
	Vector<String> ordini=new Vector<String>();
	
	TradingConnecten(int porta,String host){
		this.porta=porta;
		this.host=host;
	}
	
	/**
	 * Connection to the socket.
	 * To create the socket PrintWriter and the buffer.
	 */
	private void Connessione(){
		try {
	        s = new Socket(host, porta);
	        pW = new PrintWriter(s.getOutputStream(), true);
	        bfr = new BufferedReader(new InputStreamReader(s.getInputStream()));
	        System.out.println("Connection successful");
	        System.out.println(RLine());
	        System.out.println(RLine());
	        boolean state=true;
	        System.out.println("Orders list");
	        System.out.print("Loading");
	        while(state){
	        	String line=RLine();
	        	if(line.startsWith("ORDER")){
	        		System.out.print(".");
	        		ordini.add(line);
	        		}
	        	else if(line.startsWith("ERR")){
	        		System.out.println(line);
	        		state=false;
	        	}
	        	else	
	        		state=false;
	        }
	        System.out.println(".");
	        int i=0;
	        while(i<ordini.size()){
	        	System.out.println(ordini.get(i));
	        	i++;
	        }
	        
	        System.out.println("end List");
		} catch (Exception e) {
            System.err.println("Unable to connect to the DataFeed: " + e );
        }
	} 
	
	/**
	 * Method for the closing of the connection.
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
	
	private void sendCommand(String cmd){
		pW.println(cmd);
	}
	
	public static void main(String[] args){
		TradingConnecten t=new TradingConnecten(10002,"localhost");
		t.Connessione();
		System.out.println("fine situazione");
		//Try to control trading
		//Taking a sendcmd you could try a command trading,
		//precaution we put an example that does nothing
		//Enter the command 
		//command acqz: ACQAZ order,ticker,quantity,price
		String cmd="ACQAZ ORD001,PIPPO,10,0.000";
		t.sendCommand(cmd);
		System.out.println(cmd);
		String reply;
		//in this case the answer will be a heartbeat because our command does nothing
		//specifies that the answer is not the heartbeat but having nothing, print what passes or a H
		reply=t.RLine();
		
		System.out.println(reply);
		t.closeConnection();
	}
}