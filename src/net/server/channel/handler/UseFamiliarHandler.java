package net.server.channel.handler;

import client.MapleClient;
import client.MonsterFamiliar;
import client.character.MapleCharacter;
import client.inventory.Item;
import client.inventory.MapleInventoryType;
import net.MaplePacketHandler;
import net.RecvPacketOpcode;
import net.netty.MaplePacketReader;
import net.packet.CField;
import net.packet.CWvsContext;
import server.MapleInventoryManipulator;
import server.MapleItemInformationProvider;
import server.StructFamiliar;
import server.life.MapleLifeFactory;

public class UseFamiliarHandler extends MaplePacketHandler {

	public UseFamiliarHandler(RecvPacketOpcode recv) {
		super(recv);
	}

	@Override
	public void handlePacket(MaplePacketReader lea, MapleClient c, MapleCharacter chr) {
		if ((chr == null) || (!chr.isAlive()) || (chr.getMap() == null) || (chr.hasBlockedInventory())) {
            c.sendPacket(CWvsContext.enableActions());
            return;
        }
        c.getCharacter().updateTick(lea.readInt());
        short slot = lea.readShort();
        int itemId = lea.readInt();
        Item toUse = chr.getInventory(MapleInventoryType.USE).getItem(slot);

        c.sendPacket(CWvsContext.enableActions());
        if ((toUse == null) || (toUse.getQuantity() < 1) || (toUse.getItemId() != itemId) || (itemId / 10000 != 287)) {
            return;
        }
        StructFamiliar f = MapleItemInformationProvider.getInstance().getFamiliarByItem(itemId);
        if (MapleLifeFactory.getMonsterStats(f.mob).getLevel() <= c.getCharacter().getLevel()) {
            MonsterFamiliar mf = (MonsterFamiliar) c.getCharacter().getFamiliars().get(Integer.valueOf(f.familiar));
            if (mf != null) {
                if (mf.getVitality() >= 3) {
                    mf.setExpiry(Math.min(System.currentTimeMillis() + 7776000000L, mf.getExpiry() + 2592000000L));
                } else {
                    mf.setVitality(mf.getVitality() + 1);
                    mf.setExpiry(mf.getExpiry() + 2592000000L);
                }
            } else {
                mf = new MonsterFamiliar(c.getCharacter().getID(), f.familiar, System.currentTimeMillis() + 2592000000L);
                c.getCharacter().getFamiliars().put(Integer.valueOf(f.familiar), mf);
            }
            MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (short) 1, false, false);
            c.sendPacket(CField.registerFamiliar(mf));
            return;
        }
	}

}
