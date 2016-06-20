package net.server.channel.handler;

import client.MapleClient;
import client.character.MapleCharacter;
import net.MaplePacketHandler;
import net.RecvPacketOpcode;
import net.netty.MaplePacketReader;
import net.packet.CWvsContext;

public class MonsterBookInfoHandler extends MaplePacketHandler {

	public MonsterBookInfoHandler(RecvPacketOpcode recv) {
		super(recv);
	}

	@Override
	public void handlePacket(MaplePacketReader mpr, MapleClient c, MapleCharacter chr) {
		if (c.getCharacter() == null || c.getCharacter().getMap() == null) {
            return;
        }
        mpr.readInt(); // tick
        final MapleCharacter player = c.getCharacter().getMap().getCharacterById(mpr.readInt());
        c.sendPacket(CWvsContext.enableActions());
        if (player != null && !player.isClone()) {
            if (!player.isGM() || c.getCharacter().isGM()) {
                c.sendPacket(CWvsContext.getMonsterBookInfo(player));
            }
        }
	}

}
