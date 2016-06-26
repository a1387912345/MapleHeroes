package net.packet;

import client.character.MapleCharacter;
import net.SendPacketOpcode;
import tools.data.MaplePacketWriter;

public class TalkPacket {

	public static byte[] onPing() {
		MaplePacketWriter mpw = new MaplePacketWriter(SendPacketOpcode.PING_TALK);
				
		return mpw.getPacket();
	}
	
	public static byte[] onMigrateResponse() {
		MaplePacketWriter mpw = new MaplePacketWriter(SendPacketOpcode.MIGRATE_RESPONSE);
		mpw.write(0);
		
		return mpw.getPacket();
	}
	
	public static byte[] onAccountInfo(MapleCharacter chr) {
		MaplePacketWriter mpw = new MaplePacketWriter(SendPacketOpcode.UNK_RESPONSE);
		mpw.write(0);
		mpw.write(chr.getAccountID());
		
		return mpw.getPacket();
	}
	
	public static byte[] onGuildChat(MapleCharacter chr, String message) {
		MaplePacketWriter mpw = new MaplePacketWriter(SendPacketOpcode.GUILDCHAT);
		mpw.writeInt(0); // [26 36 F3 02] tick?
		mpw.writeInt(chr.getGuildId());
		mpw.writeInt(chr.getAccountID());
		mpw.writeInt(chr.getID());
		mpw.writeLong(PacketHelper.getTime(System.currentTimeMillis()));
		mpw.writeMapleAsciiString(message);
		
		return mpw.getPacket();
	}
}
