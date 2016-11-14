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
 
 
		//Metodo costruttore - imposta HOST e PORTA di connessione
		public DarwinClient (String host, int port)
		{
 
			this.host = host;
			this.port = port;
			clientSocket = new TcpClient ();
 
		}
 
		//Apre la connessione al server
		public void startConnection ()
		{
			clientSocket.Connect (this.host, this.port); 
			serverStream = clientSocket.GetStream ();
			streamReader = new System.IO.StreamReader (clientSocket.GetStream ());
		}
 
		//Termina la connessione
		public void endConnection ()
		{
 
			clientSocket.Close ();
 
		}
 
		//Invia il comando al server (aggiunge in automatico il NEWLINE)
		public void sendCmd (String msg)
		{
			byte[] outStream = System.Text.Encoding.ASCII.GetBytes (msg + "\n");
			serverStream.Write (outStream, 0, outStream.Length);
			serverStream.Flush ();	
 
		}
 
		//Legge dal server una riga
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
 
				//Instanziamo la classe che ci server per gestire il socket
				DarwinClient dClient = new DarwinClient ("localhost", 10001);
 
				//Apriamo la connessione
				dClient.startConnection ();
 
				Console.WriteLine ("Connection to  localhost:10001 successfull");
 
				//Inviamo il comando (sottoscrizione standard Fiat Chrysler Automobiles  Book + Price + Bidask)
				String cmd = "SUBALL FCA";
				dClient.sendCmd (cmd);
				Console.WriteLine (" --> " + cmd);
				String line = null;
				long start = DateTime.UtcNow.Ticks;
				bool continua = true;
 
				//Leggiamo per 120 secondi
				while (continua) {
 
					//Leggo la prossima linea
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
 
				//Terminiamo la sottoscrizione
				Console.WriteLine ("Terminating subscriptions");
				dClient.sendCmd ("UNS FCA");
				Console.WriteLine ("Connection closed");
 
				//Chiudiamo il socket
				dClient.endConnection ();
 
			} catch (Exception exx) {
 
				//Se ci sono errori li visualizziamo
				Console.WriteLine ("Error in DarwinClient: " + exx.Message);
				Console.WriteLine (exx.StackTrace);
 
			}
 
 
 
		}
	}
}