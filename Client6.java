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

class Client6
{	
	   private static String getIP() {
        String ipAddr = "";
        boolean validIPAddr = false;
        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Enter an IP address: ");
        while (validIPAddr == false) {
            try {
                ipAddr = input.readLine();
                String[] pieces = ipAddr.split("\\.");

                for (String s : pieces) {
                    int i = Integer.parseInt(s);
                    if ((i < 0) || i > 255) {
                        System.out.println("IP address parts cannot be smaller than 0 or larger than 255. \nEnter an IP Address: ");
                        break;
                    }
                }

                if (ipAddr == null || ipAddr.isEmpty()) {
                    System.out.println("IP address is empty. \nEnter an IP Address: ");
                } else if (pieces.length != 4) {
                    System.out.println("IP address does not contain correct number of parts. \nEnter an IP Address: ");
                } else if (ipAddr.endsWith(".")) {
                    System.out.println("IP addresses cannot end with a period. \nEnter an IP Address: ");
                } else {
                    validIPAddr = true;
                }


            } catch (NumberFormatException e) {
                System.out.println("Invalid IP. \nEnter an IP address: ");
            } catch (IOException e) {
                System.out.println("Invalid IP. \nEnter an IP address: ");
            }
        }
        return ipAddr;
    }
	private static int getPort() {
        int port = 0;
        boolean valid = false;
        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Enter a port number: ");

        while (!valid) {
            try {
                port = Integer.parseInt(input.readLine());
                if (port > 1024 && port < 65536) {
                    valid = true;
                } else {
                    System.out.println("Invalid Port. \nEnter a port number: ");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid Port. \nEnter a port number: ");
            } catch (IOException e) {
                System.out.println("Invalid Port. \nEnter a port number: ");
            }
        }
        System.out.println("connecting to port: " + port);
        return port;
    }
	public static void main(String args[])
	{
		//Console cons = System.console();
		//int portNum = Integer.parseInt(cons.readLine("Enter a port number: "));
		//String ipAddr = cons.readLine("Enter an IP address: ");
		int portNum = getPort();
		String ipAddr = getIP();
		
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
					System.out.println("Sending file send packet");
					
					//serverSocket.setSoTimeout(2000);
					//receive message ack
					byte[] messageAck = new byte[1024];
					DatagramPacket messageReceiveAck = new DatagramPacket(messageAck, messageAck.length);
					clientSocket.receive(messageReceiveAck);
					String messageAckString = new String(messageReceiveAck.getData());
					System.out.println("Received ack from file send packet");
					
					System.out.println("");
					
					
					
				//Receive File Size from Server
					byte[] receiveFileSize = new byte[1024];
					DatagramPacket receiveFileSizePacket = new DatagramPacket(receiveFileSize, receiveFileSize.length);
					clientSocket.receive(receiveFileSizePacket);
					String fileSize = new String(receiveFileSizePacket.getData());
					int length = receiveFileSizePacket.getLength();
					String fileSize2 = fileSize.substring(0, length);
					int fileSizeInt = Integer.parseInt(fileSize2);
					//System.out.println("File size int: " + fileSizeInt); //prints file size in bytes
					System.out.println("Received file size packet");
					
					//send file size ack
					String fileSizeStr = "received file size";
					byte[] fileSizeData = new byte[userInput.length()];
					fileSizeData = fileSizeStr.getBytes();
					DatagramPacket fileSizePacket = new DatagramPacket(fileSizeData, fileSizeData.length, ipAddress, portNum);
					clientSocket.send(fileSizePacket);
					System.out.println("Sending ack for file size packet");
					
					System.out.println("");
					
					
				//receive file packets and save to a new file
					int numPackets = ((int)fileSizeInt/1016) + 1;
					int numBytesLastPacket = (int)fileSizeInt%1016;
					//System.out.println("Number of packets = " + numPackets);
					//System.out.println("Number of bytes in final packet = " + numBytesLastPacket);
						
					File file = new File("downloaded" + userInput.substring(5, userInput.length()));
					FileOutputStream fos = new FileOutputStream(file);
					
					System.out.println("Will receive " + numPackets + " packets for entire file");
					
					int packetsReceived = 0;
					int[] deliveredArray = new int[numPackets];
					int nextInOrder = 0;
					CustomPacket[] outOfOrder = new CustomPacket[4];
						
					while(packetsReceived < numPackets)
					{
						CustomPacket customPacket = null;
						
						//receive packet
						byte[] receiveData = new byte[1024];
						DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
						clientSocket.receive(receivePacket);
						customPacket = new CustomPacket(receivePacket);
						System.out.println("Receiving Packet #" + customPacket.getId());
						
						//If we haven't already received this packet then either write to file
						//if in order or save to outOfOrder array
						//else ignore received data
						if(deliveredArray[customPacket.getId()] == 0)
						{
							deliveredArray[customPacket.getId()] = 1;
							packetsReceived++;
							
							//in order: write to file
							if(customPacket.getId() == nextInOrder)
							{	
								//write all packets except last
								if(customPacket.getId() != (numPackets - 1))
								{
									//write packet data to file
									fos.write(customPacket.getPacketData());
									nextInOrder++;
								}
								//writes last packet
								else
								{
									fos.write(customPacket.getLastPacketData(numBytesLastPacket + 8));
									nextInOrder++;
								}
								
								//check for any stored in order packets following received packet
								int i = ((customPacket.getId() % 4) + 1) % 4;
								while(outOfOrder[i] != null)
								{
									System.out.println("Writing saved in order packets to file");
									
									//write all packets except last
									if(outOfOrder[i].getId() != (numPackets - 1))
									{
										fos.write(outOfOrder[i].getPacketData());
										System.out.println("Writing packet #" + outOfOrder[i].getId());
										nextInOrder++;
										
										//free space in outOfOrder array for new outOfOrder packets
										outOfOrder[i] = null;
										
										//increment index of array keeping it between 0-3 (cyclic ordering)
										i = (i + 1) % 4;
									}
									//writes last packet
									else
									{
										fos.write(customPacket.getLastPacketData(numBytesLastPacket + 8));
										nextInOrder++;
										
										//free space in outOfOrder array for new outOfOrder packets
										outOfOrder[i] = null;
										
										//increment index of array keeping it between 0-4 (cyclic ordering)
										i = (i + 1) % 4;
									}
								}
							}
							//out of order: add to outOfOrder array
							else
							{
								outOfOrder[customPacket.getId() % 4] = customPacket;
								System.out.println("Out of order. Adding packet # " + customPacket.getId() + " to outOfOrder array");
							}
						}
						
						if(deliveredArray[deliveredArray.length-1] ==1) {
							break;
						}
						//send ack	
						String ackStr = "received packet " + customPacket.getId();
						byte[] ack = new byte[ackStr.length()];
						ack = ackStr.getBytes();
						DatagramPacket sendAck = new DatagramPacket(ack, ack.length, ipAddress, portNum);
						System.out.println("Sending ack for packet #" + customPacket.getId());
						clientSocket.send(sendAck);
							
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
