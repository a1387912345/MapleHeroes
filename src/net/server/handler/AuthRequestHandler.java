package net.server.handler;

import client.MapleClient;
import client.character.MapleCharacter;
import net.MaplePacketHandler;
import net.RecvPacketOpcode;
import net.SendPacketOpcode;
import net.netty.MaplePacketReader;
import net.packet.LoginPacket;

public class AuthRequestHandler extends MaplePacketHandler {

	public AuthRequestHandler(RecvPacketOpcode recv) {
		super(recv);
	}
	
	public void handlePacket(final MaplePacketReader inPacket, final MapleClient client, MapleCharacter chr) {
		client.sendPacket(LoginPacket.sendAuthResponse(SendPacketOpcode.AUTH_RESPONSE.getOpcode() ^ inPacket.readInt()));
	}

	@Override
	public boolean validateState(MapleClient c) {
		return true;
	}

}
