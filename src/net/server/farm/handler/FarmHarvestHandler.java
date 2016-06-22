package net.server.farm.handler;

import client.MapleClient;
import client.character.MapleCharacter;
import net.MaplePacketHandler;
import net.RecvPacketOpcode;
import net.netty.MaplePacketReader;

public class FarmHarvestHandler extends MaplePacketHandler {

	public FarmHarvestHandler(RecvPacketOpcode recv) {
		super(recv);
	}

	@Override
	public void handlePacket(MaplePacketReader mpr, MapleClient c, MapleCharacter chr) {
		mpr.readInt(); //position
        //c.getFarm().getFarmInventory().updateItemQuantity(oid, -1);
        //c.getFarm().gainAestheticPoints(aesthetic); //rewarded from building
	}

}