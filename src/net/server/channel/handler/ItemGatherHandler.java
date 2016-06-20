package net.server.channel.handler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import client.MapleClient;
import client.character.MapleCharacter;
import client.inventory.Item;
import client.inventory.MapleInventory;
import client.inventory.MapleInventoryType;
import net.MaplePacketHandler;
import net.RecvPacketOpcode;
import net.netty.MaplePacketReader;
import net.packet.CWvsContext;
import server.MapleInventoryManipulator;

public class ItemGatherHandler extends MaplePacketHandler {

	public ItemGatherHandler(RecvPacketOpcode recv) {
		super(recv);
	}

	@Override
	public void handlePacket(final MaplePacketReader mpr, final MapleClient c, final MapleCharacter chr) {
		// [41 00] [E5 1D 55 00] [01]
        // [32 00] [01] [01] // Sent after

        c.getCharacter().updateTick(mpr.readInt());
        c.getCharacter().setScrolledPosition((short) 0);
        if (c.getCharacter().hasBlockedInventory()) {
            c.sendPacket(CWvsContext.enableActions());
            return;
        }
        final byte mode = mpr.readByte();
        final MapleInventoryType invType = MapleInventoryType.getByType(mode);
        MapleInventory Inv = c.getCharacter().getInventory(invType);

        final List<Item> itemMap = new LinkedList<>();
        for (Item item : Inv.list()) {
            itemMap.add(item.copy()); // clone all  items T___T.
        }
        for (Item itemStats : itemMap) {
            MapleInventoryManipulator.removeFromSlot(c, invType, itemStats.getPosition(), itemStats.getQuantity(), true, false);
        }

        final List<Item> sortedItems = sortItems(itemMap);
        for (Item item : sortedItems) {
            MapleInventoryManipulator.addFromDrop(c, item, false);
        }
        c.sendPacket(CWvsContext.finishedGather(mode));
        c.sendPacket(CWvsContext.enableActions());
        itemMap.clear();
        sortedItems.clear();
	}
	
	private static List<Item> sortItems(final List<Item> passedMap) {
        final List<Integer> itemIds = new ArrayList<>(); // empty list.
        for (Item item : passedMap) {
            itemIds.add(item.getItemId()); // adds all item ids to the empty list to be sorted.
        }
        Collections.sort(itemIds); // sorts item ids

        final List<Item> sortedList = new LinkedList<>(); // ordered list pl0x <3.

        for (Integer val : itemIds) {
            for (Item item : passedMap) {
                if (val == item.getItemId()) { // Goes through every index and finds the first value that matches
                    sortedList.add(item);
                    passedMap.remove(item);
                    break;
                }
            }
        }
        return sortedList;
    }
}
