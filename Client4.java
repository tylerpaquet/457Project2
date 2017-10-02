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

class Client4
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
					byte[] sendData = new byte[userInput.length()];
					sendData = userInput.getBytes();
					DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, ipAddress, portNum);
					clientSocket.send(sendPacket);
					
					
					//Receive File Size from Server
					byte[] receiveFileSize = new byte[1024];
					DatagramPacket receiveFileSizePacket = new DatagramPacket(receiveFileSize, receiveFileSize.length);
					clientSocket.receive(receiveFileSizePacket);
					String fileSize = new String(receiveFileSizePacket.getData());
					int length = receiveFileSizePacket.getLength();
					String fileSize2 = fileSize.substring(0, length);
					//System.out.println("File size string: " + fileSize);
					int fileSizeInt = Integer.parseInt(fileSize2);
					//System.out.println("File size int: " + fileSizeInt); //prints file size in bytes
					
					
					//receive file packets (1024 bytes each) and save to a new file
					
					//File can be sent in one packet
					if(fileSizeInt <= 1024)
					{
						File file = new File("downloaded" + userInput.substring(5, userInput.length()));
						FileOutputStream fos = new FileOutputStream(file);
					
						byte[] receiveData = new byte[1024];
						DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
						clientSocket.receive(receivePacket);
					
						int packetSize = receivePacket.getLength();
						fos.write(receiveData, 0, packetSize);
					}
					
					//File must be sent using multiple packets
					else
					{
						int numPackets = ((int)fileSizeInt/1024) + 1;
						int numBytesLastPacket = (int)fileSizeInt%1024;
						//System.out.println("Number of packets = " + numPackets);
						//System.out.println("Number of bytes in final packet = " + numBytesLastPacket);
						
						File file = new File("downloaded" + userInput.substring(5, userInput.length()));
						FileOutputStream fos = new FileOutputStream(file);
						
						byte[] fileData = new byte[fileSizeInt];
						int offset = 0;
						
						for(int i = 0; i < numPackets; i++)
						{
							if(i == numPackets - 1)
							{
								byte[] receiveData = new byte[1024];
								DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
								clientSocket.receive(receivePacket);
								for(int j = 0; j < numBytesLastPacket; j++)
								{
									fileData[j+offset] = receiveData[j];
								}
							}
							else
							{
								byte[] receiveData = new byte[1024];
								DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
								clientSocket.receive(receivePacket);
								for(int j = 0; j < 1024; j++)
								{
									fileData[j+offset] = receiveData[j];
								}
								offset += 1024;
							}
							
						}
						fos.write(fileData);
					}
					
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
