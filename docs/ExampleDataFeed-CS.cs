//DarwinClient.cs

using System;
using System.Net.Sockets;
 
namespace DarwinClient
{
	public class DarwinClient
	{
 
		private TcpClient clientSocket;
		private String host;
		private int port;
		private NetworkStream serverStream;
		private System.IO.StreamReader streamReader;
 
 
		//Constructor - sets HOST and PORT connection
		public DarwinClient (String host, int port)
		{
 
			this.host = host;
			this.port = port;
			clientSocket = new TcpClient ();
 
		}
 
		//Opens the connection to the server
		public void startConnection ()
		{
			clientSocket.Connect (this.host, this.port); 
			serverStream = clientSocket.GetStream ();
			streamReader = new System.IO.StreamReader (clientSocket.GetStream ());
		}
 
		//Terminates the connection
		public void endConnection ()
		{
 
			clientSocket.Close ();
 
		}
 
		//Sends the command to the server (automatically adds the NEWLINE)
		public void sendCmd (String msg)
		{
			byte[] outStream = System.Text.Encoding.ASCII.GetBytes (msg + "\n");
			serverStream.Write (outStream, 0, outStream.Length);
			serverStream.Flush ();	
 
		}
 
		//Reads a line from the server
		public String readLine ()
		{
 
			String returndata = streamReader.ReadLine ();
			return returndata;
 
		}
 
 
	}
}



//Main.cs

using System;
 
namespace DarwinClient
{
	class MainClass
	{
 
 
		public static void Main ()
		{
 
			Console.WriteLine ("Starting client...");
 
			try {
 
				//Instantiate the class that server to handle the socket
				DarwinClient dClient = new DarwinClient ("localhost", 10001);
 
				//Start Connection
				dClient.startConnection ();
 
				Console.WriteLine ("Connection to  localhost:10001 successfull");
 
				//We send the command (standard subscription Fiat Chrysler Automobiles  Book + Price + Bidask)
				String cmd = "SUBALL FCA";
				dClient.sendCmd (cmd);
				Console.WriteLine (" --> " + cmd);
				String line = null;
				long start = DateTime.UtcNow.Ticks;
				bool continua = true;
 
				//To read for 120 seconds
				while (continua) {
 
					//To read the next line
					line = dClient.readLine ();
					if (line != null) {
						Console.WriteLine (" <-- " + line);
					}
 
					long now = DateTime.UtcNow.Ticks;
 
					long sec = (now - start) / 10000000;
					if (sec > 120) {
						continua = false;
					}
 
				}
 
				//End the subscription
				Console.WriteLine ("Terminating subscriptions");
				dClient.sendCmd ("UNS FCA");
				Console.WriteLine ("Connection closed");
 
				//Close socket
				dClient.endConnection ();
 
			} catch (Exception exx) {
 
				//If there are errors we display them
				Console.WriteLine ("Error in DarwinClient: " + exx.Message);
				Console.WriteLine (exx.StackTrace);
 
			}
 
 
 
		}
	}
}