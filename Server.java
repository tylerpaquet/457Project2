/*
*
*	Tyler Paquet & John Marker
*	Project 3 Server
*	Due: 10/16/2017
*
*/

import java.io.*;
import java.net.*;
import java.nio.*;
import java.nio.channels.*;

class Server
{
	public static void main(String args[])
	{
		Console cons = System.console();
		int portNum = Integer.parseInt(cons.readLine("Enter a port number: "));
		
		try
		{
			DatagramChannel dc = DatagramChannel.open();
			dc.bind(new InetSocketAddress(portNum));
			while(true)
			{
				//Receive message from client
				ByteBuffer buffer = ByteBuffer.allocate(10000);
				SocketAddress clientaddr = dc.receive(buffer);
				String clientSentence = new String(buffer.array());
				System.out.println("From client: " + clientSentence);
				String fileToSend = clientSentence.substring(5, clientSentence.length());
				
				//Send file to client in packet sizes of 1024 bytes
				//Sliding window of 5 packets
			}
		}
		catch(IOException e)
		{
			System.out.println("Got exception");
		}
	}
}
