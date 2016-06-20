package net.server.channel.handler;

import client.MapleClient;
import client.character.MapleCharacter;
import net.MaplePacketHandler;
import net.RecvPacketOpcode;
import net.netty.MaplePacketReader;
import net.packet.CWvsContext;

public class MesoDropHandler extends MaplePacketHandler {

	public MesoDropHandler(RecvPacketOpcode recv) {
		super(recv);
	}

	@Override
	public void handlePacket(final MaplePacketReader mpr, final MapleClient c, final MapleCharacter chr) {
		chr.updateTick(mpr.readInt());
		final int meso = mpr.readInt();
		
		if ((!chr.isAlive()) || (meso < 10) || (meso > 50000) || (meso > chr.getMeso())) {
            chr.getClient().sendPacket(CWvsContext.enableActions());
            return;
        }
        chr.gainMeso(-meso, false, true);
        chr.getMap().spawnMesoDrop(meso, chr.getTruePosition(), chr, chr, true, (byte) 0);
        chr.getCheatTracker().checkDrop(true);
	}

}
