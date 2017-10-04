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
				System.out.println("");
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
				
				int numPackets = ((int)fileSize/1024) + 1;
				int numBytesLastPacket = (int)fileSize%1024;
				//System.out.println("Number of packets = " + numPackets);
				//System.out.println("Number of bytes in final packet = " + numBytesLastPacket);
				
				System.out.println("File will be sent in " + numPackets + " packets");
				
				int packetsSent = 0;
				int acksReceived = 0;
				
				while(packetsSent < numPackets)
				{
					while(packetsSent < 5 && packetsSent != numPackets)
					{
						byte[] sendData = new byte[1024];
						fis.read(sendData);
						DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, ipAddress, portnumber);
						System.out.println("Sending packet #" + (packetsSent+1));
						serverSocket.send(sendPacket);
						packetsSent++;
					}
					
					byte[] ack = new byte[1024];
					DatagramPacket receiveAck = new DatagramPacket(ack, ack.length);
					serverSocket.receive(receiveAck);
					System.out.println("Received ack");
					acksReceived++;
					
					//if file was sent in 5 packets or less then ignore following if else
					if(packetsSent == numPackets)
					{
						break;
					}
					
					if(packetsSent == numPackets - 1)
					{
						byte[] sendData = new byte[numBytesLastPacket];
						fis.read(sendData);
						DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, ipAddress, portnumber);
						System.out.println("Sending packet #" + (packetsSent+1));
						serverSocket.send(sendPacket);
						packetsSent++;
					}
					else
					{
						byte[] sendData = new byte[1024];
						fis.read(sendData);
						DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, ipAddress, portnumber);
						System.out.println("Sending packet #" + (packetsSent+1));
						serverSocket.send(sendPacket);
						packetsSent++;
					}
					
					
				}
				
				//Consume extra acks
				while(acksReceived < packetsSent)
				{
					byte[] ack = new byte[1024];
					DatagramPacket receiveAck = new DatagramPacket(ack, ack.length);
					serverSocket.receive(receiveAck);
					System.out.println("Received ack");
					acksReceived++;
				}
				
				System.out.println("All packets have been sent");
				System.out.println("");
				
			}
		}
		catch(IOException e)
		{
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}
}
