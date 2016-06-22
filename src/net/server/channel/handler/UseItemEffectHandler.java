package net.server.channel.handler;

import client.MapleClient;
import client.character.MapleCharacter;
import client.inventory.Item;
import client.inventory.MapleInventoryType;
import net.MaplePacketHandler;
import net.RecvPacketOpcode;
import net.netty.MaplePacketReader;
import net.packet.CField;
import net.packet.CWvsContext;

public class UseItemEffectHandler extends MaplePacketHandler {

	public UseItemEffectHandler(RecvPacketOpcode recv) {
		super(recv);
	}

	@Override
	public void handlePacket(MaplePacketReader mpr, MapleClient c, MapleCharacter chr) {
		final int itemId = mpr.readInt();
		
		Item toUse = chr.getInventory((itemId == 4290001) || (itemId == 4290000) ? MapleInventoryType.ETC : MapleInventoryType.CASH).findById(itemId);
        if ((toUse == null) || (toUse.getItemId() != itemId) || (toUse.getQuantity() < 1)) {
            c.sendPacket(CWvsContext.enableActions());
            return;
        }
        if (itemId != 5510000) {
            chr.setItemEffect(itemId);
        }
        chr.getMap().broadcastMessage(chr, CField.itemEffect(chr.getID(), itemId), false);
	}

}
