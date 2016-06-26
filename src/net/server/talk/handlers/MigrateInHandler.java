package net.server.talk.handlers;

import client.MapleClient;
import client.character.MapleCharacter;
import net.AbstractMaplePacketHandler;
import net.RecvPacketOpcode;
import tools.data.LittleEndianAccessor;

public class MigrateInHandler extends AbstractMaplePacketHandler {

	public MigrateInHandler(RecvPacketOpcode recv) {
		super(recv);
	}

	@Override
	public void handlePacket(LittleEndianAccessor lea, MapleClient c, MapleCharacter chr) {
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
