package net.talk.handler;

import client.MapleCharacter;
import client.MapleClient;
import net.MaplePacketHandler;
import net.RecvPacketOpcode;
import net.netty.MaplePacketReader;

public class MigrateInHandler extends MaplePacketHandler {

	public MigrateInHandler(RecvPacketOpcode recv) {
		super(recv);
	}

	@Override
	public void handlePacket(MaplePacketReader lea, MapleClient c, MapleCharacter chr) {
		final int sessionId = lea.readInt();
		lea.readInt(); // 1
		lea.readLong(); // ??
		lea.readByte(); // 0, could be a boolean
		final int charId = lea.readInt();
		final String charName = lea.readMapleAsciiString();
		lea.readInt(); // 1
		lea.readInt(); // Char Level
		lea.readInt(); // Job ID
		
		
		
	}

}
