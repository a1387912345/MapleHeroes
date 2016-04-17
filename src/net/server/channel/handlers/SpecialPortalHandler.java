package net.server.channel.handlers;

import client.MapleCharacter;
import client.MapleClient;
import net.AbstractMaplePacketHandler;
import net.RecvPacketOpcode;
import server.MaplePortal;
import tools.data.LittleEndianAccessor;
import tools.packet.CWvsContext;

public class SpecialPortalHandler extends AbstractMaplePacketHandler {

	public SpecialPortalHandler(RecvPacketOpcode recv) {
		super(recv);
	}

	@Override
	public void handlePacket(final LittleEndianAccessor lea, final MapleClient c, final MapleCharacter chr) {
		if ((c.getPlayer() == null) || (c.getPlayer().getMap() == null)) {
            return;
        }
		lea.readByte();
		final String portalName = lea.readMapleAsciiString();
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
