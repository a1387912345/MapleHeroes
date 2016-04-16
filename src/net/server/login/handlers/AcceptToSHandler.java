package net.server.login.handlers;

import client.MapleClient;
import net.AbstractMaplePacketHandler;
import tools.data.LittleEndianAccessor;

public class AcceptToSHandler extends AbstractMaplePacketHandler {

	@Override
	public boolean validateState(MapleClient c) {
		return !c.isLoggedIn();
	}
	
	@Override
	public void handlePacket(final LittleEndianAccessor lea, final MapleClient c) {

	}

}
