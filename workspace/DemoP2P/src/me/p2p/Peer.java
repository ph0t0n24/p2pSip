package me.p2p;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import org.json.JSONObject;

import me.p2p.constant.PeerPort;
import me.p2p.data.DataManager;
import me.p2p.message.EMsgType;
import me.p2p.message.Message;
import me.p2p.message.MessageParser;
import me.p2p.request.Request;
import me.p2p.request.RequestHandler;
import me.p2p.spec.IPeer;
import me.p2p.spec.MessageCallback;

public class Peer extends Thread implements IPeer, MessageCallback {
	final String TAG = "Peer";
	
	final int MAX_CONNECTION = 20;
	// msg handler to handle message;
	ServerSocket serverSocket;
	
	/**
	 * Đối tượng xử lý request từ boostrap sau khi thông điệp msg_join<br>
	 * được gửi đi.
	 */
	RequestHandler bootstrapRequestHandler;

	// socket to request to bootstrap;
	Socket bootstrapSocket;
	Request requestBootstrap;

	InetAddress localAddress;
	String bstrAddress;

	/**
	 * Chứa thông tin của một peer. Bao gồm:<br>
	 * - Thông tin về địa chỉ ip của peer.<br>
	 * - Thông tin về username đăng ký.
	 */
	PeerInfo peerInfo;

	/**
	 * Biến dùng để set tình trạng cho Peer: có tiếp tục lắng nghe request<br>
	 * từ những peer khác hay không?
	 */
	boolean shutdown = false;
	
	/**
	 * Đối tượng quản lý data của peer node;
	 */
	DataManager dataManager;
	
	/**
	 * Boolean để kiểm tra thử peer có init hay chưa?
	 */
	boolean hasInit = false;

	public Peer(String fileListPeerPath, String userName, 
			InetAddress localAdress, String bootstrapAddress) {
		// khởi tạo inetadress;
		if (localAdress != null) {
			/**
			 * Khởi tạo lại local Address bằng cách truyền lại giá trị
			 * localAdress. Trên máy java thông thường thì sử dụng được nhưng
			 * trên thiết bị Android thì ko sử dụng được, nên phải lấy ip theo
			 * cách khác và khởi tạo theo cách này;
			 */
			this.localAddress = localAdress;
		} else {
			try {
				this.localAddress = InetAddress.getLocalHost();
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		// khởi tạo thông tin về peerInfo;
		peerInfo = new PeerInfo(this.localAddress.getHostAddress(), userName);
		
		// khởi tạo serverSocket cho việc lắng nghe update thông tin từ những nút khác
		try {
			serverSocket = new ServerSocket(PeerPort.PORT_PEER, MAX_CONNECTION,
					localAddress);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// khởi tạo socket bootstrap và request cho để message cho nó;
		try {
			this.bstrAddress = bootstrapAddress;
			bootstrapSocket = new Socket(this.bstrAddress.toString(), PeerPort.PORT_BOOTSTRAP);
			requestBootstrap = new Request(bootstrapSocket);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// khởi tạo đối tượng quản lý dữ liệu;
		DataManager.prepare(fileListPeerPath);
		dataManager = DataManager.getInstance();
	}

	public Peer(String fileListPeerPath, String userName, String bootstrapAddress) {
		// TODO Auto-generated constructor stub
		this(fileListPeerPath, userName, null, bootstrapAddress);
	}

	public InetAddress getAddress() {
		return localAddress;
	}

	@Override
	public void joinRequest() {
		// TODO Auto-generated method stub
		// start session;
		requestBootstrap.startSession();
		// start msg;
		requestBootstrap.startMsg();
		
		// send message join with peer info;
		Message message = new Message(EMsgType.JOIN, peerInfo.toJSONObject());
		requestBootstrap.sendMessage(message);
		
		// send end msg;
		requestBootstrap.endMsg();
		
		/**
		 * Sau khi send msg join thì peer chờ boostrap node xử lý join msg
		 * sau đó đợi nhận list peer;
		 */
		bootstrapRequestHandler = new RequestHandler(bootstrapSocket, this);
		bootstrapRequestHandler.handleRequest();
	}

	@Override
	public void leaveRequest() {
		// TODO Auto-generated method stub
	}

	@Override
	public void updateRequest() {
		// TODO Auto-generated method stub

	}

	/**
	 * Implement as Server, always listen change from other node;
	 */
	@Override
	public void run() {
		// TODO Auto-generated method stub
		while (!shutdown) {
			// listen;
			try {
				Socket localSocket = serverSocket.accept();
				synchronized (bootstrapRequestHandler) {
					bootstrapRequestHandler = new RequestHandler(localSocket, this);
					bootstrapRequestHandler.handleRequest();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		bootstrapRequestHandler.stopHandle();
	}

	public void shutdown() {
		shutdown = true;
	}

	@Override
	public void onSessionStart() {
		// TODO Auto-generated method stub
		Log.logToConsole(TAG, "onSessionStart(): bootstrap send start session");
	}

	@SuppressWarnings("incomplete-switch")
	@Override
	public void onMessage(Socket peerSocket, JSONObject data) {
		// TODO Auto-generated method stub
		Log.logToConsole(TAG, "onMessage(): bootstrap send start message");
		/**
		 * Hàm này được gọi khi bootstrap node gửi dữ liệu list data;
		 */
		MessageParser msgParser = new MessageParser(data);
		switch (msgParser.getMessageType()) {
		case TRANSFER_LIST:
		{
			handleTransferRequest(msgParser.getMessageData());
		}
			break;
		}
		
		// nhận xong rồi thì thông báo kết thúc phiên với server;
		requestBootstrap.endSession();
		
		// tải danh sách xong rồi thì close socket;
		try {
			peerSocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void onSessionEnd() {
		// TODO Auto-generated method stub
		Log.logToConsole(TAG, "onSessionEnd(): bootstrap send end session");
		/**
		 * Nhận được lệnh kết thúc phiên từ bootstrap node,
		 * dừng xử lý request từ server và bắt đầu tự vận động
		 */
		bootstrapRequestHandler.stopHandle();
	}

	@Override
	public void handleTransferRequest(JSONObject bstrListPeerInfo) {
		// TODO Auto-generated method stub
		Log.logToConsole(TAG, "handleTransferRequest()");
	}
}
