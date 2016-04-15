package net.login.handler;

import client.MapleClient;
import net.AbstractMaplePacketHandler;
import tools.data.input.SeekableLittleEndianAccessor;
import tools.packet.LoginPacket;

public class ClientRequestHandler extends AbstractMaplePacketHandler {

	@Override
	public void handlePacket(final SeekableLittleEndianAccessor slea, final MapleClient c) {
		c.getSession().write(LoginPacket.getIntegrityResponse(slea.readInt()));

	}


}
