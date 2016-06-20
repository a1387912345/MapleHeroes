package net.server.farm.handler;

import client.MapleClient;
import client.character.MapleCharacter;
import net.MaplePacketHandler;
import net.RecvPacketOpcode;
import net.netty.MaplePacketReader;

public class FarmCheckQuestHandler extends MaplePacketHandler {

	public FarmCheckQuestHandler(RecvPacketOpcode recv) {
		super(recv);
	}

	@Override
	public void handlePacket(MaplePacketReader mpr, MapleClient c, MapleCharacter chr) {
		int farmId = mpr.readInt();
        //TODO code farm quests
        if (c.getFarm().getName().equals("Creating...")) {
            //c.sendPacket(FarmPacket.updateQuestInfo(1111, 1, "A1/Z/"));
        }
	}

}
