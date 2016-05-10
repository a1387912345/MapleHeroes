package net.server.channel.handlers;

import client.MapleCharacter;
import client.MapleClient;
import client.inventory.Item;
import client.inventory.ItemFlag;
import client.inventory.MapleInventoryType;
import net.AbstractMaplePacketHandler;
import net.RecvPacketOpcode;
import tools.data.LittleEndianAccessor;
import tools.packet.CField;
import tools.packet.CWvsContext;
import tools.packet.CWvsContext.InventoryPacket;

public class UseBagHandler extends AbstractMaplePacketHandler {

	public UseBagHandler(RecvPacketOpcode recv) {
		super(recv);
	}

	@Override
	public void handlePacket(LittleEndianAccessor lea, MapleClient c, MapleCharacter chr) {
		if (chr == null || !chr.isAlive() || chr.getMap() == null || chr.hasBlockedInventory()) {
            c.getSession().write(CWvsContext.enableActions());
            return;
        }
        c.getPlayer().updateTick(lea.readInt());
        final byte slot = (byte) lea.readShort();
        final int itemId = lea.readInt();
        final Item toUse = chr.getInventory(MapleInventoryType.ETC).getItem(slot);

        if (toUse == null || toUse.getQuantity() < 1 || toUse.getItemId() != itemId || itemId / 10000 != 433) {
            c.getSession().write(CWvsContext.enableActions());
            return;
        }
        boolean firstTime = !chr.getExtendedSlots().contains(itemId);
        if (firstTime) {
            chr.getExtendedSlots().add(itemId);
            chr.changedExtended();
            short flag = toUse.getFlag();
            flag |= ItemFlag.LOCK.getValue();
            flag |= ItemFlag.UNTRADABLE.getValue();
            toUse.setFlag(flag);
            c.getSession().write(InventoryPacket.updateSpecialItemUse(toUse, (byte) 4, toUse.getPosition(), true, chr));
        }
        c.getSession().write(CField.openBag(chr.getExtendedSlots().indexOf(itemId), itemId, firstTime));
        c.getSession().write(CWvsContext.enableActions());
	}

}
