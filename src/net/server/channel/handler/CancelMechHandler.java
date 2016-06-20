package net.server.channel.handler;

import client.MapleClient;
import client.Skill;
import client.SkillFactory;
import client.character.MapleCharacter;
import net.MaplePacketHandler;
import net.RecvPacketOpcode;
import net.netty.MaplePacketReader;
import net.packet.CField;

public class CancelMechHandler extends MaplePacketHandler {

	public CancelMechHandler(RecvPacketOpcode recv) {
		super(recv);
	}

	@Override
	public void handlePacket(MaplePacketReader mpr, MapleClient c, MapleCharacter chr) {
		if (chr == null) {
            return;
        }
        int sourceid = mpr.readInt();
        if ((sourceid % 10000 < 1000) && (SkillFactory.getSkill(sourceid) == null)) {
            sourceid += 1000;
        }
        Skill skill = SkillFactory.getSkill(sourceid);
        if (skill == null) {
            return;
        }
        if (skill.isChargeSkill()) {
            chr.setKeyDownSkill_Time(0L);
            chr.getMap().broadcastMessage(chr, CField.skillCancel(chr, sourceid), false);
        } else {
           
            chr.cancelEffect(skill.getEffect(mpr.readByte()), false, -1L);
        }
	}

}
