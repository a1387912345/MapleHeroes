package net.server.login.handlers;

import client.MapleClient;
import net.MaplePacketHandler;
import tools.data.LittleEndianAccessor;

public class ClientHelloHandler implements MaplePacketHandler {

	@Override
	public void handlePacket(final LittleEndianAccessor lea, final MapleClient c) {
		System.out.println(c.getSessionIPAddress() + " Connected!");
	}

	@Override
	public boolean validateState(MapleClient c) {
		return true;
	}

}
