import java.io.*;
import java.net.*;
import java.nio.*;
import java .nio.channels.*;

class udpserver_timeout
{
	public static void main(String args[])
	{
		try
		{
			DatagramSocket serverSocket = new DatagramSocket(2222);
			serverSocket.setSoTimeout(5000);
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
					System.out.println("");
				}
				catch(SocketTimeoutException e)
				{
					System.out.println("Timeout reached. Received nothing.");
				}
			}
		}
		catch(IOException e)
		{
			System.out.println("Got exception");
		}
	}
}
