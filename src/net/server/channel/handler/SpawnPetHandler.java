package net.server.channel.handler;

import client.MapleClient;
import client.character.MapleCharacter;
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
