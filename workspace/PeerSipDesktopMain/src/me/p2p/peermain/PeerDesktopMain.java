package me.p2p.peermain;

import org.zoolu.sip.address.NameAddress;

import local.ua.UserAgent;
import local.ua.UserAgentListener;
import me.p2p.Peer;
import me.p2p.PeerInfo;
import me.p2p.log.Log;
import me.p2p.specify.PeerCallback;
import me.sip.SipNode;

public class PeerDesktopMain implements PeerCallback, UserAgentListener {
	static final String TAG = "PeerMain";
	static final String filePath = "E:/PeerData";
	SipNode sipNode;

	public static void main(String[] args) {
		String bootstrapAdress = "192.168.3.120";

		Peer peer = new Peer(filePath, "tobeNguyen", bootstrapAdress);
		// listen for request;
		peer.listenRequest();
		
		// send join request;
		peer.joinRequest();
	}

	@Override
	public void onJoined(Peer peer) {
		Log.logToConsole(TAG, "onJoined(): Peer has joined network");
		// TODO Auto-generated method stub
		Log.logToConsole(TAG, "Init SipNode");
		sipNode = new SipNode(peer, this);
		// listen for incomming call;
		Log.logToConsole(TAG, "Listen for incomming call");
		sipNode.listen();
	}

	@Override
	public void onAddedNode(PeerInfo peerInfo) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onUpdated(PeerInfo peerInfo) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onLeaved(PeerInfo peerInfo) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onUaCallIncoming(UserAgent ua, NameAddress caller,
			NameAddress callee) {
		// TODO Auto-generated method stub
		Log.logToConsole(TAG, "onUaCallIncoming(): " + caller.getAddress() + " calling...");
	}

	@Override
	public void onUaCallCancelled(UserAgent ua) {
		// TODO Auto-generated method stub
		Log.logToConsole(TAG, "onUaCallCancelled()");
	}

	@Override
	public void onUaCallRinging(UserAgent ua) {
		// TODO Auto-generated method stub
		Log.logToConsole(TAG, "onUaCallRinging()");
	}

	@Override
	public void onUaCallAccepted(UserAgent ua) {
		// TODO Auto-generated method stub
		Log.logToConsole(TAG, "onUaCallAccepted()");
	}

	@Override
	public void onUaCallTrasferred(UserAgent ua) {
		// TODO Auto-generated method stub
		Log.logToConsole(TAG, "onUaCallFailed()");
	}

	@Override
	public void onUaCallFailed(UserAgent ua) {
		// TODO Auto-generated method stub
		Log.logToConsole(TAG, "onUaCallFailed()");
	}

	@Override
	public void onUaCallClosed(UserAgent ua) {
		// TODO Auto-generated method stub
		Log.logToConsole(TAG, "onUaCallClosed()");
	}
}