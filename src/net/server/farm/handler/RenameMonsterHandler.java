package net.server.farm.handler;

import client.MapleClient;
import client.character.MapleCharacter;
import net.MaplePacketHandler;
import net.RecvPacketOpcode;
import net.netty.MaplePacketReader;
import net.packet.FarmPacket;

public class RenameMonsterHandler extends MaplePacketHandler {

	public RenameMonsterHandler(RecvPacketOpcode recv) {
		super(recv);
	}

	@Override
	public void handlePacket(MaplePacketReader mpr, MapleClient c, MapleCharacter chr) {
		int monsterIndex = mpr.readInt();
        String name = mpr.readMapleAsciiString();
        //c.getFarm().getMonster(monsterIndex).setName(name);
        c.sendPacket(FarmPacket.renameMonster(monsterIndex, name));
	}

}
