package me.p2p;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Request {
	DataOutputStream dtos;
	Socket socket;

	public Request(Socket socket) {
		this.socket = socket;
		try {
			this.dtos = new DataOutputStream(this.socket.getOutputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void send(String message) {
		try {
			this.dtos.writeBytes(MessageBuilder.build(message));
			this.dtos.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void startSession() {
		try {
			this.dtos.writeBytes(MessageBuilder.build(MsgProtocol.START_SESSION_MSG));
			this.dtos.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void startMsg() {
		try {
			this.dtos.writeBytes(MessageBuilder.build(MsgProtocol.START_MSG));
			this.dtos.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void endMsg() {
		try {
			this.dtos.writeBytes(MessageBuilder.build(MsgProtocol.END_MSG));
			this.dtos.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void endSession() {
		try {
			this.dtos.writeBytes(MessageBuilder.build(MsgProtocol.END_SESSION_MSG));
			this.dtos.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
