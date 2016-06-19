package net.server.farm.handler;

import client.MapleClient;
import client.character.MapleCharacter;
import net.MaplePacketHandler;
import net.RecvPacketOpcode;
import net.netty.MaplePacketReader;
import net.packet.FarmPacket;

public class FarmFirstEntryHandler extends MaplePacketHandler {

	public FarmFirstEntryHandler(RecvPacketOpcode recv) {
		super(recv);
	}

	@Override
	public void handlePacket(MaplePacketReader lea, MapleClient c, MapleCharacter chr) {
		//give random waru consume item
        c.sendPacket(FarmPacket.farmMessage("Find your reward for logging in today \r\nin your inventory."));
	}

}
