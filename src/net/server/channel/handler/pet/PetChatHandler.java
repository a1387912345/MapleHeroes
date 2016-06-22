package net.server.channel.handler.pet;

import client.MapleClient;
import client.character.MapleCharacter;
import net.MaplePacketHandler;
import net.RecvPacketOpcode;
import net.netty.MaplePacketReader;
import net.packet.PetPacket;

public class PetChatHandler extends MaplePacketHandler {

	public PetChatHandler(RecvPacketOpcode recv) {
		super(recv);
	}

	@Override
	public void handlePacket(MaplePacketReader mpr, MapleClient c, MapleCharacter chr) {
		if (mpr.available() < 12) {
            return;
        }
        final int petid = c.getCharacter().getPetIndex((int) mpr.readLong());
        c.getCharacter().updateTick(mpr.readInt());
        final short command = mpr.readShort();
        final String text = mpr.readMapleAsciiString();
        
        if (chr == null || chr.getMap() == null || chr.getPet(petid) == null) {
            return;
        }
        chr.getMap().broadcastMessage(chr, PetPacket.petChat(chr.getID(), command, text, (byte) petid), true);
	}

}
