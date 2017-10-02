import java.io.*;
import java.net.*;
import java.util.*;

public class Client3 {
	private DatagramSocket socket;
	private int portNum;
	private InetAddress ipAddr;
	private File file;
	private FileOutputStream fos;
	public Client3(int port, String ip, String fileName){
		try {
			socket = new DatagramSocket();

			ipAddr = InetAddress.getByName(ip);
			portNum = port;

			byte[] outBytes = fileName.getBytes();
			DatagramPacket outPacket = 
					new DatagramPacket( outBytes, outBytes.length,ipAddr,portNum);
			System.out.println("requesting file from server...");
			socket.send(outPacket);	

			file = new File("downloaded" + fileName);
			fos = new FileOutputStream(file);
			try{
				byte[] incBytes = new byte[1024];
				DatagramPacket incPacket = new DatagramPacket( incBytes, incBytes.length );
				socket.receive(incPacket);
				fos.write(incBytes);
			}
			catch(IOException e){}
		}catch(IOException e){
		}
	}

	public static void main(String[] args){
		Client3 client = new Client3(Integer.valueOf(args[0]), args[1],args[2]);
	}
}
