package net.channel.handler.monster;

import client.MapleCharacter;
import client.MapleClient;
import net.MaplePacketHandler;
import net.RecvPacketOpcode;
import net.netty.MaplePacketReader;
import net.packet.MobPacket;
import server.life.MapleMonster;

public class DisplayNodeHandler extends MaplePacketHandler {

	public DisplayNodeHandler(RecvPacketOpcode recv) {
		super(recv);
	}

	@Override
	public void handlePacket(MaplePacketReader lea, MapleClient c, MapleCharacter chr) {
		MapleMonster mob_from = chr.getMap().getMonsterByOid(lea.readInt());
        if (mob_from != null) {
            chr.getClient().sendPacket(MobPacket.getNodeProperties(mob_from, chr.getMap()));
        }
	}

}
