package net.channel.handler;

import client.MapleCharacter;
import client.MapleClient;
import net.MaplePacketHandler;
import net.RecvPacketOpcode;
import net.netty.MaplePacketReader;
import net.packet.CWvsContext;

public class SpecialStatHandler extends MaplePacketHandler {

	public SpecialStatHandler(RecvPacketOpcode recv) {
		super(recv);
	}

	@Override
	public void handlePacket(MaplePacketReader lea, MapleClient c, MapleCharacter chr) {
		String stat = lea.readMapleAsciiString();
        int array = lea.readInt();
        int mode = lea.readInt();
        switch (stat) {
            case "honorLeveling":
                c.sendPacket(CWvsContext.updateSpecialStat(stat, array, mode, c.getCharacter().getHonourNextExp()));
                break;
            case "hyper":
                c.sendPacket(CWvsContext.updateSpecialStat(stat, array, mode, 0));
                break;
        }
	}

}
