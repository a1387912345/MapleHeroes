package net.server.channel.handler;

import client.MapleClient;
import client.character.MapleCharacter;
import net.MaplePacketHandler;
import net.RecvPacketOpcode;
import net.SendPacketOpcode;
import net.netty.MaplePacketReader;
import net.netty.MaplePacketWriter;

public class NPCAnimationHandler extends MaplePacketHandler {

	public NPCAnimationHandler(RecvPacketOpcode recv) {
		super(recv);
	}

	@Override
	public void handlePacket(MaplePacketReader inPacket, MapleClient c, MapleCharacter chr) {
		MaplePacketWriter outPacket = new MaplePacketWriter(SendPacketOpcode.NPC_ACTION);
        int length = (int) inPacket.available();
        if (length == 10) {
            outPacket.writeInt(inPacket.readInt());
            outPacket.writeShort(inPacket.readShort());
            outPacket.writeInt(inPacket.readInt());
        } else if (length > 10) {
            //mpw.write(inPacket.read(length - 9));
        	outPacket.write(inPacket.read(length));
        	outPacket.write(0);
        } else {
            return;
        }
        
        c.sendPacket(outPacket.getPacket());
	}

}
