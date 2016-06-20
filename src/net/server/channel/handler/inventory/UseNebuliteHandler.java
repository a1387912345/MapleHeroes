package net.server.channel.handler.inventory;

import client.MapleClient;
import client.character.MapleCharacter;
import client.inventory.Equip;
import client.inventory.Item;
import client.inventory.MapleInventoryType;
import constants.GameConstants;
import net.MaplePacketHandler;
import net.RecvPacketOpcode;
import net.netty.MaplePacketReader;
import net.packet.CField;
import net.packet.CWvsContext;
import net.packet.CWvsContext.InventoryPacket;
import server.MapleInventoryManipulator;
import server.MapleItemInformationProvider;
import server.StructItemOption;

public class UseNebuliteHandler extends MaplePacketHandler {

	public UseNebuliteHandler(RecvPacketOpcode recv) {
		super(recv);
	}

	@Override
	public void handlePacket(MaplePacketReader mpr, MapleClient c, MapleCharacter chr) {
		c.getCharacter().updateTick(mpr.readInt());
        c.getCharacter().setScrolledPosition((short) 0);
        final Item nebulite = c.getCharacter().getInventory(MapleInventoryType.SETUP).getItem((byte) mpr.readShort());
        final int nebuliteId = mpr.readInt();
        final Item toMount = c.getCharacter().getInventory(MapleInventoryType.EQUIP).getItem((byte) mpr.readShort());
        if (nebulite == null || nebuliteId != nebulite.getItemId() || toMount == null || c.getCharacter().hasBlockedInventory()) {
            c.sendPacket(InventoryPacket.getInventoryFull());
            return;
        }
        final Equip eqq = (Equip) toMount;
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        boolean success = false;
        if (eqq.getSocket1() == 0/* || eqq.getSocket2() == 0 || eqq.getSocket3() == 0*/) { // GMS removed 2nd and 3rd sockets, we can put into npc.
            final StructItemOption pot = ii.getSocketInfo(nebuliteId);
            if (pot != null && GameConstants.optionTypeFits(pot.optionType, eqq.getItemId())) {
                //if (eqq.getSocket1() == 0) { // priority comes first
                eqq.setSocket1(pot.opID);
                //}// else if (eqq.getSocket2() == 0) {
                //    eqq.setSocket2(pot.opID);
                //} else if (eqq.getSocket3() == 0) {
                //    eqq.setSocket3(pot.opID);
                //}
                MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.SETUP, nebulite.getPosition(), (short) 1, false);
                c.getCharacter().forceReAddItem(toMount, MapleInventoryType.EQUIP);
                success = true;
            }
        }
        c.getCharacter().getMap().broadcastMessage(CField.showNebuliteEffect(c.getCharacter().getID(), success));
        c.sendPacket(CWvsContext.enableActions());
	}

}
