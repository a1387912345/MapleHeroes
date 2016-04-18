package net.server.channel.handlers;

import java.awt.Point;
import java.util.List;

import client.MapleCharacter;
import client.MapleClient;
import net.AbstractMaplePacketHandler;
import net.RecvPacketOpcode;
import net.channel.handler.MovementParse;
import tools.data.LittleEndianAccessor;
import tools.packet.CField;

public class MoveHakuHandler extends AbstractMaplePacketHandler {

	public MoveHakuHandler(RecvPacketOpcode recv) {
		super(recv);
	}

	@Override
	public void handlePacket(LittleEndianAccessor lea, MapleClient c, MapleCharacter chr) {
		lea.skip(17);
        List res = MovementParse.parseMovement(lea, 6);

        if ((res != null) && (chr != null) && (!res.isEmpty()) && (chr.getMap() != null) && (chr.getHaku() != null)) {
            Point pos = new Point(chr.getHaku().getPosition());
            chr.getHaku().updatePosition(res);
            chr.getMap().broadcastMessage(chr, CField.moveHaku(chr.getId(), chr.getHaku().getObjectId(), pos, res), false);
        }
	}

}
