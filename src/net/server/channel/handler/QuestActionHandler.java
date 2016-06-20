package net.server.channel.handler;

import client.MapleClient;
import client.character.MapleCharacter;
import constants.GameConstants;
import net.MaplePacketHandler;
import net.RecvPacketOpcode;
import net.netty.MaplePacketReader;
import net.packet.CWvsContext;
import net.packet.CField.EffectPacket;
import scripting.npc.NPCScriptManager;
import server.quest.MapleQuest;

public class QuestActionHandler extends MaplePacketHandler {

	public QuestActionHandler(RecvPacketOpcode recv) {
		super(recv);
	}

	@Override
	public void handlePacket(final MaplePacketReader mpr, final MapleClient c, final MapleCharacter chr) {
		if (chr == null) {
            return;
        }
		final byte action = mpr.readByte();
        int quest = mpr.readInt();
        if (quest == 20734) {
            c.sendPacket(CWvsContext.ultimateExplorer());
            return;
        }

        final MapleQuest q = MapleQuest.getInstance(quest);
        switch (action) {
            case 0: { // Restore lost item
                //chr.updateTick(inPacket.readInt());
                mpr.readInt();
                final int itemid = mpr.readInt();
                q.RestoreLostItem(chr, itemid);
                break;
            }
            case 1: { // Start Quest
                final int npc = mpr.readInt();
                if (npc == 0 && quest > 0) {
                    q.forceStart(chr, npc, null);
                } else if (!q.hasStartScript()) {
                    q.start(chr, npc);
                }
                break;
            }
            case 2: { // Complete Quest
                final int npc = mpr.readInt();
                //chr.updateTick(inPacket.readInt());
                mpr.readInt();
                if (q.hasEndScript()) {
                    return;
                }
                if (mpr.available() >= 4) {
                    q.complete(chr, npc, mpr.readInt());
                } else {
                    q.complete(chr, npc);
                }
                // c.sendPacket(CField.completeQuest(c.getPlayer(), quest));
                //c.sendPacket(CField.updateQuestInfo(c.getPlayer(), quest, npc, (byte)14));
                // 6 = start quest
                // 7 = unknown error
                // 8 = equip is full
                // 9 = not enough mesos
                // 11 = due to the equipment currently being worn wtf o.o
                // 12 = you may not posess more than one of this item
                break;
            }
            case 3: { // Forfeit Quest
                if (GameConstants.canForfeit(q.getId())) {
                    q.forfeit(chr);
                } else {
                    chr.dropMessage(1, "You may not forfeit this quest.");
                }
                break;
            }
            case 4: { // Scripted Start Quest
                final int npc = mpr.readInt();
                if (chr.hasBlockedInventory()) {
                    return;
                }
                //c.getPlayer().updateTick(inPacket.readInt());
                NPCScriptManager.getInstance().startQuest(c, npc, quest);
                break;
            }
            case 5: { // Scripted End Quest
                final int npc = mpr.readInt();
                if (chr.hasBlockedInventory()) {
                    return;
                }
                //c.getPlayer().updateTick(inPacket.readInt());
                NPCScriptManager.getInstance().endQuest(c, npc, quest, false);
                break;
            }
        }
	}

}
