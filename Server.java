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
			DatagramSocket serverSocket = new DatagramSocket(portNum);
			
			while(true)
			{
				//Receive message from client
				byte[] receiveData = new byte[1024];
				DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
				serverSocket.receive(receivePacket);
				String sentence = new String(receivePacket.getData());
				System.out.println("From client: " + sentence);
				
				String fileToSend = sentence.substring(5, sentence.length());
				File myFile = new File(fileToSend);
				
				//Send file to client in packet sizes of 1024 bytes
				//Sliding window of 5 packets
				byte[] sendData = new byte[1024];
			}
		}
		catch(IOException e)
		{
			System.out.println("Got exception");
		}
	}
}
