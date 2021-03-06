package android.me.p2psip.activity;

import org.zoolu.sip.address.NameAddress;
import org.zoolu.sip.address.SipURL;

import local.ua.UserAgent;
import me.sip.ua.specify.UACListener;
import android.content.Intent;
import android.me.p2psip.R;
import android.me.p2psip.constant.Constant;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

public class CallingActivity extends SipActivity implements UACListener {
	TextView mTxtCaller;
	ImageButton mBtCall;
	ImageButton mBtEndCall;
	
	Intent mStartItent;
	SipURL mCallerSipUr;

	@Override
	public void onSipServiceConnected(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		setContentView(R.layout.activity_calling);

		mStartItent = getIntent();
		mCallerSipUr = (SipURL) mStartItent.getSerializableExtra(Constant.KEY_CALLER);
		initView();
		initComponents();
	}

	private void initView() {
		mTxtCaller = (TextView) findViewById(R.id.calling_txtCaller);
		mTxtCaller.setText(mCallerSipUr.getUserName());
		
		mBtCall = (ImageButton) findViewById(R.id.calling_btCall);
		mBtEndCall = (ImageButton) findViewById(R.id.calling_btEnCall);

		mBtCall.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				/*
				 * Chấp nhận cuộc gọi;
				 */
				mSipService.accept();
				
				/*
				 * Khởi động Activity OnCall
				 */
				Intent intent = new Intent(CallingActivity.this, OnCallActivity.class);
				startActivity(intent);
				
				/*
				 * Kết thúc Activity này;
				 */
				finish();
			}
		});

		mBtEndCall.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				/**
				 * Từ chối cuộc gọi;
				 */
				mSipService.hangup();
				
				/**
				 * Gọi listen để tiếp tục lắng nghe cuộc gọi đến
				 */
				mSipService.listen();

				/*
				 * Kết thúc Activity này
				 */
				finish();
			}
		});
	}

	private void initComponents() {
		mSipService.setUACListener(this);
	}

	@Override
	public void onUACCallIncoming(UserAgent ua, final NameAddress caller,
			NameAddress callee) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onCallUASCancelled(UserAgent ua) {
		// TODO Auto-generated method stub
		/*
		 * Kết thúc Activity;
		 */
		finish();
	}

	@Override
	public void onUACCallClosed(UserAgent ua) {
		// TODO Auto-generated method stub
		
	}
}
