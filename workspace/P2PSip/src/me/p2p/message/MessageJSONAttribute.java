package me.p2p.message;

public class MessageJSONAttribute {
	public static final String MSG_TYPE = "msg_type";
	public static final String MSG_DATA = "msg_data";
	
	/**
	 * Thông điệp peer node gửi tới bootstrap node để<br>
	 * bootstrap node xử lý yêu cầu từ peer node.
	 */
	public static final String MSG_TYPE_JOIN = "join";
	public static final String MSG_TYPE_LEAVE = "leave";
	public static final String MSG_TYPE_UPDATE = "update";
	
	/**
	 * Thông điệp gửi từ bootstrap ndoe tới peer node để<br>
	 * peer node xử lý;
	 */
	public static final String MSG_TYPE_TRANSFERLIST = "transfer_list";
	public static final String MSG_TYPE_ADD_NODE = "add_node";
}
