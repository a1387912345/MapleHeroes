package net.channel.handler;

import client.MapleCharacter;
import client.MapleClient;
import net.MaplePacketHandler;
import net.RecvPacketOpcode;
import net.netty.MaplePacketReader;

public class CancelDebuffHandler extends MaplePacketHandler {

	public CancelDebuffHandler(RecvPacketOpcode recv) {
		super(recv);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void handlePacket(MaplePacketReader lea, MapleClient c, MapleCharacter chr) {
		// TODO Auto-generated method stub

	}

}
