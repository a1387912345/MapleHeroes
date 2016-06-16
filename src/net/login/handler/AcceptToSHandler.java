package net.login.handler;

import client.MapleCharacter;
import client.MapleClient;
import net.MaplePacketHandler;
import net.RecvPacketOpcode;
import net.netty.MaplePacketReader;

public class AcceptToSHandler extends MaplePacketHandler {

	public AcceptToSHandler(RecvPacketOpcode recv) {
		super(recv);
	}

	@Override
	public boolean validateState(MapleClient c) {
		return !c.isLoggedIn();
	}
	
	@Override
	public void handlePacket(final MaplePacketReader lea, final MapleClient c, MapleCharacter chr) {

	}

}
