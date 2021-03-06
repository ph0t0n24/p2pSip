package android.me.p2psip.service;

import local.ua.UserAgent;
import me.p2p.Peer;
import me.p2p.PeerInfo;
import me.sip.SipNode;
import me.sip.ua.specify.UACListener;
import me.sip.ua.specify.UASListener;

import org.zoolu.sip.address.NameAddress;
import org.zoolu.sip.address.SipURL;

import android.app.Service;
import android.content.Intent;
import android.me.p2psip.activity.CallingActivity;
import android.me.p2psip.application.MeApplication;
import android.me.p2psip.constant.Constant;
import android.me.p2psip.log.LogAndroid;
import android.os.Binder;
import android.os.IBinder;

public class SipService extends Service implements UASListener, UACListener {
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

	UASListener mUASListener;
	UACListener mUACListener;

	SipURL mLocalSipUrl;
	SipURL mCallSipUrl;

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
		mSipNode = new SipNode(mPeer);
		mSipNode.setUASListener(this);
		mSipNode.setUACListener(this);

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

		mSipNode.hangup();
		mSipNode.shutdown();
	}

	// ///////////////////////////////////////
	// // GET
	// //////////////////////////////////////
	public SipURL getLocalSipUrl() {
		return mLocalSipUrl;
	}

	public SipURL getCallSipUrl() {
		return mCallSipUrl;
	}

	public SipURL getCallerSipUrl() {
		return mSipNode.getCallerSipUrl();
	}

	// ///////////////////////////////////////
	// // SET
	// //////////////////////////////////////
	public void setUASListener(UASListener listener) {
		mUASListener = listener;
	}

	public void setUACListener(UACListener listener) {
		mUACListener = listener;
	}

	public void setSipUrlCall(PeerInfo peerInfo) {
		SipURL sipURL = new SipURL(peerInfo.userName, peerInfo.address);
		this.mCallSipUrl = sipURL;
	}

	public void setSipUrlCall(String stringSipUrl) {
		SipURL sipUrl = new SipURL(stringSipUrl);
		this.mCallSipUrl = sipUrl;
	}

	// ///////////////////////////////////////
	// // FUNCTION
	// //////////////////////////////////////
	public void call() {
		if (mCallSipUrl == null) {
			throw new IllegalStateException(
					"Must set CallSipUrl befor use Call function");
		}

		mSipNode.call(mCallSipUrl.toString());
	}

	/**
	 * Hủy bỏ một cuộc gọi<br>
	 * Để tiếp tục lắng nghe thì gọi lại hàm listen()
	 */
	public void hangup() {
		// TODO Auto-generated method stub
		mSipNode.hangup();
	}

	/**
	 * Lắng nghe cuộc gọi đến
	 */
	public void listen() {
		mSipNode.listen();
	}

	/**
	 * Chấp nhận một cuộc gọi
	 */
	public void accept() {
		mSipNode.accept();
	}

	// /////////////////////////////////////
	// /// UAC Listener
	// /////////////////////////////////////
	@Override
	public void onUACCallIncoming(UserAgent ua, NameAddress caller,
			NameAddress callee) {
		// TODO Auto-generated method stub
		LogAndroid.log(TAG, "onUaCallIncoming(): " + caller.getAddress()
				+ " calling...");

		/*
		 * Gọi Activity onCall;
		 */
		Intent intent = new Intent(this, CallingActivity.class);
		intent.putExtra(Constant.KEY_CALLER, mSipNode.getCallerSipUrl());
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		startActivity(intent);

		/*
		 * Truyền sự kiện ra bên ngoài;
		 */
		if (mUACListener != null) {
			mUACListener.onUACCallIncoming(ua, caller, callee);
		}
	}

	@Override
	public void onCallUASCancelled(UserAgent ua) {
		// TODO Auto-generated method stub
		LogAndroid.log(TAG, "onCallUASCancelled(): UAS call cancelled");

		/*
		 * Truyền sự kiện ra bên ngoài;
		 */
		if (mUACListener != null) {
			mUACListener.onCallUASCancelled(ua);
		}
	}

	@Override
	public void onUACCallClosed(UserAgent ua) {
		// TODO Auto-generated method stub
		LogAndroid.log(TAG, "onUACCallClosed(): Call ended");

		/*
		 * Truyền sự kiện ra bên ngoài;
		 */
		if (mUACListener != null) {
			mUACListener.onUACCallClosed(ua);
		}
	}

	// ///////////////////////////////////////////
	// //// UAS Listener
	// ///////////////////////////////////////////
	@Override
	public void onUASCallRinging(UserAgent ua) {
		// TODO Auto-generated method stub
		LogAndroid.log(TAG, "onUASCallRinging(): 180 Ringing from UAC");

		/*
		 * Truyền sự kiện ra bên ngoài;
		 */
		if (mUASListener != null) {
			mUASListener.onUASCallRinging(ua);
		}
	}

	@Override
	public void onCallUACAccepted(UserAgent ua) {
		// TODO Auto-generated method stub
		LogAndroid.log(TAG, "onCallUACAccepted(): 200 OK from UAC");

		/*
		 * Truyền sự kiện ra bên ngoài;
		 */
		if (mUASListener != null) {
			mUASListener.onCallUACAccepted(ua);
		}
	}

	@Override
	public void onCallUACFailed(UserAgent ua) {
		// TODO Auto-generated method stub
		LogAndroid.log(TAG, "onCallUACFailed(): call to UAC failed");

		/*
		 * Truyền sự kiện ra bên ngoài;
		 */
		if (mUASListener != null) {
			mUASListener.onCallUACFailed(ua);
		}
	}

	@Override
	public void onUASCallClosed(UserAgent ua) {
		// TODO Auto-generated method stub
		LogAndroid.log(TAG, "onUASCallClosed(): call ended");

		if (mUASListener != null) {
			mUASListener.onUASCallClosed(ua);
		}
	}
}
