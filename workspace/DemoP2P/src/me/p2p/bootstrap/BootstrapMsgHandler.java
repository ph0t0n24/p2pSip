package me.p2p.bootstrap;

import java.net.Socket;

import org.json.JSONObject;

import me.p2p.spec.IBootstrap;
import me.p2p.spec.MessageCallback;

public class BootstrapMsgHandler implements MessageCallback, IBootstrap {

	@Override
	public void handleJoinMsg(JSONObject data, Socket peerSocket) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleLeaveMsg(JSONObject data, Socket peerSocket) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleUpdateMsg(JSONObject data) {
		// TODO Auto-generated method stub

	}

	@Override
	public void sendBroadCast(Socket peerSocket, JSONObject data) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSessionStart() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onMessage(Socket peerSocket, JSONObject data) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSessionEnd() {
		// TODO Auto-generated method stub

	}
}
