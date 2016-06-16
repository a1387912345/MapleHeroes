package net.packet;

import client.MapleCharacter;
import net.SendPacketOpcode;
import net.netty.MaplePacketWriter;

public class TalkPacket {

	public static byte[] onPing() {
		MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.PING_TALK);
				
		return mplew.getPacket();
	}
	
	public static byte[] onMigrateResponse() {
		MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.MIGRATE_RESPONSE);
		mplew.write(0);
		
		return mplew.getPacket();
	}
	
	public static byte[] onAccountInfo(MapleCharacter chr) {
		MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.UNK_RESPONSE);
		mplew.write(0);
		mplew.write(chr.getAccountID());
		
		return mplew.getPacket();
	}
	
	public static byte[] onGuildChat(MapleCharacter chr, String message) {
		MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.GUILDCHAT);
		mplew.writeInt(0); // [26 36 F3 02] tick?
		mplew.writeInt(chr.getGuildId());
		mplew.writeInt(chr.getAccountID());
		mplew.writeInt(chr.getId());
		mplew.writeLong(PacketHelper.getTime(System.currentTimeMillis()));
		mplew.writeMapleAsciiString(message);
		
		return mplew.getPacket();
	}
}
