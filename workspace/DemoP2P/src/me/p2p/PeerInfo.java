package me.p2p;

import org.json.JSONException;
import org.json.JSONObject;

public class PeerInfo {
	public String userName;
	public String address;

	public PeerInfo() {
		address = null;
		userName = null;
	}

	public PeerInfo(String address, String userName) {
		this.address = address;
		this.userName = userName;
	}

	public JSONObject toJSONObject() {
		JSONObject jsObject = new JSONObject();
		
		try {
			jsObject.accumulate(PeerInfoAttribute.PEER_ADDRESS, address);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			jsObject.accumulate(PeerInfoAttribute.PEER_USER_NAME, userName);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return jsObject;
	}
}
