/*
*
*	Tyler Paquet & John Marker
*	Project 2 Server
*	Due: 10/16/2017
*
*/

import java.io.*;
import java.net.*;
import java.nio.*;
import java.nio.channels.*;
import static java.lang.Math.toIntExact;

class Server4
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
				
				int numPackets = ((int)fileSize/1016) + 1;
				int numBytesLastPacket = (int)fileSize%1016;
				
				System.out.println("File will be sent in " + numPackets + " packets");

				final int windowSize = 5;
				CustomPacket[] window = new CustomPacket[windowSize];
				int seq = 0;
				int id = (int)(Math.random() * 100);
				//need a better way of getting random id...
				int acksReceived = 0;
				int packetsSent = 0;
				
				while(packetsSent < numPackets)
				{
					//create window
					while(seq < windowSize && seq != numPackets)
					{	
						byte[] customData = new byte[1016];
						fis.read(customData);	
						byte[] sendData = new byte[1024];
						DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, ipAddress, portnumber);
						CustomPacket customPacket = new CustomPacket(seq, id, customData, sendPacket);
						System.out.println("Sending packet #" + (packetsSent+1));

						window[seq] = customPacket;
						seq++;
						//packetsSent++;
						
					}
					//send window
					while(seq < windowSize && seq !=numPackets) {
						serverSocket.send(window[seq].packet);
						seq++;
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
