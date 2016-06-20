package net.server.channel.handler;

import client.MapleClient;
import client.character.MapleCharacter;
import client.inventory.MapleInventoryType;
import net.MaplePacketHandler;
import net.RecvPacketOpcode;
import net.netty.MaplePacketReader;
import server.MapleInventoryManipulator;

public class ItemMoveHandler extends MaplePacketHandler {

	public ItemMoveHandler(RecvPacketOpcode recv) {
		super(recv);
	}

	@Override
	public void handlePacket(final MaplePacketReader mpr, final MapleClient c, final MapleCharacter chr) {
		if (c.getCharacter().hasBlockedInventory()) { //hack
            return;
        }
        c.getCharacter().setScrolledPosition((short) 0);
        c.getCharacter().updateTick(mpr.readInt());
        final MapleInventoryType type = MapleInventoryType.getByType(mpr.readByte());
        final short src = mpr.readShort();
        final short dst = mpr.readShort();
        final short quantity = mpr.readShort();
        System.out.println("item move " + type.name() + " " + src + " " + dst + " " + quantity);

        if (src < 0 && dst > 0) {
            MapleInventoryManipulator.unequip(c, src, dst);
        } else if (dst < 0) {
            MapleInventoryManipulator.equip(c, src, dst);
        } else if (dst == 0) {
            MapleInventoryManipulator.drop(c, type, src, quantity);
        } else {
            MapleInventoryManipulator.move(c, type, src, dst);
        }
	}

}
