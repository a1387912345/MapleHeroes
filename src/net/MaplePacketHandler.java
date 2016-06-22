package net;

import client.MapleClient;
import client.character.MapleCharacter;
import net.netty.MaplePacketReader;

public abstract class MaplePacketHandler {
	
	protected RecvPacketOpcode recv;
	
	public MaplePacketHandler(RecvPacketOpcode recv) {
		this.recv = recv;
	}

	public abstract void handlePacket(MaplePacketReader inPacket, MapleClient client, MapleCharacter chr);
	
	public boolean validateState(final MapleClient client) {
        return client.isLoggedIn();
    }

	public RecvPacketOpcode getRecvOpcode() {
    	return recv;
    }
}