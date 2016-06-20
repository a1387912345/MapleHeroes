package net.server.login.handler;

import client.MapleClient;
import client.PartTimeJob;
import client.character.MapleCharacter;
import net.MaplePacketHandler;
import net.RecvPacketOpcode;
import net.netty.MaplePacketReader;
import net.packet.LoginPacket;

public class PartTimeJobHandler extends MaplePacketHandler {

	public PartTimeJobHandler(RecvPacketOpcode recv) {
		super(recv);
	}

	@Override
	public void handlePacket(MaplePacketReader mpr, MapleClient c, MapleCharacter chr) {
		if (c.getCharacter() != null || !c.isLoggedIn()) {
            c.close();
            return;
        }
        final byte mode = mpr.readByte();
        final int cid = mpr.readInt();
        if (mode == 1) {
            final PartTimeJob partTime = MapleCharacter.getPartTime(cid);
            final byte job = mpr.readByte();
            if (/*chr.getLevel() < 30 || */job < 0 || job > 5 || partTime.getReward() > 0
                    || (partTime.getJob() > 0 && partTime.getJob() <= 5)) {
                c.close();
                return;
            }
            partTime.setTime(System.currentTimeMillis());
            partTime.setJob(job);
            c.sendPacket(LoginPacket.updatePartTimeJob(partTime));
            MapleCharacter.removePartTime(cid);
            MapleCharacter.addPartTime(partTime);
        } else if (mode == 2) {
            final PartTimeJob partTime = MapleCharacter.getPartTime(cid);
            if (/*chr.getLevel() < 30 || */partTime.getReward() > 0
                    || partTime.getJob() < 0 || partTime.getJob() > 5) {
                c.close();
                return;
            }
            final long distance = (System.currentTimeMillis() - partTime.getTime()) / (60 * 60 * 1000L);
            if (distance > 1) {
                partTime.setReward((int) (((partTime.getJob() + 1) * 1000L) + distance));
            } else {
                partTime.setJob((byte) 0);
                partTime.setReward(0);
            }
            partTime.setTime(System.currentTimeMillis());
            MapleCharacter.removePartTime(cid);
            MapleCharacter.addPartTime(partTime);
            c.sendPacket(LoginPacket.updatePartTimeJob(partTime));
        }
	}

}
