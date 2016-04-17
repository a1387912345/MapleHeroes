package net.server.channel.handlers;

import client.MapleCharacter;
import client.MapleClient;
import net.AbstractMaplePacketHandler;
import net.RecvPacketOpcode;
import tools.data.LittleEndianAccessor;
import tools.packet.CWvsContext;

public class SpecialStatHandler extends AbstractMaplePacketHandler {

	public SpecialStatHandler(RecvPacketOpcode recv) {
		super(recv);
	}

	@Override
	public void handlePacket(LittleEndianAccessor lea, MapleClient c, MapleCharacter chr) {
		String stat = lea.readMapleAsciiString();
        int array = lea.readInt();
        int mode = lea.readInt();
        switch (stat) {
            case "honorLeveling":
                c.getSession().write(CWvsContext.updateSpecialStat(stat, array, mode, c.getPlayer().getHonourNextExp()));
                break;
            case "hyper":
                c.getSession().write(CWvsContext.updateSpecialStat(stat, array, mode, 0));
                break;
        }
	}

}
