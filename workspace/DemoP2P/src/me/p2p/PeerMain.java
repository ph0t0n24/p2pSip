package me.p2p;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;

import me.p2p.CommandHandler.ICommand;
import me.p2p.constant.PeerPort;

public class PeerMain {
	final static String TAG = "PeerMain";

	static String data = "{\"msg_type\":\"join\", \"msg_data\":{\"address\":\"0.0.0.0\", \"username\":\"QuangSang\"}}";

	public static void main(String[] args) {
		try {
			Socket socket = new Socket("127.0.0.1", PeerPort.PORT_BOOTSTRAP);
			DataOutputStream dtos = new DataOutputStream(
					socket.getOutputStream());
			String input = null;

			BufferedReader br = new BufferedReader(new InputStreamReader(
					System.in));

			Request request = new Request(socket);
			request.send(MsgProtocol.START_SESSION_MSG);

			// gui 10 lan;
			for (int i = 0; i < 5; i++) {
				System.out.println("Send Message...");
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				System.out.println("Send: " + MsgProtocol.START_MSG + "\n");
				// send start_msg;
				request.send(MsgProtocol.START_MSG);

				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// send data;
				System.out.println("Send: " + data + "\n");
				request.send(data);

				// send end_msg;
				System.out.println("Send " + MsgProtocol.END_MSG + "\n");
				request.send(MsgProtocol.END_MSG);

				// sleep 500;
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			// end session;
			request.send(MsgProtocol.END_SESSION_MSG);

			/* Chờ đọc lại dữ liệu từ Server */
			BufferedReader sReader = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));
			/* Block and Read */
			CommandHandler commandHandler = new CommandHandler(sReader);
			commandHandler.setStartSession(new ICommand() {

				@Override
				public void onCommand(String command) {
					// TODO Auto-generated method stub
					Log.logToConsole(TAG, command);
				}
			});
			commandHandler.setStartMsg(new ICommand() {

				@Override
				public void onCommand(String command) {
					// TODO Auto-generated method stub
					Log.logToConsole(TAG, command);
				}
			});
			commandHandler.setInMsg(new ICommand() {

				@Override
				public void onCommand(String command) {
					// TODO Auto-generated method stub
					Log.logToConsole(TAG, command);
				}
			});
			commandHandler.setEndMsg(new ICommand() {

				@Override
				public void onCommand(String command) {
					// TODO Auto-generated method stub
					Log.logToConsole(TAG, command);
				}
			});
			commandHandler.setEndSession(new ICommand() {

				@Override
				public void onCommand(String command) {
					// TODO Auto-generated method stub
					Log.logToConsole(TAG, command);
				}
			});
			
			commandHandler.processCommand();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
