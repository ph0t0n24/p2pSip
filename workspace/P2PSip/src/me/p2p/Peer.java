package me.p2p;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import me.p2p.constant.PeerPort;
import me.p2p.data.DataManager;
import me.p2p.log.Log;
import me.p2p.message.EMsgType;
import me.p2p.message.Message;
import me.p2p.message.MessageDataParser;
import me.p2p.message.MessageParser;
import me.p2p.request.Request;
import me.p2p.request.RequestHandler;
import me.p2p.specify.IP2PProtocol;
import me.p2p.specify.IPeer;
import me.p2p.specify.MessageCallback;
import me.p2p.specify.PeerCallback;

import org.json.JSONObject;

public class Peer extends Thread implements IPeer, MessageCallback {
	final String TAG = "PeerNode";

	// msg handler to handle message;
	ServerSocket peerServerSocket;

	/**
	 * Đối tượng xử lý request từ boostrap sau khi thông điệp msg_join<br>
	 * được gửi đi.
	 */
	// RequestHandler bootstrapRequestHandler;

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

	/**
	 * Hàm được gọi khi có những sự kiện diễn ra
	 */
	PeerCallback peerCallback;

	public Peer(String fileListPeerPath, String userName, String localAdress,
			String bootstrapAddress) {
		// khởi tạo inetadress;
		if (localAdress != null) {
			/**
			 * Khởi tạo lại local Address bằng cách truyền lại giá trị
			 * localAdress. Trên máy java thông thường thì sử dụng được nhưng
			 * trên thiết bị Android thì ko sử dụng được, nên phải lấy ip theo
			 * cách khác và khởi tạo theo cách này;
			 */
			try {
				this.localAddress = InetAddress.getByName(localAdress);
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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

		// khởi tạo serverSocket cho việc lắng nghe update thông tin từ những
		// nút khác
		try {
			peerServerSocket = new ServerSocket(PeerPort.PORT_PEER,
					IP2PProtocol.BACK_LOG, localAddress);
			peerServerSocket.setReuseAddress(true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		/*
		 * Cài đặt địa chỉ bootstrap cho peer, nếu peer chưa join thì sẽ yêu gửi
		 * yêu cầu join, nếu join rồi thì không cần khởi tạo socket để liên kết
		 * với bootstrap node nữa
		 */
		this.bstrAddress = bootstrapAddress;

		// khởi tạo đối tượng quản lý dữ liệu;
		dataManager = new DataManager(fileListPeerPath, true);

		// Vì đối tượng log sử dụng đối tượng DataManager nên phải init sau;
		Log.logStartPartLog();
		Log.logToConsole(TAG, "Init PeerNode");
	}

	public Peer(String fileListPeerPath, String userName,
			String bootstrapAddress) {
		// TODO Auto-generated constructor stub
		this(fileListPeerPath, userName, null, bootstrapAddress);
	}

	public InetAddress getAddress() {
		return localAddress;
	}

	@Override
	public void joinRequest() {
		/**
		 * - Kiểm tra thử nếu chưa tham gia vào mạng thì gửi tin nhắn tham gia
		 * vào mạng. Nếu tham gia rồi thì gọi hàm onJoined() trong hàm CallBack.
		 * - Để kiểm tra peer node có tham gia vào mạng hay chưa? Nếu ko tồn tại
		 * file list_peer.json thì status sẽ change thành joined.
		 */
		if (!dataManager.isJoined()) {
			// khởi tạo socket bootstrap và request cho để message cho nó;
			try {
				bootstrapSocket = new Socket(this.bstrAddress.toString(),
						PeerPort.PORT_BOOTSTRAP);
				requestBootstrap = new Request(bootstrapSocket);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			Log.logToConsole(TAG,
					"Send joinRequest() because list_peer not exist");
			// TODO Auto-generated method stub
			// start msg;
			requestBootstrap.startMsg();
			// block to wait server respone;

			// send message join with peer info;
			Message message = new Message(EMsgType.JOIN,
					peerInfo.toJSONObject());
			requestBootstrap.sendMessage(message);

			// send end msg;
			requestBootstrap.endMsg();

			/**
			 * Sau khi send msg join thì peer chờ boostrap node xử lý join msg
			 * sau đó đợi nhận list peer ở port 8686 đã được peer mở ra và lắng
			 * nghe từ đầu;
			 */
		} else {
			Log.logToConsole(TAG, "PeerNode has joined to peer network");
			/**
			 * Gọi hàm callback nếu node đã tham gia vào mạng rồi
			 */
			if (peerCallback != null) {
				peerCallback.onJoined(this);
			}
		}
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
	 * Implement as Server, always listen change from other node; Chỉ có một
	 * handler để lắng nghe request, vì chương trình chỉ đang trong quá trình
	 * xây dựng nên điều này sẽ làm cho dễ quản lý chương trình. Nếu trường hợp
	 * có nhiều thread thì socket sẽ được đồng bộ hóa, những thread khác sẽ phải
	 * chờ cho đến khi thread xử lý xong.
	 */
	@Override
	public void run() {
		// TODO Auto-generated method stub
		while (!shutdown) {
			// listen;
			try {
				Socket localSocket = peerServerSocket.accept();
				RequestHandler bootstrapRequestHandler = new RequestHandler(
						localSocket, this);
				bootstrapRequestHandler.handleRequest();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		Log.logToConsole(TAG, "Stop handle request from bootstrap");

	}

	@Override
	public void shutdown() {
		// ngắt vòng lặp handle message
		// release server socket;
		try {
			Log.logToConsole(TAG, "Close peerServerSocket listener");
			peerServerSocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.logToConsole(TAG, e.toString());
			e.printStackTrace();
		}
		
		// shutdown;
		shutdown = true;
	}

	@SuppressWarnings("incomplete-switch")
	@Override
	public void onMessage(JSONObject message) {
		// TODO Auto-generated method stub
		/**
		 * Hàm này được gọi khi bootstrap node gửi dữ liệu list data;
		 */
		MessageParser msgParser = new MessageParser(message);
		Log.logToConsole(TAG,
				"onMessage(): " + this.localAddress.getHostAddress()
						+ " send message: "
						+ msgParser.getMessageType().toString());
		switch (msgParser.getMessageType()) {
		case TRANSFER_LIST: {
			handleTransferRequest(msgParser.getMessageData());
		}
			break;
		case ADD_NODE: {
			handleAddNodeRequest(msgParser.getMessageData());
		}
			break;
		case LEAVE: {
			handleLeaveRequest(msgParser.getMessageData());
		}
			break;
		case UPDATE: {
			handleUpdateRequest(msgParser.getMessageData());
			break;
		}
		}
	}

	@Override
	public void handleTransferRequest(JSONObject bstrListPeerInfo) {
		// TODO Auto-generated method stub
		Log.logToConsole(TAG, "handleTransferRequest()");

		MessageDataParser msgDataParser = new MessageDataParser(
				bstrListPeerInfo);
		ArrayList<PeerInfo> listPeerInfo = msgDataParser.getListPeerInfo();

		for (PeerInfo pi : listPeerInfo) {
			dataManager.add(pi);
		}

		// thay đổi status của peer;
		dataManager.joined(peerInfo);

		/*
		 * - Sau khi nhận được danh sách từ bootstrap, peer này sẽ thông
		 * báo đến tất cả những nút còn lại về việc thêm nút này
		 * vào.
		 */
		for (PeerInfo peerInfo : dataManager.getListPeerInfo()) {
			if (!peerInfo.isEqual(this.peerInfo)) {
				// nếu không phải là nút hiện tại
				try {
					// mở socket đến nút
					Socket socket = new Socket(peerInfo.address,
							PeerPort.PORT_PEER);
					// khởi tạo request để chuyển thông điệp
					Request request = new Request(socket);
					// gửi tin bắt đầu một message;
					request.startMsg();
					// khởi tạo message add node;
					Message message = new Message(EMsgType.ADD_NODE,
							this.peerInfo.toJSONObject());
					// send message;
					request.sendMessage(message);
					// kết thúc message;
					request.endMsg();
				} catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		/**
		 * Triệu gọi sự kiện onJoined thông báo là đã tham gia mạng
		 */
		if (peerCallback != null) {
			peerCallback.onJoined(this);
		}
	}

	@Override
	public void listenRequest() {
		// TODO Auto-generated method stub
		Log.logToConsole(TAG, " listenRequest()");
		setName(TAG);
		start();
	}

	@Override
	public void onMessageStart() {
		// TODO Auto-generated method stub
		Log.logToConsole(TAG, "onSessionStart(): handle start_msg message");
	}

	@Override
	public void onMessageEnd() {
		// TODO Auto-generated method stub
		Log.logToConsole(TAG, "onSessionEnd(): handle end_msg message");
		/**
		 * Nhận được lệnh kết thúc phiên từ bootstrap node, dừng xử lý request
		 * từ server và bắt đầu tự vận động
		 */
	}

	@Override
	public void handleLeaveRequest(JSONObject requestPeerInfo) {
		// TODO Auto-generated method stub
		/**
		 * Triệu gọi sự kiện onJoined thông báo là đã tham gia mạng
		 */
		if (peerCallback != null) {
			peerCallback.onLeaved(null);
		}
	}

	@Override
	public void handleUpdateRequest(JSONObject requestPeerInfo) {
		// TODO Auto-generated method stub
		/**
		 * Triệu gọi sự kiện onJoined thông báo là đã tham gia mạng
		 */
		if (peerCallback != null) {
			peerCallback.onUpdated(null);
		}
	}

	@Override
	public void handleAddNodeRequest(JSONObject requestPeerInfo) {
		// TODO Auto-generated method stub
		Log.logToConsole(TAG, "handleAddNodeRequest(): " + requestPeerInfo.toString());
		PeerInfo peerInfo = new PeerInfo(requestPeerInfo);
		dataManager.add(peerInfo);

		if (peerCallback != null) {
			peerCallback.onAddedNode(peerInfo);
		}
	}

	// /////////////////////////////////////////////////////////
	// //////////// GET
	// /////////////////////////////////////////////////////////
	public DataManager getDataManager() {
		return this.dataManager;
	}

	public PeerInfo getLocalPeerInfo() {
		return this.peerInfo;
	}

	public String getBootstrapAddress() {
		return this.bstrAddress;
	}

	public boolean isShutdown() {
		return this.shutdown;
	}

	/*
	 * Peer node tự xử lý vấn đề join hay chưa join, khi sử dụng peer node thì
	 * chỉ cần peer node đã tham gia vào mạng là được rồi
	 */
	// public boolean isJoined() {
	// return this.dataManager.isJoined();
	// }

	// //////////////////////////////////////////////////////////
	// //////// SET
	// //////////////////////////////////////////////////////////
	public void setPeerCallback(PeerCallback peerCallback) {
		this.peerCallback = peerCallback;
	}
}
