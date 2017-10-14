/*
*
*	Tyler Paquet & John Marker
*	Project 2 Client
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
				System.out.println("");
				String userInput = cons.readLine("Enter your command: ");
				if(userInput.equals("exit"))
				{
					break;
				}
				else if(userInput.length() < 4)
				{
					System.out.println("That is not a known command");
				}
				else if(userInput.length() == 4 && userInput.equals("send"))
				{
					System.out.println("Must include filename to download");
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
					int fileSizeInt = Integer.parseInt(fileSize2);
					//System.out.println("File size int: " + fileSizeInt); //prints file size in bytes
					
					
				//receive file packets and save to a new file
					int numPackets = ((int)fileSizeInt/1016) + 1;
					int numBytesLastPacket = (int)fileSizeInt%1016;
						
					File file = new File("downloaded" + userInput.substring(5, userInput.length()));
					FileOutputStream fos = new FileOutputStream(file);
						
					for(int i = 0; i < numPackets; i++)
					{
					CustomPacket customPacket = null;
						//System.out.println("Receiving Packet #" + (i+1) + "/" + numPackets);
						if(i == numPackets - 1)
						{
							byte[] receiveData = new byte[numBytesLastPacket+8];								
							DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
							clientSocket.receive(receivePacket);
							customPacket = new CustomPacket(receivePacket);
							System.out.println("Got packet, sequence #"+customPacket.getSequenceNum());
                                                        fos.write(customPacket.getLastPacketData(numBytesLastPacket+8));
								
						}
						else
						{
							byte[] receiveData = new byte[1024];
							DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
							clientSocket.receive(receivePacket);
							customPacket = new CustomPacket(receivePacket);
							System.out.println("Got packet, sequence #"+customPacket.getSequenceNum());
							fos.write(customPacket.getPacketData());
						}
												
						int sequenceNum = customPacket.getSequenceNum();
						byte[] bytes = new byte[ 4 ];
						bytes[3] = (byte) (sequenceNum & 0xFF);
						bytes[2] = (byte) ((sequenceNum >> 8) & 0xFF);
						bytes[1] = (byte) ((sequenceNum >> 16) & 0xFF);
						bytes[0] = (byte) ((sequenceNum >> 24) & 0xFF);
						DatagramPacket ackPacket = new DatagramPacket( bytes, bytes.length, ipAddress, portNum);
						System.out.println("Sending ack");
						clientSocket.send(ackPacket);
						
						/*			
						String ackStr = "received packet"; 
						byte[] ack = new byte[ackStr.length()];
						ack = ackStr.getBytes();
						DatagramPacket sendAck = new DatagramPacket(ack, ack.length, ipAddress, portNum);
						System.out.println("Sending ack for packet #" + (i+1));
						clientSocket.send(sendAck);
						*/	
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
