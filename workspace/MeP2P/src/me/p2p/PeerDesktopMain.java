package me.p2p;
public class PeerDesktopMain {
	static final String TAG = "PeerMain";
	static final String filePath = "D:/PeerData";
	
	public static void main(String[] args) {
		String bootstrapAdress = "192.168.0.106";
		
		Peer peer = new Peer(filePath, "tobeNguyen", bootstrapAdress);
		// listen for request;
		peer.listenRequest();
		// send join request
		peer.joinRequest();
	}
}