package android.me.p2psip.activity;

import local.ua.UserAgent;
import local.ua.UserAgentListener;
import me.p2p.PeerInfo;

import org.zoolu.sip.address.NameAddress;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.me.p2psip.R;
import android.me.p2psip.service.SipService;
import android.me.p2psip.service.SipService.SipServiceBinder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.ActionBarActivity;
import android.widget.TextView;

public class CallerActivity extends ActionBarActivity implements
		UserAgentListener {
	final String TAG = "CallActivity";
	SipService mSipService;
	boolean mBound = false; 
	ServiceConnection mServiceConnection = new ServiceConnection() {
		
		@Override
		public void onServiceDisconnected(ComponentName name) {
			// TODO Auto-generated method stub
			mBound = false;
			mSipService = null;
		}
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			// TODO Auto-generated method stub
			mBound = true;
			mSipService = ((SipServiceBinder) service).getService();
			
			/*
			 * G�?i hàm callback khi kết nối service thành công;
			 */
			onSipServiceConnected();
		}
	};
	
	/*
	 * Những view cần thiết để user control activity này;
	 */
	private TextView mTextView;
	
	@SuppressLint("InlinedApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_call);
		
		/*
		 * Nối vào SipService đã được cài đặt từ trước;
		 */
		Intent intent = new Intent(this, SipService.class);
		bindService(intent, mServiceConnection, Context.BIND_IMPORTANT);

		/*
		 * Cài đặt những thành phần View của Activity này;
		 */
		mTextView = (TextView) findViewById(R.id.txtSipNodeAddress);
	}

	@Override
	public void onUaCallIncoming(UserAgent ua, NameAddress caller,
			NameAddress callee) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onUaCallCancelled(UserAgent ua) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onUaCallRinging(UserAgent ua) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onUaCallAccepted(UserAgent ua) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onUaCallTrasferred(UserAgent ua) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onUaCallFailed(UserAgent ua) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onUaCallClosed(UserAgent ua) {
		// TODO Auto-generated method stub

	}
	
	private void onSipServiceConnected() {
		/*
		 * Cài đặt các thông số cho Service;
		 */
		mSipService.setUserAgentListener(this);
		
		/*
		 * Lấy thông tin của peer được chỉ định gọi;
		 */
		PeerInfo peerInfo = (PeerInfo) getIntent().getSerializableExtra(
				"peer_info");
		
		mTextView.setText(peerInfo.toJSONObject().toString());
		
		/*
		 * Thực hiện cuộc gọi đến peer vừa chọn;
		 */
		mSipService.callTo(peerInfo);
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}
		
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
		/*
		 * Chấm dứt kết nối đến SipService;
		 */
		mSipService.hangup();
		unbindService(mServiceConnection);
	}
}
