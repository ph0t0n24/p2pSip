package me.p2p;

import java.io.Serializable;

import me.p2p.data.DataJSONAttribute;

import org.json.JSONException;
import org.json.JSONObject;

public class PeerInfo implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Thông tin về tên người sử dụng của node peer này,<br>
	 * được sử dụng bởi sip để định danh người dùng
	 */
	public String userName;
	
	/**
	 * Thông tin về địa chỉ ip của peer node
	 */
	public String address;
	
	public PeerInfo(JSONObject jsPeerInfo) {
		PeerInfoParser peerInfoParser = new PeerInfoParser(jsPeerInfo);
		this.address = peerInfoParser.getPeerInfo().address;
		this.userName = peerInfoParser.getPeerInfo().userName;
	}

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
			jsObject.accumulate(DataJSONAttribute.JS_PEER_ADDRESS, address);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			jsObject.accumulate(DataJSONAttribute.JS_PEER_USER_NAME, userName);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return jsObject;
	}
	
	public boolean isEqual(PeerInfo peerInfo) {
		if (peerInfo.address.equals(address) && 
				peerInfo.userName.equals(userName)) {
			return true;
		}
		
		return false;
	}
}
