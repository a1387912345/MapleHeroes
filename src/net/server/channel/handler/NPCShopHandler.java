package net.server.channel.handler;

import client.MapleClient;
import client.character.MapleCharacter;
import constants.GameConstants;
import net.MaplePacketHandler;
import net.RecvPacketOpcode;
import net.netty.MaplePacketReader;
import server.shops.MapleShop;

public class NPCShopHandler extends MaplePacketHandler {

	public NPCShopHandler(RecvPacketOpcode recv) {
		super(recv);
	}

	@Override
	public void handlePacket(final MaplePacketReader mpr, final MapleClient c, MapleCharacter chr) {
		byte bmode = mpr.readByte();
		
        if (chr == null) {
            return;
        }

        switch (bmode) {
            case 0: {
                MapleShop shop = chr.getShop();
                if (shop == null) {
                    return;
                }
                short slot = mpr.readShort();
                slot++;
                int itemId = mpr.readInt();
                short quantity = mpr.readShort();
                // int unitprice = inPacket.readInt();
                shop.buy(c, slot, itemId, quantity);
                break;
            }
            case 1: {
                MapleShop shop = chr.getShop();
                if (shop == null) {
                    return;
                }
                byte slot = (byte) mpr.readShort();
                int itemId = mpr.readInt();
                short quantity = mpr.readShort();
                shop.sell(c, GameConstants.getInventoryType(itemId), slot, quantity);
                break;
            }
            case 2: {
                MapleShop shop = chr.getShop();
                if (shop == null) {
                    return;
                }
                byte slot = (byte) mpr.readShort();
                shop.recharge(c, slot);
                break;
            }
            default:
                chr.setConversation(0);
        }
	}

}
