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

public class MoveHakuHandler extends MaplePacketHandler {

	public MoveHakuHandler(RecvPacketOpcode recv) {
		super(recv);
	}

	@Override
	public void handlePacket(MaplePacketReader lea, MapleClient c, MapleCharacter chr) {
		lea.skip(17);
        List res = MovementParse.parseMovement(lea, 6, null, null);

        if ((res != null) && (chr != null) && (!res.isEmpty()) && (chr.getMap() != null) && (chr.getHaku() != null)) {
            Point pos = new Point(chr.getHaku().getPosition());
            chr.getHaku().updatePosition(res);
            chr.getMap().broadcastMessage(chr, CField.moveHaku(chr.getId(), chr.getHaku().getObjectId(), pos, res), false);
        }
	}

}
