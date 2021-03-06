package me.p2p;

import me.p2p.data.DataJSONAttribute;

import org.json.JSONException;
import org.json.JSONObject;

public class PeerInfoParser {
	private PeerInfo peerInfo;
	private JSONObject jsPeerInfo;

	/**
	 * Hàm tạo này được sử dụng khi muốn sử dụng đối tượng này nhiều lần<br>
	 * và thực hiện parse lấy kết quả trực tiếp trong hàm<br>
	 * parseToDirectResult(JSONObject)
	 */
	public PeerInfoParser() {
	}

	/**
	 * Hàm tạo này được sử dụng khi muốn sử dụng đối tượng này một lần<br>
	 * (hoặc nhiều hơn). Truyền trực tiếp data để phân tích và lấy thông tin<br>
	 * trong hàm getPeerInfo();
	 * 
	 * @param data
	 */
	public PeerInfoParser(JSONObject data) {
		jsPeerInfo = data;

		parse();
	}

	private void parse() {
		peerInfo = new PeerInfo();
		try {
			peerInfo.address = jsPeerInfo
					.getString(DataJSONAttribute.JS_PEER_ADDRESS);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			peerInfo.userName = jsPeerInfo
					.getString(DataJSONAttribute.JS_PEER_USER_NAME);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Sử dụng hàm này để parse PeerInfo từ data và lấy kết quả trực tiếp
	 * @param data
	 * @return
	 */
	public PeerInfo parseToDirectResult(JSONObject data) {
		this.jsPeerInfo = data;
		parse();
		return this.peerInfo;
	}

	public PeerInfo getPeerInfo() {
		return this.peerInfo;
	}
}
