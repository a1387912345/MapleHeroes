package net.server.channel.handler;

import client.MapleClient;
import client.character.MapleCharacter;
import net.MaplePacketHandler;
import net.RecvPacketOpcode;
import net.netty.MaplePacketReader;
import net.packet.CWvsContext;
import server.MaplePortal;

public class SpecialPortalHandler extends MaplePacketHandler {

	public SpecialPortalHandler(RecvPacketOpcode recv) {
		super(recv);
	}

	@Override
	public void handlePacket(final MaplePacketReader mpr, final MapleClient c, final MapleCharacter chr) {
		if ((c.getCharacter() == null) || (c.getCharacter().getMap() == null)) {
            return;
        }
		mpr.readByte();
		final String portalName = mpr.readMapleAsciiString();
        final MaplePortal portal = c.getCharacter().getMap().getPortal(portalName);

        // if (chr.getGMLevel() > ServerConstants.PlayerGMRank.GM.getLevel()) {
        //  chr.dropMessage(6, new StringBuilder().append(portal.getScriptName()).append(" accessed").toString());
        //  }
        if ((portal != null) && (!c.getCharacter().hasBlockedInventory())) {
            portal.enterPortal(c);
        } else {
            c.sendPacket(CWvsContext.enableActions());
        }
	}

}
