package net.channel.handler.inventory;

import client.MapleCharacter;
import client.MapleClient;
import client.inventory.Equip;
import client.inventory.Item;
import client.inventory.MapleInventoryType;
import net.MaplePacketHandler;
import net.RecvPacketOpcode;
import net.netty.MaplePacketReader;
import net.packet.CSPacket;
import net.packet.CWvsContext.InventoryPacket;
import server.MapleInventoryManipulator;

public class UseAlienSocketHandler extends MaplePacketHandler {

	public UseAlienSocketHandler(RecvPacketOpcode recv) {
		super(recv);
	}

	@Override
	public void handlePacket(MaplePacketReader inPacket, MapleClient c, MapleCharacter chr) {
		c.getCharacter().updateTick(inPacket.readInt());
        c.getCharacter().setScrolledPosition((short) 0);
        final Item alienSocket = c.getCharacter().getInventory(MapleInventoryType.USE).getItem((byte) inPacket.readShort());
        final int alienSocketId = inPacket.readInt();
        final Item toMount = c.getCharacter().getInventory(MapleInventoryType.EQUIP).getItem((byte) inPacket.readShort());
        if (alienSocket == null || alienSocketId != alienSocket.getItemId() || toMount == null || c.getCharacter().hasBlockedInventory()) {
            c.sendPacket(InventoryPacket.getInventoryFull());
            return;
        }
        // Can only use once-> 2nd and 3rd must use NPC.
        final Equip eqq = (Equip) toMount;
        if (eqq.getSocketState() != 0) { // Used before
            c.getCharacter().dropMessage(1, "This item already has a socket.");
        } else {
            c.sendPacket(CSPacket.useAlienSocket(false));
            eqq.setSocket1(0); // First socket, GMS removed the other 2
            MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, alienSocket.getPosition(), (short) 1, false);
            c.getCharacter().forceReAddItem(toMount, MapleInventoryType.EQUIP);
        }
        c.sendPacket(CSPacket.useAlienSocket(true));
        //c.getPlayer().fakeRelog();
        //c.getPlayer().dropMessage(1, "Added 1 socket successfully to " + toMount);
	}

}