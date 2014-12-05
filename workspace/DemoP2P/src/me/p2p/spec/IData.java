package me.p2p.spec;

import java.util.ArrayList;

import me.p2p.PeerInfo;

public interface IData {
	public ArrayList<PeerInfo> getListPeer();
	public void add(PeerInfo peerInfo);
	public void remove(PeerInfo peerInfo);
	public void update(PeerInfo peerInfo);
	public boolean isExist(PeerInfo peerInfo);
}
