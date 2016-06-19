package net.server.login.handler;

import client.MapleClient;
import client.character.MapleCharacter;
import net.MaplePacketHandler;
import net.RecvPacketOpcode;
import net.netty.MaplePacketReader;
import net.packet.LoginPacket;

public class DeleteCharHandler extends MaplePacketHandler {

	public DeleteCharHandler(RecvPacketOpcode recv) {
		super(recv);
	}

	private static boolean loginFailCount(final MapleClient c) {
        c.loginAttempt++;
        return c.loginAttempt > 3;
    }
	
	public void handlePacket(final MaplePacketReader lea, final MapleClient c, MapleCharacter chr) {
        String secondPassword = lea.readMapleAsciiString();
        if (secondPassword == null) {
            if (lea.readByte() > 0) { // Specific if user have second password or not
                secondPassword = lea.readMapleAsciiString();
            }
            lea.readMapleAsciiString();
        }

        final int charid = lea.readInt();

        if (!c.loginAuth(charid) || !c.isLoggedIn() || loginFailCount(c)) {
            c.close();
            return; // Attempting to delete other character
        }
        byte response = 0;

        if (c.getPIC() != null) { // On the server, there's a second password
            if (secondPassword == null) { // Client's hacking
                c.close();
                return;
            } else {
                if (!c.checkPIC(secondPassword)) { // Wrong Password
                    response = 20;
                }
            }
        }

        if (response == 0) {
            response = (byte) c.deleteCharacter(charid);
        }
        c.sendPacket(LoginPacket.deleteCharResponse(charid, response));
    }
}
