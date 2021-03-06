package me.p2p.message;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Khi các node được kết nối với nhau thì các node sẽ giao tiếp bằng<br>
 * thông điệp. Nội dung của thông điệp bao gồm:<br>
 * - Loại thông điệp.<br>
 * - Dữ liệu của thông điệp.<br>
 * 
 * @author Sang
 * 
 */
public class Message {
	EMsgType msgType;
	JSONObject msgData;

	public Message(JSONObject jsMsg) {
		// get msgType
		MessageParser msgParser = new MessageParser(jsMsg);

		this.msgType = msgParser.getMessageType();
		this.msgData = msgParser.getMessageData();
	}

	public Message(EMsgType msgType, JSONObject msgData) {
		// TODO Auto-generated constructor stub
		this.msgType = msgType;
		this.msgData = msgData;
	}

	public EMsgType getMsgType() {
		return msgType;
	}

	public JSONObject getMsgData() {
		return msgData;
	}

	/**
	 * Vì message được gửi và nhận theo giao thức json nên cần phải<br>
	 * có hàm chuyển đổi thành json.
	 */
	public JSONObject toJsonObject() {
		JSONObject jsMsg = new JSONObject();

		try {
			jsMsg.accumulate(MessageJSONAttribute.MSG_TYPE,
					convertMsgTypeToString());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			jsMsg.accumulate(MessageJSONAttribute.MSG_DATA, msgData);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return jsMsg;
	}

	private String convertMsgTypeToString() {
		String result = null;
		switch (msgType) {
		case JOIN:
			result = MessageJSONAttribute.MSG_TYPE_JOIN;
			break;
		case LEAVE:
			result = MessageJSONAttribute.MSG_TYPE_LEAVE;
		case UPDATE:
			result = MessageJSONAttribute.MSG_TYPE_UPDATE;
			break;
		case TRANSFER_LIST:
			result = MessageJSONAttribute.MSG_TYPE_TRANSFERLIST;
			break;
		case ADD_NODE:
			result = MessageJSONAttribute.MSG_TYPE_ADD_NODE;
			break;
		}

		return result;
	}
}
