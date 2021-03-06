package me.sip.ua.specify;

import org.zoolu.sip.address.NameAddress;

import local.ua.UserAgent;

public interface UACListener {
	/**
	 * Hàm này được gọi khi có cuộc gọi phát sinh từ Server tới;
	 */
	public void onUACCallIncoming(UserAgent ua, NameAddress caller,
			NameAddress callee);
	
	/**
	 * Hàm này được gọi khi UAC đang trong quá trình rung chuông mà UAS chấm dứt<br>
	 * cuộc gọi
	 * 
	 * @param ua
	 */
	public void onCallUASCancelled(UserAgent ua);

	/**
	 * Hàm này được gọi khi kết thúc cuộc gọi và UAC hangup()
	 * 
	 * @param ua
	 */
	public void onUACCallClosed(UserAgent ua);
}
