import java.io.*;
import java.net.*;
import java.util.*;

public class Server3 {
	
	private DatagramSocket socket;
	private int portNum;
	private InetAddress ipAddr;
	private FileInputStream fis;
	
	public Server3(int port){
		try {
			System.out.println("Waiting for client connection");
			socket = new DatagramSocket(port);
			
			while(true){
				System.out.println("receiving client message");
				byte[] incBytes = new byte[ 1024 ];
				DatagramPacket incPacket = new DatagramPacket(incBytes,incBytes.length );
				socket.receive(incPacket);
				
				ipAddr = incPacket.getAddress();
				portNum = incPacket.getPort();
				
				String fileName = new String(incPacket.getData(),0,incPacket.getLength());
				System.out.println("Client requesting file: "+ fileName);
				File file = new File(fileName);
				fis = new FileInputStream(file);
				
				//initalize
				if(fis.available() >0){
					byte bytes[] = new byte[1024];
					fis.read(bytes);
					DatagramPacket Packet = new DatagramPacket(bytes,bytes.length,ipAddr,portNum);
					socket.send(Packet);
					System.out.println("Sending packet");
					
				}
			}
			
			
		}catch(IOException e){}
		
		
	}
	public static void main(String[] args){
		Server3 server = new Server3(Integer.valueOf(args[0]));
	}
}
