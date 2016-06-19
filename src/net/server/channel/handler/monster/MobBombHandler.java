package net.server.channel.handler.monster;

import client.MapleClient;
import client.MonsterStatus;
import client.character.MapleCharacter;
import net.MaplePacketHandler;
import net.RecvPacketOpcode;
import net.netty.MaplePacketReader;
import server.life.MapleMonster;
import server.maps.MapleMap;

public class MobBombHandler extends MaplePacketHandler {

	public MobBombHandler(RecvPacketOpcode recv) {
		super(recv);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void handlePacket(MaplePacketReader lea, MapleClient c, MapleCharacter chr) {
		MapleMap map = chr.getMap();
        if (map == null) {
            return;
        }
        MapleMonster mobfrom = map.getMonsterByOid(lea.readInt());
        lea.skip(4);
        lea.readInt();

        if ((mobfrom != null) && (mobfrom.getBuff(MonsterStatus.MONSTER_BOMB) != null));
	}

}
