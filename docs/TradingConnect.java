import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Vector;
/**
 *Classe d'esempio per la connessione al Trading tramite la porta 10002.
 *Questo esempio si connette e stampa la situazione portafoglio e l'elenco degli ordini.
 *Poi fa un esempio di send di un comando trading,per sicurezza e' stato inserito un comando che non fa NULLA.
 *Dopo aver mandato il comando si otterrà la risposta a video e si chiuderà la connessione e terminarà il programma.
 *
 *NB:avendo messo un comando NULLO ovviamente la risposta sarà NULLA,a video si visualizzerà un H(HeartBeat),
 *	 ma questa non è la risposta ma quello che passa in quel momento,cioè NULLA.
 */
public class TradingConnect{
	Socket s; //il socket che utilizziamo per la connessione
	PrintWriter pW; //fornisce i metodi print e println, che permettono di scrivere qualunque dato Java, convertendolo automaticamente in stringa.
	BufferedReader bfr; //usa un buffer (memoria tampone) per memorizzare temporaneamente i caratteri da leggere/scrivere
	int porta; //porta sul quale fare l'accesso
	String host; //host sul quale fare l'accesso
	Vector<String> ordini=new Vector<String>();
	TradingConnect(int porta,String host){
		this.porta=porta;
		this.host=host;
	}
	
	/**
	 * Connessione al socket.
	 * creo il socket il printwriter e il buffer.
	 */
	private void Connessione(){
		try {
	        s = new Socket(host, porta);
	        pW = new PrintWriter(s.getOutputStream(), true);
	        bfr = new BufferedReader(new InputStreamReader(s.getInputStream()));
	        System.out.println("Connessione avvenuta");
	        System.out.println(RLine());
	        System.out.println(RLine());
	        boolean state=true;
	        System.out.println("Lista ordini");
	        System.out.print("Caricamento");
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
	        
	        System.out.println("end Lista");
		} catch (Exception e) {
            System.err.println("Unable to connect to the DataFeed: " + e );
        }
	} 
	
	/**
	 * Metodo per la chiusura della connessione.
	 */
	private void closeConnection(){
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
	 * Metodo per stampare il contenuto del buffer.
	 * @return stringa contente la stringa del buffer
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
		TradingConnect t=new TradingConnect(10002,"localhost");
		t.Connessione();
		System.out.println("fine situazione");
		//Prova di comando trading
		//Facendo una sendcmd si potrebbe provare un comando trading,
		//per precauzione mettiamo un esempio che non fa nulla..
		//qui inserire il comando 
		//comando acqz: ACQAZ ordione,ticker,quantita',prezzo
		String cmd="ACQAZ ORD001,PIPPO,10,0.000";
		t.sendCommand(cmd);
		System.out.println(cmd);
		String risposta;
		//in questo caso la risposta sarà un heartbeat perchè il nostro comando non fa nulla
		//si specifica che la risposta non è l'heartbeat ma non avendo nulla,stampa quello che che passa ovvero un H
		risposta=t.RLine();
		
		System.out.println(risposta);
		t.closeConnection();
	}
}