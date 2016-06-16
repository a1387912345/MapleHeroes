package net.farm.handler;

import client.MapleCharacter;
import client.MapleClient;
import net.MaplePacketHandler;
import net.RecvPacketOpcode;
import net.netty.MaplePacketReader;

public class FarmPlaceObjectHandler extends MaplePacketHandler {

	public FarmPlaceObjectHandler(RecvPacketOpcode recv) {
		super(recv);
	}

	@Override
	public void handlePacket(MaplePacketReader lea, MapleClient c, MapleCharacter chr) {
		int position = lea.readInt();
        int itemId = lea.readInt();
        lea.readByte(); //idk
        if (itemId / 10000 < 112 || itemId / 10000 > 114) {
            return;
        }
        if (position > (25 * 25) - 1) { //biggest farm 25x25
            return;
        }
        int size = (itemId / 10000) % 10;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (!c.getFarm().checkSpace(size, position - j - i)) {
                    return;
                }
            }
        }
        //c.getFarm().getFarmInventory().updateItemQuantity(itemId, -1);
        //c.getFarm().gainAestheticPoints(aesthetic); //rewarded from building
	}

}