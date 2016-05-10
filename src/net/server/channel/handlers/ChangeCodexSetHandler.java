package net.server.channel.handlers;

import client.MapleCharacter;
import client.MapleClient;
import constants.GameConstants;
import net.AbstractMaplePacketHandler;
import net.RecvPacketOpcode;
import server.quest.MapleQuest;
import tools.data.LittleEndianAccessor;
import tools.packet.CWvsContext;

public class ChangeCodexSetHandler extends AbstractMaplePacketHandler {

	public ChangeCodexSetHandler(RecvPacketOpcode recv) {
		super(recv);
	}

	@Override
	public void handlePacket(LittleEndianAccessor lea, MapleClient c, MapleCharacter chr) {
		if (c.getPlayer() == null || c.getPlayer().getMap() == null) {
            return;
        }
        final int set = lea.readInt();
        if (chr.getMonsterBook().changeSet(set)) {
            chr.getMonsterBook().applyBook(chr, false);
            chr.getQuestNAdd(MapleQuest.getInstance(GameConstants.CURRENT_SET)).setCustomData(String.valueOf(set));
            c.getSession().write(CWvsContext.changeCardSet(set));
        }
	}

}
