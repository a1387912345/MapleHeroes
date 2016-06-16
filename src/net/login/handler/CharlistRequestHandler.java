package net.login.handler;

import java.util.List;

import client.MapleCharacter;
import client.MapleClient;
import constants.WorldConstants;
import constants.WorldConstants.WorldOption;
import net.MaplePacketHandler;
import net.RecvPacketOpcode;
import net.channel.ChannelServer;
import net.netty.MaplePacketReader;
import net.packet.CWvsContext;
import net.packet.LoginPacket;
import net.world.World;

public class CharlistRequestHandler extends MaplePacketHandler {

	public CharlistRequestHandler(RecvPacketOpcode recv) {
		super(recv);
	}

	@Override
	public void handlePacket(final MaplePacketReader inPacket, final MapleClient c, MapleCharacter chr) {
		if (!c.isLoggedIn()) {
            c.getSocketChannel().close();
            return;
        }
        inPacket.readByte(); //2?
        final int server = inPacket.readByte();
        final int channel = inPacket.readByte() + 1;
        System.out.println("CHANNEL READ: " + channel);
        if (!World.isChannelAvailable(channel, server) || !WorldOption.isExists(server)) {
            c.sendPacket(LoginPacket.getLoginFailed(10)); //cannot process so many
            return;
        }

        if (!WorldOption.getById(server).isAvailable() && !(c.isGm() && server == WorldConstants.gmserver)) {
            c.sendPacket(CWvsContext.broadcastMsg(1, "We are sorry, but " + WorldConstants.getNameById(server) + " is currently not available. \r\nPlease try another world."));
            c.sendPacket(LoginPacket.getLoginFailed(1)); //Shows no message, but it is used to unstuck
            return;
        }

        //System.out.println("Client " + c.getSession().getRemoteAddress().toString().split(":")[0] + " is connecting to server " + server + " channel " + channel + "");
        final List<MapleCharacter> chars = c.loadCharacters(server);
        if (chars != null && ChannelServer.getInstance(channel) != null) {
            c.setWorld(server);
            c.setChannel(channel);
            //this shit aint needed. c.sendPacket(LoginPacket.getSecondAuthSuccess(c));
            c.sendPacket(LoginPacket.getCharList(c.getSecondPassword(), chars, c.getCharacterSlots()));
        } else {
            c.getSocketChannel().close();
        }
	}

}
