package net.server.channel.handler;

import client.MapleClient;
import client.character.MapleCharacter;
import net.MaplePacketHandler;
import net.RecvPacketOpcode;
import net.netty.MaplePacketReader;
import net.packet.CWvsContext.GuildPacket;

public class GuildInvitationHandler extends MaplePacketHandler {

	public GuildInvitationHandler(RecvPacketOpcode recv) {
		super(recv);
	}

	@Override
	public void handlePacket(MaplePacketReader lea, MapleClient c, MapleCharacter chr) {
		final int response = lea.readByte();
		final String from = lea.readMapleAsciiString();
		final MapleCharacter cfrom = c.getChannelServer().getPlayerStorage().getCharacterByName(from);
		
		
        if (cfrom != null && GuildOperationHandler.getInvited().remove(c.getCharacter().getName().toLowerCase()) != null) {
        	if (response == 87) { // Deny Guild Invitation
        		cfrom.showMessage(11, c.getCharacter().getName() + " has denied the guild invitation.");
        		//cfrom.getClient().sendPacket(GuildPacket.denyGuildInvitation(c.getPlayer().getName()));
        	}
        }
	}

}
