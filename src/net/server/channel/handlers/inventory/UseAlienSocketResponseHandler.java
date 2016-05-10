package net.server.channel.handlers.inventory;

import client.MapleCharacter;
import client.MapleClient;
import net.AbstractMaplePacketHandler;
import net.RecvPacketOpcode;
import tools.data.LittleEndianAccessor;
import tools.packet.CSPacket;

public class UseAlienSocketResponseHandler extends AbstractMaplePacketHandler {

	public UseAlienSocketResponseHandler(RecvPacketOpcode recv) {
		super(recv);
	}

	@Override
	public void handlePacket(LittleEndianAccessor lea, MapleClient c, MapleCharacter chr) {
		lea.skip(4); // all 0
        c.getSession().write(CSPacket.useAlienSocket(false));
	}
	
}
