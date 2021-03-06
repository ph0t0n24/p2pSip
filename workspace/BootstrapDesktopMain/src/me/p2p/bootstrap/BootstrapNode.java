package me.p2p.bootstrap;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import me.p2p.PeerInfoParser;
import me.p2p.constant.PeerPort;
import me.p2p.data.DataManager;
import me.p2p.log.Log;
import me.p2p.message.EMsgType;
import me.p2p.message.Message;
import me.p2p.message.MessageParser;
import me.p2p.request.Request;
import me.p2p.request.RequestHandler;
import me.p2p.specify.IBootstrap;
import me.p2p.specify.IP2PProtocol;
import me.p2p.specify.MessageCallback;

import org.json.JSONObject;

public class BootstrapNode extends Thread implements MessageCallback,
		IBootstrap {
	final String TAG = "BootstrapNode";
	String filePath = null;

	ServerSocket serverSocket;
	InetAddress inetAddress;
	MessageParser msgParser;
	/**
	 * Request dùng để gửi dữ liệu trở lại cho peer node;
	 */
	Request request;

	/**
	 * Đối tượng quản lý danh sách peer;
	 */
	DataManager dataManager;

	/**
	 * Bootstrap node shuwdown?
	 */
	boolean shutdown = false;

	// create bootstrap node with default port
	public BootstrapNode(String filePath) {
		try {
			inetAddress = InetAddress.getLocalHost();
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		try {
			serverSocket = new ServerSocket(PeerPort.PORT_BOOTSTRAP, IP2PProtocol.BACK_LOG,
					inetAddress);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		this.filePath = filePath;

		dataManager = new DataManager(this.filePath, false);
	}

	// run it
	public void run() {
		// TODO Auto-generated method stub
		while (!shutdown) {
			try {
				Socket socket = serverSocket.accept();
				RequestHandler requestHandler = new RequestHandler(socket, this);
				requestHandler.handleRequest();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void onMessage(JSONObject message) {
		// TODO Auto-generated method stub
		Log.logToConsole(TAG, "onMessage(): " + message.toString());

		msgParser = new MessageParser(message);
		EMsgType msgType = msgParser.getMessageType();
		JSONObject msgData = msgParser.getMessageData();

		switch (msgType) {
		case JOIN: {
			handleJoinMsg(msgData);
		}
			break;

		case LEAVE: {
			Log.logToConsole(TAG, "");
			handleLeaveRequest(msgParser.getMessageData());
		}
			break;
		case UPDATE: {
			handleUpdateRequest(msgParser.getMessageData());
		}
			break;

		case TRANSFER_LIST: {
			// bootstrap does not handle this message;
		}
		
		case ADD_NODE: {
			// bootstrap does not handle this message;
		}
			break;
		}
	}

	@Override
	public void onMessageStart() {
		// TODO Auto-generated method stub
		Log.logToConsole(TAG,
				"onMessageStart(): peer node send session start request");
	}

	@Override
	public void onMessageEnd() {
		// TODO Auto-generated method stub
		Log.logToConsole(TAG,
				"onMessageEnd(): peer node send session end request");
	}

	@Override
	public void handleJoinMsg(JSONObject data /* peer info data */) {
		// TODO Auto-generated method stub
		Log.logToConsole(TAG, "handleJoinMsg()");

		/* Lưu thông tin của peer vừa giao tiếp vào file list peer */
		PeerInfoParser piParser = new PeerInfoParser(data);
		dataManager.add(piParser.getPeerInfo());

		/* Gửi danh sách peer đến nút đang giao tiếp */
		/**
		 * - Peer sẽ lắng nghe ở cổng 8686, bootstrap node sẽ kết nối đến cổng<br>
		 * này và thực hiện giao tiếp theo chiều ngược lại từ bootstrap node đến<br>
		 * Peer node
		 */
		Log.logToConsole(TAG, "Connect to: "
				+ piParser.getPeerInfo().address + " to transfer list peer");
		Socket socketToPeer = null;
		try {
			socketToPeer = new Socket(piParser.getPeerInfo().address,
					PeerPort.PORT_PEER);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (socketToPeer == null) {
			Log.logToConsole(TAG, "Connect to: "
					+ piParser.getPeerInfo().address + " error");
		} else {
			Request request = new Request(socketToPeer);
			request.startMsg();
			
			// build message;
			Message message = new Message(EMsgType.TRANSFER_LIST,
					dataManager.getJsonListPeer());
			// send message;
			request.sendMessage(message);
			
			//send end msg;
			request.endMsg();
		}
	}

	@Override
	public void shutdown() {
		Log.logToConsole(TAG, "Shutdown");
		shutdown = true;
	}

	@Override
	public void listenRequest() {
		// TODO Auto-generated method stub
		Log.logToConsole(TAG, "IP Adress: " + inetAddress.getHostAddress());
		Log.logToConsole(TAG, "Bootstrap Listening...");
		setName(TAG);
		start();
	}

	@Override
	public void handleLeaveRequest(JSONObject requestPeerInfo) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void handleUpdateRequest(JSONObject requestPeerInfo) {
		// TODO Auto-generated method stub
		
	}
}
