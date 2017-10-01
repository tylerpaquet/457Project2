/*
 *
 *	Tyler Paquet & John Marker
 *	Project 3 Server
 *	Due: 10/16/2017
 * 
 * */

import java.io.*;
import java.net.*;
import java.nio.*;
import java.nio.channels.*;

class Server2
{
	final int windowSize = 5;
	DatagramSocket socket = null;
        InetAddress ipAddress;
        FileInputStream fis = null;
        Console cons = System.console();
        int portNum = Integer.parseInt(cons.readLine("Enter a port number: "));
        int numberOrder;
        Packet[] window;
      	public Server2(){
	
		try
		{
			window = new Packet[windowSize];
			socket = new DatagramSocket(portNum);
			byte[] incomingData = new byte[1024];

			while(true)
			{
				//Receive message from client
				DatagramPacket incomingPacket = new DatagramPacket(incomingData, incomingData.length);
				socket.receive(incomingPacket);
				ipAddress = incomingPacket.getAddress();
				String clientSentence = new String(incomingPacket.getData());
				System.out.println("From client: " + clientSentence);
				String fileToSend = clientSentence.substring(5, clientSentence.length());
				//Send file to client in packet sizes of 1024 bytes
				File file = new File(fileToSend);
				
				try {
					fis = new FileInputStream(file);
				} catch(Exception e){}
				while(true){
					try {
					
						byte[] bytes = new byte[1024];
						fis.read(bytes);
						DatagramPacket packet = new DatagramPacket(bytes,bytes.length,ipAddress,portNum);	   	 
						socket.send(packet);			
					} catch(Exception e){}
				}
				
				//Sliding window of 5 packets
			}
		}
		catch(IOException e)
		{
			System.out.println("Got exception");
		}
	}
	public void createWindow() {
        for (int i=0; i<windowSize;i++) {

                }

        }
        public void send() {
        for (int i=0; i<windowSize;i++) {
                /*socket.send(packet)*/
                }

        }
        public boolean isWindowEmpty() {
        return false;
        }
        public static void main(String args[])
        {       
                Server server = new Server();
        }

}
