package net.farm.handler;

import client.MapleCharacter;
import client.MapleClient;
import net.MaplePacketHandler;
import net.RecvPacketOpcode;
import net.netty.MaplePacketReader;

public class FarmHarvestHandler extends MaplePacketHandler {

	public FarmHarvestHandler(RecvPacketOpcode recv) {
		super(recv);
	}

	@Override
	public void handlePacket(MaplePacketReader lea, MapleClient c, MapleCharacter chr) {
		lea.readInt(); //position
        //c.getFarm().getFarmInventory().updateItemQuantity(oid, -1);
        //c.getFarm().gainAestheticPoints(aesthetic); //rewarded from building
	}

}
