package net.server.channel.handlers;

import client.MapleCharacter;
import client.MapleClient;
import net.AbstractMaplePacketHandler;
import net.RecvPacketOpcode;
import tools.data.LittleEndianAccessor;
import tools.packet.CWvsContext;

public class GetBookInfoHandler extends AbstractMaplePacketHandler {

	public GetBookInfoHandler(RecvPacketOpcode recv) {
		super(recv);
	}

	@Override
	public void handlePacket(LittleEndianAccessor lea, MapleClient c, MapleCharacter chr) {
		if (c.getPlayer() == null || c.getPlayer().getMap() == null) {
            return;
        }
        lea.readInt(); // tick
        final MapleCharacter player = c.getPlayer().getMap().getCharacterById(lea.readInt());
        c.getSession().write(CWvsContext.enableActions());
        if (player != null && !player.isClone()) {
            if (!player.isGM() || c.getPlayer().isGM()) {
                c.getSession().write(CWvsContext.getMonsterBookInfo(player));
            }
        }
	}

}
