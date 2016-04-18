package net.server.channel.handlers.pet;

import client.MapleCharacter;
import client.MapleClient;
import net.AbstractMaplePacketHandler;
import net.RecvPacketOpcode;
import tools.data.LittleEndianAccessor;
import tools.packet.PetPacket;

public class PetChatHandler extends AbstractMaplePacketHandler {

	public PetChatHandler(RecvPacketOpcode recv) {
		super(recv);
	}

	@Override
	public void handlePacket(LittleEndianAccessor lea, MapleClient c, MapleCharacter chr) {
		if (lea.available() < 12) {
            return;
        }
        final int petid = c.getPlayer().getPetIndex((int) lea.readLong());
        c.getPlayer().updateTick(lea.readInt());
        final short command = lea.readShort();
        final String text = lea.readMapleAsciiString();
        
        if (chr == null || chr.getMap() == null || chr.getPet(petid) == null) {
            return;
        }
        chr.getMap().broadcastMessage(chr, PetPacket.petChat(chr.getId(), command, text, (byte) petid), true);
	}

}
