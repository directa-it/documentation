#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <string.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <netdb.h>

//Method for handling errors, not essential for the purposes of the connection
void error(const char *msg)
{
    perror(msg);
    exit(0);
}

//Class Main
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
	
   printf("Connection to the datafeed\n\n");
   
   char host[20];
   sprintf(host,"localhost");
    
    //variable containing the port number
    portno=10001;
    //opening socket
    sockfd = socket(AF_INET, SOCK_STREAM, 0);
    if (sockfd < 0) 
        error("ERROR opening socket");
    	//to set the host variable in this case localhost (127.0.0.1)
      	server=gethostbyname(host); 	
    if (server == NULL) {
        fprintf(stderr,"ERROR, no such host\n");
        exit(0);
    }
    
    //connection management to the socket
    bzero((char *) &serv_addr, sizeof(serv_addr));
    
    serv_addr.sin_family = AF_INET;
    
    bcopy((char *)server->h_addr, 
         (char *)&serv_addr.sin_addr.s_addr,
         server->h_length);
         
    serv_addr.sin_port = htons(portno);
    
    //management for any errors in connection with the socket
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
          
    //Printing successful connection    
    printf("CONNECTION SUCCESSFUL on port %d\n",portno);
    printf("%s\n", buffer);
    
    //send the client's control in this case as the subscription of FCA
    nb=sprintf(buffer,"SUB FCA\n");
    write(sockfd,buffer,nb);
    
    while(i<=nLine){
    	//reading and printing the buffer or the server response
    	//test for the first 100 rows, then exits the loop
    	nb=read(sockfd,buffer,256);
    	buffer[nb]=0;
    	printf("%d Message from server: %s\n",i,buffer);
    	i++;
    }
    //chiusura della connessione al socket.  
    printf("closing the connection to the socket\n"); 
    close(sockfd);
    return 0;
}