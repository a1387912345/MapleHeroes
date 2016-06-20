package net.server.channel.handler.stat;

import client.MapleClient;
import client.Skill;
import client.SkillFactory;
import client.character.MapleCharacter;
import constants.GameConstants;
import net.MaplePacketHandler;
import net.RecvPacketOpcode;
import net.netty.MaplePacketReader;
import net.packet.CWvsContext;

public class DistributeHyperHandler extends MaplePacketHandler {

	public DistributeHyperHandler(RecvPacketOpcode recv) {
		super(recv);
	}

	@Override
	public void handlePacket(MaplePacketReader mpr, MapleClient c, MapleCharacter chr) {
		chr.updateTick(mpr.readInt());
        int skillid = mpr.readInt();
        final Skill skill = SkillFactory.getSkill(skillid);
        final int remainingSp = chr.getRemainingHSp(skill.getHyper() - 1);

        final int maxlevel = 1;
        final int curLevel = chr.getSkillLevel(skill);

        if (skill.isInvisible() && chr.getSkillLevel(skill) == 0) {
            if (maxlevel <= 0) {
                c.sendPacket(CWvsContext.enableActions());
                //AutobanManager.getInstance().addPoints(c, 1000, 0, "Illegal distribution of SP to invisible skills (" + skillid + ")");
                return;
            }
        }

        for (int i : GameConstants.blockedSkills) {
            if (skill.getId() == i) {
                c.sendPacket(CWvsContext.enableActions());
                chr.dropMessage(1, "This skill has been blocked and may not be added.");
                return;
            }
        }

        if ((remainingSp >= 1 && curLevel == 0) && skill.canBeLearnedBy(chr.getJob())) {
            chr.setRemainingHSp(skill.getHyper() - 1, remainingSp - 1);
            chr.changeSingleSkillLevel(skill, (byte) 1, (byte) 1, -1L, true);
        } else {
            c.sendPacket(CWvsContext.enableActions());
        }
	}

}
