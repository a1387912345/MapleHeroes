package net.server.channel.handlers;

import client.MapleCharacter;
import client.MapleClient;
import net.AbstractMaplePacketHandler;
import net.RecvPacketOpcode;
import tools.data.LittleEndianAccessor;
import tools.packet.CWvsContext.GuildPacket;

public class GuildRequestHandler extends AbstractMaplePacketHandler {

	public GuildRequestHandler(RecvPacketOpcode recv) {
		super(recv);
	}

	@Override
	public void handlePacket(LittleEndianAccessor lea, MapleClient c, MapleCharacter chr) {
		lea.skip(1);
		final String from = lea.readMapleAsciiString();
		final MapleCharacter cfrom = c.getChannelServer().getPlayerStorage().getCharacterByName(from);
        if (cfrom != null && GuildOperationHandler.getInvited().remove(c.getPlayer().getName().toLowerCase()) != null) {
            cfrom.getClient().getSession().write(GuildPacket.denyGuildInvitation(c.getPlayer().getName()));
        }
	}

}
