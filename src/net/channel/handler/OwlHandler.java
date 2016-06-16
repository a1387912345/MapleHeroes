package net.channel.handler;

import client.MapleCharacter;
import client.MapleClient;
import net.MaplePacketHandler;
import net.RecvPacketOpcode;
import net.netty.MaplePacketReader;
import net.packet.CWvsContext;

public class OwlHandler extends MaplePacketHandler {

	public OwlHandler(RecvPacketOpcode recv) {
		super(recv);
	}

	@Override
	public void handlePacket(MaplePacketReader lea, MapleClient c, MapleCharacter chr) {
		if (c.getCharacter().haveItem(5230000, 1, true, false) || c.getCharacter().haveItem(2310000, 1, true, false)) {
            if (c.getCharacter().getMapId() >= 910000000 && c.getCharacter().getMapId() <= 910000022) {
                c.sendPacket(CWvsContext.getOwlOpen());
            } else {
                c.getCharacter().dropMessage(5, "This can only be used inside the Free Market.");
                c.sendPacket(CWvsContext.enableActions());
            }
        }
	}

}
