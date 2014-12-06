package me.p2p.spec;

import org.json.JSONObject;

public interface IPeer {
	public void joinRequest();
	public void leaveRequest();
	public void updateRequest();
	public void handleTransferRequest(JSONObject bstrListPeerInfo);
}
