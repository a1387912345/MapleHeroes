package net.server.farm.handler;

import client.MapleClient;
import client.character.MapleCharacter;
import net.MaplePacketHandler;
import net.RecvPacketOpcode;
import net.netty.MaplePacketReader;

public class FarmUseItemHandler extends MaplePacketHandler {

	public FarmUseItemHandler(RecvPacketOpcode recv) {
		super(recv);
	}

	@Override
	public void handlePacket(MaplePacketReader mpr, MapleClient c, MapleCharacter chr) {
		int itemId = mpr.readInt();
        //c.getFarm().getFarmInventory().updateItemQuantity(itemId, -1);
        //c.getFarm().gainAestheticPoints(aesthetic); //rewarded from building
	}

}
