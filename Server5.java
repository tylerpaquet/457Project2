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

class Server5
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
			
			//declare variables outside of try statement to avoid scoping issue
				int length = 0;
				String fileToSend = null;
				File myFile = null;
				FileInputStream fis = null;
				InetAddress ipAddress = null;
				int portnumber = 0;
				long fileSize = 0;
				
			//Receive message from client
				serverSocket.setSoTimeout(5000);
				System.out.print("Waiting for send call from client..");
				while(true)
				{
					try
					{
						byte[] receiveData = new byte[1024];
						DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
						serverSocket.receive(receivePacket);
						String sentence = new String(receivePacket.getData());
						System.out.println("");
						System.out.println("From client: " + sentence);
				
						length = receivePacket.getLength();
						fileToSend = sentence.substring(5, length);
						myFile = new File(fileToSend);
						fis = new FileInputStream(myFile);
						ipAddress = receivePacket.getAddress();
						portnumber = receivePacket.getPort();
				
				
						fileSize = myFile.length();
						//System.out.println("File size: " + fileSize);
						
						/*
						logic to send message ack
						*/
						
						break;
					}
					catch(SocketTimeoutException e)
					{
						System.out.print(".");
					}
				}
				
	
			//Send file size to client
				//send file size packet
				String fileSizeStr = Long.toString(fileSize);
				byte[] sendFileSize = new byte[fileSizeStr.length()];
				sendFileSize = fileSizeStr.getBytes();
				DatagramPacket sendFileSizePacket = new DatagramPacket(sendFileSize, sendFileSize.length, ipAddress, portnumber);
				serverSocket.send(sendFileSizePacket);
				
				/*
				logic to receive file size ack
				*/
				
				
			//Send file to client in packet sizes of 1024 bytes Sliding window of 5 packets
				
				int numPackets = ((int)fileSize/1016) + 1;
				int numBytesLastPacket = (int)fileSize%1016;
				//System.out.println("Number of packets = " + numPackets);
				//System.out.println("Number of bytes in final packet = " + numBytesLastPacket);
				
				System.out.println("File will be sent in " + numPackets + " packets");
				
				int packetsSent = 0;
				int acksReceived = 0;
				CustomPacket[] window = new CustomPacket[5];
				int[] deliveredArray = new int[numPackets];
				int frontOfWindow = 0;
				int frontOfWindowID = 0;
				
				serverSocket.setSoTimeout(2000);
				
				while(packetsSent < numPackets)
				{
					while(packetsSent < 5 && packetsSent != numPackets)
					{
						byte[] customData = new byte[1016];
						fis.read(customData);
						byte[] sendData = new byte[1024];
						DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, ipAddress, portnumber);
						CustomPacket customPacket = new CustomPacket(packetsSent, packetsSent, customData, sendPacket);
						window[packetsSent] = customPacket;
						System.out.println("Sending packet #" + customPacket.getId());
						serverSocket.send(customPacket.packet);
						packetsSent++;
					}
					
					//ack loop. Stay in loop until front of window is acknowledged. Then break to send next packet
					serverSocket.setSoTimeout(2000);
					while(true)
					{
						//attempt to receive ack, if timeout then something needs resent
						try
						{
							byte[] ack = new byte[1016];
							DatagramPacket receiveAck = new DatagramPacket(ack, ack.length);
							serverSocket.receive(receiveAck);
							String ackstring = new String(receiveAck.getData());
							int ackstringlength = receiveAck.getLength();
							String packNum = ackstring.substring(16, ackstringlength);
							int packNumInt = Integer.parseInt(packNum);
							System.out.println("Received ack from packet #" + packNum);
							acksReceived++;
						
							//mark packet delivered (used for sliding window logic)
							deliveredArray[packNumInt] = 1;
							
							//if frontOfWindow is confirmed delivered, update frontOfWindow, set temp data to null
							//(making room for next temp data), break to send next packet
							//if(deliveredArray[window[frontOfWindow].getId()] == 1)
							if(deliveredArray[frontOfWindowID] == 1)
							{
								System.out.println("Moving window..");
								frontOfWindow = (frontOfWindow + 1) % 5;
								frontOfWindowID++;
								window[packNumInt % 5] = null;
								break;
							}
						}
						
						//resend packet (frontOfWindow)
						catch(SocketTimeoutException e)
						{
							CustomPacket customPacket = window[frontOfWindow];
							System.out.println("Resending packet #" + customPacket.getId());
							serverSocket.send(customPacket.packet);
						}
					}
					
					
					//if file was sent in 5 packets or less then ignore following if else
					if(packetsSent == numPackets)
					{
						break;
					}
					
					if(packetsSent == numPackets - 1)
					{
						//sends last packet because it will contain difference amount of bytes
						byte[] customData = new byte[numBytesLastPacket];
						fis.read(customData);
						byte[] sendData = new byte[numBytesLastPacket + 8];
						DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, ipAddress, portnumber);
						CustomPacket customPacket = new CustomPacket(packetsSent, packetsSent, customData, sendPacket);
						System.out.println("Sending packet #" + customPacket.getId());
						serverSocket.send(customPacket.packet);
						packetsSent++;
					}
					else
					{
						//sends all other packets
						byte[] customData = new byte[1016];
						fis.read(customData);
						byte[] sendData = new byte[1024];
						DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, ipAddress, portnumber);
						CustomPacket customPacket = new CustomPacket(packetsSent, packetsSent, customData, sendPacket);
						System.out.println("Sending packet #" + customPacket.getId());
						serverSocket.send(customPacket.packet);
						packetsSent++;
					}
					
					
				}
				
				//Consume extra acks
				//serverSocket.setSoTimeout(2000);
				while(acksReceived < numPackets)
				{
					//attempt to receive ack, if timeout then something needs resent
					try
					{
						byte[] ack = new byte[1016];
						DatagramPacket receiveAck = new DatagramPacket(ack, ack.length);
						serverSocket.receive(receiveAck);
						String ackstring = new String(receiveAck.getData());
						int ackstringlength = receiveAck.getLength();
						String packNum = ackstring.substring(16, ackstringlength);
						int packNumInt = Integer.parseInt(packNum);
						System.out.println("Received ack from packet #" + packNum);
						acksReceived++;
						deliveredArray[packNumInt] = 1;
						
						//if frontOfWindow is confirmed delivered, update frontOfWindow, set temp data to null
						//if(deliveredArray[window[frontOfWindow].getId()] == 1)
						if(deliveredArray[frontOfWindowID] == 1)
						{
							System.out.println("Moving window..");
							frontOfWindow = (frontOfWindow + 1) % 5;
							frontOfWindowID++;
							window[packNumInt % 5] = null;
						}
					}
					
					//resend packet (frontOfWindow)
					catch(SocketTimeoutException e)
					{
						CustomPacket customPacket = window[frontOfWindow];
						System.out.println("Resending packet #" + customPacket.getId());
						serverSocket.send(customPacket.packet);
					}
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
