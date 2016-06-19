package net.server.login.handler;

import java.util.LinkedHashMap;
import java.util.Map;

import client.MapleClient;
import client.character.MapleCharacter;
import constants.WorldConstants.WorldOption;
import net.MaplePacketHandler;
import net.RecvPacketOpcode;
import net.netty.MaplePacketReader;
import net.server.channel.ChannelServer;

public class CharacterCardHandler extends MaplePacketHandler {

	public CharacterCardHandler(RecvPacketOpcode recv) {
		super(recv);
	}

	@Override
	public void handlePacket(MaplePacketReader mpr, MapleClient client, MapleCharacter chr) {
		if (mpr.available() != 36 || !client.isLoggedIn()) {
            client.close();
            return;
        }
        final Map<Integer, Integer> charIDs = new LinkedHashMap<>();
        for (int i = 1; i <= 6; i++) { // 6 chars
            final int charId = mpr.readInt();
            if ((!client.loginAuth(charId) && charId != 0) || ChannelServer.getInstance(client.getChannel()) == null || !WorldOption.isExists(client.getWorld())) {
                client.close();
                return;
            }
            charIDs.put(i, charId);
        }
        client.updateCharacterCards(charIDs);
	}

}
