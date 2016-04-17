package net.server.login.handlers;

import client.MapleClient;
import net.MaplePacketHandler;
import net.RecvPacketOpcode;
import tools.data.LittleEndianAccessor;

public class ClientHelloHandler implements MaplePacketHandler {
	private RecvPacketOpcode recv;
	
	public ClientHelloHandler(RecvPacketOpcode recv) {
		this.recv = recv;
	}

	public void handlePacket(final LittleEndianAccessor lea, final MapleClient c) {
		System.out.println(c.getSessionIPAddress() + " Connected!");
	}

	public boolean validateState(MapleClient c) {
		return true;
	}

	public RecvPacketOpcode getRecvOpcode() {
		return recv;
	}

}
