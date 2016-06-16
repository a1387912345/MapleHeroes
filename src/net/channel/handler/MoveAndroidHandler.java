package net.channel.handler;

import java.awt.Point;
import java.util.List;

import client.MapleCharacter;
import client.MapleClient;
import net.MaplePacketHandler;
import net.RecvPacketOpcode;
import net.channel.handler.deprecated.MovementParse;
import net.netty.MaplePacketReader;
import net.packet.CField;
import server.movement.LifeMovementFragment;

public class MoveAndroidHandler extends MaplePacketHandler {

	public MoveAndroidHandler(RecvPacketOpcode recv) {
		super(recv);
	}

	@Override
	public void handlePacket(MaplePacketReader lea, MapleClient c, MapleCharacter chr) {
		lea.skip(12);
        final List<LifeMovementFragment> res = MovementParse.parseMovement(lea, 3, null, null);
       

        if ((res != null) && (chr != null) && (!res.isEmpty()) && (chr.getMap() != null) && (chr.getAndroid() != null)) {
            Point pos = new Point(chr.getAndroid().getPos());
            chr.getAndroid().updatePosition(res);
            chr.getMap().broadcastMessage(chr, CField.moveAndroid(chr.getId(), pos, res), false);
        }
	}

}