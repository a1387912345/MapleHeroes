package net.server.login.handler;

import java.util.List;

import client.MapleClient;
import client.character.MapleCharacter;
import constants.ServerConstants;
import constants.WorldConstants.TespiaWorldOption;
import constants.WorldConstants.WorldOption;
import net.MaplePacketHandler;
import net.RecvPacketOpcode;
import net.netty.MaplePacketReader;
import net.packet.LoginPacket;
import net.server.login.LoginServer;

public class ServerlistRequestHandler extends MaplePacketHandler {

	public ServerlistRequestHandler(RecvPacketOpcode recv) {
		super(recv);
	}

	@Override
	public void handlePacket(final MaplePacketReader mpr, final MapleClient client, MapleCharacter chr) {
		client.sendPacket(LoginPacket.changeBackground(ServerConstants.backgrounds));
    	if (ServerConstants.TESPIA) {
            for (TespiaWorldOption tespiaservers : TespiaWorldOption.values()) {
                if (TespiaWorldOption.getById(tespiaservers.getWorld()).show() && TespiaWorldOption.getById(tespiaservers.getWorld()) != null) {
                    client.sendPacket(LoginPacket.getServerList(Integer.parseInt(tespiaservers.getWorld().replace("t", "")), LoginServer.getInstance().getLoad()));
                }
            }
        } else {
            for (WorldOption servers : WorldOption.values()) {
                if (WorldOption.getById(servers.getWorld()).show() && servers != null) {
                    client.sendPacket(LoginPacket.getServerList(servers.getWorld(), LoginServer.getInstance().getLoad()));
                }
            }
        }
        client.sendPacket(LoginPacket.getEndOfServerList());
        boolean hasCharacters = false;
        for (int world = 0; world < WorldOption.values().length; world++) {
            final List<MapleCharacter> chars = client.loadCharacters(world);
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
            client.sendPacket(LoginPacket.enableRecommended(WorldOption.recommended));
        }
        if (WorldOption.recommended >= 0) {
            client.sendPacket(LoginPacket.sendRecommended(WorldOption.recommended, WorldOption.recommendedmsg));
        }
	}

}
