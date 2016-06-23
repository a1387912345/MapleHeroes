package net.server.handler;

import client.MapleClient;
import client.character.MapleCharacter;
import net.MaplePacketHandler;
import net.RecvPacketOpcode;
import net.netty.MaplePacketReader;

public class ClientErrorHandler extends MaplePacketHandler {

	public ClientErrorHandler(RecvPacketOpcode recv) {
		super(recv);
	}

	@Override
	public void handlePacket(MaplePacketReader in, MapleClient client, MapleCharacter chr) {
		String crashInfo = in.readMapleAsciiString();
		System.out.println(crashInfo);
	}
	
	@Override
    public boolean validateState(final MapleClient client) {
        return true;
    }

}
