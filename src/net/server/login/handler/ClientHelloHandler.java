package net.server.login.handler;

import client.MapleClient;
import client.character.MapleCharacter;
import net.MaplePacketHandler;
import net.RecvPacketOpcode;
import net.netty.MaplePacketReader;

public class ClientHelloHandler extends MaplePacketHandler {

	public ClientHelloHandler(RecvPacketOpcode recv) {
		super(recv);
	}

	public void handlePacket(final MaplePacketReader inPacket, final MapleClient client, MapleCharacter chr) {
		System.out.println(client.getSessionIPAddress() + " Connected!");
	}

	@Override
	public boolean validateState(MapleClient c) {
		return true;
	}

}
