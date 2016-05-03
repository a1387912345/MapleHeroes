package server.skills;

import java.util.Map;

import client.MapleBuffStat;
import constants.GameConstants;
import net.SendPacketOpcode;
import server.AbstractSkillHandler;
import tools.data.MaplePacketLittleEndianWriter;
import tools.packet.PacketHelper;

public class AdvancedBlessSkill extends AbstractSkillHandler {
	
	@Override
	public byte[] giveBuff(int buffid, int bufflength, Map<MapleBuffStat, Integer> statups) {
		System.out.println("Trying for advanced bless handler");
		MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
    	
    	mplew.writeShort(SendPacketOpcode.GIVE_BUFF.getValue());
    	PacketHelper.writeBuffMask(mplew, statups);

        for (Map.Entry<MapleBuffStat, Integer> stat : statups.entrySet()) {
            if (!stat.getKey().canStack()) {
                if (GameConstants.isSpecialBuff(buffid)) {
                    mplew.writeInt(stat.getValue());
                } else {
                    mplew.writeShort(stat.getValue());
                }
                mplew.writeInt(buffid);
                mplew.writeInt(bufflength);
            }
        }

        mplew.writeZeroBytes(13);
        
        for (Map.Entry<MapleBuffStat, Integer> stat : statups.entrySet()) {
            if (stat.getKey().canStack()) {
                mplew.writeInt(1); // stacks size
                mplew.writeInt(buffid);
                mplew.writeInt(stat.getValue());
                mplew.writeInt((int) (System.currentTimeMillis() % 1000000000)); // ?
                mplew.writeInt(1); 
                mplew.writeInt(bufflength);
            }
        } 

        mplew.writeLong(0);
        mplew.write(0);
        
    	return mplew.getPacket();
	}

}
