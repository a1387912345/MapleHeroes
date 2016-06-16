package net.channel.handler;

import client.MapleCharacter;
import client.MapleClient;
import net.MaplePacketHandler;
import net.RecvPacketOpcode;
import net.netty.MaplePacketReader;

public class SpawnPetHandler extends MaplePacketHandler {

	public SpawnPetHandler(RecvPacketOpcode recv) {
		super(recv);
	}

	@Override
	public void handlePacket(MaplePacketReader lea, MapleClient c, MapleCharacter chr) {
		chr.updateTick(lea.readInt());
        chr.spawnPet(lea.readByte(), lea.readByte() > 0);
	}

}
