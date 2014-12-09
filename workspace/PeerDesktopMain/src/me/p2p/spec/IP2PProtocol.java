package me.p2p.spec;

import java.net.Socket;

import org.json.JSONObject;

public interface IP2PProtocol {
	/**
	 * Thời gian hết hiệu lực kết nối tới một cổng là 30 giây 
	 */
	public static final int SC_TIME_OUT = 30000;
	
	/**
	 * Số lượng kết nối tối đa, ở môi trường thử nghiệm chỉ là 10;
	 */
	public static final int BACK_LOG = 10;
	
	/**
	 * Khi nhận được msg leave thì việc bootstrap cần làm là:<br>
	 * - Lấy thông tin về peer được gửi từ client.<br>
	 * - Xóa thông tin của node trong danh sách peer.<br>
	 * - (?) Truyền msg ok đã xóa ok đến peer.<br>
	 * => Các tham số cần là:<br>
	 * - Message Data.<br>
	 * - Socket của client đang đợi để lấy dữ liệu.<br>
	 */
	public void handleLeaveMsg(JSONObject data, Socket peerSocket);
	
	/**
	 * Khi nhận được msg update thì việc bootstrap cần làm là:<br>
	 * - Lấy thông tin về peer được gửi từ client.<br>
	 * - Cập nhật thông tin của node trong danh sách.<br>
	 * - Tung broadcast đến tất cả những peer trong list peer.
	 * => Các tham số cần là:<br>
	 * - Message Data.<br>
	 * - Trong trường hợp này bootstrap node đóng vai trò là client gửi<br>
	 * yêu cầu đến tất cả server là tất cả các peer node trong list peer.
	 */
	public void handleUpdateMsg(JSONObject data);
	
	/**
	 * - Khởi động lắng nghe request từ những client khác.<br>
	 * Lúc này peer đóng vai trò là server để xử lý các yêu<br>
	 * như chuyển dữ liệu từ bootstrap (transfer peer list),<br>
	 * yêu cầu leave, update từ những peer khác. 
	 */
	public void listenRequest();
	
	/**
	 * Bất kỳ node nào đều cũng cần phải shutdown khi kết thúc hoạt động
	 */
	public void shutdown();
}