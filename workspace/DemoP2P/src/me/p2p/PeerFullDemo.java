package me.p2p;



public class PeerFullDemo {
	static final String TAG = "PeerMain";
	static final String filePath = "E:/PeerData";
	
	public static void main(String[] args) {
		String bootstrapAdress = "192.168.1.51";
		
		Peer peer = new Peer(filePath, "tobeNguyen", bootstrapAdress);
		peer.joinRequest();
	}
}
