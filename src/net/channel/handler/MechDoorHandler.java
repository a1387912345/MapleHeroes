package net.channel.handler;

import java.awt.Point;

import client.MapleCharacter;
import client.MapleClient;
import net.MaplePacketHandler;
import net.RecvPacketOpcode;
import net.netty.MaplePacketReader;
import net.packet.CWvsContext;
import server.maps.MapleMapObject;
import server.maps.MechDoor;

public class MechDoorHandler extends MaplePacketHandler {

	public MechDoorHandler(RecvPacketOpcode recv) {
		super(recv);
	}

	@Override
	public void handlePacket(MaplePacketReader lea, MapleClient c, MapleCharacter chr) {
		final int oid = lea.readInt();
        final Point pos = lea.readPos();
        final int mode = lea.readByte(); // specifies if backwarp or not, 1 town to target, 0 target to town
        chr.getClient().sendPacket(CWvsContext.enableActions());
        for (MapleMapObject obj : chr.getMap().getAllMechDoorsThreadsafe()) {
            final MechDoor door = (MechDoor) obj;
            if (door.getOwnerId() == oid && door.getId() == mode) {
                chr.checkFollow();
                chr.getMap().movePlayer(chr, pos);
                break;
            }
        }
	}

}