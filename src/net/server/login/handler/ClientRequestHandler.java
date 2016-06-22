package net.server.login.handler;

import client.MapleClient;
import client.character.MapleCharacter;
import net.MaplePacketHandler;
import net.RecvPacketOpcode;
import net.netty.MaplePacketReader;
import net.packet.LoginPacket;

public class ClientRequestHandler extends MaplePacketHandler {

	public ClientRequestHandler(RecvPacketOpcode recv) {
		super(recv);
	}
	
	public void handlePacket(final MaplePacketReader inPacket, final MapleClient client, MapleCharacter chr) {
		client.sendPacket(LoginPacket.getClientResponse(inPacket.readInt()));
	}

	public boolean validateState(MapleClient c) {
		return true;
	}

}
