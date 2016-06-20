package net.server.channel.handler;

import client.MapleClient;
import client.character.MapleCharacter;
import net.MaplePacketHandler;
import net.RecvPacketOpcode;
import net.netty.MaplePacketReader;
import net.packet.CWvsContext;

public class SpecialStatHandler extends MaplePacketHandler {

	public SpecialStatHandler(RecvPacketOpcode recv) {
		super(recv);
	}

	@Override
	public void handlePacket(MaplePacketReader mpr, MapleClient c, MapleCharacter chr) {
		String stat = mpr.readMapleAsciiString();
        int array = mpr.readInt();
        int mode = mpr.readInt();
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
