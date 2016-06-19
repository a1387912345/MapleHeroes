package net.server.channel.handler;

import client.MapleClient;
import client.character.MapleCharacter;
import constants.GameConstants;
import net.MaplePacketHandler;
import net.RecvPacketOpcode;
import net.netty.MaplePacketReader;
import server.quest.MapleQuest;

public class PetBuffHandler extends MaplePacketHandler {

	public PetBuffHandler(RecvPacketOpcode recv) {
		super(recv);
	}

	@Override
	public void handlePacket(MaplePacketReader lea, MapleClient c, MapleCharacter chr) {
		lea.readInt(); //0
        int skill = lea.readInt();
        lea.readByte(); //0
        if (skill <= 0) {
            chr.getQuestRemove(MapleQuest.getInstance(GameConstants.BUFF_ITEM));
        } else {
            chr.getQuestNAdd(MapleQuest.getInstance(GameConstants.BUFF_ITEM)).setCustomData(String.valueOf(skill));
        }
	}

}
