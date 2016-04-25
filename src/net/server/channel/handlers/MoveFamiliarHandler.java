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

public class MoveFamiliarHandler extends AbstractMaplePacketHandler {

	public MoveFamiliarHandler(RecvPacketOpcode recv) {
		super(recv);
	}

	@Override
	public void handlePacket(LittleEndianAccessor lea, MapleClient c, MapleCharacter chr) {
		lea.skip(17);
        List res = MovementParse.parseMovement(lea, 6, null, null);
        if ((chr != null) && (chr.getSummonedFamiliar() != null) && (res.size() > 0)) {
            Point pos = chr.getSummonedFamiliar().getPosition();
            MovementParse.updatePosition(res, chr.getSummonedFamiliar(), 0);
            chr.getSummonedFamiliar().updatePosition(res);
            if (!chr.isHidden()) {
                chr.getMap().broadcastMessage(chr, CField.moveFamiliar(chr.getId(), pos, res), chr.getTruePosition());
            }
        }
	}

}
