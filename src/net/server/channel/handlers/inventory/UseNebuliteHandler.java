package net.server.channel.handlers.inventory;

import client.MapleCharacter;
import client.MapleClient;
import client.inventory.Equip;
import client.inventory.Item;
import client.inventory.MapleInventoryType;
import constants.GameConstants;
import net.AbstractMaplePacketHandler;
import net.RecvPacketOpcode;
import server.MapleInventoryManipulator;
import server.MapleItemInformationProvider;
import server.StructItemOption;
import tools.data.LittleEndianAccessor;
import tools.packet.CField;
import tools.packet.CWvsContext;
import tools.packet.CWvsContext.InventoryPacket;

public class UseNebuliteHandler extends AbstractMaplePacketHandler {

	public UseNebuliteHandler(RecvPacketOpcode recv) {
		super(recv);
	}

	@Override
	public void handlePacket(LittleEndianAccessor lea, MapleClient c, MapleCharacter chr) {
		c.getPlayer().updateTick(lea.readInt());
        c.getPlayer().setScrolledPosition((short) 0);
        final Item nebulite = c.getPlayer().getInventory(MapleInventoryType.SETUP).getItem((byte) lea.readShort());
        final int nebuliteId = lea.readInt();
        final Item toMount = c.getPlayer().getInventory(MapleInventoryType.EQUIP).getItem((byte) lea.readShort());
        if (nebulite == null || nebuliteId != nebulite.getItemId() || toMount == null || c.getPlayer().hasBlockedInventory()) {
            c.getSession().write(InventoryPacket.getInventoryFull());
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
                c.getPlayer().forceReAddItem(toMount, MapleInventoryType.EQUIP);
                success = true;
            }
        }
        c.getPlayer().getMap().broadcastMessage(CField.showNebuliteEffect(c.getPlayer().getId(), success));
        c.getSession().write(CWvsContext.enableActions());
	}

}
