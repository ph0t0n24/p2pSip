package me.p2p.data;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import me.p2p.PeerInfo;
import me.p2p.PeerInfoParser;
import me.p2p.spec.IData;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DataManager implements IData {
	static final String ERROR = "Must call prepare() first";
	static final String FILE_NAME_LIST_PEER = "list_peer.json";
	static final String FILE_NAME_STATUS = "status.json";
	static final String FILE_NAME_LOG = "log.log";
	static final String INIT_DATA = "{\"" + DataJSONAttribute.JS_LIST_PEER
			+ "\":[]}"; // dữ liệu khởi đầu là mảng rỗng;

	static DataManager INSTANCE = null;
	String fileListPeerPath;
	String fileStatusPath;
	String fileLogPath;

	File fileListPeer;
	File fileStatus;
	File fileLog;

	ArrayList<PeerInfo> listPeerInfo;

	/**
	 * Biến dùng để xác định thử peer có tham gia vào mạng hay chưa?
	 */
	boolean join = false;

	/**
	 * 
	 * Có ghi ra status file hay không?
	 */
	boolean isStatus = true;
	/**
	 * Có ghi ra log file hay không?
	 */
	boolean isLog = true;

	public static void prepare(String filePath, boolean status, boolean log) {
		INSTANCE = new DataManager(filePath, status, log);
	}

	public static DataManager getInstance() {
		if (INSTANCE == null) {
			throw new IllegalStateException(ERROR);
		}

		return INSTANCE;
	}

	private DataManager(String filePath, boolean status, boolean log) {
		// TODO Auto-generated constructor stub
		this.isStatus = status;
		this.isLog = log;

		// create status file if allow;
		if (this.isStatus) {
			this.fileStatusPath = filePath + "/" + FILE_NAME_STATUS;
			this.fileStatus = new File(this.fileStatusPath);

			if (!fileStatus.exists()) {
				try {
					fileStatus.createNewFile();
					join = false;
					writeToStatusFile();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				readStatusFile();
			}
		}

		// create file log if allow
		if (this.isLog) {
			this.fileLogPath = filePath + "/" + FILE_NAME_LOG;
			this.fileLog = new File(this.fileLogPath);
		}

		this.fileListPeerPath = filePath + "/" + FILE_NAME_LIST_PEER;
		this.fileListPeer = new File(this.fileListPeerPath);
		/*
		 * Nếu file list peer không tồn tại thì khởi tạo nó và init data;
		 */
		if (!fileListPeer.exists()) {
			try {
				fileListPeer.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			try {
				FileUtils.writeStringToFile(fileListPeer, INIT_DATA);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		listPeerInfo = new ArrayList<>();

		// read file only one time;
		readListPeerFile();
	}

	private String readFile(File fileToRead) {
		String data = null;
		try {
			data = FileUtils.readFileToString(fileToRead);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		;

		return data;
	}

	private void writeFile(File fileToWrite, String data) {
		try {
			FileUtils.writeStringToFile(fileToWrite, data);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void readStatusFile() {
		if (!this.isStatus) {
			throw new IllegalAccessError("Status is not enable");
		}
		
		String data = readFile(fileStatus);

		try {
			JSONObject jsObject = new JSONObject(data);
			join = jsObject.getBoolean(DataJSONAttribute.STATUS_JOIN);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void writeToStatusFile() {
		if (!this.isStatus) {
			throw new IllegalAccessError("Status is not enable");
		}
		
		JSONObject jsObject = new JSONObject();

		try {
			jsObject.accumulate(DataJSONAttribute.STATUS_JOIN, join);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		writeFile(fileStatus, jsObject.toString());
	}

	private void readListPeerFile() {
		ArrayList<PeerInfo> result = new ArrayList<>();
		PeerInfoParser peerInfoParser = new PeerInfoParser();
		String data = readFile(fileListPeer);

		JSONArray jsPeerList = null;
		try {
			JSONObject jsObjectListPeer = new JSONObject(data);
			jsPeerList = jsObjectListPeer
					.getJSONArray(DataJSONAttribute.JS_LIST_PEER);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		for (int i = 0; i < jsPeerList.length(); i++) {
			try {
				JSONObject jsonObject = jsPeerList.getJSONObject(i);
				result.add(peerInfoParser.parseToDirectResult(jsonObject));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void writeToListPeerFile() {
		JSONArray jaPeerInfo = new JSONArray();
		// convert from list to json array;
		for (PeerInfo peerInfo : listPeerInfo) {
			jaPeerInfo.put(peerInfo.toJSONObject());
		}

		// convert to list json;
		JSONObject jsonObjectListPeer = new JSONObject();
		try {
			jsonObjectListPeer.put(DataJSONAttribute.JS_LIST_PEER, jaPeerInfo);
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		// write to file;
		String data = jsonObjectListPeer.toString();
		writeFile(fileListPeer, data);
	}

	@Override
	public ArrayList<PeerInfo> getListPeerInfo() {
		// TODO Auto-generated method stub
		return listPeerInfo;
	}

	public JSONObject getJsonListPeer() {
		JSONObject jsObjectListPeer = new JSONObject();
		JSONArray jsListPeer = new JSONArray();
		for (PeerInfo peerInfo : listPeerInfo) {
			jsListPeer.put(peerInfo.toJSONObject());
		}

		try {
			jsObjectListPeer.put(DataJSONAttribute.JS_LIST_PEER, jsListPeer);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return jsObjectListPeer;
	}

	@Override
	public void add(PeerInfo peerInfo) {
		// TODO Auto-generated method stub
		if (!isExist(peerInfo)) {
			listPeerInfo.add(peerInfo);

			writeToListPeerFile();
		}
	}

	@Override
	public void remove(PeerInfo peerInfo) {
		// TODO Auto-generated method stub
		for (PeerInfo p : listPeerInfo) {
			if (p.equals(peerInfo)) {
				listPeerInfo.remove(p);
				break;
			}
		}

		writeToListPeerFile();
	}

	@Override
	public void update(PeerInfo peerInfo) {
		// TODO Auto-generated method stub
		int index = -1;
		for (int i = 0; i < listPeerInfo.size(); i++) {
			PeerInfo p = listPeerInfo.get(i);
			if (p.equals(peerInfo)) {
				index = i;
				break;
			}
		}

		if (index != -1) {
			listPeerInfo.remove(index);
			listPeerInfo.add(index, peerInfo);
		}

		writeToListPeerFile();
	}

	@Override
	public boolean isExist(PeerInfo peerInfo) {
		// TODO Auto-generated method stub
		for (PeerInfo pInfo : listPeerInfo) {
			if (pInfo.isEqual(peerInfo)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Ghi dữ liệu xác định rằng nút peer đã tham gia vào mạng;
	 */
	public void joined() {
		join = true;
		writeToStatusFile();
	}
	
	/**
	 * Ghi log file;
	 */
	public void writeToLogFile(String aLogLine) {
		try {
			FileWriter fileWriter = new FileWriter(fileLog, true);
			fileWriter.write(aLogLine + "\n");
			fileWriter.flush();
			fileWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Nút đã tham gia vào mạng hay chưa?
	 * 
	 * @return
	 */
	public boolean isJoined() {
		return this.join;
	}
}