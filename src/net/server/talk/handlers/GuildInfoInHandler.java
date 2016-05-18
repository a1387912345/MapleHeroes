package net.server.talk.handlers;

import client.MapleCharacter;
import client.MapleClient;
import net.AbstractMaplePacketHandler;
import net.RecvPacketOpcode;
import tools.data.LittleEndianAccessor;

public class GuildInfoInHandler extends AbstractMaplePacketHandler {

	public GuildInfoInHandler(RecvPacketOpcode recv) {
		super(recv);
	}

	@Override
	public void handlePacket(LittleEndianAccessor lea, MapleClient c, MapleCharacter chr) {
		final int guildid = lea.readInt();
		final int charid = lea.readInt();
	}

}
