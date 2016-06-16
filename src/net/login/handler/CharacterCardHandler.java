package net.login.handler;

import java.util.LinkedHashMap;
import java.util.Map;

import client.MapleCharacter;
import client.MapleClient;
import constants.WorldConstants.WorldOption;
import net.MaplePacketHandler;
import net.RecvPacketOpcode;
import net.channel.ChannelServer;
import net.netty.MaplePacketReader;

public class CharacterCardHandler extends MaplePacketHandler {

	public CharacterCardHandler(RecvPacketOpcode recv) {
		super(recv);
	}

	@Override
	public void handlePacket(MaplePacketReader lea, MapleClient c, MapleCharacter chr) {
		if (lea.available() != 36 || !c.isLoggedIn()) {
            c.getSocketChannel().close();
            return;
        }
        final Map<Integer, Integer> cids = new LinkedHashMap<>();
        for (int i = 1; i <= 6; i++) { // 6 chars
            final int charId = lea.readInt();
            if ((!c.login_Auth(charId) && charId != 0) || ChannelServer.getInstance(c.getChannel()) == null || !WorldOption.isExists(c.getWorld())) {
                c.getSocketChannel().close();
                return;
            }
            cids.put(i, charId);
        }
        c.updateCharacterCards(cids);
	}

}
