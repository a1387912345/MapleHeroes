package net.server.login.handlers;

import client.MapleClient;
import net.MaplePacketHandler;
import tools.data.LittleEndianAccessor;
import tools.packet.LoginPacket;

public class ClientRequestHandler implements MaplePacketHandler {

	public void handlePacket(final LittleEndianAccessor lea, final MapleClient c) {
		c.getSession().write(LoginPacket.getIntegrityResponse(lea.readInt()));
	}

	public boolean validateState(MapleClient c) {
		return true;
	}

}
