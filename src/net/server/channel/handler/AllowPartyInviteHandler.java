package net.server.channel.handler;

import client.MapleClient;
import client.character.MapleCharacter;
import net.MaplePacketHandler;
import net.RecvPacketOpcode;
import net.netty.MaplePacketReader;
import server.quest.MapleQuest;

public class AllowPartyInviteHandler extends MaplePacketHandler {

	public AllowPartyInviteHandler(RecvPacketOpcode recv) {
		super(recv);
	}

	@Override
	public void handlePacket(final MaplePacketReader lea, final MapleClient c, final MapleCharacter chr) {
		if (lea.readByte() > 0) {
            c.getCharacter().getQuestRemove(MapleQuest.getInstance(122901));
        } else {
            c.getCharacter().getQuestNAdd(MapleQuest.getInstance(122901));
        }
	}

}
