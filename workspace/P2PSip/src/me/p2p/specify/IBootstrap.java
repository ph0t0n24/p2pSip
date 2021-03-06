package me.p2p.specify;

import org.json.JSONObject;

public interface IBootstrap extends IP2PProtocol {
	/**
	 * Khi nhận msg join thì việc boostrap cần làm là:<br>
	 * - Lấy thông tin về peer được gửi từ client.<br>
	 * - Lưu thông tin vào danh sách peer.<br>
	 * - Truyền thông tin danh sách peer đến peer vừa đưa vào theo phương thức giao<br>
	 * tiếp TextProtocol.<br>
	 * - Thông báo đến tất cả những node còn lại để cập nhật danh sách peer.<br>
	 * => Các tham số cần là:<br>
	 * - Message Data.<br>
	 * - Socket của client đang đợi để lấy dữ liệu.<br>
	 */
	public void handleJoinMsg(JSONObject data);
}
