package net.server.channel.handler;

import java.awt.Point;

import client.MapleClient;
import client.anticheat.CheatingOffense;
import client.character.MapleCharacter;
import net.MaplePacketHandler;
import net.RecvPacketOpcode;
import net.netty.MaplePacketReader;
import server.MaplePortal;

public class InnerPortalHandler extends MaplePacketHandler {

	public InnerPortalHandler(RecvPacketOpcode recv) {
		super(recv);
	}

	@Override
	public void handlePacket(final MaplePacketReader mpr, final MapleClient c, final MapleCharacter chr) {
		mpr.skip(1);
		if ((chr == null) || (chr.getMap() == null)) {
            return;
        }
        MaplePortal portal = chr.getMap().getPortal(mpr.readMapleAsciiString());
        int toX = mpr.readShort();
        int toY = mpr.readShort();

        if (portal == null) {
            return;
        }
        if ((portal.getPosition().distanceSq(chr.getTruePosition()) > 22500.0D) && (!chr.isGM())) {
            chr.getCheatTracker().registerOffense(CheatingOffense.USING_FARAWAY_PORTAL);
            return;
        }
        chr.getMap().movePlayer(chr, new Point(toX, toY));
        chr.checkFollow();
	}

}
