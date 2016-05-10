package net.server.channel.handlers.inventory;

import client.MapleCharacter;
import client.MapleClient;
import client.inventory.Equip;
import client.inventory.Item;
import client.inventory.MapleInventoryType;
import net.AbstractMaplePacketHandler;
import net.RecvPacketOpcode;
import server.MapleInventoryManipulator;
import server.MapleItemInformationProvider;
import tools.FileoutputUtil;
import tools.data.LittleEndianAccessor;
import tools.packet.CField;
import tools.packet.CWvsContext;
import tools.packet.CWvsContext.InventoryPacket;

public class UseCraftedCubeHandler extends AbstractMaplePacketHandler {

	public UseCraftedCubeHandler(RecvPacketOpcode recv) {
		super(recv);
	}

	@Override
	public void handlePacket(LittleEndianAccessor lea, MapleClient c, MapleCharacter chr) {
		//[47 80 12 04] [0B 00] [03 00]
        c.getPlayer().updateTick(lea.readInt());
        final Item item = c.getPlayer().getInventory(MapleInventoryType.EQUIP).getItem(lea.readShort());
        final Item toUse = c.getPlayer().getInventory(MapleInventoryType.USE).getItem(lea.readShort());
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        if (toUse.getItemId() / 10000 != 271 || item == null || toUse == null
                || c.getPlayer().getInventory(MapleInventoryType.USE).getNumFreeSlot() < 1
                || ii.getEquipStats(toUse.getItemId()).containsKey("success")) {
            c.getPlayer().getMap().broadcastMessage(CField.showPotentialReset(c.getPlayer().getId(), false, item.getItemId()));
            c.getSession().write(CField.enchantResult(0));
            return;
        }
        final Equip eq = (Equip) item;
        if (eq.getState() >= 17 && eq.getState() <= 20) {
            eq.renewPotential(0, 0, 0, false);
            c.getPlayer().getMap().broadcastMessage(CField.showPotentialReset(c.getPlayer().getId(), true, item.getItemId()));
            c.getSession().write(InventoryPacket.scrolledItem(toUse, MapleInventoryType.EQUIP, item, false, true, false));
            c.getPlayer().forceReAddItem_NoUpdate(item, MapleInventoryType.EQUIP);
            MapleInventoryManipulator.addById(c, 2430112, (short) 1, "Cube" + " on " + FileoutputUtil.CurrentReadable_Date());
            c.getSession().write(CField.enchantResult(1));
            c.getSession().write(CWvsContext.enableActions());
        } else {
            c.getPlayer().dropMessage(5, "This item's Potential cannot be reset.");
        }
	}

}
