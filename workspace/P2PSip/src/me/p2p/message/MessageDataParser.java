package me.p2p.message;

import java.util.ArrayList;

import me.p2p.PeerInfo;
import me.p2p.data.DataJSONAttribute;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MessageDataParser {
	JSONObject msgData;
	ArrayList<PeerInfo> listPeerInfo;
	
	public MessageDataParser(JSONObject msgData) {
		// TODO Auto-generated constructor stub
		this.msgData = msgData;
		this.listPeerInfo = new ArrayList<>();
		
		parse();
	}
	
	private void parse() {
		// get list peer in json form;
		JSONArray jsListPeer = null;
		try {
			jsListPeer = msgData.getJSONArray(DataJSONAttribute.JS_LIST_PEER);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		for (int i = 0; i < jsListPeer.length(); i++) {
			try {
				PeerInfo peerInfo = new PeerInfo(jsListPeer.getJSONObject(i));
				listPeerInfo.add(peerInfo);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public ArrayList<PeerInfo> getListPeerInfo() {
		return listPeerInfo;
	}
}
