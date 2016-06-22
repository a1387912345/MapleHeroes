package net.server.channel.handler;

import java.awt.Point;
import java.util.List;

import client.MapleClient;
import client.character.MapleCharacter;
import constants.Skills;
import net.MaplePacketHandler;
import net.RecvPacketOpcode;
import net.netty.MaplePacketReader;
import net.packet.CField.EffectPacket;
import server.maps.MapleMist;

public class HolyFountainHandler extends MaplePacketHandler {

	public HolyFountainHandler(RecvPacketOpcode recv) {
		super(recv);
	}

	@Override
	public void handlePacket(final MaplePacketReader mpr, final MapleClient c, final MapleCharacter chr) {
		if (chr == null || !chr.isAlive()) {
			return;
		}
		
		List<MapleMist> mistsInMap = chr.getMap().getAllMistsThreadsafe();
		MapleMist holyFountain = null;
		
		for (MapleMist mist: mistsInMap) {
			if (mist.getSource().getSourceId() == Skills.Bishop.HOLY_FOUNTAIN) {
				holyFountain = mist;
			}
		}
		
		if (holyFountain == null) { // If the player is trying to use Holy Fountain without it existing.
			return;
		}
		
		mpr.readByte();
		final int timesUsed = mpr.readInt();
		final int skillId = mpr.readInt();
		final Point pos = mpr.readPos();
		
		System.out.println("Holy Fountain Used " + timesUsed);
		if (!holyFountain.getBox().contains(pos)) { // If the player is using Holy Fountain from a far distance.
			return;
		}
		
		chr.addHP(chr.getStat().getMaxHp() / 100 * holyFountain.getSource().getX());
		c.sendPacket(EffectPacket.showOwnBuffEffect(skillId, 3, chr.getLevel(), holyFountain.getSource().getLevel()));
		chr.getMap().broadcastMessage(EffectPacket.showBuffEffect(chr.getID(), skillId, 3, chr.getLevel(), holyFountain.getSource().getLevel()));
	}

}
