import java.net.DatagramPacket;
import java.util.Arrays;

public class CustomPacket {

 	int sequenceNum;
 	int id;
	DatagramPacket packet;
	
	byte[] packetData;
	byte[] finalPacket;
	
	//Server constructor for CustomPacket
	public CustomPacket(int sequenceNum, int id, byte[] packetData, DatagramPacket packet) {
		this.sequenceNum = sequenceNum;
		this.id = id;
		this.packetData = packetData;
		this.packet = packet;

		finalPacket = new byte[1024];
		
		//change server and client side to read and write packets of size [1014]
		
		//convert sequenceNum to bytes sequenceNum, add to "finalPacket"
		convertSequenceNumToBytes();
		//convert int id to bytes id, add to "finalPacket"
		convertIdToBytes();
		//add "packetData" to the end of the "finalPacket"
		for( int i = 0; i <data.length-8;i++) {
		finalPacket[i+8]=data[i];	
		}
		
		//packet.setData(finalPacket);
	}
	 
	public CustomPacket(DatagramPacket packet) {
		this.packet = packet;
		//methods for constrcution
		// convert id bytes to id int
		// convert sequenceNum bytes to sequenceNum int

	}
	
	
	public void convertSequenceNumToBytes() {
                byte[] bytes = new byte[4];
                bytes[3] = (byte) (sequenceNum & 0xFF);
                bytes[2] = (byte) ((sequenceNum >> 8) & 0xFF);
                bytes[1] = (byte) ((sequenceNum >> 16) & 0xFF);
                bytes[0] = (byte) ((sequenceNum >> 24) & 0xFF);
                for ( int i = 0; i < 4; i++ )
                {
                        this.finalPacket[i] = bytes[i];
                }
        }

	public void convertIdToBytes() {
		byte[] bytes = new byte[4];
		bytes[3] = (byte) (id & 0xFF);
		bytes[2] = (byte) ((id >> 8) & 0xFF);
		bytes[1] = (byte) ((id >> 16) & 0xFF);
		bytes[0] = (byte) ((id >> 24) & 0xFF);
		for ( int i = 0; i < 4; i++ )
		{
			this.finalPacket[i+4] = bytes[i];
		}
	}
}
