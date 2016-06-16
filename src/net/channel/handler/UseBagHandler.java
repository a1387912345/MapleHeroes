package net.channel.handler;

import client.MapleCharacter;
import client.MapleClient;
import client.inventory.Item;
import client.inventory.ItemFlag;
import client.inventory.MapleInventoryType;
import net.MaplePacketHandler;
import net.RecvPacketOpcode;
import net.netty.MaplePacketReader;
import net.packet.CField;
import net.packet.CWvsContext;
import net.packet.CWvsContext.InventoryPacket;

public class UseBagHandler extends MaplePacketHandler {

	public UseBagHandler(RecvPacketOpcode recv) {
		super(recv);
	}

	@Override
	public void handlePacket(MaplePacketReader lea, MapleClient c, MapleCharacter chr) {
		if (chr == null || !chr.isAlive() || chr.getMap() == null || chr.hasBlockedInventory()) {
            c.sendPacket(CWvsContext.enableActions());
            return;
        }
        c.getCharacter().updateTick(lea.readInt());
        final byte slot = (byte) lea.readShort();
        final int itemId = lea.readInt();
        final Item toUse = chr.getInventory(MapleInventoryType.ETC).getItem(slot);

        if (toUse == null || toUse.getQuantity() < 1 || toUse.getItemId() != itemId || itemId / 10000 != 433) {
            c.sendPacket(CWvsContext.enableActions());
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
            c.sendPacket(InventoryPacket.updateSpecialItemUse(toUse, (byte) 4, toUse.getPosition(), true, chr));
        }
        c.sendPacket(CField.openBag(chr.getExtendedSlots().indexOf(itemId), itemId, firstTime));
        c.sendPacket(CWvsContext.enableActions());
	}

}
