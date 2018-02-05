
import socket
import time
import threading
import Queue


def worker(socket, stopper, rx_queue):
    """thread worker function"""
    message = ""
    while not stopper.is_set():
        data = client_socket.recv(512)
        
        if data == "":
            time.sleep(1)
        else:
            message = message + (data)
            if "END " in data:
                print("Message Received");
                rx_queue.put(message)
                message = ""
    return

###########################################
###########################################

if __name__ == "__main__":
    rx_queue =Queue.Queue()     # create a Thread Safe Queue
    stopper = threading.Event() # create a Thread Safe Event
        
    client_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    client_socket.connect(('localhost', 10003)) # connect to HISTORICAL DATA socket
    
    t = threading.Thread(target=worker, args=(client_socket, stopper, rx_queue ) )
    t.start()
    time.sleep( 5 )
    
    
    # http://213.92.13.32/apiwiki/#ch
    # IT3L+0,00%7,395ETFS 3X DAILY LONG FTSE MIB17:35:36mercato chiuso0JE00B8DVWK97
    # CANDLE titolo numero_giorni periodo_candela
    
    print("Extract 1 hour candles")
    #cmd="CANDLE IT3L 40 3600" 
    #client_socket.send(cmd+"\n")
    #time.sleep( 5 )
    
    #cmd="CANDLE IT3S 40 3600" 
    #client_socket.send(cmd+"\n")
    #time.sleep( 5 )
    
    print("Extract EOD pricesfor last 5 years")
    cmd="CANDLE MSE 1250 86400" 
    client_socket.send(cmd+"\n")
    time.sleep( 5 )
    
    print("Wait for 15 seconds")
    time.sleep( 15 )
    
    stopper.set()   # force thread stop
    t.join();
    client_socket.close()
    
    f =  open("MSE.txt", 'w')
    while not rx_queue.empty():
        message = rx_queue.get()
        f.write(message)
        
    f.close()
    print("Done!")