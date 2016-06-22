package net.server.channel.handler;

import client.MapleClient;
import client.character.MapleCharacter;
import constants.GameConstants;
import net.MaplePacketHandler;
import net.RecvPacketOpcode;
import net.netty.MaplePacketReader;
import net.packet.CWvsContext;
import server.quest.MapleQuest;

public class ChangeCodexSetHandler extends MaplePacketHandler {

	public ChangeCodexSetHandler(RecvPacketOpcode recv) {
		super(recv);
	}

	@Override
	public void handlePacket(MaplePacketReader mpr, MapleClient c, MapleCharacter chr) {
		if (c.getCharacter() == null || c.getCharacter().getMap() == null) {
            return;
        }
        final int set = mpr.readInt();
        if (chr.getMonsterBook().changeSet(set)) {
            chr.getMonsterBook().applyBook(chr, false);
            chr.getQuestNAdd(MapleQuest.getInstance(GameConstants.CURRENT_SET)).setCustomData(String.valueOf(set));
            c.sendPacket(CWvsContext.changeCardSet(set));
        }
	}

}
