package android.me.p2psip.service;

import local.ua.UserAgent;
import local.ua.UserAgentListener;
import me.p2p.Peer;
import me.p2p.PeerInfo;
import me.sip.SipNode;

import org.zoolu.sip.address.NameAddress;
import org.zoolu.sip.address.SipURL;

import android.app.Service;
import android.content.Intent;
import android.me.p2psip.data.MeApplication;
import android.me.p2psip.log.LogAndroid;
import android.os.Binder;
import android.os.IBinder;

public class SipService extends Service implements UserAgentListener {
	static final String TAG = "SipService";

	public class SipServiceBinder extends Binder {
		public SipService getService() {
			return SipService.this;
		}
	}

	SipServiceBinder mBinder = new SipServiceBinder();

	SipNode mSipNode;
	MeApplication mApplication;
	Peer mPeer;

	UserAgentListener mCallback;

	SipURL mLocalSipUrl;
	SipURL mSipUrlCallTo;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		LogAndroid.log(TAG, "onBind()");
		return mBinder;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		LogAndroid.log(TAG, "onCreate()");

		// Lấy thông tin về peer đã được tạo;
		mApplication = (MeApplication) getApplication();
		mPeer = mApplication.getPeer();

		// Khởi tạo SipNode;
		mSipNode = new SipNode(mPeer, this);

		new Thread("SipNodeStartUp") {
			public void run() {
				mSipNode.listen();
			}
		}.start();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		LogAndroid.log(TAG, "onStartCommand()");

		return START_STICKY;
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
		mSipNode.shutdown();
	}

	@Override
	public void onUaCallIncoming(UserAgent ua, NameAddress caller,
			NameAddress callee) {
		// TODO Auto-generated method stub

		/**
		 * Được gọi để truyền sự kiện cho những thành phần khác khi sự kiện đã
		 * được xử lý trong Service.
		 */
		if (mCallback != null) {
			mCallback.onUaCallIncoming(ua, caller, callee);
		}
	}

	@Override
	public void onUaCallCancelled(UserAgent ua) {
		// TODO Auto-generated method stub

		/**
		 * Được gọi để truyền sự kiện cho những thành phần khác khi sự kiện đã
		 * được xử lý trong Service.
		 */
		if (mCallback != null) {
			mCallback.onUaCallCancelled(ua);
		}
	}

	@Override
	public void onUaCallRinging(UserAgent ua) {
		// TODO Auto-generated method stub

		/**
		 * Được gọi để truyền sự kiện cho những thành phần khác khi sự kiện đã
		 * được xử lý trong Service.
		 */
		if (mCallback != null) {
			mCallback.onUaCallRinging(ua);
		}
	}

	@Override
	public void onUaCallAccepted(UserAgent ua) {
		// TODO Auto-generated method stub

		/**
		 * Được gọi để truyền sự kiện cho những thành phần khác khi sự kiện đã
		 * được xử lý trong Service.
		 */
		if (mCallback != null) {
			mCallback.onUaCallAccepted(ua);
		}
	}

	@Override
	public void onUaCallTrasferred(UserAgent ua) {
		// TODO Auto-generated method stub

		/**
		 * Được gọi để truyền sự kiện cho những thành phần khác khi sự kiện đã
		 * được xử lý trong Service.
		 */
		if (mCallback != null) {
			mCallback.onUaCallTrasferred(ua);
		}
	}

	@Override
	public void onUaCallFailed(UserAgent ua) {
		// TODO Auto-generated method stub

		/**
		 * Được gọi để truyền sự kiện cho những thành phần khác khi sự kiện đã
		 * được xử lý trong Service.
		 */
		if (mCallback != null) {
			mCallback.onUaCallFailed(ua);
		}
	}

	@Override
	public void onUaCallClosed(UserAgent ua) {
		// TODO Auto-generated method stub

		/**
		 * Được gọi để truyền sự kiện cho những thành phần khác khi sự kiện đã
		 * được xử lý trong Service.
		 */
		if (mCallback != null) {
			mCallback.onUaCallClosed(ua);
		}

	}

	// ///////////////////////////////////////
	// // GET
	// //////////////////////////////////////
	public SipURL getLocalSipUrl() {
		return this.mLocalSipUrl;
	}

	public SipURL getCallToSipUrl() {
		return this.mSipUrlCallTo;
	}

	// ///////////////////////////////////////
	// // SET
	// //////////////////////////////////////
	public void setUserAgentListener(UserAgentListener listener) {
		this.mCallback = listener;
	}

	// ///////////////////////////////////////
	// // FUNCTION
	// //////////////////////////////////////
	public void callTo(String sipUrl) {
		mSipNode.callTo(sipUrl);
	}

	public void callTo(PeerInfo peerInfo) {
		mSipNode.callTo(peerInfo);
	}

	public void hangup() {
		// TODO Auto-generated method stub
		mSipNode.hangup();
	}
}
