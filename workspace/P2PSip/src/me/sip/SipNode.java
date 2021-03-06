package me.sip;

import local.ua.UserAgent;
import local.ua.UserAgentListener;
import local.ua.UserAgentProfile;
import me.p2p.Peer;
import me.p2p.PeerInfo;
import me.p2p.log.Log;
import me.sip.ua.specify.UACListener;
import me.sip.ua.specify.UASListener;

import org.zoolu.sdp.SessionDescriptor;
import org.zoolu.sdp.SessionNameField;
import org.zoolu.sip.address.NameAddress;
import org.zoolu.sip.address.SipURL;
import org.zoolu.sip.provider.SipProvider;
import org.zoolu.sip.provider.SipStack;

/**
 * SipNode là lớp wrapper chịu trách nhiệm xử lý gọi và nhận cuộc gọi<br>
 * trong chương trình. SipNode đóng gói hết tất cả những thành phần cần thiết có<br>
 * thể thực được một cuộc gọi trong model SIP thực thi bởi MJSip, thiết lập các<br>
 * thông số cài đặt mặc định như: <br>
 * - SipNode sẽ lấy thông tin từ PeerNode để tạo ra địa chỉ SIP của thiết bị.
 * Thông tin này bao gồm "username" và "address" được khởi tạo từ PeerNode. Mỗi
 * thiết bị sẽ được gán địa chỉ SIP được tạo bởi cách trên.<br>
 * - SipNode sẽ tự động handle tất cả các thiết đặt, sự kiện SIP diễn ra và chỉ<br>
 * đơn giản cung câp những API đơn giản nhất để những thực thể khác sử dụng một<br>
 * cách dễ dàng, bao gồm:<br>
 * + Thực hiện cuộc gọi<br>
 * + Chấp nhận hoặc hủy bỏ cuộc gọi<br>
 * + Lắng nghe các sự kiện phát sinh trong một phiên SIP bằng cung cấp cơ chế<br>
 * listener<br>
 * SipNode bao gồm những thành phần cần thiết để có thể khởi<br>
 * tạo một cuộc gọi:<br>
 * - Peer: local peer đã được khởi tạo và tham gia vào mạng.<br>
 * - UserAgent, UserAgentProfile, SipProvider: những thành phần cần thiết trong<br>
 * mô hình SIP. Tất cả đều được thiết đặt mặc định theo quy định của chương<br>
 * trình:<br>
 * + UserName: được lấy từ peer.<br>
 * + Address: lấy thông tin từ peer.<br>
 * + Sip Url: sẽ được tạo từ hai thành phần trên.<br>
 * - UserAgentListener: được thiết đặt để gọi những hàm dựa trên sự kiện của<br>
 * cuộc gọi cần được thông báo và xử lý trong những thành phần khác của chương<br>
 * trình. <br>
 * 
 * @author Sang
 * 
 */
public class SipNode implements UserAgentListener {
	final String TAG = "SipNode";

	final int DEBUG_LEVEL = -1;
	final int SIP_PORT = 5060;

	/*
	 * Những thành phần trong mô hình SIP;
	 */
	UserAgent userAgent;
	UserAgentProfile userAgentProfile;
	SipProvider sipProvider;
	SessionDescriptor sessionDescriptor;

	/*
	 * Sip Url;
	 */
	SipURL localSipUrl;
	SipURL calleeSipUrl = null;
	SipURL callerSipUrl = null;

	/*
	 * Instance Peer trong mạng P2P;
	 */
	Peer peer;

	/*
	 * UAS listener, UAC listener;
	 */
	UASListener uasListener;
	UACListener uacListener;

	public SipNode(Peer peer) {
		this.peer = peer;

		// init sip stack;
		if (!SipStack.isInit()) {
			SipStack.init();
			SipStack.debug_level = DEBUG_LEVEL;
		}

		// create user agent profile
		createUserAgentProfile();

		// create SipProvider;
		createSipProvider();

		// create useragent;
		this.userAgent = new UserAgent(sipProvider, userAgentProfile, this);

		// create and add session descriptor;
		createSessionDescriptor();
	}

	private void createUserAgentProfile() {
		/*
		 * Khởi tạo user agent profile;
		 */
		userAgentProfile = new UserAgentProfile();

		/*
		 * Cài đặt các thông tin cho user agent profile;
		 */
		userAgentProfile.username = peer.getLocalPeerInfo().userName;
		SipURL localSipUrl = new SipURL(peer.getLocalPeerInfo().userName,
				peer.getLocalPeerInfo().address);
		userAgentProfile.from_url = localSipUrl.toString();
		userAgentProfile.realm = peer.getLocalPeerInfo().address;

		/*
		 * Cài đặt các thông tin về phiên đa phương tiện;
		 */
		userAgentProfile.audio = true;
		userAgentProfile.video = true;
	}

	private void createSessionDescriptor() {
		SessionDescriptor sdp = new SessionDescriptor(
				peer.getLocalPeerInfo().userName,
				peer.getLocalPeerInfo().address);
		SessionNameField sessionNameField = new SessionNameField(
				peer.getLocalPeerInfo().userName);
		sdp.setSessionName(sessionNameField);

		userAgent.setSessionDescriptor(sdp.toString());
	}

	/**
	 * Hàm khởi tạo SipProvider;
	 */
	private void createSipProvider() {
		sipProvider = new SipProvider(peer.getAddress().toString(), SIP_PORT);
	}

	@Override
	public void onUaCallIncoming(UserAgent ua, NameAddress caller,
			NameAddress callee) {
		// TODO Auto-generated method stub
		Log.logToConsole(TAG, "UA Comming Call");
		/*
		 * Set up remote ua;
		 */

		/*
		 * Lấy thông tin người gọi
		 */
		callerSipUrl = caller.getAddress();

		/**
		 * Gọi listener bên ngoài;
		 */
		if (uacListener != null) {
			uacListener.onUACCallIncoming(ua, caller, callee);
		}
	}

	@Override
	public void onUaCallCancelled(UserAgent ua) {
		// TODO Auto-generated method stub
		Log.logToConsole(TAG, "UA Call Canceled");

		/**
		 * Gọi listener bên ngoài;
		 */
		if (uacListener != null) {
			uacListener.onCallUASCancelled(ua);
		}

		/**
		 * Sau khi hủy bỏ một cuộc gọi thì tiếp tục cài đặt cho UserAgent tiếp
		 * tục lắng nghe các cuộc gọi đến khác.
		 */
		ua.listen();
	}

	@Override
	public void onUaCallRinging(UserAgent ua) {
		// TODO Auto-generated method stub
		Log.logToConsole(TAG, "UA Call Ring");

		/**
		 * Gọi listener bên ngoài;
		 */
		if (uasListener != null) {
			uasListener.onUASCallRinging(ua);
		}
	}

	@Override
	public void onUaCallAccepted(UserAgent ua) {
		// TODO Auto-generated method stub
		Log.logToConsole(TAG, "UA Call Accepted");

		// listenet rtp;
		// rtp clinet -> decode by g722 codec;

		// tao ra local rtp;
		// streamming send -> encode by g722 codec;
		/**
		 * Gọi listener bên ngoài;
		 */
		if (uasListener != null) {
			uasListener.onCallUACAccepted(ua);
		}
	}

	@Override
	public void onUaCallTrasferred(UserAgent ua) {
		// TODO Auto-generated method stub
		Log.logToConsole(TAG, "UA Call Trasferred");

		/**
		 * Sau khi hủy bỏ một cuộc gọi thì tiếp tục cài đặt cho UserAgent tiếp
		 * tục lắng nghe các cuộc gọi đến khác.
		 */
		ua.listen();
	}

	@Override
	public void onUaCallFailed(UserAgent ua) {
		// TODO Auto-generated method stub
		Log.logToConsole(TAG, "UA Call Failed");

		/**
		 * Gọi listener bên ngoài;
		 */
		if (uasListener != null) {
			uasListener.onCallUACFailed(ua);
		}

		/**
		 * Sau khi hủy bỏ một cuộc gọi thì tiếp tục cài đặt cho UserAgent tiếp
		 * tục lắng nghe các cuộc gọi đến khác.
		 */
		ua.listen();
	}

	@Override
	public void onUaCallClosed(UserAgent ua) {
		// TODO Auto-generated method stub
		Log.logToConsole(TAG, "UA Call Closed");

		// remove all rtp service;
		/**
		 * Gọi listener bên ngoài;
		 */
		if (uasListener != null) {
			uasListener.onUASCallClosed(ua);
		}

		if (uacListener != null) {
			uacListener.onUACCallClosed(ua);
		}

		/**
		 * Sau khi hủy bỏ một cuộc gọi thì tiếp tục cài đặt cho UserAgent tiếp
		 * tục lắng nghe các cuộc gọi đến khác.
		 */
		ua.listen();
	}

	public void call(PeerInfo peerInfo) {
		SipURL sipURL = new SipURL(peerInfo.userName, peerInfo.address);
		Log.logToConsole(TAG, "Call to: " + sipURL.toString());

		calleeSipUrl = sipURL;
		userAgentProfile.call_to = calleeSipUrl.toString();
		
		userAgent.hangup();
		userAgent.call(userAgentProfile.call_to);
	}

	public void call(String stringSipUrl) {
		Log.logToConsole(TAG, "Call to: " + stringSipUrl);

		SipURL sipURL = new SipURL(stringSipUrl);
		calleeSipUrl = sipURL;
		userAgentProfile.call_to = calleeSipUrl.toString();
		
		userAgent.hangup();
		userAgent.call(userAgentProfile.call_to);
	}

	public void listen() {
		Log.logToConsole(TAG, "This node listen for incoming call...");
		userAgent.listen();
	}

	// /////////////////////////////////////////
	// // GET
	// /////////////////////////////////////////
	public UserAgent getUserAgent() {
		return this.userAgent;
	}

	public UserAgentProfile getUserAgentProfile() {
		return this.userAgentProfile;
	}

	public SipProvider getSipProvider() {
		return this.sipProvider;
	}

	public SessionDescriptor getSessionDescriptor() {
		return this.sessionDescriptor;
	}

	public Peer getPeer() {
		return this.peer;
	}

	// ///////////////////////////////////////////
	// // SET
	// ///////////////////////////////////////////
	public void setSessionDescription(String sdp) {
		SessionDescriptor sessionDescriptor = new SessionDescriptor(sdp);
		this.sessionDescriptor = sessionDescriptor;
	}

	public void setUASListener(UASListener listener) {
		this.uasListener = listener;
	}

	public void setUACListener(UACListener listener) {
		this.uacListener = listener;
	}

	/**
	 * Kết thúc hoạt động của SipNode<br>
	 * Hàm này được gọi khi kết thúc chương trình, có tác dụng kết thúc<br>
	 * SipProvider<br>
	 */
	public void shutdown() {
		// TODO Auto-generated method stub
		sipProvider.halt();
	}

	/**
	 * Hủy bỏ cuộc gọi
	 */
	public void hangup() {
		// TODO Auto-generated method stub
		userAgent.hangup();
	}

	/**
	 * Chấp nhận cuộc gọi
	 */
	public void accept() {
		userAgent.accept();
	}

	/**
	 * Lấy địa chỉ Sip Local;
	 */
	public SipURL getLocalSipUrl() {
		return this.localSipUrl;
	}

	/**
	 * Lấy địa chỉ Call To Url;
	 */
	public SipURL getCalleeSipUrl() {
		return this.calleeSipUrl;
	}

	/**
	 * Lấy địa chỉ người gọi;
	 */
	public SipURL getCallerSipUrl() {
		return this.callerSipUrl;
	}
}
