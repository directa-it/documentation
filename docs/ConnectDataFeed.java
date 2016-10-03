import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 *Classe esempio per la connessione al DataFeed porta 10001.
 *Effetuata la connessione viene chiesto un comando in input,
 *dato questo comando partirà l'informativa del titolo/i richiesto/i per 60 secondi.
 *Passato il minuto verrà sottoscritto il titolo/i e chiusa la connessione.
 *
 *Ovviamente se viene inserito un comando sbagliato ci sarà codice errore e chiusura.
 */
public class ConnectDataFeed{
	Socket s; //il socket che utilizziamo per la connessione
	PrintWriter pW; //fornisce i metodi print e println, che permettono di scrivere qualunque dato Java, convertendolo automaticamente in stringa.
	BufferedReader bfr; //usa un buffer (memoria tampone) per memorizzare temporaneamente i caratteri da leggere/scrivere
	int porta; //porta sul quale fare l'accesso
	String host; //host sul quale fare l'accesso
	//variabili per sviluppo del codice
	static String tick;
	static boolean verifica=true;
	
	/**
	 *Costruttore,in questo caso inizializziamo l'host e la porta per la connessiona 
	 */
	public ConnectDataFeed(int porta,String host){
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
	 * Metodo per fare la send del comando dato in input
	 * @param cmd il comando
	 */
	private void sendCommand(String cmd){
		pW.println(cmd);
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
	
	/**
	 * metodo utilizzato per la stampa del buffer per un numero di secondi preso in input
	 */
	private void sott_nsec()
	{
		//Scanner input=new Scanner(System.in);
		//System.out.println("Inserire per quanto tempo effetturare le sottoscrizioni(in secondi):");
	     //int timer=input.nextInt();
		int timer=120;//secondi
	     boolean continua=true;
	     String line="";
	     long start= System.currentTimeMillis();//variabile per il temporizzatore
	     System.out.println("Stampati per -->"+timer+"<-- secondi le sottoscrizioni per "+tick);
	     while(continua){
	    	 line=RLine();//prende il messaggio dal buffer
	    	 if(line.startsWith("ERR")){
	    		 continua=false;
	    		 verifica=false;
	    	 }
	    	 System.out.println(line);//stampa delle risposta
	    	 long now=System.currentTimeMillis();//gestione del temporizzatore
	    	 long tot= (now-start)/1000;
	    	 //System.out.println("verifica tempo:"+tot);
	    	 if(tot>timer) continua=false;
	     }//test per il temporizzatore se oltre il limite via dal ciclo	
	}
	
	/**
	 * Metodo utilizzato per ricavare il ticker del titolo dal comando in input
	 * @param cmd comando da input
	 * @return il ticker
	 */
	private String formatTick(String cmd){
		String formats=cmd.replace(" ", "\t\t\t\t\t\t\t\t");
		String tick=formats.substring(formats.length()-8,formats.length()).trim();
		return tick;
	}
	
	public static void main(String args[]){
		System.out.println("TEST DATAFEED");
					
		ConnectDataFeed t=new ConnectDataFeed(10001,"localhost");//creazione dell'oggetto
		t.Connessione();//connessione 
		
		Scanner scanner = new Scanner(System.in);

        System.out.print("Digitare il comando:");
        String cmd = scanner.nextLine();//input del comando
        
        t.sendCommand(cmd);//invio il comando
        
        //richiamo della stampa del buffer,con dei controlli
        if(cmd.startsWith("SUB")){
        	tick=t.formatTick(cmd);//prendo il ticker
        	t.sott_nsec();//metodo per la sottoscrzione per n secondi
        	if(verifica==true){
        		System.out.println("\ndesottoscrizione del titolo---> UNS "+tick);
        		//t.sendCommand("UNS"+tick);//mandiamo la desottoscrizione del titolo
				}
			}
        else 
        	System.out.println(t.RLine()); 
		
		t.closeConnection();//chiusura della connessione
		}
}