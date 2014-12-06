package me.p2p;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import me.p2p.constant.PeerPort;
import me.p2p.message.EMsgType;
import me.p2p.message.Message;
import me.p2p.request.RequestHandler;
import me.p2p.request.RequestType;
import me.p2p.request.Request;
import me.p2p.spec.MessageCallback;

import org.json.JSONException;
import org.json.JSONObject;

public class PeerMain {
	final static String TAG = "PeerMain";
	static String data = "{\"address\":\"0.0.0.0\", \"username\":\"QuangSang\"}";
	
	public static void main(String[] args) {
		try {
			Socket socket = new Socket("192.168.1.51", PeerPort.PORT_BOOTSTRAP);

			// send request;
			Request request = new Request(socket);
			request.startSession();

			for (int i = 0; i < 3; i++) {
				System.out.println("Send Message...");
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				System.out.println("Send: " + RequestType.START_MSG + "\n");
				// send start_msg;
				request.startMsg();

				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				// send data;
				System.out.println("Send: " + data + "\n");
				JSONObject jsData = null;
				try {
					jsData = new JSONObject(data);
				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				Message message = new Message(EMsgType.JOIN, jsData);
				request.sendMessage(message);

				// send end_msg;
				System.out.println("Send " + RequestType.END_MSG + "\n");
				request.endMsg();

				// sleep 500;
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			// end session;
			request.endSession();
			
			RequestHandler requestHandler = new RequestHandler(socket, new MessageCallback() {
				
				@Override
				public void onSessionStart() {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onSessionEnd() {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onMessage(Socket peerSocket, JSONObject data) {
					// TODO Auto-generated method stub
					
				}
			});
			
			requestHandler.handleRequest();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
