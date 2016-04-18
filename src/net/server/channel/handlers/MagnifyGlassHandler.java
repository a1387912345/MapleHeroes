package net.server.channel.handlers;

import java.util.LinkedList;
import java.util.List;

import client.MapleCharacter;
import client.MapleClient;
import client.MapleTrait.MapleTraitType;
import client.inventory.Equip;
import client.inventory.Item;
import client.inventory.MapleInventoryType;
import constants.GameConstants;
import net.AbstractMaplePacketHandler;
import net.RecvPacketOpcode;
import server.MapleInventoryManipulator;
import server.MapleItemInformationProvider;
import server.Randomizer;
import server.StructItemOption;
import tools.data.LittleEndianAccessor;
import tools.packet.CField;
import tools.packet.CWvsContext;
import tools.packet.CWvsContext.InventoryPacket;

public class MagnifyGlassHandler extends AbstractMaplePacketHandler {

	public MagnifyGlassHandler(RecvPacketOpcode recv) {
		super(recv);
	}

	@Override
	public void handlePacket(LittleEndianAccessor lea, MapleClient c, MapleCharacter chr) {
		c.getPlayer().updateTick(lea.readInt());
        c.getPlayer().setScrolledPosition((short) 0);
        final byte src = (byte) lea.readShort();
        final boolean insight = src == 127 && c.getPlayer().getTrait(MapleTraitType.sense).getLevel() >= 30;
        final Item magnify = c.getPlayer().getInventory(MapleInventoryType.USE).getItem(src);
        byte eqSlot = (byte) lea.readShort();
        boolean equipped = eqSlot < 0;
        final Item toReveal = c.getPlayer().getInventory(equipped ? MapleInventoryType.EQUIPPED : MapleInventoryType.EQUIP).getItem(eqSlot);
        if (toReveal == null || c.getPlayer().hasBlockedInventory()) {
            c.getSession().write(InventoryPacket.getInventoryFull());
            System.out.println("Return 1");
            return;
        }
        final Equip eqq = (Equip) toReveal;
        final long price = GameConstants.getMagnifyPrice(eqq);
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        final int reqLevel = ii.getReqLevel(eqq.getItemId()) / 10;
        if (eqq.getState() == 1 && (src == 0x7F && price != -1 && c.getPlayer().getMeso() >= price
                || insight || magnify.getItemId() == 2460003 || (magnify.getItemId() == 2460002 && reqLevel <= 12)
                || (magnify.getItemId() == 2460001 && reqLevel <= 7) || (magnify.getItemId() == 2460000 && reqLevel <= 3))) {
            final List<List<StructItemOption>> pots = new LinkedList<>(ii.getAllPotentialInfo().values());
            int lockedLine = 0;
            int locked = 0;
            if (Math.abs(eqq.getPotential1()) / 100000 > 0) {
                lockedLine = 1;
                locked = Math.abs(eqq.getPotential1());
            } else if (Math.abs(eqq.getPotential2()) / 100000 > 0) {
                lockedLine = 2;
                locked = Math.abs(eqq.getPotential2());
            } else if (Math.abs(eqq.getPotential3()) / 100000 > 0) {
                lockedLine = 3;
                locked = Math.abs(eqq.getPotential3());
            }
            int new_state = Math.abs(eqq.getPotential1());
            if (lockedLine == 1) {
                new_state = locked / 10000 < 1 ? 17 : 16 + locked / 10000;
            }
            if (new_state > 20 || new_state < 17) { // incase overflow
                new_state = 17;
            }
            int lines = 2; // default
            if (eqq.getPotential2() != 0) {
                lines++;
            }
            while (eqq.getState() != new_state) {
                //31001 = haste, 31002 = door, 31003 = se, 31004 = hb, 41005 = combat orders, 41006 = advanced blessing, 41007 = speed infusion
                for (int i = 0; i < lines; i++) { // minimum 2 lines, max 5
                    boolean rewarded = false;
                    while (!rewarded) {
                        StructItemOption pot = pots.get(Randomizer.nextInt(pots.size())).get(reqLevel);
                        if (pot != null && pot.reqLevel / 1 <= reqLevel && GameConstants.optionTypeFits(pot.optionType, eqq.getItemId()) && GameConstants.potentialIDFits(pot.opID, new_state, i)) { //optionType
                            //have to research optionType before making this truely official-like
                            if (isAllowedPotentialStat(eqq, pot.opID)) {
                                if (i == 0) {
                                    eqq.setPotential1(pot.opID);
                                } else if (i == 1) {
                                    eqq.setPotential2(pot.opID);
                                } else if (i == 2) {
                                    eqq.setPotential3(pot.opID);
                                } else if (i == 3) {
                                    eqq.setPotential4(pot.opID);
                                }
                                rewarded = true;
                            }
                        }
                    }
                }
            }
            switch (lockedLine) {
                case 1:
                    eqq.setPotential1(Math.abs(locked - lockedLine * 100000));
                    break;
                case 2:
                    eqq.setPotential2(Math.abs(locked - lockedLine * 100000));
                    break;
                case 3:
                    eqq.setPotential3(Math.abs(locked - lockedLine * 100000));
                    break;
            }
            c.getPlayer().getTrait(MapleTraitType.insight).addExp((src == 0x7F && price != -1 ? 10 : insight ? 10 : ((magnify.getItemId() + 2) - 2460000)) * 2, c.getPlayer());
            c.getPlayer().getMap().broadcastMessage(CField.showMagnifyingEffect(c.getPlayer().getId(), eqq.getPosition()));
            if (!insight && src != 0x7F) {
                c.getSession().write(InventoryPacket.scrolledItem(magnify, equipped ? MapleInventoryType.EQUIPPED : MapleInventoryType.EQUIP, toReveal, false, true, equipped));
                MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, magnify.getPosition(), (short) 1, false);
                System.out.println("Return 2");
            } else {
                if (price != -1 && !insight) {
                    c.getPlayer().gainMeso(-price, false);
                }
                c.getPlayer().forceReAddItem(toReveal, eqSlot >= 0 ? MapleInventoryType.EQUIP : MapleInventoryType.EQUIPPED);
                System.out.println("Return 3");
            }
            c.getSession().write(CWvsContext.enableActions());
        } else {
            c.getSession().write(InventoryPacket.getInventoryFull());
            System.out.println("Return 4");
        }
	}
	
	public static boolean isAllowedPotentialStat(Equip eqq, int opID) { //For now
        //if (GameConstants.isWeapon(eqq.getItemId())) {
        //    return !(opID > 60000) || (opID >= 1 && opID <= 4) || (opID >= 9 && opID <= 12) || (opID >= 10001 && opID <= 10006) || (opID >= 10011 && opID <= 10012) || (opID >= 10041 && opID <= 10046) || (opID >= 10051 && opID <= 10052) || (opID >= 10055 && opID <= 10081) || (opID >= 10201 && opID <= 10291) || (opID >= 210001 && opID <= 20006) || (opID >= 20011 && opID <= 20012) || (opID >= 20041 && opID <= 20046) || (opID >= 20051 && opID <= 20052) || (opID >= 20055 && opID <= 20081) || (opID >= 20201 && opID <= 20291) || (opID >= 30001 && opID <= 30006) || (opID >= 30011 && opID <= 30012) || (opID >= 30041 && opID <= 30046) || (opID >= 30051 && opID <= 30052) || (opID >= 30055 && opID <= 30081) || (opID >= 30201 && opID <= 30291) || (opID >= 40001 && opID <= 40006) || (opID >= 40011 && opID <= 40012) || (opID >= 40041 && opID <= 40046) || (opID >= 40051 && opID <= 40052) || (opID >= 40055 && opID <= 40081) || (opID >= 40201 && opID <= 40291);
        //}
        return opID < 60000;
    }

}
