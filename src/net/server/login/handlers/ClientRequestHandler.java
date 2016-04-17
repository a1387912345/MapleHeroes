package net.server.login.handlers;

import client.MapleClient;
import net.MaplePacketHandler;
import net.RecvPacketOpcode;
import tools.data.LittleEndianAccessor;
import tools.packet.LoginPacket;

public class ClientRequestHandler implements MaplePacketHandler {
	private RecvPacketOpcode recv;
	
	public ClientRequestHandler(RecvPacketOpcode recv) {
		this.recv = recv;
	}
	
	public void handlePacket(final LittleEndianAccessor lea, final MapleClient c) {
		c.getSession().write(LoginPacket.getIntegrityResponse(lea.readInt()));
	}

	public boolean validateState(MapleClient c) {
		return true;
	}

	public RecvPacketOpcode getRecvOpcode() {
		return recv;
	}

}
