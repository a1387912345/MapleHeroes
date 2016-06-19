package net.server.channel.handler;

import client.MapleClient;
import client.MapleQuestStatus;
import client.anticheat.ReportType;
import client.character.MapleCharacter;
import constants.GameConstants;
import net.MaplePacketHandler;
import net.RecvPacketOpcode;
import net.netty.MaplePacketReader;
import net.packet.CWvsContext;
import server.quest.MapleQuest;

public class ReportHandler extends MaplePacketHandler {

	public ReportHandler(RecvPacketOpcode recv) {
		super(recv);
	}

	@Override
	public void handlePacket(MaplePacketReader lea, MapleClient c, MapleCharacter chr) {
		//0 = success 1 = unable to locate 2 = once a day 3 = you've been reported 4+ = unknown reason
        MapleCharacter other;
        ReportType type;
        type = ReportType.getById(lea.readByte());
        other = c.getCharacter().getMap().getCharacterByName(lea.readMapleAsciiString());
        //then,byte(?) and string(reason)
        if (other == null || type == null || other.isIntern()) {
            c.sendPacket(CWvsContext.report(4));
            return;
        }
        final MapleQuestStatus stat = c.getCharacter().getQuestNAdd(MapleQuest.getInstance(GameConstants.REPORT_QUEST));
        if (stat.getCustomData() == null) {
            stat.setCustomData("0");
        }
        final long currentTime = System.currentTimeMillis();
        final long theTime = Long.parseLong(stat.getCustomData());
        if (theTime + 7200000 > currentTime && !c.getCharacter().isIntern()) {
            c.sendPacket(CWvsContext.enableActions());
            c.getCharacter().dropMessage(5, "You may only report every 2 hours.");
        } else {
            stat.setCustomData(String.valueOf(currentTime));
            other.addReport(type);
            c.sendPacket(CWvsContext.report(2));
        }
	}

}
