package net.server.channel.handler;

import client.MapleClient;
import client.character.MapleCharacter;
import client.inventory.Item;
import client.inventory.ItemFlag;
import client.inventory.MapleInventoryType;
import constants.GameConstants;
import net.MaplePacketHandler;
import net.RecvPacketOpcode;
import net.netty.MaplePacketReader;
import net.packet.CField;
import net.packet.CWvsContext;
import server.MapleInventoryManipulator;
import server.MapleItemInformationProvider;
import server.MapleStorage;

public class StorageHandler extends MaplePacketHandler {

	public StorageHandler(RecvPacketOpcode recv) {
		super(recv);
	}

	@Override
	public void handlePacket(final MaplePacketReader mpr, final MapleClient c, MapleCharacter chr) {
		byte mode = mpr.readByte();
		
        if (chr == null) {
            return;
        }
        MapleStorage storage = chr.getStorage();

        switch (mode) {
            case 4: {
                byte type = mpr.readByte();
                byte slot = storage.getSlot(MapleInventoryType.getByType(type), mpr.readByte());
                Item item = storage.takeOut(slot);

                if (item != null) {
                    if (!MapleInventoryManipulator.checkSpace(c, item.getItemId(), item.getQuantity(), item.getOwner())) {
                        storage.store(item);
                        chr.dropMessage(1, "Your inventory is full");
                    } else {
                        MapleInventoryManipulator.addFromDrop(c, item, false);
                        storage.sendTakenOut(c, GameConstants.getInventoryType(item.getItemId()));
                    }
                } else {
                    c.sendPacket(CWvsContext.enableActions());
                }
                break;
            }
            case 5: {
                byte slot = (byte) mpr.readShort();
                int itemId = mpr.readInt();
                MapleInventoryType type = GameConstants.getInventoryType(itemId);
                short quantity = mpr.readShort();
                MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
                if (quantity < 1) {
                    return;
                }
                if (storage.isFull()) {
                    c.sendPacket(CField.NPCPacket.getStorageFull());
                    return;
                }
                if (chr.getInventory(type).getItem((short) slot) == null) {
                    c.sendPacket(CWvsContext.enableActions());
                    return;
                }

                if (chr.getMeso() < 100L) {
                    chr.dropMessage(1, "You don't have enough mesos to store the item");
                } else {
                    Item item = chr.getInventory(type).getItem((short) slot).copy();

                    if (GameConstants.isPet(item.getItemId())) {
                        c.sendPacket(CWvsContext.enableActions());
                        return;
                    }
                    short flag = item.getFlag();
                    if ((ii.isPickupRestricted(item.getItemId())) && (storage.findById(item.getItemId()) != null)) {
                        c.sendPacket(CWvsContext.enableActions());
                        return;
                    }
                    if ((item.getItemId() == itemId) && ((item.getQuantity() >= quantity) || (GameConstants.isThrowingStar(itemId)) || (GameConstants.isBullet(itemId)))) {
                        if (ii.isDropRestricted(item.getItemId())) {
                            if (ItemFlag.KARMA_EQ.check(flag)) {
                                item.setFlag((short) (flag - ItemFlag.KARMA_EQ.getValue()));
                            } else if (ItemFlag.KARMA_USE.check(flag)) {
                                item.setFlag((short) (flag - ItemFlag.KARMA_USE.getValue()));
                            } else if (ItemFlag.KARMA_ACC.check(flag)) {
                                item.setFlag((short) (flag - ItemFlag.KARMA_ACC.getValue()));
                            } else if (ItemFlag.KARMA_ACC_USE.check(flag)) {
                                item.setFlag((short) (flag - ItemFlag.KARMA_ACC_USE.getValue()));
                            } else {
                                c.sendPacket(CWvsContext.enableActions());
                                return;
                            }
                        }
                        if ((GameConstants.isThrowingStar(itemId)) || (GameConstants.isBullet(itemId))) {
                            quantity = item.getQuantity();
                        }
                        chr.gainMeso(-100L, false, false);
                        MapleInventoryManipulator.removeFromSlot(c, type, (short) slot, quantity, false);
                        item.setQuantity(quantity);
                        storage.store(item);
                    } else {
                        return;
                    }
                }
                storage.sendStored(c, GameConstants.getInventoryType(itemId));
                break;
            }
            case 6:
                storage.arrange();
                storage.update(c);
                break;
            case 7: {
                long meso = mpr.readInt();
                long storageMesos = storage.getMeso();
                long playerMesos = chr.getMeso();

                if (((meso > 0L) && (storageMesos >= meso)) || ((meso < 0L) && (playerMesos >= -meso))) {
                    if ((meso < 0L) && (storageMesos - meso < 0L)) {
                        meso = -(9999999999L - storageMesos);
                        if (-meso <= playerMesos);
                    } else if ((meso > 0L) && (playerMesos + meso < 0L)) {
                        meso = 9999999999L - playerMesos;
                        if (meso > storageMesos) {
                            return;
                        }
                    }
                    storage.setMeso(storageMesos - meso);
                    chr.gainMeso(meso, false, false);
                } else {
                    return;
                }
                storage.sendMeso(c);
                break;
            }
            case 8:
                storage.close();
                chr.setConversation(0);
                break;
            default:
                System.out.println("Unhandled Storage mode : " + mode);
        }
	}

}