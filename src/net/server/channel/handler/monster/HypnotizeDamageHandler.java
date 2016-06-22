package net.server.channel.handler.monster;

import client.MapleClient;
import client.character.MapleCharacter;
import net.MaplePacketHandler;
import net.RecvPacketOpcode;
import net.netty.MaplePacketReader;
import server.life.MapleMonster;

public class HypnotizeDamageHandler extends MaplePacketHandler {

	public HypnotizeDamageHandler(RecvPacketOpcode recv) {
		super(recv);
	}

	@Override
	public void handlePacket(MaplePacketReader mpr, MapleClient c, MapleCharacter chr) {
		MapleMonster mob_from = chr.getMap().getMonsterByOid(mpr.readInt());
        mpr.skip(4);
        int to = mpr.readInt();
        mpr.skip(1);
        int damage = mpr.readInt();

        MapleMonster mob_to = chr.getMap().getMonsterByOid(to);

        if ((mob_from != null) && (mob_to != null) && (mob_to.getStats().isFriendly())) {
            if (damage > 30000) {
                return;
            }
            mob_to.damage(chr, damage, true);
            MobHelper.checkShammos(chr, mob_to, chr.getMap());
        }
	}

}
