package net.farm.handler;

import client.MapleCharacter;
import client.MapleClient;
import net.MaplePacketHandler;
import net.RecvPacketOpcode;
import net.netty.MaplePacketReader;

public class FarmCheckQuestHandler extends MaplePacketHandler {

	public FarmCheckQuestHandler(RecvPacketOpcode recv) {
		super(recv);
	}

	@Override
	public void handlePacket(MaplePacketReader lea, MapleClient c, MapleCharacter chr) {
		int farmId = lea.readInt();
        //TODO code farm quests
        if (c.getFarm().getName().equals("Creating...")) {
            //c.sendPacket(FarmPacket.updateQuestInfo(1111, 1, "A1/Z/"));
        }
	}

}
