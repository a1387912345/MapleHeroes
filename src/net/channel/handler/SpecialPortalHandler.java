package net.channel.handler;

import client.MapleClient;
import server.MaplePortal;
import tools.data.LittleEndianAccessor;
import tools.packet.CWvsContext;

public class SpecialPortalHandler {

	public static void handlePacket(final LittleEndianAccessor slea, final MapleClient c) {
		if ((c.getPlayer() == null) || (c.getPlayer().getMap() == null)) {
            return;
        }
		slea.readByte();
		final String portalName = slea.readMapleAsciiString();
        final MaplePortal portal = c.getPlayer().getMap().getPortal(portalName);

        // if (chr.getGMLevel() > ServerConstants.PlayerGMRank.GM.getLevel()) {
        //  chr.dropMessage(6, new StringBuilder().append(portal.getScriptName()).append(" accessed").toString());
        //  }
        if ((portal != null) && (!c.getPlayer().hasBlockedInventory())) {
            portal.enterPortal(c);
        } else {
            c.getSession().write(CWvsContext.enableActions());
        }
	}
}
