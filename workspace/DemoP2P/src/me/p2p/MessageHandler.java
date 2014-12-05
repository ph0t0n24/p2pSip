package me.p2p;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import me.p2p.spec.MessageCallback;

import org.json.JSONException;
import org.json.JSONObject;

public class MessageHandler extends Thread {
	static final String TAG = "MessageHandler";
	Socket socket;
	boolean inMessage = false;
	StringBuilder msgData = null;
	MessageCallback msgListener;
	String clientCommand = null;
	BufferedReader bufferedReader;

	public MessageHandler(Socket socket, MessageCallback msgListener)
			throws IOException {
		// TODO Auto-generated constructor stub
		// client;
		this.socket = socket;
		this.msgListener = msgListener;

		this.clientCommand = null;
		this.bufferedReader = new BufferedReader(new InputStreamReader(
				this.socket.getInputStream()));
	}

	public void run() {
		// TODO Auto-generated method stub
		while (true) {
			// handle request, read data;
			try {
				clientCommand = bufferedReader.readLine();
				System.out.println("Client Command: " + clientCommand);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if (clientCommand != null) {
				if (clientCommand.equals(MsgProtocol.START_SESSION_MSG)) {
					System.out.println(TAG + ": Start Session");
					if (msgListener != null) {
						msgListener.onSessionStart();
					}
				} else {
					if (clientCommand.equals(MsgProtocol.END_SESSION_MSG)) {
						System.out.println(TAG + ": Exit Session");
						if (msgListener != null) {
							msgListener.onSessionEnd();
						}

						break;
					} else {
						if (clientCommand.equals(MsgProtocol.START_MSG)) {
							// bat dau doc data, chuan bi doi de chua;
							System.out.println(TAG + ": Start Command");

							inMessage = true;
							msgData = new StringBuilder();
						} else {
							if (clientCommand.equals(MsgProtocol.END_MSG)) {
								// ket thuc doc data;
								System.out.println(TAG + ": End Command");
								// change state of message handler;
								inMessage = false;
								// log to console;
								System.out.println(msgData.toString());
								// notify msg listener;
								if (msgListener != null) {
									try {
										JSONObject data = new JSONObject(
												msgData.toString());
										msgListener.onMessage(socket, data);
									} catch (JSONException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								}
							} else {
								if (inMessage) {
									// nếu không phải hai trường hợp kia thì
									// append
									// nó
									// vào
									// data;
									System.out.println(TAG
											+ ": In Message: Append Data");
									msgData.append(clientCommand);
								} else {
									System.out.println(TAG
											+ ": Not In Message: Skip");
								}
							}
						}
					}
				}
			} else {
				System.out.println(TAG + ": client command is null");
			}
		}
	}

	public void handleMessage() {
		System.out.println(TAG + " handle message...");
		start();
	}
}