package com.example.sipdemo;

import local.ua.UserAgent;
import local.ua.UserAgentListener;
import local.ua.UserAgentProfile;

import org.zoolu.sip.address.NameAddress;
import org.zoolu.sip.provider.SipProvider;
import org.zoolu.sip.provider.SipStack;

import android.annotation.SuppressLint;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.ActionBarActivity;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends ActionBarActivity implements
		UserAgentListener {
	static final int PORT = 5060;
	private static final String TAG = "MainActivity";
	String mContactUrl;
	// view control;
	Button mBtCall;
	Button mBtEndCall;
	TextView mTxtInfo;
	EditText mEditText;

	// sip component;
	UserAgentProfile mProfile;
	UserAgent mUserAgent;
	SipProvider mSipProvider;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
				.detectDiskReads().detectDiskWrites().detectNetwork()
				.penaltyLog().build());
		StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
				.detectLeakedSqlLiteObjects().detectLeakedClosableObjects()
				.penaltyLog().penaltyDeath().build());

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// init view;
		mBtCall = (Button) findViewById(R.id.bt_call);
		mBtEndCall = (Button) findViewById(R.id.bt_endcall);
		mTxtInfo = (TextView) findViewById(R.id.txt_info);
		mEditText = (EditText) findViewById(R.id.input_contact);

		// init sip stack;
		if (!SipStack.isInit()) {
			SipStack.init();
			SipStack.debug_level = -1;
		}

		WifiManager wm = (WifiManager) getSystemService(WIFI_SERVICE);
		String ip = Formatter.formatIpAddress(wm.getConnectionInfo()
				.getIpAddress());

		// init sip provider;
		mSipProvider = new SipProvider(ip, PORT); // default config

		// init user agent profile;
		mProfile = new UserAgentProfile();
		mProfile.audio = true;
		mProfile.video = true;

		// init user agent;
		mUserAgent = new UserAgent(mSipProvider, mProfile, this);
		mUserAgent.listen();

		// init info;
		mTxtInfo.setText(mSipProvider.getViaAddress().toString());
		mBtCall.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				mContactUrl = mEditText.getText().toString();
				mUserAgent.hangup();
				mUserAgent.call(mContactUrl);
			}
		});
		
		mBtEndCall.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mUserAgent.hangup();
			}
		});
	}

	@Override
	public void onUaCallIncoming(UserAgent ua, NameAddress caller,
			NameAddress callee) {
		// TODO Auto-generated method stub
		Log.e(TAG, "UA Comming Call");
		
		// remote ua;
		
		// set up remote ua;
	}

	@Override
	public void onUaCallCancelled(UserAgent ua) {
		// TODO Auto-generated method stub
		Log.e(TAG, "UA Call Canceled");
		ua.listen();
	}

	@Override
	public void onUaCallRinging(UserAgent ua) {
		// TODO Auto-generated method stub
		Log.e(TAG, "UA Call Ring");
	}

	@Override
	public void onUaCallAccepted(UserAgent ua) {
		// TODO Auto-generated method stub
		Log.e(TAG, "UA Call Accepted");
		
		// listenet rtp;
		// rtp clinet -> decode by g722 codec;
		
		// tao ra local rtp;
		// streamming send -> encode by g722 codec;
	}

	@Override
	public void onUaCallTrasferred(UserAgent ua) {
		// TODO Auto-generated method stub
		Log.e(TAG, "UA Call Trasferred");
		ua.listen();
	}

	@Override
	public void onUaCallFailed(UserAgent ua) {
		// TODO Auto-generated method stub
		Log.e(TAG, "UA Call Failed");
		ua.listen();
	}

	@Override
	public void onUaCallClosed(UserAgent ua) {
		// TODO Auto-generated method stub
		Log.e(TAG, "UA Call Closed");
		ua.listen();
		
		// remove all rtp service;
	}
}
