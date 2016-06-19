package net.server.channel.handler.inventory;

import client.MapleClient;
import client.character.MapleCharacter;
import client.inventory.Equip;
import client.inventory.Item;
import client.inventory.MapleInventoryType;
import net.MaplePacketHandler;
import net.RecvPacketOpcode;
import net.netty.MaplePacketReader;
import net.packet.CField;
import net.packet.CWvsContext;
import net.packet.CWvsContext.InventoryPacket;
import server.MapleInventoryManipulator;
import server.MapleItemInformationProvider;
import tools.FileoutputUtil;

public class UseCraftedCubeHandler extends MaplePacketHandler {

	public UseCraftedCubeHandler(RecvPacketOpcode recv) {
		super(recv);
	}

	@Override
	public void handlePacket(MaplePacketReader lea, MapleClient c, MapleCharacter chr) {
		//[47 80 12 04] [0B 00] [03 00]
        c.getCharacter().updateTick(lea.readInt());
        final Item item = c.getCharacter().getInventory(MapleInventoryType.EQUIP).getItem(lea.readShort());
        final Item toUse = c.getCharacter().getInventory(MapleInventoryType.USE).getItem(lea.readShort());
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        if (toUse.getItemId() / 10000 != 271 || item == null || toUse == null
                || c.getCharacter().getInventory(MapleInventoryType.USE).getNumFreeSlot() < 1
                || ii.getEquipStats(toUse.getItemId()).containsKey("success")) {
            c.getCharacter().getMap().broadcastMessage(CField.showPotentialReset(c.getCharacter().getID(), false, item.getItemId()));
            c.sendPacket(CField.enchantResult(0));
            return;
        }
        final Equip eq = (Equip) item;
        if (eq.getState() >= 17 && eq.getState() <= 20) {
            eq.renewPotential(0, 0, 0, false);
            c.getCharacter().getMap().broadcastMessage(CField.showPotentialReset(c.getCharacter().getID(), true, item.getItemId()));
            c.sendPacket(InventoryPacket.scrolledItem(toUse, MapleInventoryType.EQUIP, item, false, true, false));
            c.getCharacter().forceReAddItem_NoUpdate(item, MapleInventoryType.EQUIP);
            MapleInventoryManipulator.addById(c, 2430112, (short) 1, "Cube" + " on " + FileoutputUtil.CurrentReadable_Date());
            c.sendPacket(CField.enchantResult(1));
            c.sendPacket(CWvsContext.enableActions());
        } else {
            c.getCharacter().dropMessage(5, "This item's Potential cannot be reset.");
        }
	}

}
