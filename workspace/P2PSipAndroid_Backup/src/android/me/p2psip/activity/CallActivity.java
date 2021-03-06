package android.me.p2psip.activity;

import local.ua.UserAgent;
import me.p2p.PeerInfo;
import me.sip.ua.specify.UASListener;
import android.content.Intent;
import android.me.p2psip.R;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

public class CallActivity extends SipActivity implements UASListener {
	public static final String ACTION_HAS_CALL = "android.me.p2psip.action.HAS_CALL";
	public static final String ACTION_MAKE_CALL = "android.me.p2psip.action.MAKE_CALL";

	final String TAG = "CallActivity";

	/*
	 * Những view cần thiết để user control activity này;
	 */
	private TextView mTxtCallTo;
	private ImageButton mBtEndCall;

	@Override
	public void onSipServiceConnected(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		/*
		 * Cài đặt những thành phần View của Activity này;
		 */
		setContentView(R.layout.activity_call);
		initView();
		initComponents();
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
	}

	private void initView() {
		mTxtCallTo = (TextView) findViewById(R.id.calling_txtCaller);

		mBtEndCall = (ImageButton) findViewById(R.id.call_btEnCall);
		mBtEndCall.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				/*
				 * Chấm dứt cuộc gọi
				 */
				mSipService.hangup();
				
				/*
				 * Service tiếp tục lắng nghe
				 */
				mSipService.listen(); 

				/*
				 * Kết thúc activity;
				 */
				finish();
			}
		});
	}

	private void initComponents() {
		/*
		 * Cài đặt các thông số cho Service;
		 */
		mSipService.setUASListener(this);

		/*
		 * Lấy thông tin của peer được chỉ định gọi;
		 */
		PeerInfo peerInfo = (PeerInfo) getIntent().getSerializableExtra(
				"peer_info");
		/*
		 * Cài đặt địa chỉ người được gọi;
		 */
		mSipService.setSipUrlCall(peerInfo);

		/*
		 * Cài đặt địa chỉ;
		 */
		mTxtCallTo.setText(mSipService.getCallSipUrl().getUserName());

		/*
		 * Thực hiện cuộc gọi đến peer vừa chọn;
		 */
		mSipService.call();
	}

	@Override
	public void onUASCallRinging(UserAgent ua) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onCallUACAccepted(UserAgent ua) {
		// TODO Auto-generated method stub
		/*
		 * Khởi động Activity OnCall
		 */
		Intent intent = new Intent(this, OnCallActivity.class);
		startActivity(intent);
		
		/*
		 * Kết thúc Activity;
		 */
		finish();
	}

	@Override
	public void onCallUACFailed(UserAgent ua) {
		// TODO Auto-generated method stub
		finish();
	}

	@Override
	public void onUASCallClosed(UserAgent ua) {
		// TODO Auto-generated method stub
	}
}
