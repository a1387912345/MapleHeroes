package tools.packet;

import client.MapleCharacter;
import net.SendPacketOpcode;
import tools.data.output.MaplePacketLittleEndianWriter;

public class TalkPacket {

	public static byte[] onPing() {
		MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
		
		mplew.writeShort(SendPacketOpcode.PING_TALK.getValue());
		
		return mplew.getPacket();
	}
	
	public static byte[] onMigrateResponse() {
		MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
		
		mplew.writeShort(SendPacketOpcode.MIGRATE_RESPONSE.getValue());
		mplew.write(0);
		
		return mplew.getPacket();
	}
	
	public static byte[] onAccountInfo(MapleCharacter chr) {
		MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
		
		mplew.writeShort(SendPacketOpcode.UNK_RESPONSE.getValue());
		mplew.write(0);
		mplew.write(chr.getAccountID());
		
		return mplew.getPacket();
	}
	
	public static byte[] onGuildChat(MapleCharacter chr, String message) {
		MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
		
		mplew.writeShort(SendPacketOpcode.GUILDCHAT.getValue());
		mplew.writeInt(0); // [26 36 F3 02] tick?
		mplew.writeInt(chr.getGuildId());
		mplew.writeInt(chr.getAccountID());
		mplew.writeInt(chr.getId());
		mplew.writeLong(PacketHelper.getTime(System.currentTimeMillis()));
		mplew.writeMapleAsciiString(message);
		
		return mplew.getPacket();
	}
}
