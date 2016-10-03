import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Class example for calls historical socket connection on port 10003
 * Connection on port and connect message occurred through line Darwinstatus,
 * Once connected sends the command via a send and print to video content required.
 */
public class TbtConnecten{
	Socket s; //To use the socket for connection
	PrintWriter pW; //provides methods print and println, which allow you to write any given Java, automatically converting it to a string.
	BufferedReader bfr; //uses a buffer to temporarily store the characters to read/write
	int porta; //port on which to access
	String host; //host on which to access
	
	public TbtConnecten(int porta,String host){
		this.porta=porta;
		this.host=host;
	}
	
	/**
	 * Connection to the socket.
	 * To create the socket PrintWriter and the buffer.
	 */
	public void Connessione(){
		try {
	        s = new Socket(host, porta);
	        pW = new PrintWriter(s.getOutputStream(), true);
	        bfr = new BufferedReader(new InputStreamReader(s.getInputStream()));
	        System.out.println("Connection successful!");
	        System.out.println(RLine());
		} catch (Exception e) {
            System.err.println("Unable to connect to the DataFeed: " + e );
        }
	}
	
	/**
	 * Method for the closing of the connection.
	 */
	public void closeConnection(){
		try {
			System.out.println("Shutting.");
			pW.close();
			bfr.close();
			s.close();
			System.out.println("close connection successful!");
		} catch (Exception e){
            System.err.println("Error disconnecting from the DataFeed: " + e);
		}
	}
	
	/**
	 * Method to do the send command given input
	 * @param cmd command
	 */
	public void sendCommand(String cmd){
		pW.println(cmd);
	}
	
	/**
	 * Method to print the contents of the buffer.
	 * @return string containing the message buffer
	 */
	public String RLine(){
		String line = null;
		try {
			line = bfr.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return line;
	}
	
	public static void main(String args[]){
		TbtConnecten t=new TbtConnecten(10003,"localhost");
		t.Connessione();
		String cmd="CANDLE FCA 1 60";
		//String cmd="TBT FCA 1";
		//TBT <tit> <nday>
		t.sendCommand(cmd);
		boolean stato=true;
		while(stato){
			String line=t.RLine();
			if(line.equals("END TBT") || line.equals("END CANDLES")){
				System.out.println(line);
				stato=false;
			}
			else
				System.out.println(line);
		}
		
		t.closeConnection();
	}
}//end main