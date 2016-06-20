package net.server.channel.handler;

import client.MapleClient;
import client.character.MapleCharacter;
import net.MaplePacketHandler;
import net.RecvPacketOpcode;
import net.netty.MaplePacketReader;
import net.packet.CField;
import server.MapleInventoryManipulator;
import server.quest.MapleQuest;
import tools.FileoutputUtil;

public class ReissueMedalHandler extends MaplePacketHandler {

	public ReissueMedalHandler(RecvPacketOpcode recv) {
		super(recv);
	}

	@Override
	public void handlePacket(MaplePacketReader mpr, MapleClient c, MapleCharacter chr) {
		MapleQuest q = MapleQuest.getInstance(mpr.readShort());
        int itemid = q.getMedalItem();
        if ((itemid != mpr.readInt()) || (itemid <= 0) || (q == null) || (chr.getQuestStatus(q.getId()) != 2)) {
            c.sendPacket(CField.UIPacket.reissueMedal(itemid, 4));
            return;
        }
        if (chr.haveItem(itemid, 1, true, true)) {
            c.sendPacket(CField.UIPacket.reissueMedal(itemid, 3));
            return;
        }
        if (!MapleInventoryManipulator.checkSpace(c, itemid, 1, "")) {
            c.sendPacket(CField.UIPacket.reissueMedal(itemid, 2));
            return;
        }
        if (chr.getMeso() < 100) {
            c.sendPacket(CField.UIPacket.reissueMedal(itemid, 1));
            return;
        }
        chr.gainMeso(-100, true, true);
        MapleInventoryManipulator.addById(c, itemid, (byte) 1, new StringBuilder().append("Redeemed item through medal quest ").append(q.getId()).append(" on ").append(FileoutputUtil.CurrentReadable_Date()).toString());
        c.sendPacket(CField.UIPacket.reissueMedal(itemid, 0));
	}

}
