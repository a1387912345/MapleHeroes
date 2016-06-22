package net.server.login.handler;

import client.MapleClient;
import client.character.MapleCharacter;
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
	public void handlePacket(final MaplePacketReader mpr, final MapleClient client, MapleCharacter chr) {

	}

}
