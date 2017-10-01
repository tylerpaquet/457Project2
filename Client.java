/*
*
*	Tyler Paquet & John Marker
*	Project 3 Client
*	Due: 10/16/2017
*
*/

import java.io.*;
import java.net.*;
import java.nio.*;
import java.nio.channels.*;

class Client
{
	public static void main(String args[])
	{
		Console cons = System.console();
		int portNum = Integer.parseInt(cons.readLine("Enter a port number: "));
		String ipAddr = cons.readLine("Enter an IP address: ");
		
		try
		{
			DatagramSocket clientSocket = new DatagramSocket();
			InetAddress ipAddress = InetAddress.getByName(ipAddr);
			
			System.out.println("");
			System.out.println("Commands: 'exit', 'send <filename>'");
			
			while(true)
			{
				String userInput = cons.readLine("Enter your command: ");
				if(userInput.equals("exit"))
				{
					break;
				}
				else if(userInput.length() < 4)
				{
					System.out.println("That is not a known command");
				}
				else if(userInput.substring(0, 4).toLowerCase().equals("send"))
				{
					//Send file request to server
					byte[] sendData = new byte[1024];
					sendData = userInput.getBytes();
					DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, ipAddress, portNum);
					clientSocket.send(sendPacket);
					
					//receive file packets (1024 bytes each) and save to a new file
					//Sliding window of 5 packets
				}
				else
				{
					System.out.println("That is not a known command");
				}
			}
			
			clientSocket.close();
		}
		catch(IOException e)
		{
			System.out.println("Got an IO Exception");
		}
	}
}
