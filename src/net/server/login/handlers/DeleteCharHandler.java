package net.server.login.handlers;

import client.MapleClient;
import net.AbstractMaplePacketHandler;
import tools.data.LittleEndianAccessor;
import tools.packet.LoginPacket;

public class DeleteCharHandler extends AbstractMaplePacketHandler {

	private static boolean loginFailCount(final MapleClient c) {
        c.loginAttempt++;
        return c.loginAttempt > 3;
    }
	
	public void handlePacket(final LittleEndianAccessor lea, final MapleClient c) {
        String secondPassword = lea.readMapleAsciiString();
        if (secondPassword == null) {
            if (lea.readByte() > 0) { // Specific if user have second password or not
                secondPassword = lea.readMapleAsciiString();
            }
            lea.readMapleAsciiString();
        }

        final int charid = lea.readInt();

        if (!c.login_Auth(charid) || !c.isLoggedIn() || loginFailCount(c)) {
            c.getSession().close();
            return; // Attempting to delete other character
        }
        byte response = 0;

        if (c.getSecondPassword() != null) { // On the server, there's a second password
            if (secondPassword == null) { // Client's hacking
                c.getSession().close();
                return;
            } else {
                if (!c.CheckSecondPassword(secondPassword)) { // Wrong Password
                    response = 20;
                }
            }
        }

        if (response == 0) {
            response = (byte) c.deleteCharacter(charid);
        }
        c.getSession().write(LoginPacket.deleteCharResponse(charid, response));
    }
}
