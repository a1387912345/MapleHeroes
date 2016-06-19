package net.server.farm.handler;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

import client.MapleClient;
import client.character.MapleCharacter;
import net.MaplePacketHandler;
import net.RecvPacketOpcode;
import net.netty.MaplePacketReader;
import net.packet.FarmPacket;

public class FarmCompleteQuestHandler extends MaplePacketHandler {

	public FarmCompleteQuestHandler(RecvPacketOpcode recv) {
		super(recv);
	}

	@Override
	public void handlePacket(MaplePacketReader lea, MapleClient c, MapleCharacter chr) {
		int questId = lea.readInt();
        if (questId == 1111) {
            c.sendPacket(FarmPacket.updateQuestInfo(1111, 1, ""));
            SimpleDateFormat sdfGMT = new SimpleDateFormat("yyyy-MM-dd-HH-mm");
            sdfGMT.setTimeZone(TimeZone.getTimeZone("Canada/Pacific"));
            String timeStr = sdfGMT.format(Calendar.getInstance().getTime()).replaceAll("-", "");
            c.sendPacket(FarmPacket.updateQuestInfo(1111, 2, timeStr));
            System.out.println(timeStr);
            c.sendPacket(FarmPacket.alertQuest(1111, 0));
            c.sendPacket(FarmPacket.updateQuestInfo(1112, 0, "A1/"));
            c.sendPacket(FarmPacket.updateQuestInfo(1112, 1, "A1/Z/"));
        }
	}

}
