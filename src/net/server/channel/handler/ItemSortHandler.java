package net.server.channel.handler;

import client.MapleClient;
import client.character.MapleCharacter;
import client.inventory.MapleInventory;
import client.inventory.MapleInventoryType;
import net.MaplePacketHandler;
import net.RecvPacketOpcode;
import net.netty.MaplePacketReader;
import net.packet.CWvsContext;
import server.MapleInventoryManipulator;

public class ItemSortHandler extends MaplePacketHandler {

	public ItemSortHandler(RecvPacketOpcode recv) {
		super(recv);
	}

	@Override
	public void handlePacket(final MaplePacketReader mpr, final MapleClient c, final MapleCharacter chr) {
		c.getCharacter().updateTick(mpr.readInt());
        c.getCharacter().setScrolledPosition((short) 0);
        final MapleInventoryType pInvType = MapleInventoryType.getByType(mpr.readByte());
        if (pInvType == MapleInventoryType.UNDEFINED || c.getCharacter().hasBlockedInventory()) {
            c.sendPacket(CWvsContext.enableActions());
            return;
        }
        final MapleInventory pInv = c.getCharacter().getInventory(pInvType); //Mode should correspond with MapleInventoryType
        boolean sorted = false;

        while (!sorted) {
            final byte freeSlot = (byte) pInv.getNextFreeSlot();
            if (freeSlot != -1) {
                byte itemSlot = -1;
                for (byte i = (byte) (freeSlot + 1); i <= pInv.getSlotLimit(); i++) {
                    if (pInv.getItem(i) != null) {
                        itemSlot = i;
                        break;
                    }
                }
                if (itemSlot > 0) {
                    MapleInventoryManipulator.move(c, pInvType, itemSlot, freeSlot);
                } else {
                    sorted = true;
                }
            } else {
                sorted = true;
            }
        }
        c.sendPacket(CWvsContext.finishedSort(pInvType.getType()));
        c.sendPacket(CWvsContext.enableActions());
	}

}
