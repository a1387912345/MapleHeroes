package net.server.channel.handlers;

import client.MapleCharacter;
import client.MapleClient;
import net.AbstractMaplePacketHandler;
import net.RecvPacketOpcode;
import tools.data.LittleEndianAccessor;
import tools.packet.CWvsContext.GuildPacket;

public class GuildInvitationHandler extends AbstractMaplePacketHandler {

	public GuildInvitationHandler(RecvPacketOpcode recv) {
		super(recv);
	}

	@Override
	public void handlePacket(LittleEndianAccessor lea, MapleClient c, MapleCharacter chr) {
		final int response = lea.readByte();
		final String from = lea.readMapleAsciiString();
		final MapleCharacter cfrom = c.getChannelServer().getPlayerStorage().getCharacterByName(from);
		
		
        if (cfrom != null && GuildOperationHandler.getInvited().remove(c.getPlayer().getName().toLowerCase()) != null) {
        	if (response == 87) { // Deny Guild Invitation
        		cfrom.showMessage(11, c.getPlayer().getName() + " has denied the guild invitation.");
        		//cfrom.getClient().getSession().write(GuildPacket.denyGuildInvitation(c.getPlayer().getName()));
        	}
        }
	}

}
