import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Classe esempio per le chiamate storiche connessione socket sulla porta 10003
 * Connessione sulla porta e messaggio di connessione avvenuta tramite riga Darwinstatus,
 * Effettuata la connessione manda il comando tramite una send e stampa a video il contenuto richiesto.
 */
public class TbtConnect{
	Socket s; //il socket che utilizziamo per la connessione
	PrintWriter pW; //fornisce i metodi print e println, che permettono di scrivere qualunque dato Java, convertendolo automaticamente in stringa.
	BufferedReader bfr; //usa un buffer (memoria tampone) per memorizzare temporaneamente i caratteri da leggere/scrivere
	int porta; //porta sul quale fare l'accesso
	String host; //host sul quale fare l'accesso
	
	public TbtConnect(int porta,String host){
		this.porta=porta;
		this.host=host;
	}
	
	/**
	 * Connessione al socket.
	 * creo il socket il printwriter e il buffer.
	 */
	public void Connessione(){
		try {
	        s = new Socket(host, porta);
	        pW = new PrintWriter(s.getOutputStream(), true);
	        bfr = new BufferedReader(new InputStreamReader(s.getInputStream()));
	        System.out.println("Connessione avvenuta");
	        System.out.println(RLine());
		} catch (Exception e) {
            System.err.println("Unable to connect to the DataFeed: " + e );
        }
	}
	
	/**
	 * Metodo per la chiusura della connessione.
	 */
	public void closeConnection(){
		try {
			System.out.println("Chiusura in corso.");
			pW.close();
			bfr.close();
			s.close();
			System.out.println("Chiusura effettuata!");
		} catch (Exception e){
            System.err.println("Error disconnecting from the DataFeed: " + e);
		}
	}
	
	/**
	 * Metodo per fare la send del comando dato in input
	 * @param cmd il comando
	 */
	public void sendCommand(String cmd){
		pW.println(cmd);
	}
	
	/**
	 * Metodo per stampare il contenuto del buffer.
	 * @return stringa contente la stringa del buffer
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
		TbtConnect t=new TbtConnect(10003,"localhost");
		t.Connessione();
		String cmd="CANDLE FCA 1 60";
		//String cmd="TBT FCA 1";
		//TBT <tit> <ngiorni>
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
}