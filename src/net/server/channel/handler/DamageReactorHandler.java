package net.server.channel.handler;

import client.MapleClient;
import client.character.MapleCharacter;
import net.MaplePacketHandler;
import net.RecvPacketOpcode;
import net.netty.MaplePacketReader;
import server.maps.MapleReactor;

public class DamageReactorHandler extends MaplePacketHandler {

	public DamageReactorHandler(RecvPacketOpcode recv) {
		super(recv);
	}

	@Override
	public void handlePacket(MaplePacketReader mpr, MapleClient c, MapleCharacter chr) {
		final int oid = mpr.readInt();
        final int charPos = mpr.readInt();
        final short stance = mpr.readShort();
        final MapleReactor reactor = c.getCharacter().getMap().getReactorByOid(oid);
        System.out.println("Hit Reactor:  " + reactor);

        if (reactor == null || !reactor.isAlive()) {
            return;
        }
        reactor.hitReactor(charPos, stance, c);
	}

}
