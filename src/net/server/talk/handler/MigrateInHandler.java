package net.server.talk.handler;

import client.MapleClient;
import client.character.MapleCharacter;
import net.MaplePacketHandler;
import net.RecvPacketOpcode;
import net.netty.MaplePacketReader;

public class MigrateInHandler extends MaplePacketHandler {

	public MigrateInHandler(RecvPacketOpcode recv) {
		super(recv);
	}

	@Override
	public void handlePacket(MaplePacketReader mpr, MapleClient c, MapleCharacter chr) {
		final int sessionId = mpr.readInt();
		mpr.readInt(); // 1
		mpr.readLong(); // ??
		mpr.readByte(); // 0, could be a boolean
		final int charId = mpr.readInt();
		final String charName = mpr.readMapleAsciiString();
		mpr.readInt(); // 1
		mpr.readInt(); // Char Level
		mpr.readInt(); // Job ID
		
		
		
	}

}
