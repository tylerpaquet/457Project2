import java.io.*;
import java.net.*;
import java.nio.*;
import java.nio.channels.*;

class udpclient3
{
	public static void main(String args[])
	{
		try
		{
			DatagramSocket clientSocket = new DatagramSocket();
			Console cons = System.console();
			String userInput = cons.readLine("Enter your message: ");
			byte[] sendData = new byte[userInput.length()];
			sendData = userInput.getBytes();
			DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName("127.0.0.1"), 2222);
			clientSocket.send(sendPacket);
		}
		catch(IOException e)
		{
			System.out.println("Got an IO Exception");
		}
	}
}
