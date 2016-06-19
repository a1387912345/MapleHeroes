package net.server.login.handler;

import client.MapleClient;
import client.character.MapleCharacter;
import constants.WorldConstants.WorldOption;
import net.MaplePacketHandler;
import net.RecvPacketOpcode;
import net.netty.MaplePacketReader;
import net.packet.CField;
import net.packet.LoginPacket;
import net.server.channel.ChannelServer;
import net.server.login.LoginServer;
import net.server.login.handler.deprecated.CharLoginHandler;

public class CharSelectWithNoPicHandler extends MaplePacketHandler {

	public CharSelectWithNoPicHandler(RecvPacketOpcode recv) {
		super(recv);
	}
	
	@Override
	public void handlePacket(final MaplePacketReader mpr, final MapleClient client, MapleCharacter chr) {
		boolean view = false, hasPIC = false;
		
		if (constants.ServerConfig.DISABLE_PIC) {
            CharLoginHandler.Character_login_noPIC((MaplePacketReader) mpr, client, view, hasPIC);
        }
        mpr.readByte(); // 1?
        mpr.readByte(); // 1?
        final int charID = mpr.readInt();
        if (view) {
            client.setChannel(1);
            client.setWorld(mpr.readInt());
        }
        final String currentPIC = client.getPIC();
        
        if (!client.isLoggedIn() || client.loginFailCount() || (currentPIC != null && (!currentPIC.equals("") || hasPIC)) || !client.loginAuth(charID) || ChannelServer.getInstance(client.getChannel()) == null || !WorldOption.isExists(client.getWorld())) {
            client.close();
            return;
        }
        
        client.updateMacs(mpr.readMapleAsciiString());
        mpr.readMapleAsciiString();
        if (mpr.available() != 0) {
            final String setpassword = mpr.readMapleAsciiString();

            if (setpassword.length() >= 6 && setpassword.length() <= 16) {
                client.setPIC(setpassword);
                client.updatePIC();
            } else {
                client.sendPacket(LoginPacket.secondPwError((byte) 0x14));
                return;
            }
        } else if (hasPIC) {
            return;
        }
        if (client.getIdleTask() != null) {
            client.getIdleTask().cancel(true);
        }
        final String s = client.getSessionIPAddress();
        LoginServer.getInstance().putLoginAuth(charID, s.substring(s.indexOf('/') + 1, s.length()), client.getTempIP(), client.getChannel());
        client.updateLoginState(MapleClient.LOGIN_SERVER_TRANSITION, s);
        client.sendPacket(CField.getServerIP(ChannelServer.getInstance(client.getChannel()).getPort(), client.getWorld(), charID));
	}

}
