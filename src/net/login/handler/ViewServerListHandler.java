package net.login.handler;

import client.MapleCharacter;
import client.MapleClient;
import net.MaplePacketHandler;
import net.MaplePacketHandler;
import net.RecvPacketOpcode;
import net.netty.MaplePacketReader;

public class ViewServerListHandler extends MaplePacketHandler {

	public ViewServerListHandler(RecvPacketOpcode recv) {
		super(recv);
	}

	@Override
	public void handlePacket(final MaplePacketReader lea, final MapleClient c, MapleCharacter chr) {
		if(lea.readByte() == 0) {
			MaplePacketHandler handler = new ServerlistRequestHandler(recv);
			handler.handlePacket(lea, c, chr);
		}

	}

}
