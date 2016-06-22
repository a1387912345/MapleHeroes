package net.server.channel.handler;

import java.util.ArrayList;
import java.util.List;

import client.MapleClient;
import client.character.MapleCharacter;
import net.MaplePacketHandler;
import net.RecvPacketOpcode;
import net.netty.MaplePacketReader;
import net.packet.CWvsContext;
import server.MapleItemInformationProvider;
import server.life.MapleMonsterInformationProvider;
import server.life.MonsterDropEntry;
import server.life.MonsterGlobalDropEntry;

public class MonsterBookDropsRequestHandler extends MaplePacketHandler {

	public MonsterBookDropsRequestHandler(RecvPacketOpcode recv) {
		super(recv);
	}

	@Override
	public void handlePacket(MaplePacketReader mpr, MapleClient c, MapleCharacter chr) {
		if (c.getCharacter() == null || c.getCharacter().getMap() == null) {
            return;
        }
        chr.updateTick(mpr.readInt()); // tick
        final int cardid = mpr.readInt();
        final int mobid = MapleItemInformationProvider.getInstance().getCardMobId(cardid);
        if (mobid <= 0 || !chr.getMonsterBook().hasCard(cardid)) {
            c.sendPacket(CWvsContext.getCardDrops(cardid, null));
            return;
        }
        final MapleMonsterInformationProvider ii = MapleMonsterInformationProvider.getInstance();
        final List<Integer> newDrops = new ArrayList<>();
        for (final MonsterDropEntry de : ii.retrieveDrop(mobid)) {
            if (de.itemId > 0 && de.questid <= 0 && !newDrops.contains(de.itemId)) {
                newDrops.add(de.itemId);
            }
        }
        for (final MonsterGlobalDropEntry de : ii.getGlobalDrop()) {
            if (de.itemId > 0 && de.questid <= 0 && !newDrops.contains(de.itemId)) {
                newDrops.add(de.itemId);
            }
        }
        c.sendPacket(CWvsContext.getCardDrops(cardid, newDrops));
	}

}
