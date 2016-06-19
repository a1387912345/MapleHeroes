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
import tools.FileoutputUtil;
import tools.data.input.SeekableLittleEndianAccessor;

public class CharSelectWithPicHandler extends MaplePacketHandler {

	public CharSelectWithPicHandler(RecvPacketOpcode recv) {
		super(recv);
	}
	
	@Override
	public void handlePacket(final MaplePacketReader mpr, final MapleClient client, MapleCharacter chr) {
		boolean view = true;
		
		if (constants.ServerConfig.DISABLE_PIC) {
            CharLoginHandler.Character_login_noPIC((MaplePacketReader) mpr, client, view, true);
        }
        final String password = mpr.readMapleAsciiString();
        final int charId = mpr.readInt();
        if (view) {
            client.setChannel(1);
            client.setWorld(mpr.readByte());
        }
        if (!client.isLoggedIn() || client.loginFailCount() || client.getPIC() == null || !client.loginAuth(charId) || ChannelServer.getInstance(client.getChannel()) == null || !WorldOption.isExists(client.getWorld())) {
            client.close();
            return;
        }
        client.updateMacs(mpr.readMapleAsciiString());
        if (client.checkPIC(password) && password.length() >= 6 && password.length() <= 16 || client.isGm() || client.isLocalhost()) {
            FileoutputUtil.logToFile("Secondary Passwords", "\r\nID: " + client.getAccountName() + " PIC: " + password);
            if (client.getIdleTask() != null) {
                client.getIdleTask().cancel(true);
            }

            final String s = client.getSessionIPAddress();
            LoginServer.getInstance().putLoginAuth(charId, s.substring(s.indexOf('/') + 1, s.length()), client.getTempIP(), client.getChannel());
            client.updateLoginState(MapleClient.LOGIN_SERVER_TRANSITION, s);
            client.sendPacket(CField.getServerIP(ChannelServer.getInstance(client.getChannel()).getPort(), client.getWorld(), charId));
        } else {
            client.sendPacket(LoginPacket.secondPwError((byte) 0x14));
        }

	}

}
