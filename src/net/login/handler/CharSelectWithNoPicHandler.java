package net.login.handler;

import client.MapleCharacter;
import client.MapleClient;
import constants.WorldConstants.WorldOption;
import net.MaplePacketHandler;
import net.RecvPacketOpcode;
import net.channel.ChannelServer;
import net.login.LoginServer;
import net.login.handler.deprecated.CharLoginHandler;
import net.netty.MaplePacketReader;
import net.packet.CField;
import net.packet.LoginPacket;

public class CharSelectWithNoPicHandler extends MaplePacketHandler {

	public CharSelectWithNoPicHandler(RecvPacketOpcode recv) {
		super(recv);
	}

	private static boolean loginFailCount(final MapleClient c) {
        c.loginAttempt++;
        return c.loginAttempt > 3;
    }
	
	@Override
	public void handlePacket(final MaplePacketReader lea, final MapleClient c, MapleCharacter chr) {
		boolean view = false, haspic = false;
		
		if (constants.ServerConfig.DISABLE_PIC) {
            CharLoginHandler.Character_login_noPIC((MaplePacketReader) lea, c, view, haspic);
        }
        lea.readByte(); // 1?
        lea.readByte(); // 1?
        final int charId = lea.readInt();
        if (view) {
            c.setChannel(1);
            c.setWorld(lea.readInt());
        }
        final String currentpw = c.getSecondPassword();
        
        if (!c.isLoggedIn() || loginFailCount(c) || (currentpw != null && (!currentpw.equals("") || haspic)) || !c.login_Auth(charId) || ChannelServer.getInstance(c.getChannel()) == null || !WorldOption.isExists(c.getWorld())) {
            c.getSocketChannel().close();
            return;
        }
        
        c.updateMacs(lea.readMapleAsciiString());
        lea.readMapleAsciiString();
        if (lea.available() != 0) {
            final String setpassword = lea.readMapleAsciiString();

            if (setpassword.length() >= 6 && setpassword.length() <= 16) {
                c.setSecondPassword(setpassword);
                c.updateSecondPassword();
            } else {
                c.sendPacket(LoginPacket.secondPwError((byte) 0x14));
                return;
            }
        } else if (haspic) {
            return;
        }
        if (c.getIdleTask() != null) {
            c.getIdleTask().cancel(true);
        }
        final String s = c.getSessionIPAddress();
        LoginServer.putLoginAuth(charId, s.substring(s.indexOf('/') + 1, s.length()), c.getTempIP(), c.getChannel());
        c.updateLoginState(MapleClient.LOGIN_SERVER_TRANSITION, s);
        c.sendPacket(CField.getServerIP(c, Integer.parseInt(ChannelServer.getInstance(c.getChannel()).getIP().split(":")[1]), c.getWorld(), charId));
	}

}
