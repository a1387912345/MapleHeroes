package net.channel.handler.inventory;

import client.MapleCharacter;
import client.MapleClient;
import net.MaplePacketHandler;
import net.RecvPacketOpcode;
import net.netty.MaplePacketReader;
import net.packet.CSPacket;

public class UseAlienSocketResponseHandler extends MaplePacketHandler {

	public UseAlienSocketResponseHandler(RecvPacketOpcode recv) {
		super(recv);
	}

	@Override
	public void handlePacket(MaplePacketReader lea, MapleClient c, MapleCharacter chr) {
		lea.skip(4); // all 0
        c.sendPacket(CSPacket.useAlienSocket(false));
	}
	
}
