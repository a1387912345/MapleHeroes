package net.server.talk.handler;

import client.MapleClient;
import client.character.MapleCharacter;
import net.MaplePacketHandler;
import net.RecvPacketOpcode;
import net.netty.MaplePacketReader;

public class GuildInfoInHandler extends MaplePacketHandler {

	public GuildInfoInHandler(RecvPacketOpcode recv) {
		super(recv);
	}

	@Override
	public void handlePacket(MaplePacketReader mpr, MapleClient c, MapleCharacter chr) {
		final int guildid = mpr.readInt();
		final int charid = mpr.readInt();
	}

}
