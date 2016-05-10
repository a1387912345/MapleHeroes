package net.server.farm.handlers;

import client.MapleCharacter;
import client.MapleClient;
import net.AbstractMaplePacketHandler;
import net.RecvPacketOpcode;
import net.channel.ChannelServer;
import net.login.LoginServer;
import net.server.farm.FarmServer;
import net.world.CharacterTransfer;
import net.world.World;
import tools.data.LittleEndianAccessor;
import tools.packet.CField;

public class LeaveFarmHandler extends AbstractMaplePacketHandler {

	public LeaveFarmHandler(RecvPacketOpcode recv) {
		super(recv);
	}

	@Override
	public void handlePacket(LittleEndianAccessor lea, MapleClient c, MapleCharacter chr) {
		FarmServer.getPlayerStorage().deregisterPlayer(chr);
        c.updateLoginState(MapleClient.LOGIN_SERVER_TRANSITION, c.getSessionIPAddress());
        try {
            World.ChannelChange_Data(new CharacterTransfer(chr), chr.getId(), c.getChannel());
            c.getSession().write(CField.getChannelChange(c, Integer.parseInt(ChannelServer.getInstance(c.getChannel()).getIP().split(":")[1])));
        } finally {
            final String s = c.getSessionIPAddress();
            LoginServer.addIPAuth(s.substring(s.indexOf('/') + 1, s.length()));
            chr.saveToDB(false, true);
            c.setPlayer(null);
            c.setReceiving(false);
            c.getSession().close();
        }
	}

}
