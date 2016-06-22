package net.server.login.handler;

import java.util.Calendar;

import client.MapleClient;
import client.character.MapleCharacter;
import constants.ServerConfig;
import constants.ServerConstants;
import net.MaplePacketHandler;
import net.RecvPacketOpcode;
import net.netty.MaplePacketReader;
import net.packet.CWvsContext;
import net.packet.LoginPacket;
import net.packet.PacketHelper;
import net.server.login.LoginWorker;
import tools.FileoutputUtil;

public class LoginPasswordHandler extends MaplePacketHandler {
	
	public LoginPasswordHandler(RecvPacketOpcode recv) {
		super(recv);
	}
	
	public void handlePacket(final MaplePacketReader mpr, final MapleClient client, MapleCharacter chr) {
		mpr.readByte();
    	String pwd = client.isLocalhost() ? "admin" : mpr.readMapleAsciiString();
        String login = client.isLocalhost() ? "admin" : mpr.readMapleAsciiString();

        System.out.println("Username: " + login);

        final boolean ipBan = client.hasBannedIP();
        final boolean macBan = client.hasBannedMac();

        int loginok = 0;
        loginok = client.login(login, pwd, ipBan || macBan);
        final Calendar tempbannedTill = client.getTempBanCalendar();

        if (!client.isGm() && !client.isLocalhost() && ServerConstants.Use_Localhost) {
            client.sendPacket(CWvsContext.broadcastMsg(1, "We are sorry, but the server is under a maintenance, please check the forums for more information."));
            client.sendPacket(LoginPacket.getLoginFailed(1)); //Shows no message, used for unstuck the login button
        }

        if (loginok == 0 && (ipBan || macBan) && !client.isGm()) {
            loginok = 3;
            if (macBan) {
                // this is only an ipban o.O" - maybe we should refactor this a bit so it's more readable
                MapleCharacter.ban(client.getSocketChannel().remoteAddress().toString().split(":")[0], "Enforcing account ban, account " + login, false, 4, false);
            }
        }
        if (loginok != 0) {
            if (!client.loginFailCount()) {
                client.clearInformation();
                if (loginok == 3) {
                    client.sendPacket(CWvsContext.broadcastMsg(1, client.showBanReason(login, true)));
                    client.sendPacket(LoginPacket.getLoginFailed(1)); //Shows no message, used for unstuck the login button
                } else {
                    client.sendPacket(LoginPacket.getLoginFailed(loginok));
                }
            } else {
                client.getSocketChannel().close();
            }
        } else if (tempbannedTill.getTimeInMillis() != 0) {
            if (!client.loginFailCount()) {
                client.clearInformation();
                client.sendPacket(LoginPacket.getTempBan(PacketHelper.getTime(tempbannedTill.getTimeInMillis()), client.getBanReason()));
            } else {
                client.getSocketChannel().close();
            }
        } else {
            if (ServerConfig.logAccounts) {
                FileoutputUtil.logToFile("Accounts", "\r\nID: " + login + " Password: " + pwd);
            }
            client.loginAttempt = 0;
            LoginWorker.registerClient(client);
        }
	}

    public boolean validateState(MapleClient c) {
        return !c.isLoggedIn();
    }
   
}
