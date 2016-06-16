package net.farm.handler;

import client.MapleCharacter;
import client.MapleClient;
import net.MaplePacketHandler;
import net.RecvPacketOpcode;
import net.netty.MaplePacketReader;

public class FarmShopBuyHandler extends MaplePacketHandler {

	public FarmShopBuyHandler(RecvPacketOpcode recv) {
		super(recv);
	}

	@Override
	public void handlePacket(MaplePacketReader lea, MapleClient c, MapleCharacter chr) {
		int itemId = lea.readInt();
        //c.getFarm().getFarmInventory().gainWaru(-price);
        //c.getFarm().getFarmInventory().updateItemQuantity(itemId, 1);
        //c.getFarm().gainAestheticPoints(aesthetic); //rewarded from building
	}

}
