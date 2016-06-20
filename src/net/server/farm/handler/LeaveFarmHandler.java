package net.server.farm.handler;

import client.MapleClient;
import client.character.MapleCharacter;
import net.MaplePacketHandler;
import net.RecvPacketOpcode;
import net.netty.MaplePacketReader;
import net.packet.CField;
import net.server.channel.ChannelServer;
import net.server.farm.FarmServer;
import net.server.login.LoginServer;
import net.world.CharacterTransfer;
import net.world.World;

public class LeaveFarmHandler extends MaplePacketHandler {

	public LeaveFarmHandler(RecvPacketOpcode recv) {
		super(recv);
	}

	@Override
	public void handlePacket(MaplePacketReader mpr, MapleClient c, MapleCharacter chr) {
		FarmServer.getPlayerStorage().deregisterPlayer(chr);
        c.updateLoginState(MapleClient.LOGIN_SERVER_TRANSITION, c.getSessionIPAddress());
        try {
            World.ChannelChange_Data(new CharacterTransfer(chr), chr.getID(), c.getChannel());
            c.sendPacket(CField.getChannelChange(c, Integer.parseInt(ChannelServer.getInstance(c.getChannel()).getIP().split(":")[1])));
        } finally {
            final String s = c.getSessionIPAddress();
            LoginServer.getInstance().addIPAuth(s.substring(s.indexOf('/') + 1, s.length()));
            chr.saveToDB(false, true);
            c.setCharacter(null);
            c.setReceiving(false);
            c.close();
        }
	}

}
