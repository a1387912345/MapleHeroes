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

public class MoveHakuHandler extends MaplePacketHandler {

	public MoveHakuHandler(RecvPacketOpcode recv) {
		super(recv);
	}

	@Override
	public void handlePacket(MaplePacketReader mpr, MapleClient c, MapleCharacter chr) {
		mpr.skip(17);
        List res = MovementParse.parseMovement(mpr, 6, null, null);

        if ((res != null) && (chr != null) && (!res.isEmpty()) && (chr.getMap() != null) && (chr.getHaku() != null)) {
            Point pos = new Point(chr.getHaku().getPosition());
            chr.getHaku().updatePosition(res);
            chr.getMap().broadcastMessage(chr, CField.moveHaku(chr.getID(), chr.getHaku().getObjectId(), pos, res), false);
        }
	}

}
