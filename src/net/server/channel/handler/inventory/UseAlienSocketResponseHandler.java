package net.server.channel.handler.inventory;

import client.MapleClient;
import client.character.MapleCharacter;
import net.MaplePacketHandler;
import net.RecvPacketOpcode;
import net.netty.MaplePacketReader;
import net.packet.CSPacket;

public class UseAlienSocketResponseHandler extends MaplePacketHandler {

	public UseAlienSocketResponseHandler(RecvPacketOpcode recv) {
		super(recv);
	}

	@Override
	public void handlePacket(MaplePacketReader mpr, MapleClient c, MapleCharacter chr) {
		mpr.skip(4); // all 0
        c.sendPacket(CSPacket.useAlienSocket(false));
	}
	
}
