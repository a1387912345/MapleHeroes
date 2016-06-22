package net.server.channel.handler.inventory;

import client.MapleClient;
import client.character.MapleCharacter;
import client.inventory.Item;
import client.inventory.MapleInventoryType;
import net.MaplePacketHandler;
import net.RecvPacketOpcode;
import net.netty.MaplePacketReader;
import net.packet.CWvsContext;
import server.MapleInventoryManipulator;

public class UseExpPotionHandler extends MaplePacketHandler {

	public UseExpPotionHandler(RecvPacketOpcode recv) {
		super(recv);
	}

	@Override
	public void handlePacket(MaplePacketReader mpr, MapleClient c, MapleCharacter chr) {
		//inPacket: [F5 4F D6 2E] [60 00] [F4 06 22 00]
        System.err.println("eror");
        c.getCharacter().updateTick(mpr.readInt());
        final byte slot = (byte) mpr.readShort();
        int itemid = mpr.readInt();
        final Item toUse = chr.getInventory(MapleInventoryType.USE).getItem(slot);
        if (toUse == null || toUse.getQuantity() < 1
                || toUse.getItemId() != itemid || chr.getLevel() >= 250
                || chr.hasBlockedInventory() || itemid / 10000 != 223) {
            c.sendPacket(CWvsContext.enableActions());
            return;
        }
        if (itemid != 2230004) { //for now
            c.sendPacket(CWvsContext.enableActions());
            return;
        }
        int level = chr.getLevel();
        chr.gainExp(chr.getNeededExp() - chr.getExp(), true, true, false);
        boolean first = false;
        boolean last = false;
        int potionDstLevel = 18;
        if (!chr.getInfoQuest(7985).contains("2230004=")) {
            first = true;
        } else {
            if (chr.getInfoQuest(7985).equals("2230004=" + potionDstLevel + "#384")) {
                last = true;
            }
        }
        c.sendPacket(CWvsContext.updateExpPotion(last ? 0 : 2, chr.getID(), itemid, first, level, potionDstLevel));
        if (first) {
            chr.updateInfoQuest(7985, "2230004=" + level + "#384");
        }
        if (last) {
            MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (short) 1, false);
        }
        c.sendPacket(CWvsContext.enableActions());
	}

}
