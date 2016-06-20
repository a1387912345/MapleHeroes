package net.server.channel.handler;

import client.MapleClient;
import client.character.MapleCharacter;
import net.MaplePacketHandler;
import net.RecvPacketOpcode;
import net.netty.MaplePacketReader;
import net.packet.CField;

public class CancelChairHandler extends MaplePacketHandler {

	public CancelChairHandler(RecvPacketOpcode recv) {
		super(recv);
	}

	@Override
	public void handlePacket(final MaplePacketReader mpr, final MapleClient c, MapleCharacter chr) {
		final short id = mpr.readShort();
		
		/*
		if (id == -1) {
            chr.cancelFishingTask();
            chr.setChair(0);
            c.sendPacket(CField.cancelChair(-1));
            if (chr.getMap() != null) {
                chr.getMap().broadcastMessage(chr, CField.showChair(chr.getId(), 0), false);
            }
        } else {
            chr.setChair(id);
            c.sendPacket(CField.cancelChair(id));
        }
        */
		if (id == -1) {
            chr.cancelFishingTask();
            chr.setChair(0);
            c.sendPacket(CField.cancelChair(chr));
            if (chr.getMap() != null) {
                chr.getMap().broadcastMessage(chr, CField.showChair(chr.getID(), 0), false);
            }
        }
		
	}

}
