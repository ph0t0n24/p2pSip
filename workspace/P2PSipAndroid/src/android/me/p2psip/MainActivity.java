package android.me.p2psip;

import me.p2p.Peer;
import me.p2p.PeerInfo;
import me.p2p.log.Log;
import me.p2p.specify.PeerCallback;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.me.p2psip.R;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.format.Formatter;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class MainActivity extends ActionBarActivity implements PeerCallback {
	final String TAG = "MainActivity";
	final String bootstrapIpAddress = "192.168.3.120";
	final String userName = "SangNguyen";

	MeApplication mApplication;
	ListView listPeerView;
	AListPeer aListPeer;

	Peer mPeer;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// get ip address;
		WifiManager wm = (WifiManager) getSystemService(WIFI_SERVICE);
		final String ipAddress = Formatter.formatIpAddress(wm
				.getConnectionInfo().getIpAddress());
		final String fileListPeerPath = getFilesDir().getAbsolutePath();

		mApplication = (MeApplication) getApplication();

		new Thread("PeerStartUp") {
			public void run() {
				mPeer = new Peer(fileListPeerPath, userName, ipAddress,
						bootstrapIpAddress);
				mPeer.setPeerCallbak(MainActivity.this);

				mPeer.listenRequest();
				mPeer.joinRequest();
			};
		}.start();

		listPeerView = (ListView) findViewById(R.id.listPeer);
		listPeerView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				mApplication.setPeer(mPeer);
				
				PeerInfo peerInfo = (PeerInfo) arg0.getAdapter().getItem(arg2);
				Intent intent = new Intent(MainActivity.this,
						CallActivity.class);
				intent.putExtra("peer_info", peerInfo);

				startActivity(intent);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		mPeer.shutdown();
		
		super.onDestroy();
	}

	@Override
	public void onJoined(final Peer peer) {
		// TODO Auto-generated method stub
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				aListPeer = new AListPeer(MainActivity.this, R.layout.listpeer_item, peer
						.getDataManager().getListPeerInfo());
				listPeerView.setAdapter(aListPeer);
			}
		});
	}

	@Override
	public void onAddedNode(final PeerInfo peerInfo) {
		// TODO Auto-generated method stub
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Log.logToConsole(TAG, "onAddedNode(): " + peerInfo.toJSONObject().toString());
				aListPeer.notifyDataSetChanged();
			}
		});
	}

	@Override
	public void onUpdated(PeerInfo peerInfo) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onLeaved(PeerInfo peerInfo) {
		// TODO Auto-generated method stub

	}
}