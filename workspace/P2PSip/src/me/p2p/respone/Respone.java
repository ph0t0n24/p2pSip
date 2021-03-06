package me.p2p.respone;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import me.p2p.request.RequestBuilder;

public class Respone {
	Socket socket;
	DataOutputStream dtos;
	
	public Respone(Socket socket) {
		this.socket = socket;
		
		try {
			this.dtos = new DataOutputStream(this.socket.getOutputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Gửi respone cho biết server đã chấp nhận
	 */
	public void sendServerOk() {
		try {
			this.dtos.writeBytes(RequestBuilder.build(ResponeType.SERVER_OK));
			this.dtos.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
