package net.channel.handler.pet;

import client.MapleCharacter;
import client.MapleClient;
import net.MaplePacketHandler;
import net.RecvPacketOpcode;
import net.netty.MaplePacketReader;
import net.packet.PetPacket;

public class PetChatHandler extends MaplePacketHandler {

	public PetChatHandler(RecvPacketOpcode recv) {
		super(recv);
	}

	@Override
	public void handlePacket(MaplePacketReader lea, MapleClient c, MapleCharacter chr) {
		if (lea.available() < 12) {
            return;
        }
        final int petid = c.getCharacter().getPetIndex((int) lea.readLong());
        c.getCharacter().updateTick(lea.readInt());
        final short command = lea.readShort();
        final String text = lea.readMapleAsciiString();
        
        if (chr == null || chr.getMap() == null || chr.getPet(petid) == null) {
            return;
        }
        chr.getMap().broadcastMessage(chr, PetPacket.petChat(chr.getId(), command, text, (byte) petid), true);
	}

}
