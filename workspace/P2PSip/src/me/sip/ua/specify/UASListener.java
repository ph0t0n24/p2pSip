package me.sip.ua.specify;

import local.ua.UserAgent;

public interface UASListener {
	/**
	 * Hàm này được gọi khi nhận được phản hồi 180 Ringging từ UAC và UAS rung<br>
	 * chuông trở lại
	 * 
	 * @param ua
	 */
	public void onUASCallRinging(UserAgent ua);

	/**
	 * Hàm này được gọi khi cuộc gọi do UAS được chấp nhận bởi UAC
	 * 
	 * @param ua
	 */
	public void onCallUACAccepted(UserAgent ua);

	/**
	 * Hàm này được gọi khi đang trong quá trình rung chuông mà UAC từ chối cuộc<br>
	 * gọi hoặc chưa được UAC chấp nhận mà UAS gọi hangup()
	 */
	public void onCallUACFailed(UserAgent ua);

	/**
	 * Hàm này được gọi khi kết thúc cuộc gọi và UAC hangup()
	 * 
	 * @param ua
	 */
	public void onUASCallClosed(UserAgent ua);
}
