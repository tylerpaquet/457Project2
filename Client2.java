/*
 * *
 * *	Tyler Paquet & John Marker
 * *	Project 3 Client
 * *	Due: 10/16/2017
 * *
 * */

import java.io.*;
import java.net.*;
import java.nio.*;
import java.nio.channels.*;

class Client2
{	
	Console cons = System.console();
        int portNum = Integer.parseInt(cons.readLine("Enter a port number: "));
        String ipAddr = cons.readLine("Enter an IP address: ");
        DatagramSocket socket;
        InetAddress ipAddress;
        FileOutputStream fos;
        File file;
	Packet[] window;
	final int windowSize = 5;
	public Client() {

		try
		{
			socket = new DatagramSocket();
			ipAddress = InetAddress.getByName(ipAddr);	
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
					byte[] fileName = userInput.getBytes();
					DatagramPacket sendPacket = new DatagramPacket(fileName,fileName.length,ipAddress,portNum);
					socket.send(sendPacket);
					
					file = new File(fileName + "downloaded");
					file.createNewFile();
					fos = new FileOutputStream(file);
					
					//receive file packets (1024 bytes each) and save to a new file
					try {
						while(true) {
							byte[] incData = new byte[1024];
							DatagramPacket incPacket = new DatagramPacket(incData, incData.length);
							socket.receive(incPacket);
											
							fos.write(incData);
						}
					}catch(IOException e) {}
				}
				else
				{
					System.out.println("That is not a known command");
				}
			}
			
		
		}
		catch(IOException e)
		{
			System.out.println("Got an IO Exception");
		}
	}
	public void moveWindow() {
	for (int i=0; i<this.windowSize-1; i++)
		{
			//this.window[i] = this.window[i+1];
		}
	}
	public static void main(String args[])
        {
        Client client = new Client();
        }

}
