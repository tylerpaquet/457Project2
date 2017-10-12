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
		SequenceNumToBytes();
		//convert int id to bytes id, add to "finalPacket"
		IdToBytes();
		//add "packetData" to the end of the "finalPacket"
		for( int i = 0; i <packetData.length-8;i++) {
		finalPacket[i+8]=packetData[i];	
		}
		
		packet.setData(finalPacket);
	}
	//Client constructor for CustomPacket 
	public CustomPacket(DatagramPacket packet) {
		this.packet = packet;
		// convert sequenceNum bytes to sequenceNum int
		setSequenceNum();
		// convert id bytes to id int, set to packet
		setId();
	}
	public int bytesToInt( byte[] bytes) {
		return bytes[3] & 0xFF | 
			(bytes[2] & 0xFF) << 8 |
			(bytes[1] & 0xFF) << 16 |
			(bytes[0] & 0xFF) << 24;
	}
	
	public void setSequenceNum() {
		byte[] bytes = Arrays.copyOfRange(packet.getData(),0,4);
		sequenceNum = bytesToInt(bytes);
	}
	
	 public void setId() {
                byte[] bytes = Arrays.copyOfRange(packet.getData(),4,8);
                id = bytesToInt(bytes);
        }	
	
	public void SequenceNumToBytes() {
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

	public void IdToBytes() {
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
	public byte[] getPacketData() {

	byte[] bytes = Arrays.copyOfRange(packet.getData(),8, 1024);
	return bytes;
	}

	public int getId() {

	return id;
	}
	public int getSequenceNUmber() {

	return sequenceNum;
	}
}
