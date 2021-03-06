package android.me.p2psip.adapter;

import java.util.ArrayList;

import me.p2p.PeerInfo;
import android.content.Context;
import android.me.p2psip.R;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class AListPeer extends ArrayAdapter<PeerInfo> {
	Context context;
	int resourceId;
	ArrayList<PeerInfo> listPeerInfo;
	
	public AListPeer(Context context, int resource, ArrayList<PeerInfo> objects) {
		super(context, resource, objects);
		// TODO Auto-generated constructor stub
		this.context = context;
		this.resourceId = resource;
		this.listPeerInfo = objects;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		convertView = LayoutInflater.from(context).inflate(resourceId, null);
		TextView textView = (TextView) convertView.findViewById(R.id.txtPeerInfo);
		
		PeerInfo peerInfo = listPeerInfo.get(position);
		textView.setText(peerInfo.toJSONObject().toString());
		
		return convertView;
	}
}
