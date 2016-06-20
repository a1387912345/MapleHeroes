/*
 This file is part of the OdinMS Maple Story Server
 Copyright (C) 2008 ~ 2010 Patrick Huy <patrick.huy@frz.cc> 
 Matthias Butz <matze@odinms.de>
 Jan Christian Meyer <vimes@odinms.de>

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU Affero General Public License version 3
 as published by the Free Software Foundation. You may not use, modify
 or distribute this program under any other version of the
 GNU Affero General Public License.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU Affero General Public License for more details.

 You should have received a copy of the GNU Affero General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.server.channel.handler.deprecated;

import client.InnerAbillity;
import client.InnerSkillValueHolder;
import client.MapleClient;
import client.Skill;
import client.SkillFactory;
import client.character.MapleCharacter;
import client.character.PlayerStats;
import client.inventory.Equip;
import client.inventory.Equip.ScrollResult;
import client.inventory.Item;
import client.inventory.ItemFlag;
import client.inventory.MapleInventoryType;
import constants.GameConstants;
import database.DatabaseConnection;
import net.netty.MaplePacketReader;
import net.packet.CField;
import net.packet.CSPacket;
import net.packet.CWvsContext;
import net.packet.PlayerShopPacket;
import net.packet.CField.EffectPacket;
import net.packet.CWvsContext.InfoPacket;
import net.packet.CWvsContext.InventoryPacket;
import net.world.MaplePartyCharacter;

import java.awt.Rectangle;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import server.MapleInventoryManipulator;
import server.MapleItemInformationProvider;
import server.MapleStatEffect;
import server.Randomizer;
import server.StructRewardItem;
import server.maps.FieldLimitType;
import server.maps.MapleMap;
import server.maps.MapleMapItem;
import server.maps.MapleMapObject;
import server.maps.MapleMapObjectType;
import server.stores.HiredMerchant;
import server.stores.IMaplePlayerShop;
import tools.FileoutputUtil;
import tools.Pair;

public class InventoryHandler {

    public static final void SwitchBag(final MaplePacketReader inPacket, final MapleClient c) {
        if (c.getCharacter().hasBlockedInventory()) { //hack
            return;
        }
        c.getCharacter().setScrolledPosition((short) 0);
        c.getCharacter().updateTick(inPacket.readInt());
        final short src = (short) inPacket.readInt();                                       //01 00
        final short dst = (short) inPacket.readInt();                                       //00 00
        if (src < 100 || dst < 100) {
            return;
        }
        MapleInventoryManipulator.move(c, MapleInventoryType.ETC, src, dst);
    }

    public static final void MoveBag(final MaplePacketReader inPacket, final MapleClient c) {
        if (c.getCharacter().hasBlockedInventory()) { //hack
            return;
        }
        c.getCharacter().setScrolledPosition((short) 0);
        c.getCharacter().updateTick(inPacket.readInt());
        final boolean srcFirst = inPacket.readInt() > 0;
        short dst = (short) inPacket.readInt();                                       //01 00
        if (inPacket.readByte() != 4) { //must be etc
            c.sendPacket(CWvsContext.enableActions());
            return;
        }
        short src = inPacket.readShort();                                             //00 00
        MapleInventoryManipulator.move(c, MapleInventoryType.ETC, srcFirst ? dst : src, srcFirst ? src : dst);
    }

    public static boolean UseRewardItem(final MaplePacketReader inPacket, final MapleClient c, final MapleCharacter chr) {
        //System.out.println("[Reward Item] " + inPacket.toString());
        final byte slot = (byte) inPacket.readShort();
        final int itemId = inPacket.readInt();
        final boolean unseal = inPacket.readByte() > 0;
        return UseRewardItem(slot, itemId, unseal, c, chr);
    }

    public static boolean UseRewardItem(byte slot, int itemId, final boolean unseal, final MapleClient c, final MapleCharacter chr) {
        final Item toUse = c.getCharacter().getInventory(GameConstants.getInventoryType(itemId)).getItem(slot);
        c.sendPacket(CWvsContext.enableActions());
        if (toUse != null && toUse.getQuantity() >= 1 && toUse.getItemId() == itemId && !chr.hasBlockedInventory()) {
            if (chr.getInventory(MapleInventoryType.EQUIP).getNextFreeSlot() > -1 && chr.getInventory(MapleInventoryType.USE).getNextFreeSlot() > -1 && chr.getInventory(MapleInventoryType.SETUP).getNextFreeSlot() > -1 && chr.getInventory(MapleInventoryType.ETC).getNextFreeSlot() > -1) {
                final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
                final Pair<Integer, List<StructRewardItem>> rewards = ii.getRewardItem(itemId);

                switch (itemId) {
                    case 2290245:
                case 2290285:
                case 2290448:
                case 2290449:
                case 2290450:
                case 2290451:
                case 2290452:
                case 2290454:
                case 2290455:
                case 2290456:
                case 2290457:
                case 2290458:
                case 2290459:
                case 2290460:
                case 2290461:
                case 2290462:
                case 2290463:
                case 2290464:
                case 2290465:
                case 2290466:
                case 2290467:
                case 2290468:
                case 2290469:
                case 2290571:
                case 2290581:
                case 2290602:
                case 2290653:
                case 2290714:
                case 2290715:
                case 2290721:
                case 2290722:
                case 2290723:
                case 2290724:
                case 2290803:
                case 2290868:
                case 2290869:
                case 2290870:
                case 2290871:
                case 2290872:
                case 2290873:
                case 2290874:
                case 2290875:
                case 2290876:
                case 2290877:
                case 2290878:
                case 2290879:
                case 2290880:
                case 2290881:
                case 2290882:
                case 2290883:
                case 2290884:
                case 2290885:
                case 2290886:
                case 2290887:
                case 2290888:
                case 2290889:
                case 2290890:
                case 2290891:
                case 2290892:
                case 2290893:
                case 2290914:
                case 2290915:
                case 2291020:
            //    case 2291021:
              //  case 2430144: //smb
                    final int itemid = Randomizer.nextInt(999) + 2290000;
                //    World.Broadcast.broadcastMessage(CField.getGameMessage("SMB.", (short) 8));
                    if (MapleItemInformationProvider.getInstance().itemExists(itemid) && !MapleItemInformationProvider.getInstance().getName(itemid).contains("Special") && !MapleItemInformationProvider.getInstance().getName(itemid).contains("Event")) {
                        MapleInventoryManipulator.addById(c, itemid, (short) 1, "Reward item: " + toUse.getItemId() + " on " + FileoutputUtil.CurrentReadable_Date());
                        MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (byte) 1, false);
                    }
                    break;
                }
                if (rewards != null && rewards.getLeft() > 0) {
                    while (true) {
                        for (StructRewardItem reward : rewards.getRight()) {
                            if (reward.prob > 0 && Randomizer.nextInt(rewards.getLeft()) < reward.prob) { // Total prob
                                if (GameConstants.getInventoryType(reward.itemid) == MapleInventoryType.EQUIP) {
                                    final Item item = ii.getEquipById(reward.itemid);
                                    if (reward.period > 0) {
                                        item.setExpiration(System.currentTimeMillis() + (reward.period * 60 * 60 * 10));
                                    }
                                    item.setGMLog("Reward item: " + itemId + " on " + FileoutputUtil.CurrentReadable_Date());
                                    MapleInventoryManipulator.addbyItem(c, item);
                                } else {
                                    MapleInventoryManipulator.addById(c, reward.itemid, reward.quantity, "Reward item: " + itemId + " on " + FileoutputUtil.CurrentReadable_Date());
                                }
                                MapleInventoryManipulator.removeById(c, GameConstants.getInventoryType(itemId), itemId, 1, false, false);

                                c.sendPacket(EffectPacket.showRewardItemAnimation(reward.itemid, reward.effect));
                                chr.getMap().broadcastMessage(chr, EffectPacket.showRewardItemAnimation(reward.itemid, reward.effect, chr.getID()), false);
                                return true;
                            }
                        }
                    }
                } else {
                    if (itemId == 2028162) { //custom test
                        List<Integer> items;
                        Integer[] itemArray = {1302000, 1302001,
                            1302002, 1302003, 1302004, 1302005, 1302006,
                            1302007};
                        items = Arrays.asList(itemArray);
                        if (unseal) {
                            MapleInventoryManipulator.removeById(c, GameConstants.getInventoryType(itemId), itemId, 1, false, false);
                            Item item = ii.getEquipById(items.get(Randomizer.nextInt(items.size())));
                            MapleInventoryManipulator.addbyItem(c, item);
                            c.sendPacket(CField.unsealBox(item.getItemId()));
                            c.sendPacket(EffectPacket.showRewardItemAnimation(2028162, "")); //sealed box
                            c.sendPacket(InfoPacket.getShowItemGain(item.getItemId(),(short) 1, true));
                        } else {
                            c.sendPacket(CField.sendSealedBox(slot, 2028162, items)); //sealed box
                        }
                    }
                    switch (itemId) {
                         case 2291021:
                             chr.dropMessage(6, "Unknown error.");
                             break;
                        default:
                         //   chr.dropMessage(6, "Unknown error." + item);
                            break;
                    }
                }
            } else {
                chr.dropMessage(6, "Insufficient inventory slot.");
            }
        }
        return false;
    }

    public static void UseExpItem(final MaplePacketReader inPacket, final MapleClient c, final MapleCharacter chr) {
        if (chr == null || !chr.isAlive() || chr.getMap() == null || chr.hasBlockedInventory() || chr.inPVP()) {
            c.sendPacket(CWvsContext.enableActions());
            return;
        }
        c.getCharacter().updateTick(inPacket.readInt());
        final byte slot = (byte) inPacket.readShort();
        final int itemId = inPacket.readInt();
        final Item toUse = chr.getInventory(MapleInventoryType.USE).getItem(slot);
        if (toUse == null || toUse.getQuantity() < 1 || toUse.getItemId() != itemId) {
            c.sendPacket(CWvsContext.enableActions());
            return;
        }
        if (!MapleItemInformationProvider.getInstance().getEquipStats(itemId).containsKey("exp")) {
            c.sendPacket(CWvsContext.enableActions());
            return;
        }
        MapleItemInformationProvider.getInstance().getEquipStats(itemId).get("exp");
        MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (short) 1, false);
    }

    public static void UseGoldenHammer(final MaplePacketReader inPacket, final MapleClient c) {
        //[21 D5 10 04] [16 00 00 00] [7B B0 25 00] [01 00 00 00] [03 00 00 00]
        c.getCharacter().updateTick(inPacket.readInt());
        byte slot = (byte) inPacket.readInt();
        int itemId = inPacket.readInt();
        inPacket.skip(4);
        byte equipslot = (byte) inPacket.readInt();
        Item toUse = c.getCharacter().getInventory(MapleInventoryType.USE).getItem(slot);
        Equip equip = (Equip) c.getCharacter().getInventory(MapleInventoryType.EQUIP).getItem(equipslot);
        if (toUse == null || toUse.getItemId() != itemId || toUse.getQuantity() < 1) {
            c.sendPacket(CWvsContext.enableActions());
            return;
        }
        int success;
        if (itemId == 2470004 && Randomizer.nextInt(100) < 20) {
            equip.setUpgradeSlots((byte) (equip.getUpgradeSlots() + 1));
            success = 0;
        } else if ((itemId == 2470001 || itemId == 2470002) && Randomizer.nextInt(100) < 50) {
            equip.setUpgradeSlots((byte) (equip.getUpgradeSlots() + 1));
            success = 0;
        } else if (itemId == 2470000 || itemId == 2470003) {
            equip.setUpgradeSlots((byte) (equip.getUpgradeSlots() + 1));
            success = 0;
        } else {
            success = 1;
        }
        c.sendPacket(CSPacket.GoldenHammer((byte) 2, success));
        equip.setViciousHammer((byte) 1);
        MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (short) 1, true);
    }

    public static boolean isAllowedPotentialStat(Equip eqq, int opID) { //For now
        //if (GameConstants.isWeapon(eqq.getItemId())) {
        //    return !(opID > 60000) || (opID >= 1 && opID <= 4) || (opID >= 9 && opID <= 12) || (opID >= 10001 && opID <= 10006) || (opID >= 10011 && opID <= 10012) || (opID >= 10041 && opID <= 10046) || (opID >= 10051 && opID <= 10052) || (opID >= 10055 && opID <= 10081) || (opID >= 10201 && opID <= 10291) || (opID >= 210001 && opID <= 20006) || (opID >= 20011 && opID <= 20012) || (opID >= 20041 && opID <= 20046) || (opID >= 20051 && opID <= 20052) || (opID >= 20055 && opID <= 20081) || (opID >= 20201 && opID <= 20291) || (opID >= 30001 && opID <= 30006) || (opID >= 30011 && opID <= 30012) || (opID >= 30041 && opID <= 30046) || (opID >= 30051 && opID <= 30052) || (opID >= 30055 && opID <= 30081) || (opID >= 30201 && opID <= 30291) || (opID >= 40001 && opID <= 40006) || (opID >= 40011 && opID <= 40012) || (opID >= 40041 && opID <= 40046) || (opID >= 40051 && opID <= 40052) || (opID >= 40055 && opID <= 40081) || (opID >= 40201 && opID <= 40291);
        //}
        return opID < 60000;
    }

    public static void addToScrollLog(int accountID, int charID, int scrollID, int itemID, byte oldSlots, byte newSlots, byte viciousHammer, String result, boolean ws, boolean ls, int vega) {
        try {
            try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement("INSERT INTO scroll_log VALUES(DEFAULT, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
                ps.setInt(1, accountID);
                ps.setInt(2, charID);
                ps.setInt(3, scrollID);
                ps.setInt(4, itemID);
                ps.setByte(5, oldSlots);
                ps.setByte(6, newSlots);
                ps.setByte(7, viciousHammer);
                ps.setString(8, result);
                ps.setByte(9, (byte) (ws ? 1 : 0));
                ps.setByte(10, (byte) (ls ? 1 : 0));
                ps.setInt(11, vega);
                ps.execute();
            }
        } catch (SQLException e) {
            FileoutputUtil.outputFileError(FileoutputUtil.PacketEx_Log, e);
        }
    }

    public static boolean UseUpgradeScroll(final short slot, final short dst, final short ws, final MapleClient c, final MapleCharacter chr, final boolean legendarySpirit) {
        return UseUpgradeScroll(slot, dst, ws, c, chr, 0, legendarySpirit);
    }

    public static boolean UseUpgradeScroll(final short slot, final short dst, final short ws, final MapleClient c, final MapleCharacter chr, final int vegas, final boolean legendarySpirit) {
        boolean whiteScroll = false; // white scroll being used?
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        chr.setScrolledPosition((short) 0);
        if ((ws & 2) == 2) {
            whiteScroll = true;
        }
        Equip toScroll = null;
        if (dst < 0) {
            toScroll = (Equip) chr.getInventory(MapleInventoryType.EQUIPPED).getItem(dst);
        } else /*if (legendarySpirit)*/ {//may want to create a boolean for strengthen ui? lol
            toScroll = (Equip) chr.getInventory(MapleInventoryType.EQUIP).getItem(dst);
        }
//        if (toScroll == null || c.getPlayer().hasBlockedInventory()) {//removed just in case :P
//            c.sendPacket(CWvsContext.enableActions());
//            return false;
//        }
        
        //07 00 F5 FF 01 00 00
        final byte oldLevel = toScroll.getLevel(); //07
        final byte oldEnhance = toScroll.getEnhance(); // 00
        final byte oldState = toScroll.getState(); // F5
        final short oldFlag = toScroll.getFlag(); // FF 01
        final short oldSlots = toScroll.getUpgradeSlots(); // v146+
        

        Item scroll = chr.getInventory(MapleInventoryType.USE).getItem(slot);
        if (scroll == null) {
            scroll = chr.getInventory(MapleInventoryType.CASH).getItem(slot);
            if (scroll == null) {
                c.sendPacket(InventoryPacket.getInventoryFull());
                c.sendPacket(CWvsContext.enableActions());
                return false;
            }
        }
        if (scroll.getItemId() == 5064200) { //TODO: test this
            Item item = chr.getInventory(MapleInventoryType.EQUIPPED).getItem(toScroll.getPosition());
            Equip equip = (Equip) item;
            int itemid = toScroll.getItemId();
            int potential1 = equip.getPotential1();
            int potential2 = equip.getPotential2();
            int potential3 = equip.getPotential3();
            int bonuspotential1 = equip.getBonusPotential1();
            int bonuspotential2 = equip.getBonusPotential2();
            short position = toScroll.getPosition();
            chr.getInventory(MapleInventoryType.EQUIPPED).removeItem(toScroll.getPosition());
            Equip neweq = (Equip) ii.getEquipById(itemid);
            neweq.setPotential1(potential1);
            neweq.setPotential2(potential2);
            neweq.setPotential3(potential3);
            neweq.setBonusPotential1(bonuspotential1);
            neweq.setBonusPotential2(bonuspotential2);
            neweq.setPosition(position);
            MapleInventoryManipulator.addbyItem(c, neweq);
        }
        if (GameConstants.isAzwanScroll(scroll.getItemId())) {
            if (toScroll.getUpgradeSlots() < MapleItemInformationProvider.getInstance().getEquipStats(scroll.getItemId()).get("tuc")) {
                c.sendPacket(InventoryPacket.getInventoryFull());
                c.sendPacket(CWvsContext.enableActions());
                return false;
            }
        }
        if (!GameConstants.isSpecialScroll(scroll.getItemId()) && !GameConstants.isCleanSlate(scroll.getItemId()) && !GameConstants.isEquipScroll(scroll.getItemId()) && !GameConstants.isPotentialScroll(scroll.getItemId())) {
            if (toScroll.getUpgradeSlots() < 1) {
                c.sendPacket(InventoryPacket.getInventoryFull());
                c.sendPacket(CWvsContext.enableActions());
                return false;
            }
        } else if (GameConstants.isEquipScroll(scroll.getItemId())) {
            if (toScroll.getUpgradeSlots() >= 1 || toScroll.getEnhance() >= 100 || vegas > 0 || ii.isCash(toScroll.getItemId())) {
                c.sendPacket(InventoryPacket.getInventoryFull());
                c.sendPacket(CWvsContext.enableActions());
                return false;
            }
        } else if (GameConstants.isPotentialScroll(scroll.getItemId())) {
            final boolean isEpic = scroll.getItemId() / 100 == 20497 && scroll.getItemId() < 2049750;
            final boolean isUnique = scroll.getItemId() / 100 == 20497 && scroll.getItemId() >= 2049750;
            if ((!isEpic && !isUnique && toScroll.getState() >= 1) || (isEpic && toScroll.getState() >= 18) || (isUnique && toScroll.getState() >= 19) || (toScroll.getLevel() == 0 && toScroll.getUpgradeSlots() == 0 && toScroll.getItemId() / 10000 != 135/* && !isEpic && !isUnique*/) || vegas > 0 || ii.isCash(toScroll.getItemId())) {
                c.sendPacket(InventoryPacket.getInventoryFull());
                c.sendPacket(CWvsContext.enableActions());
                return false;
            }
        } else if (GameConstants.isSpecialScroll(scroll.getItemId())) {
            if (ii.isCash(toScroll.getItemId()) || toScroll.getEnhance() >= 12) {
                c.sendPacket(InventoryPacket.getInventoryFull());
                c.sendPacket(CWvsContext.enableActions());
                return false;
            }
        }
        if (!GameConstants.canScroll(toScroll.getItemId()) && !GameConstants.isChaosScroll(toScroll.getItemId())) {
            c.sendPacket(InventoryPacket.getInventoryFull());
            c.sendPacket(CWvsContext.enableActions());
            return false;
        }
        if ((GameConstants.isCleanSlate(scroll.getItemId()) || GameConstants.isTablet(scroll.getItemId()) || GameConstants.isGeneralScroll(scroll.getItemId()) || GameConstants.isChaosScroll(scroll.getItemId())) && (vegas > 0 || ii.isCash(toScroll.getItemId()))) {
            c.sendPacket(InventoryPacket.getInventoryFull());
            c.sendPacket(CWvsContext.enableActions());
            return false;
        }
        if (GameConstants.isTablet(scroll.getItemId()) && toScroll.getDurability() < 0) { //not a durability item
            c.sendPacket(InventoryPacket.getInventoryFull());
            c.sendPacket(CWvsContext.enableActions());
            return false;
        } else if ((!GameConstants.isTablet(scroll.getItemId()) && !GameConstants.isPotentialScroll(scroll.getItemId()) && !GameConstants.isEquipScroll(scroll.getItemId()) && !GameConstants.isCleanSlate(scroll.getItemId()) && !GameConstants.isSpecialScroll(scroll.getItemId()) && !GameConstants.isChaosScroll(scroll.getItemId())) && toScroll.getDurability() >= 0) {
            c.sendPacket(InventoryPacket.getInventoryFull());
            c.sendPacket(CWvsContext.enableActions());
            return false;
        }
        Item wscroll = null;

        // Anti cheat and validation
        List<Integer> scrollReqs = ii.getScrollReqs(scroll.getItemId());
        if (scrollReqs != null && scrollReqs.size() > 0 && !scrollReqs.contains(toScroll.getItemId())) {
            c.sendPacket(InventoryPacket.getInventoryFull());
            c.sendPacket(CWvsContext.enableActions());
            return false;
        }

        if (whiteScroll) {
            wscroll = chr.getInventory(MapleInventoryType.USE).findById(2340000);
            if (wscroll == null) {
                whiteScroll = false;
            }
        }
        if (GameConstants.isTablet(scroll.getItemId()) || GameConstants.isGeneralScroll(scroll.getItemId())) {
            switch (scroll.getItemId() % 1000 / 100) {
                case 0: //1h
                    if (GameConstants.isTwoHanded(toScroll.getItemId()) || !GameConstants.isWeapon(toScroll.getItemId())) {
                        c.sendPacket(CWvsContext.enableActions());
                        return false;
                    }
                    break;
                case 1: //2h
                    if (!GameConstants.isTwoHanded(toScroll.getItemId()) || !GameConstants.isWeapon(toScroll.getItemId())) {
                        c.sendPacket(CWvsContext.enableActions());
                        return false;
                    }
                    break;
                case 2: //armor
                    if (GameConstants.isAccessory(toScroll.getItemId()) || GameConstants.isWeapon(toScroll.getItemId())) {
                        c.sendPacket(CWvsContext.enableActions());
                        return false;
                    }
                    break;
                case 3: //accessory
                    if (!GameConstants.isAccessory(toScroll.getItemId()) || GameConstants.isWeapon(toScroll.getItemId())) {
                        c.sendPacket(CWvsContext.enableActions());
                        return false;
                    }
                    break;
            }
        } else if (!GameConstants.isAccessoryScroll(scroll.getItemId()) && !GameConstants.isChaosScroll(scroll.getItemId()) && !GameConstants.isCleanSlate(scroll.getItemId()) && !GameConstants.isEquipScroll(scroll.getItemId()) && !GameConstants.isPotentialScroll(scroll.getItemId()) && !GameConstants.isSpecialScroll(scroll.getItemId())) {
            if (!ii.canScroll(scroll.getItemId(), toScroll.getItemId())) {
                c.sendPacket(CWvsContext.enableActions());
                return false;
            }
        }
        if (GameConstants.isAccessoryScroll(scroll.getItemId()) && !GameConstants.isAccessory(toScroll.getItemId())) {
            c.sendPacket(CWvsContext.enableActions());
            return false;
        }
        if (scroll.getQuantity() <= 0) {
            c.sendPacket(CWvsContext.enableActions());
            return false;
        }

        if (legendarySpirit && vegas == 0) {
            if (chr.getSkillLevel(SkillFactory.getSkill(PlayerStats.getSkillByJob(1003, chr.getJob()))) <= 0) {
                c.sendPacket(CWvsContext.enableActions());
                return false;
            }
        }

        // Scroll Success/ Failure/ Curse
        Equip scrolled = (Equip) ii.scrollEquipWithId(toScroll, scroll, whiteScroll, chr, vegas);
        ScrollResult scrollSuccess;
        if (scrolled == null) {
            if (ItemFlag.SHIELD_WARD.check(oldFlag)) {
                scrolled = toScroll;
                scrollSuccess = Equip.ScrollResult.FAIL;
                scrolled.setFlag((short) (oldFlag - ItemFlag.SHIELD_WARD.getValue()));
            } else {
                scrollSuccess = Equip.ScrollResult.CURSE;
            }
        } else if ((scroll.getItemId() / 100 == 20497 && scrolled.getState() == 1) || scrolled.getLevel() > oldLevel || scrolled.getEnhance() > oldEnhance || scrolled.getState() > oldState || scrolled.getFlag() > oldFlag) {
            scrollSuccess = Equip.ScrollResult.SUCCESS;
        } else if ((GameConstants.isCleanSlate(scroll.getItemId()) && scrolled.getUpgradeSlots() > oldSlots)) {
            scrollSuccess = Equip.ScrollResult.SUCCESS;
        } else if (c.getCharacter().isGM()) {
            scrollSuccess = Equip.ScrollResult.SUCCESS;
        } else {
            scrollSuccess = Equip.ScrollResult.FAIL;
        }
        // Update
        chr.getInventory(GameConstants.getInventoryType(scroll.getItemId())).removeItem(scroll.getPosition(), (short) 1, false);
        if (whiteScroll) {
            MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, wscroll.getPosition(), (short) 1, false, false);
        } else if (scrollSuccess == Equip.ScrollResult.FAIL && scrolled.getUpgradeSlots() < oldSlots && c.getCharacter().getInventory(MapleInventoryType.CASH).findById(5640000) != null) {
            chr.setScrolledPosition(scrolled.getPosition());
            if (vegas == 0) {
                c.sendPacket(CWvsContext.pamSongUI());
            }
        }

        if (scrollSuccess == Equip.ScrollResult.CURSE) {
            c.sendPacket(InventoryPacket.scrolledItem(scroll, MapleInventoryType.EQUIP, toScroll, true, false, false));
            if (dst < 0) {
                chr.getInventory(MapleInventoryType.EQUIPPED).removeItem(toScroll.getPosition());
            } else {
                chr.getInventory(MapleInventoryType.EQUIP).removeItem(toScroll.getPosition());
            }
        } else if (vegas == 0) {
            c.sendPacket(InventoryPacket.scrolledItem(scroll, MapleInventoryType.EQUIP, scrolled, false, false, false));
        }

        chr.getMap().broadcastMessage(chr, CField.getScrollEffect(c.getCharacter().getID(), scrollSuccess, legendarySpirit, toScroll.getItemId(), scroll.getItemId()), vegas == 0);
        //toscroll
        //scroll
        c.sendPacket(CField.enchantResult(scrollSuccess == ScrollResult.SUCCESS ? 1 : scrollSuccess == ScrollResult.CURSE ? 2 : 0));
        //addToScrollLog(chr.getAccountID(), chr.getId(), scroll.getItemId(), itemID, oldSlots, (byte)(scrolled == null ? -1 : scrolled.getUpgradeSlots()), oldVH, scrollSuccess.name(), whiteScroll, legendarySpirit, vegas);
        // equipped item was scrolled and changed
        if (dst < 0 && (scrollSuccess == Equip.ScrollResult.SUCCESS || scrollSuccess == Equip.ScrollResult.CURSE) && vegas == 0) {
            chr.equipChanged();
        }
        return true;
    }

    public static boolean UseSkillBook(final byte slot, final int itemId, final MapleClient c, final MapleCharacter chr) {
        final Item toUse = chr.getInventory(GameConstants.getInventoryType(itemId)).getItem(slot);

        if (toUse == null || toUse.getQuantity() < 1 || toUse.getItemId() != itemId || chr.hasBlockedInventory()) {
            return false;
        }
        final Map<String, Integer> skilldata = MapleItemInformationProvider.getInstance().getEquipStats(toUse.getItemId());
        if (skilldata == null) { // Hacking or used an unknown item
            return false;
        }
        boolean canuse = false, success = false;
        int skill = 0, maxlevel = 0;

        final Integer SuccessRate = skilldata.get("success");
        final Integer ReqSkillLevel = skilldata.get("reqSkillLevel");
        final Integer MasterLevel = skilldata.get("masterLevel");

        byte i = 0;
        Integer CurrentLoopedSkillId;
        while (true) {
            CurrentLoopedSkillId = skilldata.get("skillid" + i);
            i++;
            if (CurrentLoopedSkillId == null || MasterLevel == null) {
                break; // End of data
            }
            final Skill CurrSkillData = SkillFactory.getSkill(CurrentLoopedSkillId);
            if (CurrSkillData != null && CurrSkillData.canBeLearnedBy(chr.getJob()) && (ReqSkillLevel == null || chr.getSkillLevel(CurrSkillData) >= ReqSkillLevel) && chr.getMasterLevel(CurrSkillData) < MasterLevel) {
                canuse = true;
                if (SuccessRate == null || Randomizer.nextInt(100) <= SuccessRate) {
                    success = true;
                    chr.changeSingleSkillLevel(CurrSkillData, chr.getSkillLevel(CurrSkillData), (byte) (int) MasterLevel);
                } else {
                    success = false;
                }
                MapleInventoryManipulator.removeFromSlot(c, GameConstants.getInventoryType(itemId), slot, (short) 1, false);
                break;
            }
        }

        c.getCharacter().getMap().broadcastMessage(CWvsContext.useSkillBook(chr, skill, maxlevel, canuse, success));
        c.sendPacket(CWvsContext.enableActions());
        return canuse;
    }
    
    private static int[] dmgskinitem = {2431965, 2431966, 2432084, 2431967, 2432131, 2432153, 2432638, 2432659, 2432154, 2432637, 2432658, 2432207, 2432354, 2432355, 2432972, 2432465, 2432479, 2432526, 2432639, 2432660, 2432532, 2432592, 2432640, 2432661, 2432710, 2432836, 2432973};
    private static int[] dmgskinnum = {0, 1, 1, 2, 3, 4, 4, 4, 5, 5, 5, 6, 7, 8, 8, 9, 10, 11, 11, 11, 12, 13, 14, 14, 15, 16, 17};


    public static void ResetCoreAura(int slot, MapleClient c, MapleCharacter chr) {
        Item starDust = chr.getInventory(MapleInventoryType.USE).getItem((byte) slot);
        if ((starDust == null) || (c.getCharacter().hasBlockedInventory())) {
            c.sendPacket(CWvsContext.InventoryPacket.getInventoryFull());
        }
    }

    public static final void useInnerCirculator(MaplePacketReader inPacket, MapleClient c) {
        System.out.println("Circ used");
        int itemid = inPacket.readInt();
        System.out.println("ItemID Int");
        short slot = (short) inPacket.readInt();
        System.out.println("slot Int");
        Item item = c.getCharacter().getInventory(MapleInventoryType.USE).getItem(slot);
        if (item.getItemId() == itemid) {
            List<InnerSkillValueHolder> newValues = new LinkedList<>();
            int i = 0;
            for (InnerSkillValueHolder isvh : c.getCharacter().getInnerSkills()) {
                if (!isvh.isLocked()) {
                    if (i == 0 && c.getCharacter().getInnerSkills().size() > 1 && itemid == 2702000) { //Ultimate Circulator
                        newValues.add(InnerAbillity.getInstance().renewSkill(isvh.getRank(), itemid, true, false));
                    } else {
                        newValues.add(InnerAbillity.getInstance().renewSkill(isvh.getRank(), itemid, false, false));
                    }
                    //c.getPlayer().changeSkillLevel(SkillFactory.getSkill(isvh.getSkillId()), (byte) 0, (byte) 0);
                } else {
                    newValues.add(isvh);
                }
                i++;
            }
            c.getCharacter().getInnerSkills().clear();
            byte ability = 1;
            for (InnerSkillValueHolder isvh : newValues) {
                c.getCharacter().getInnerSkills().add(isvh);
                c.sendPacket(CField.updateInnerPotential(ability, isvh.getSkillId(), isvh.getSkillLevel(), isvh.getRank()));
                ability++;
                //c.getPlayer().changeSkillLevel(SkillFactory.getSkill(isvh.getSkillId()), isvh.getSkillLevel(), isvh.getSkillLevel());
            }
            MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (short) 1, false);

            //c.sendPacket(CField.gameMsg("Inner Potential has been reconfigured.")); //not sure if it's working
            c.getCharacter().dropMessage(5, "Inner Potential has been reconfigured.");
        }
        c.sendPacket(CWvsContext.enableActions());
    }


    
    public static final boolean useItem(final MapleClient c, final int id) {
        if (GameConstants.isUse(id)) { // TO prevent caching of everything, waste of mem
            final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
            final MapleStatEffect eff = ii.getItemEffect(id);
            if (eff == null) {
                return false;
            }
            //must hack here for ctf
            if (id / 10000 == 291) {
                boolean area = false;
                for (Rectangle rect : c.getCharacter().getMap().getAreas()) {
                    if (rect.contains(c.getCharacter().getTruePosition())) {
                        area = true;
                        break;
                    }
                }
                if (!c.getCharacter().inPVP() || (c.getCharacter().getTeam() == (id - 2910000) && area)) {
                    return false; //dont apply the consume
                }
            }
            final int consumeval = eff.getConsume();

            if (consumeval > 0) {
                consumeItem(c, eff);
                consumeItem(c, ii.getItemEffectEX(id));
                c.sendPacket(InfoPacket.getShowItemGain(id, (byte) 1));
                return true;
            }
        }
        return false;
    }

    public static final void consumeItem(final MapleClient c, final MapleStatEffect eff) {
        if (eff == null) {
            return;
        }
        if (eff.getConsume() == 2) {
            if (c.getCharacter().getParty() != null && c.getCharacter().isAlive()) {
                for (final MaplePartyCharacter pc : c.getCharacter().getParty().getMembers()) {
                    final MapleCharacter chr = c.getCharacter().getMap().getCharacterById(pc.getId());
                    if (chr != null && chr.isAlive()) {
                        eff.applyTo(chr);
                    }
                }
            } else {
                eff.applyTo(c.getCharacter());
            }
        } else if (c.getCharacter().isAlive()) {
            eff.applyTo(c.getCharacter());
        }
    }

    public static final void removeItem_Pet(final MapleCharacter chr, final MapleMapItem mapitem, int pet) {
        mapitem.setPickedUp(true);
        chr.getMap().broadcastMessage(CField.removeItemFromMap(mapitem.getObjectId(), 5, chr.getID(), pet));
        chr.getMap().removeMapObject(mapitem);
        if (mapitem.isRandDrop()) {
            chr.getMap().spawnRandDrop();
        }
    }

    private static void removeItem(final MapleCharacter chr, final MapleMapItem mapitem, final MapleMapObject ob) {
        mapitem.setPickedUp(true);
        chr.getMap().broadcastMessage(CField.removeItemFromMap(mapitem.getObjectId(), 2, chr.getID()), mapitem.getPosition());
        chr.getMap().removeMapObject(ob);
        if (mapitem.isRandDrop()) {
            chr.getMap().spawnRandDrop();
        }
    }

    


    public static final void OwlMinerva(final MaplePacketReader inPacket, final MapleClient c) {
        final byte slot = (byte) inPacket.readShort();
        final int itemid = inPacket.readInt();
        final Item toUse = c.getCharacter().getInventory(MapleInventoryType.USE).getItem(slot);
        if (toUse != null && toUse.getQuantity() > 0 && toUse.getItemId() == itemid && itemid == 2310000 && !c.getCharacter().hasBlockedInventory()) {
            final int itemSearch = inPacket.readInt();
            final List<HiredMerchant> hms = c.getChannelServer().searchMerchant(itemSearch);
            if (hms.size() > 0) {
                c.sendPacket(CWvsContext.getOwlSearched(itemSearch, hms));
                MapleInventoryManipulator.removeById(c, MapleInventoryType.USE, itemid, 1, true, false);
            } else {
                c.getCharacter().dropMessage(1, "Unable to find the item.");
            }
        }
        c.sendPacket(CWvsContext.enableActions());
    }

    public static final void Owl(final MaplePacketReader inPacket, final MapleClient c) {
        if (c.getCharacter().haveItem(5230000, 1, true, false) || c.getCharacter().haveItem(2310000, 1, true, false)) {
            if (c.getCharacter().getMapId() >= 910000000 && c.getCharacter().getMapId() <= 910000022) {
                c.sendPacket(CWvsContext.getOwlOpen());
            } else {
                c.getCharacter().dropMessage(5, "This can only be used inside the Free Market.");
                c.sendPacket(CWvsContext.enableActions());
            }
        }
    }
    public static final int OWL_ID = 2; //don't change. 0 = owner ID, 1 = store ID, 2 = object ID

    public static final void OwlWarp(final MaplePacketReader inPacket, final MapleClient c) {
        if (!c.getCharacter().isAlive()) {
            c.sendPacket(CWvsContext.getOwlMessage(4));
            return;
        } else if (c.getCharacter().getTrade() != null) {
            c.sendPacket(CWvsContext.getOwlMessage(7));
            return;
        }
        if (c.getCharacter().getMapId() >= 910000000 && c.getCharacter().getMapId() <= 910000022 && !c.getCharacter().hasBlockedInventory()) {
            final int id = inPacket.readInt();
            final int map = inPacket.readInt();
            if (map >= 910000001 && map <= 910000022) {
                c.sendPacket(CWvsContext.getOwlMessage(0));
                final MapleMap mapp = c.getChannelServer().getMapFactory().getMap(map);
                c.getCharacter().changeMap(mapp, mapp.getPortal(0));
                HiredMerchant merchant = null;
                List<MapleMapObject> objects;
                switch (OWL_ID) {
                    case 0:
                        objects = mapp.getAllHiredMerchantsThreadsafe();
                        for (MapleMapObject ob : objects) {
                            if (ob instanceof IMaplePlayerShop) {
                                final IMaplePlayerShop ips = (IMaplePlayerShop) ob;
                                if (ips instanceof HiredMerchant) {
                                    final HiredMerchant merch = (HiredMerchant) ips;
                                    if (merch.getOwnerId() == id) {
                                        merchant = merch;
                                        break;
                                    }
                                }
                            }
                        }
                        break;
                    case 1:
                        objects = mapp.getAllHiredMerchantsThreadsafe();
                        for (MapleMapObject ob : objects) {
                            if (ob instanceof IMaplePlayerShop) {
                                final IMaplePlayerShop ips = (IMaplePlayerShop) ob;
                                if (ips instanceof HiredMerchant) {
                                    final HiredMerchant merch = (HiredMerchant) ips;
                                    if (merch.getStoreId() == id) {
                                        merchant = merch;
                                        break;
                                    }
                                }
                            }
                        }
                        break;
                    default:
                        final MapleMapObject ob = mapp.getMapObject(id, MapleMapObjectType.HIRED_MERCHANT);
                        if (ob instanceof IMaplePlayerShop) {
                            final IMaplePlayerShop ips = (IMaplePlayerShop) ob;
                            if (ips instanceof HiredMerchant) {
                                merchant = (HiredMerchant) ips;
                            }
                        }
                        break;
                }
                if (merchant != null) {
                    if (merchant.isOwner(c.getCharacter())) {
                        merchant.setOpen(false);
                        merchant.removeAllVisitors((byte) 16, (byte) 0);
                        c.getCharacter().setPlayerShop(merchant);
                        c.sendPacket(PlayerShopPacket.getHiredMerch(c.getCharacter(), merchant, false));
                    } else {
                        if (!merchant.isOpen() || !merchant.isAvailable()) {
                            c.getCharacter().dropMessage(1, "The owner of the store is currently undergoing store maintenance. Please try again in a bit.");
                        } else {
                            if (merchant.getFreeSlot() == -1) {
                                c.getCharacter().dropMessage(1, "You can't enter the room due to full capacity.");
                            } else if (merchant.isInBlackList(c.getCharacter().getName())) {
                                c.getCharacter().dropMessage(1, "You may not enter this store.");
                            } else {
                                c.getCharacter().setPlayerShop(merchant);
                                merchant.addVisitor(c.getCharacter());
                                c.sendPacket(PlayerShopPacket.getHiredMerch(c.getCharacter(), merchant, false));
                            }
                        }
                    }
                } else {
                    c.getCharacter().dropMessage(1, "The room is already closed.");
                }
            } else {
                c.sendPacket(CWvsContext.getOwlMessage(23));
            }
        } else {
            c.sendPacket(CWvsContext.getOwlMessage(23));
        }
    }

    public static final void PamSong(MaplePacketReader inPacket, MapleClient c) {
        final Item pam = c.getCharacter().getInventory(MapleInventoryType.CASH).findById(5640000);
        if (inPacket.readByte() > 0 && c.getCharacter().getScrolledPosition() != 0 && pam != null && pam.getQuantity() > 0) {
            final MapleInventoryType inv = c.getCharacter().getScrolledPosition() < 0 ? MapleInventoryType.EQUIPPED : MapleInventoryType.EQUIP;
            final Item item = c.getCharacter().getInventory(inv).getItem(c.getCharacter().getScrolledPosition());
            c.getCharacter().setScrolledPosition((short) 0);
            if (item != null) {
                final Equip eq = (Equip) item;
                eq.setUpgradeSlots((byte) (eq.getUpgradeSlots() + 1));
                c.getCharacter().forceReAddItem_Flag(eq, inv);
                MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.CASH, pam.getPosition(), (short) 1, true, false);
                c.getCharacter().getMap().broadcastMessage(CField.pamsSongEffect(c.getCharacter().getID()));
            }
        } else {
            c.getCharacter().setScrolledPosition((short) 0);
        }
    }

    public static final void TeleRock(MaplePacketReader inPacket, MapleClient c) {
        final byte slot = (byte) inPacket.readShort();
        final int itemId = inPacket.readInt();
        final Item toUse = c.getCharacter().getInventory(MapleInventoryType.USE).getItem(slot);

        if (toUse == null || toUse.getQuantity() < 1 || toUse.getItemId() != itemId || itemId / 10000 != 232 || c.getCharacter().hasBlockedInventory()) {
            c.sendPacket(CWvsContext.enableActions());
            return;
        }
        boolean used = UseTeleRock(inPacket, c, itemId);
        if (used) {
            MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (short) 1, false);
        }
        c.sendPacket(CWvsContext.enableActions());
    }

  /* public static final boolean UseTeleRock(LittleEndianAccessor inPacket, MapleClient c, int itemId) {
        boolean used = false;
        if (itemId == 5041001 || itemId == 5040004) {
            inPacket.readByte(); //useless
        }
        if (inPacket.readByte() == 0) { // Rocktype
            final MapleMap target = c.getChannelServer().getMapFactory().getMap(inPacket.readInt());
            if ((itemId == 5041000 && c.getPlayer().isRockMap(target.getId())) || (itemId != 5041000 && c.getPlayer().isRegRockMap(target.getId())) || ((itemId == 5040004 || itemId == 5041001) && (c.getPlayer().isHyperRockMap(target.getId()) || GameConstants.isHyperTeleMap(target.getId())))) {
                if (!FieldLimitType.VipRock.check(c.getPlayer().getMap().getFieldLimit()) && !FieldLimitType.VipRock.check(target.getFieldLimit()) && !c.getPlayer().isInBlockedMap()) { //Makes sure this map doesn't have a forced return map
                    c.getPlayer().changeMap(target, target.getPortal(0));
                    used = true;
                }
            }
        } else {
            c.getPlayer().dropMessage(1, "You cannot go to that place.");
            final MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(inPacket.readMapleAsciiString());
            if (victim != null && !victim.isIntern() && c.getPlayer().getEventInstance() == null && victim.getEventInstance() == null) {
                if (!FieldLimitType.VipRock.check(c.getPlayer().getMap().getFieldLimit()) && !FieldLimitType.VipRock.check(c.getChannelServer().getMapFactory().getMap(victim.getMapId()).getFieldLimit()) && !victim.isInBlockedMap() && !c.getPlayer().isInBlockedMap()) {
                    if (itemId == 5041000 || itemId == 5040004 || itemId == 5041001 || (victim.getMapId() / 100000000) == (c.getPlayer().getMapId() / 100000000)) { // Viprock or same continent
                        c.getPlayer().changeMap(victim.getMap(), victim.getMap().findClosestPortal(victim.getTruePosition()));
                        used = true;
                    }
                }
            }
        }
        return used && itemId != 5041001 && itemId != 5040004;
    }
      */

    
    
 
public static final boolean UseTeleRock(MaplePacketReader inPacket, MapleClient c, int itemId) {
        boolean used = false;
        if (itemId == 5040004) {
            inPacket.readByte();
        } 
        if ((itemId == 5040004) || itemId == 5041001)
        {
            if(inPacket.readByte() == 0)
            {
                final MapleMap target = c.getChannelServer().getMapFactory().getMap(inPacket.readInt());
                if (target != null){ //Premium and Hyper rocks are allowed to go anywhere. Blocked maps are checked below. 
                    if (!FieldLimitType.VipRock.check(c.getCharacter().getMap().getFieldLimit()) && !FieldLimitType.VipRock.check(target.getFieldLimit()) && !c.getCharacter().isInBlockedMap()) { //Makes sure this map doesn't have a forced return map
                        c.getCharacter().changeMap(target, target.getPortal(0));
                        if(itemId == 5041001) used = true;
                    } else {
                        c.getCharacter().dropMessage(1, "You cannot go to that place.");
                    }
                } else {
                    c.getCharacter().dropMessage(1, "The place you want to go to does not exist."); 
                }  
            } else {
                final String name = inPacket.readMapleAsciiString();
                final MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(name);
                if (victim != null && !victim.isIntern() && c.getCharacter().getEventInstance() == null && victim.getEventInstance() == null) {
                    if (!FieldLimitType.VipRock.check(c.getCharacter().getMap().getFieldLimit()) && !FieldLimitType.VipRock.check(c.getChannelServer().getMapFactory().getMap(victim.getMapId()).getFieldLimit()) && !victim.isInBlockedMap() && !c.getCharacter().isInBlockedMap()) {
                        c.getCharacter().changeMap(victim.getMap(), victim.getMap().findClosestPortal(victim.getTruePosition()));
                        if(itemId == 5041001) used = true;
                    } else {
                        c.getCharacter().dropMessage(1, "You cannot go to where that person is.");
                    }
                } else {
                    if(victim == null) {
                        c.getCharacter().dropMessage(1, "(" +name + ") is either offline or in a different channel.");
                    }
                    else {
                        c.getCharacter().dropMessage(1, "(" +name + ") is currently difficult to locate, so the teleport will not take place.");
                    }
                }
            }
        } else {
            if (itemId == 5040004) {
                c.getCharacter().dropMessage(1, "You are not able to use this teleport rock.");
            }
        }
        return used;
    }
    
}
