package net.server.channel.handler;

import client.MapleClient;
import client.character.MapleCharacter;
import client.inventory.Item;
import client.inventory.MapleInventoryType;
import net.MaplePacketHandler;
import net.RecvPacketOpcode;
import net.netty.MaplePacketReader;
import net.packet.CWvsContext;
import server.MapleInventoryManipulator;
import server.MapleItemInformationProvider;
import server.maps.FieldLimitType;

public class ReturnScrollHandler extends MaplePacketHandler {

	public ReturnScrollHandler(RecvPacketOpcode recv) {
		super(recv);
	}

	@Override
	public void handlePacket(MaplePacketReader mpr, MapleClient c, MapleCharacter chr) {
		if (!chr.isAlive() || chr.getMapId() == 749040100 || chr.hasBlockedInventory() || chr.isInBlockedMap() || chr.inPVP()) {
            c.sendPacket(CWvsContext.enableActions());
            return;
        }
        c.getCharacter().updateTick(mpr.readInt());
        final byte slot = (byte) mpr.readShort();
        final int itemId = mpr.readInt();
        final Item toUse = chr.getInventory(MapleInventoryType.USE).getItem(slot);

        if (toUse == null || toUse.getQuantity() < 1 || toUse.getItemId() != itemId) {
            c.sendPacket(CWvsContext.enableActions());
            return;
        }
        if (!FieldLimitType.PotionUse.check(chr.getMap().getFieldLimit())) {
            if (MapleItemInformationProvider.getInstance().getItemEffect(toUse.getItemId()).applyReturnScroll(chr)) {
                MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (short) 1, false);
            } else {
                c.sendPacket(CWvsContext.enableActions());
            }
        } else {
            c.sendPacket(CWvsContext.enableActions());
        }
	}

}
