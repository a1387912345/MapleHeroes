package net.server.channel.handlers;

import client.MapleCharacter;
import client.MapleClient;
import net.AbstractMaplePacketHandler;
import net.RecvPacketOpcode;
import tools.data.LittleEndianAccessor;
import tools.packet.CField;

public class ChangeHakuHandler extends AbstractMaplePacketHandler {

	public ChangeHakuHandler(RecvPacketOpcode recv) {
		super(recv);
	}

	@Override
	public void handlePacket(LittleEndianAccessor lea, MapleClient c, MapleCharacter chr) {
		int oid = lea.readInt();
        if (chr.getHaku() != null) {
            chr.getHaku().sendStats();
            chr.getMap().broadcastMessage(chr, CField.spawnHaku_change0(chr.getId()), true);
            chr.getMap().broadcastMessage(chr, CField.spawnHaku_change1(chr.getHaku()), true);
            chr.getMap().broadcastMessage(chr, CField.spawnHaku_bianshen(chr.getId(), oid, chr.getHaku().getStats()), true);
        }
	}

}
