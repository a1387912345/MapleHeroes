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
	public void handlePacket(final MaplePacketReader lea, final MapleClient c, final MapleCharacter chr) {
		if (c.getCharacter().hasBlockedInventory()) { //hack
            return;
        }
        c.getCharacter().setScrolledPosition((short) 0);
        c.getCharacter().updateTick(lea.readInt());
        final MapleInventoryType type = MapleInventoryType.getByType(lea.readByte());
        final short src = lea.readShort();
        final short dst = lea.readShort();
        final short quantity = lea.readShort();
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
