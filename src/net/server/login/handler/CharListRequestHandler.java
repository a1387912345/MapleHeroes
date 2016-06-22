package net.server.login.handler;

import java.util.List;

import client.MapleClient;
import client.character.MapleCharacter;
import constants.WorldConstants;
import constants.WorldConstants.WorldOption;
import net.MaplePacketHandler;
import net.RecvPacketOpcode;
import net.netty.MaplePacketReader;
import net.packet.CWvsContext;
import net.packet.LoginPacket;
import net.server.channel.ChannelServer;
import net.world.World;

public class CharListRequestHandler extends MaplePacketHandler {

	public CharListRequestHandler(RecvPacketOpcode recv) {
		super(recv);
	}

	@Override
	public void handlePacket(final MaplePacketReader mpr, final MapleClient client, MapleCharacter chr) {
		if (!client.isLoggedIn()) {
            client.close();
            return;
        }
        mpr.readByte(); //2?
        final int worldID = mpr.readByte();
        final int channel = mpr.readByte() + 1;
        if (!World.isChannelAvailable(channel, worldID) || !WorldOption.isExists(worldID)) {
            client.sendPacket(LoginPacket.getLoginFailed(10)); //cannot process so many
            return;
        }

        if (!WorldOption.getById(worldID).isAvailable() && !(client.isGm() && worldID == WorldConstants.gmserver)) {
            client.sendPacket(CWvsContext.broadcastMsg(1, "We are sorry, but " + WorldConstants.getNameById(worldID) + " is currently not available. \r\nPlease try another world."));
            client.sendPacket(LoginPacket.getLoginFailed(1)); //Shows no message, but it is used to unstuck
            return;
        }

        //System.out.println("Client " + c.getSession().getRemoteAddress().toString().split(":")[0] + " is connecting to server " + server + " channel " + channel + "");
        final List<MapleCharacter> chars = client.loadCharacters(worldID);
        if (chars != null && ChannelServer.getInstance(channel) != null) {
            client.setWorld(worldID);
            client.setChannel(channel);
            client.sendPacket(LoginPacket.getCharList(client.getPIC(), chars, client.getCharacterSlots()));
        } else {
            client.close();
        }
	}

}
