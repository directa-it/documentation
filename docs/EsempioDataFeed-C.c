#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <string.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <netdb.h>

//Metodo per la gestione errori,non fondamentale ai fini della connessione
void error(const char *msg)
{
    perror(msg);
    exit(0);
}

//La nostra classe main
int main(int argc, char *argv[])
{
    int sockfd, portno, n; 
    struct sockaddr_in serv_addr;
    struct hostent *server;
	int i=0;
    char buffer[256];
    char command[256];
    int nb;
    int nLine=100;
	
   printf("Connessione al DATAFEED\n\n");
   
   char host[20];
   sprintf(host,"localhost");
    
    //variabile contente il numero della porta
    portno=10001;
    //apertura del socket
    sockfd = socket(AF_INET, SOCK_STREAM, 0);
    if (sockfd < 0) 
        error("ERROR opening socket");
    	//variabile per settare l'host in questo caso localhost ovvero 127.0.0.1
      	server=gethostbyname(host); 	
    if (server == NULL) {
        fprintf(stderr,"ERROR, no such host\n");
        exit(0);
    }
    
    //gestione della connessione al socket
    bzero((char *) &serv_addr, sizeof(serv_addr));
    
    serv_addr.sin_family = AF_INET;
    
    bcopy((char *)server->h_addr, 
         (char *)&serv_addr.sin_addr.s_addr,
         server->h_length);
         
    serv_addr.sin_port = htons(portno);
    
    //gestione per eventuali errori nella connessione al socket
    if (connect(sockfd, (struct sockaddr *) &serv_addr, sizeof(serv_addr)) < 0) 
    	error("ERROR connecting");
    bzero(buffer,256);
    n = write(sockfd, command, strlen(command));
    
    if (n < 0) 
         error("ERROR writing to socket");
         
    bzero(buffer,256);
    
    n = read(sockfd, buffer, 255);
    if (n < 0) 
         error("ERROR reading from socket");
          
    //Stampa di avvenuta connessione     
    printf("CONNESSIONE AVVENUTA sulla porta %d\n",portno);
    printf("%s\n", buffer);
    
    //send del client del comando in questo caso esempio di sottoscrizione di FCA
    nb=sprintf(buffer,"SUB FCA\n");
    write(sockfd,buffer,nb);
    
    while(i<=nLine){
    	//lettura e stampa del buffer ovvero la risposta del server
    	//qui prova per le 100 prime righe,poi esce dal ciclo
    	nb=read(sockfd,buffer,256);
    	buffer[nb]=0;
    	printf("%d messaggio da server: %s\n",i,buffer);
    	i++;
    }
    //chiusura della connessione al socket.  
    printf("Chiusura della connessione\n"); 
    close(sockfd);
    return 0;
}