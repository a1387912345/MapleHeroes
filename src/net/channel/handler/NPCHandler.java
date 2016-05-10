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
package net.channel.handler;

import client.inventory.Equip;
import client.inventory.Item;
import client.inventory.MapleInventoryType;
import client.MapleClient;
import client.MapleCharacter;
import constants.GameConstants;
import client.MapleQuestStatus;
import client.RockPaperScissors;
import client.inventory.ItemFlag;
import constants.QuickMove;
import net.SendPacketOpcode;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import server.shops.MapleShop;
import server.MapleInventoryManipulator;
import server.MapleStorage;
import server.life.MapleNPC;
import server.quest.MapleQuest;
import scripting.NPCScriptManager;
import scripting.NPCConversationManager;
import server.MapleItemInformationProvider;
import server.maps.MapScriptMethods;
import tools.FileoutputUtil;
import tools.packet.CField;
import tools.Pair;
import tools.data.LittleEndianAccessor;
import tools.data.MaplePacketLittleEndianWriter;
import tools.packet.CField.EffectPacket;
import tools.packet.CWvsContext;

public class NPCHandler {

    public static final void repairAll(final MapleClient c) {
        Equip eq;
        double rPercentage;
        int price = 0;
        Map<String, Integer> eqStats;
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        final Map<Equip, Integer> eqs = new HashMap<>();
        final MapleInventoryType[] types = {MapleInventoryType.EQUIP, MapleInventoryType.EQUIPPED};
        for (MapleInventoryType type : types) {
            for (Item item : c.getPlayer().getInventory(type).newList()) {
                if (item instanceof Equip) { //redundant
                    eq = (Equip) item;
                    if (eq.getDurability() >= 0) {
                        eqStats = ii.getEquipStats(eq.getItemId());
                        if (eqStats.containsKey("durability") && eqStats.get("durability") > 0 && eq.getDurability() < eqStats.get("durability")) {
                            rPercentage = (100.0 - Math.ceil((eq.getDurability() * 1000.0) / (eqStats.get("durability") * 10.0)));
                            eqs.put(eq, eqStats.get("durability"));
                            price += (int) Math.ceil(rPercentage * ii.getPrice(eq.getItemId()) / (ii.getReqLevel(eq.getItemId()) < 70 ? 100.0 : 1.0));
                        }
                    }
                }
            }
        }
        if (eqs.size() <= 0 || c.getPlayer().getMeso() < price) {
            return;
        }
        c.getPlayer().gainMeso(-price, true);
        Equip ez;
        for (Entry<Equip, Integer> eqqz : eqs.entrySet()) {
            ez = eqqz.getKey();
            ez.setDurability(eqqz.getValue());
            c.getPlayer().forceReAddItem(ez.copy(), ez.getPosition() < 0 ? MapleInventoryType.EQUIPPED : MapleInventoryType.EQUIP);
        }
    }

    public static final void repair(final LittleEndianAccessor slea, final MapleClient c) {
        if (slea.available() < 4) { //leafre for now
            return;
        }
        final int position = slea.readInt(); //who knows why this is a int
        final MapleInventoryType type = position < 0 ? MapleInventoryType.EQUIPPED : MapleInventoryType.EQUIP;
        final Item item = c.getPlayer().getInventory(type).getItem((byte) position);
        if (item == null) {
            return;
        }
        final Equip eq = (Equip) item;
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        final Map<String, Integer> eqStats = ii.getEquipStats(item.getItemId());
        if (eq.getDurability() < 0 || !eqStats.containsKey("durability") || eqStats.get("durability") <= 0 || eq.getDurability() >= eqStats.get("durability")) {
            return;
        }
        final double rPercentage = (100.0 - Math.ceil((eq.getDurability() * 1000.0) / (eqStats.get("durability") * 10.0)));
        //drpq level 105 weapons - ~420k per %; 2k per durability point
        //explorer level 30 weapons - ~10 mesos per %
        final int price = (int) Math.ceil(rPercentage * ii.getPrice(eq.getItemId()) / (ii.getReqLevel(eq.getItemId()) < 70 ? 100.0 : 1.0)); // / 100 for level 30?
        //TODO: need more data on calculating off client
        if (c.getPlayer().getMeso() < price) {
            return;
        }
        c.getPlayer().gainMeso(-price, false);
        eq.setDurability(eqStats.get("durability"));
        c.getPlayer().forceReAddItem(eq.copy(), type);
    }

    public static final void UpdateQuest(final LittleEndianAccessor slea, final MapleClient c) {
        final MapleQuest quest = MapleQuest.getInstance(slea.readShort());
        if (quest != null) {
            c.getPlayer().updateQuest(c.getPlayer().getQuest(quest), true);
        }
    }

    public static final void UseItemQuest(final LittleEndianAccessor slea, final MapleClient c) {
        final short slot = slea.readShort();
        final int itemId = slea.readInt();
        final Item item = c.getPlayer().getInventory(MapleInventoryType.ETC).getItem(slot);
        final int qid = slea.readInt();
        final MapleQuest quest = MapleQuest.getInstance(qid);
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        Pair<Integer, List<Integer>> questItemInfo = null;
        boolean found = false;
        for (Item i : c.getPlayer().getInventory(MapleInventoryType.ETC)) {
            if (i.getItemId() / 10000 == 422) {
                questItemInfo = ii.questItemInfo(i.getItemId());
                if (questItemInfo != null && questItemInfo.getLeft() == qid && questItemInfo.getRight() != null && questItemInfo.getRight().contains(itemId)) {
                    found = true;
                    break; //i believe it's any order
                }
            }
        }
        if (quest != null && found && item != null && item.getQuantity() > 0 && item.getItemId() == itemId) {
            final int newData = slea.readInt();
            final MapleQuestStatus stats = c.getPlayer().getQuestNoAdd(quest);
            if (stats != null && stats.getStatus() == 1) {
                stats.setCustomData(String.valueOf(newData));
                c.getPlayer().updateQuest(stats, true);
                MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.ETC, slot, (short) 1, false);
            }
        }
    }

    public static final void RPSGame(LittleEndianAccessor slea, MapleClient c) {
        if ((slea.available() == 0L) || (c.getPlayer() == null) || (c.getPlayer().getMap() == null) || (!c.getPlayer().getMap().containsNPC(9000019))) {
            if ((c.getPlayer() != null) && (c.getPlayer().getRPS() != null)) {
                c.getPlayer().getRPS().dispose(c);
            }
            return;
        }
        byte mode = slea.readByte();
        switch (mode) {
            case 0:
            case 5:
                if (c.getPlayer().getRPS() != null) {
                    c.getPlayer().getRPS().reward(c);
                }
                if (c.getPlayer().getMeso() >= 1000L) {
                    c.getPlayer().setRPS(new RockPaperScissors(c, mode));
                } else {
                    c.getSession().write(CField.getRPSMode((byte) 8, -1, -1, -1));
                }
                break;
            case 1:
                if ((c.getPlayer().getRPS() == null) || (!c.getPlayer().getRPS().answer(c, slea.readByte()))) {
                    c.getSession().write(CField.getRPSMode((byte) 13, -1, -1, -1));
                }
                break;
            case 2:
                if ((c.getPlayer().getRPS() == null) || (!c.getPlayer().getRPS().timeOut(c))) {
                    c.getSession().write(CField.getRPSMode((byte) 13, -1, -1, -1));
                }
                break;
            case 3:
                if ((c.getPlayer().getRPS() == null) || (!c.getPlayer().getRPS().nextRound(c))) {
                    c.getSession().write(CField.getRPSMode((byte) 13, -1, -1, -1));
                }
                break;
            case 4:
                if (c.getPlayer().getRPS() != null) {
                    c.getPlayer().getRPS().dispose(c);
                } else {
                    c.getSession().write(CField.getRPSMode((byte) 13, -1, -1, -1));
                }
                break;
        }
    }

    public static void OpenQuickMove(final LittleEndianAccessor slea, final MapleClient c) {
        final int npcid = slea.readInt();
        if (c.getPlayer().hasBlockedInventory() || c.getPlayer().isInBlockedMap() || c.getPlayer().getLevel() < 10) {
            return;
        }
        for (QuickMove qm : QuickMove.values()) {
            if (qm.getMap() == c.getPlayer().getMapId()) {
                List<QuickMove.QuickMoveNPC> qmn = new LinkedList();
                int npcs = qm.getNPCFlag();
                for (QuickMove.QuickMoveNPC npc : QuickMove.QuickMoveNPC.values()) {
                    if ((npcs & npc.getValue()) != 0 && npc.getId() == npcid) {
                        NPCScriptManager.getInstance().start(c, npcid, null);
                        break;
                    }
                }
            }
        }
    }

    public static Invocable getInvocable(String path, MapleClient c, boolean npc) {
        ScriptEngineManager sem = new ScriptEngineManager();
        FileReader fr = null;
        try {
            path = "scripts/" + path;
            ScriptEngine engine = null;

            if (c != null) {
                engine = c.getScriptEngine(path);
            }
            if (engine == null) {
                File scriptFile = new File(path);
                if (!scriptFile.exists()) {
                    return null;
                }
                engine = sem.getEngineByName("javascript");
                if (c != null) {
                    c.setScriptEngine(path, engine);
                }
                fr = new FileReader(scriptFile);
                engine.eval(fr);
            } else if (c != null && npc) {
                c.removeClickedNPC();
                NPCScriptManager.getInstance().dispose(c);
                c.getSession().write(CWvsContext.enableActions());
                //c.getPlayer().dropMessage(-1, "You already are talking to this NPC. Use @ea if this is not intended.");
            }
            return (Invocable) engine;
        } catch (FileNotFoundException | ScriptException e) {
            System.err.println("Error executing script. Path: " + path + "\nException " + e);
            FileoutputUtil.log(FileoutputUtil.ScriptEx_Log, "Error executing script. Path: " + path + "\nException " + e);
            return null;
        } finally {
            try {
                if (fr != null) {
                    fr.close();
                }
            } catch (IOException ignore) {
            }
        }
    }
}
