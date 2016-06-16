package net.farm.handler;

import client.MapleCharacter;
import client.MapleClient;
import net.MaplePacketHandler;
import net.RecvPacketOpcode;
import net.netty.MaplePacketReader;
import net.packet.FarmPacket;

public class RenameMonsterHandler extends MaplePacketHandler {

	public RenameMonsterHandler(RecvPacketOpcode recv) {
		super(recv);
	}

	@Override
	public void handlePacket(MaplePacketReader lea, MapleClient c, MapleCharacter chr) {
		int monsterIndex = lea.readInt();
        String name = lea.readMapleAsciiString();
        //c.getFarm().getMonster(monsterIndex).setName(name);
        c.sendPacket(FarmPacket.renameMonster(monsterIndex, name));
	}

}
