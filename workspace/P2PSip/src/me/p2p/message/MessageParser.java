package me.p2p.message;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Phân tích thông điệp từ JSONObject thành các trường riêng biệt theo quy tắc
 * @author Sang
 *
 */
public class MessageParser {
	JSONObject rawData;
	EMsgType msgType;
	JSONObject msgData;

	public MessageParser(JSONObject data) {
		this.rawData = data;
		
		parse();
	}

	private void parse() {
		try {
			// parse msg type;
			String sMsgType = rawData.getString(MessageJSONAttribute.MSG_TYPE);
			// translate to msgType;
			if (sMsgType.equals(MessageJSONAttribute.MSG_TYPE_JOIN)) {
				msgType = EMsgType.JOIN;
			} else {
				if (sMsgType.equals(MessageJSONAttribute.MSG_TYPE_LEAVE)) {
					msgType = EMsgType.LEAVE;
				} else {
					if (sMsgType.equals(MessageJSONAttribute.MSG_TYPE_UPDATE)) {
						msgType = EMsgType.UPDATE;
					} else {
						if (sMsgType.equals(MessageJSONAttribute.MSG_TYPE_TRANSFERLIST)) {
							msgType = EMsgType.TRANSFER_LIST;
						} else {
							if (sMsgType.equals(MessageJSONAttribute.MSG_TYPE_ADD_NODE)) {
								msgType = EMsgType.ADD_NODE;
							}
						}
					}
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// get msg data;
		try {
			String sMsgData = rawData.getString(MessageJSONAttribute.MSG_DATA);
			msgData = new JSONObject(sMsgData);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public EMsgType getMessageType() {
		return this.msgType;
	}
	
	public JSONObject getMessageData() {
		return this.msgData;
	}
}
