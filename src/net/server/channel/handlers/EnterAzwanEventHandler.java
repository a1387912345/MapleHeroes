package net.server.channel.handlers;

import client.MapleCharacter;
import client.MapleClient;
import net.AbstractMaplePacketHandler;
import net.RecvPacketOpcode;
import tools.data.LittleEndianAccessor;
import tools.packet.CField;
import tools.packet.CWvsContext;

public class EnterAzwanEventHandler extends AbstractMaplePacketHandler {

	public EnterAzwanEventHandler(RecvPacketOpcode recv) {
		super(recv);
	}

	@Override
	public void handlePacket(LittleEndianAccessor lea, MapleClient c, MapleCharacter chr) {
		if (c.getPlayer() == null || c.getPlayer().getMap() == null) {
        c.getSession().write(CField.pvpBlocked(1));
        c.getSession().write(CWvsContext.enableActions());
        return;
    }
    int mapid = lea.readInt();
    c.getPlayer().changeMap(c.getChannelServer().getMapFactory().getMap(mapid));

	}

}
