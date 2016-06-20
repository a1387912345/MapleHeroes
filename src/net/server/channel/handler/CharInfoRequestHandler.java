package net.server.channel.handler;

import client.MapleClient;
import client.character.MapleCharacter;
import net.MaplePacketHandler;
import net.RecvPacketOpcode;
import net.netty.MaplePacketReader;
import net.packet.CWvsContext;

public class CharInfoRequestHandler extends MaplePacketHandler {

	public CharInfoRequestHandler(RecvPacketOpcode recv) {
		super(recv);
	}

	@Override
	public void handlePacket(MaplePacketReader mpr, MapleClient c, MapleCharacter chr) {
        chr.updateTick(mpr.readInt());
        final int objectid = mpr.readInt();
        
        if (c.getCharacter() == null || c.getCharacter().getMap() == null) {
            return;
        }
        MapleCharacter player = c.getCharacter().getMap().getCharacterById(objectid);
        c.sendPacket(CWvsContext.enableActions());
        if (player != null/* && (!player.isGM() || c.getPlayer().isGM())*/) {
            c.sendPacket(CWvsContext.charInfo(player, c.getCharacter().getID() == objectid));
        }
	}

}
