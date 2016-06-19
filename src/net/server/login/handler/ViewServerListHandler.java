package net.server.login.handler;

import client.MapleClient;
import client.character.MapleCharacter;
import net.MaplePacketHandler;
import net.RecvPacketOpcode;
import net.netty.MaplePacketReader;

public class ViewServerListHandler extends MaplePacketHandler {

	public ViewServerListHandler(RecvPacketOpcode recv) {
		super(recv);
	}

	@Override
	public void handlePacket(final MaplePacketReader mpr, final MapleClient client, MapleCharacter chr) {
		if(mpr.readByte() == 0) {
			MaplePacketHandler serverlistHandler = new ServerlistRequestHandler(recv);
			serverlistHandler.handlePacket(mpr, client, chr);
		}

	}

}
