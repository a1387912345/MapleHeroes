package net.server.channel.handler;

import client.MapleClient;
import client.character.MapleCharacter;
import net.MaplePacketHandler;
import net.RecvPacketOpcode;
import net.netty.MaplePacketReader;
import net.packet.CField;
import net.packet.CWvsContext;

public class EnterAzwanEventHandler extends MaplePacketHandler {

	public EnterAzwanEventHandler(RecvPacketOpcode recv) {
		super(recv);
	}

	@Override
	public void handlePacket(MaplePacketReader lea, MapleClient c, MapleCharacter chr) {
		if (c.getCharacter() == null || c.getCharacter().getMap() == null) {
        c.sendPacket(CField.pvpBlocked(1));
        c.sendPacket(CWvsContext.enableActions());
        return;
    }
    int mapid = lea.readInt();
    c.getCharacter().changeMap(c.getChannelServer().getMapFactory().getMap(mapid));

	}

}
