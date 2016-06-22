package net.server.channel.handler;

import java.awt.Point;
import java.util.List;

import client.MapleClient;
import client.character.MapleCharacter;
import net.MaplePacketHandler;
import net.RecvPacketOpcode;
import net.netty.MaplePacketReader;
import net.packet.CField;
import net.server.channel.handler.deprecated.MovementParse;
import server.movement.LifeMovementFragment;

public class MoveAndroidHandler extends MaplePacketHandler {

	public MoveAndroidHandler(RecvPacketOpcode recv) {
		super(recv);
	}

	@Override
	public void handlePacket(MaplePacketReader mpr, MapleClient c, MapleCharacter chr) {
		mpr.skip(12);
        final List<LifeMovementFragment> res = MovementParse.parseMovement(mpr, 3, null, null);
       

        if ((res != null) && (chr != null) && (!res.isEmpty()) && (chr.getMap() != null) && (chr.getAndroid() != null)) {
            Point pos = new Point(chr.getAndroid().getPos());
            chr.getAndroid().updatePosition(res);
            chr.getMap().broadcastMessage(chr, CField.moveAndroid(chr.getID(), pos, res), false);
        }
	}

}
