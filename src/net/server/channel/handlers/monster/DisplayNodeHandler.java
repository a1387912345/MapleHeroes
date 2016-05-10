package net.server.channel.handlers.monster;

import client.MapleCharacter;
import client.MapleClient;
import net.AbstractMaplePacketHandler;
import net.RecvPacketOpcode;
import server.life.MapleMonster;
import tools.data.LittleEndianAccessor;
import tools.packet.MobPacket;

public class DisplayNodeHandler extends AbstractMaplePacketHandler {

	public DisplayNodeHandler(RecvPacketOpcode recv) {
		super(recv);
	}

	@Override
	public void handlePacket(LittleEndianAccessor lea, MapleClient c, MapleCharacter chr) {
		MapleMonster mob_from = chr.getMap().getMonsterByOid(lea.readInt());
        if (mob_from != null) {
            chr.getClient().getSession().write(MobPacket.getNodeProperties(mob_from, chr.getMap()));
        }
	}

}
