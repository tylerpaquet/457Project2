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
import static java.lang.Math.toIntExact;

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
				
				int length = receivePacket.getLength();
				String fileToSend = sentence.substring(5, length);
				File myFile = new File(fileToSend);
				FileInputStream fis = new FileInputStream(myFile);
				InetAddress ipAddress = receivePacket.getAddress();
				int portnumber = receivePacket.getPort();
				
				
				long fileSize = myFile.length();
				//System.out.println("File size: " + fileSize);
				
	
				//Send file size to client
				String fileSizeStr = Long.toString(fileSize);
				byte[] sendFileSize = new byte[fileSizeStr.length()];
				sendFileSize = fileSizeStr.getBytes();
				DatagramPacket sendFileSizePacket = new DatagramPacket(sendFileSize, sendFileSize.length, ipAddress, portnumber);
				serverSocket.send(sendFileSizePacket);
				
				
				//Send file to client in packet sizes of 1024 bytes Sliding window of 5 packets
				
				//File fits in one packet
				if(fileSize <= 1024)
				{
					byte[] sendData = new byte[(int)fileSize];
					fis.read(sendData);
					DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, ipAddress, portnumber);
					serverSocket.send(sendPacket);
				}
				
				//File must be sent using multiple packets
				else
				{
					int numPackets = ((int)fileSize/1024) + 1;
					int numBytesLastPacket = (int)fileSize%1024;
					//System.out.println("Number of packets = " + numPackets);
					//System.out.println("Number of bytes in final packet = " + numBytesLastPacket);
					
					//byte[] fileData = new byte[(int)fileSize];
					//fis.read(fileData);
					//int offset = 0;
					
					for(int i = 0; i < numPackets; i++)
					{
						System.out.println("Sending Packet #" + (i+1) + "/" + numPackets);
						if(i == numPackets - 1)
						{
							byte[] sendData = new byte[numBytesLastPacket];
							fis.read(sendData);
							DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, ipAddress, portnumber);
							serverSocket.send(sendPacket);
						}
						else
						{
							byte[] sendData = new byte[1024];
							fis.read(sendData);
							DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, ipAddress, portnumber);
							serverSocket.send(sendPacket);
						}
						
						byte[] ack = new byte[1024];
						DatagramPacket receiveAck = new DatagramPacket(ack, ack.length);
						serverSocket.receive(receivePacket);
						
						
						
					}
					
				}
				
			}
		}
		catch(IOException e)
		{
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}
}
