package net.server.channel.handlers.inventory;

import client.MapleCharacter;
import client.MapleClient;
import client.inventory.Equip;
import client.inventory.Item;
import client.inventory.MapleInventoryType;
import net.AbstractMaplePacketHandler;
import net.RecvPacketOpcode;
import server.MapleInventoryManipulator;
import tools.data.LittleEndianAccessor;
import tools.packet.CSPacket;
import tools.packet.CWvsContext.InventoryPacket;

public class UseAlienSocketHandler extends AbstractMaplePacketHandler {

	public UseAlienSocketHandler(RecvPacketOpcode recv) {
		super(recv);
	}

	@Override
	public void handlePacket(LittleEndianAccessor lea, MapleClient c, MapleCharacter chr) {
		c.getPlayer().updateTick(lea.readInt());
        c.getPlayer().setScrolledPosition((short) 0);
        final Item alienSocket = c.getPlayer().getInventory(MapleInventoryType.USE).getItem((byte) lea.readShort());
        final int alienSocketId = lea.readInt();
        final Item toMount = c.getPlayer().getInventory(MapleInventoryType.EQUIP).getItem((byte) lea.readShort());
        if (alienSocket == null || alienSocketId != alienSocket.getItemId() || toMount == null || c.getPlayer().hasBlockedInventory()) {
            c.getSession().write(InventoryPacket.getInventoryFull());
            return;
        }
        // Can only use once-> 2nd and 3rd must use NPC.
        final Equip eqq = (Equip) toMount;
        if (eqq.getSocketState() != 0) { // Used before
            c.getPlayer().dropMessage(1, "This item already has a socket.");
        } else {
            c.getSession().write(CSPacket.useAlienSocket(false));
            eqq.setSocket1(0); // First socket, GMS removed the other 2
            MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, alienSocket.getPosition(), (short) 1, false);
            c.getPlayer().forceReAddItem(toMount, MapleInventoryType.EQUIP);
        }
        c.getSession().write(CSPacket.useAlienSocket(true));
        //c.getPlayer().fakeRelog();
        //c.getPlayer().dropMessage(1, "Added 1 socket successfully to " + toMount);
	}

}