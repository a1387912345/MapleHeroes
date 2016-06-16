package net.login.handler;

import java.util.List;

import client.MapleCharacter;
import client.MapleClient;
import constants.ServerConstants;
import constants.WorldConstants.TespiaWorldOption;
import constants.WorldConstants.WorldOption;
import net.MaplePacketHandler;
import net.RecvPacketOpcode;
import net.login.LoginServer;
import net.netty.MaplePacketReader;
import net.packet.LoginPacket;

public class ServerlistRequestHandler extends MaplePacketHandler {

	public ServerlistRequestHandler(RecvPacketOpcode recv) {
		super(recv);
	}

	@Override
	public void handlePacket(final MaplePacketReader lea, final MapleClient c, MapleCharacter chr) {
		c.sendPacket(LoginPacket.changeBackground(ServerConstants.backgrounds));
    	if (ServerConstants.TESPIA) {
            for (TespiaWorldOption tespiaservers : TespiaWorldOption.values()) {
                if (TespiaWorldOption.getById(tespiaservers.getWorld()).show() && TespiaWorldOption.getById(tespiaservers.getWorld()) != null) {
                    c.sendPacket(LoginPacket.getServerList(Integer.parseInt(tespiaservers.getWorld().replace("t", "")), LoginServer.getLoad()));
                }
            }
        } else {
            for (WorldOption servers : WorldOption.values()) {
                if (WorldOption.getById(servers.getWorld()).show() && servers != null) {
                    c.sendPacket(LoginPacket.getServerList(servers.getWorld(), LoginServer.getLoad()));
                }
            }
        }
        c.sendPacket(LoginPacket.getEndOfServerList());
        boolean hasCharacters = false;
        for (int world = 0; world < WorldOption.values().length; world++) {
            final List<MapleCharacter> chars = c.loadCharacters(world);
            if (chars != null) {
                hasCharacters = true;
                break;
            }
        }
        if (ServerConstants.TESPIA) {
            for (TespiaWorldOption value : TespiaWorldOption.values()) {
                String world = value.getWorld();
                //final List<MapleCharacter> chars = c.loadTespiaCharacters(world);
                //if (chars != null) {
                //    hasCharacters = true;
                //    break;
                //}
            }
        }
        if (!hasCharacters) {
            c.sendPacket(LoginPacket.enableRecommended(WorldOption.recommended));
        }
        if (WorldOption.recommended >= 0) {
            c.sendPacket(LoginPacket.sendRecommended(WorldOption.recommended, WorldOption.recommendedmsg));
        }
	}

}
