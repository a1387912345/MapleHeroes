package net.server.channel.handler;

import client.MapleClient;
import client.character.MapleCharacter;
import net.MaplePacketHandler;
import net.RecvPacketOpcode;
import net.netty.MaplePacketReader;
import server.MapleItemInformationProvider;

public class CancelItemEffectHandler extends MaplePacketHandler {

	public CancelItemEffectHandler(RecvPacketOpcode recv) {
		super(recv);
	}

	@Override
	public void handlePacket(final MaplePacketReader lea, final MapleClient c, final MapleCharacter chr) {
		final int id = lea.readInt();
		
		chr.cancelEffect(MapleItemInformationProvider.getInstance().getItemEffect(-id), false, -1L);
	}

}
