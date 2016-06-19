package net.server.login.handler;

import client.MapleClient;
import client.character.MapleCharacter;
import net.MaplePacketHandler;
import net.RecvPacketOpcode;
import net.netty.MaplePacketReader;
import net.packet.LoginPacket;
import net.server.login.LoginServer;

public class ServerStatusRequestHandler extends MaplePacketHandler {

	public ServerStatusRequestHandler(RecvPacketOpcode recv) {
		super(recv);
	}

	@Override
	public void handlePacket(final MaplePacketReader mpr, final MapleClient client, MapleCharacter chr) {
		// 0 = Select world normally
        // 1 = "Since there are many users, you may encounter some..."
        // 2 = "The concurrent users in this world have reached the max"
        final int numPlayer = LoginServer.getInstance().getUsersOn();
        final int userLimit = LoginServer.getInstance().getUserLimit();
        if (numPlayer >= userLimit) {
            client.sendPacket(LoginPacket.getServerStatus(2));
        } else if (numPlayer * 2 >= userLimit) {
            client.sendPacket(LoginPacket.getServerStatus(1));
        } else {
            client.sendPacket(LoginPacket.getServerStatus(0));
        }
	}

}
