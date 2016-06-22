package net.server.channel.handler.monster;

import client.MapleClient;
import client.character.MapleCharacter;
import net.MaplePacketHandler;
import net.RecvPacketOpcode;
import net.netty.MaplePacketReader;
import server.Randomizer;
import server.life.MapleMonster;
import server.maps.MapleMap;

public class FriendlyDamageHandler extends MaplePacketHandler {

	public FriendlyDamageHandler(RecvPacketOpcode recv) {
		super(recv);
	}

	@Override
	public void handlePacket(MaplePacketReader mpr, MapleClient c, MapleCharacter chr) {
		MapleMap map = chr.getMap();
        if (map == null) {
            return;
        }
        MapleMonster mobfrom = map.getMonsterByOid(mpr.readInt());
        mpr.skip(4);
        MapleMonster mobto = map.getMonsterByOid(mpr.readInt());

        if ((mobfrom != null) && (mobto != null) && (mobto.getStats().isFriendly())) {
            int damage = mobto.getStats().getLevel() * Randomizer.nextInt(mobto.getStats().getLevel()) / 2;
            mobto.damage(chr, damage, true);
            MobHelper.checkShammos(chr, mobto, map);
        }
	}

}
