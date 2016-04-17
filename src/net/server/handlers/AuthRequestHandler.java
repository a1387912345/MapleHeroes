package net.server.handlers;

import client.MapleCharacter;
import client.MapleClient;
import net.MaplePacketHandler;
import net.RecvPacketOpcode;
import net.SendPacketOpcode;
import tools.data.LittleEndianAccessor;
import tools.packet.LoginPacket;

public class AuthRequestHandler implements MaplePacketHandler {
	private RecvPacketOpcode recv;
	
	public AuthRequestHandler(RecvPacketOpcode recv) {
		this.recv = recv;
	}
	
	public void handlePacket(final LittleEndianAccessor lea, final MapleClient c, MapleCharacter chr) {
		c.getSession().write(LoginPacket.sendAuthResponse(SendPacketOpcode.AUTH_RESPONSE.getValue() ^ lea.readInt()));
	}

	public boolean validateState(MapleClient c) {
		return true;
	}

	@Override
	public RecvPacketOpcode getRecvOpcode() {
		return recv;
	}

}
