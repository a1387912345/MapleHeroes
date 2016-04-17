package net.server.channel.handlers;

import client.MapleCharacter;
import client.MapleClient;
import net.AbstractMaplePacketHandler;
import net.RecvPacketOpcode;
import net.SendPacketOpcode;
import tools.data.LittleEndianAccessor;
import tools.data.MaplePacketLittleEndianWriter;

public class NPCAnimation extends AbstractMaplePacketHandler {

	public NPCAnimation(RecvPacketOpcode recv) {
		super(recv);
	}

	@Override
	public void handlePacket(LittleEndianAccessor lea, MapleClient c, MapleCharacter chr) {
		MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.NPC_ACTION.getValue());
        int length = (int) lea.available();
        if (length == 10) {
            mplew.writeInt(lea.readInt());
            mplew.writeShort(lea.readShort());
            mplew.writeInt(lea.readInt());
        } else if (length > 10) {
            //mplew.write(slea.read(length - 9));
        	mplew.write(lea.read(length));
        	mplew.write(0);
        } else {
            return;
        }
        
        c.getSession().write(mplew.getPacket());
	}

}
