package net.server.channel.handler;

import client.MapleClient;
import client.character.MapleCharacter;
import client.character.PlayerStats;
import constants.GameConstants;
import net.MaplePacketHandler;
import net.RecvPacketOpcode;
import net.netty.MaplePacketReader;

public class HealOverTimeHandler extends MaplePacketHandler {

	public HealOverTimeHandler(RecvPacketOpcode recv) {
		super(recv);
	}

	@Override
	public void handlePacket(final MaplePacketReader mpr, final MapleClient c, final MapleCharacter chr) {
		if (chr == null) {
            return;
        }
        chr.updateTick(mpr.readInt());
        if (mpr.available() >= 8L) {
            mpr.skip(mpr.available() >= 12L ? 8 : 4);
        }
        int healHP = mpr.readShort();
        int healMP = mpr.readShort();

        PlayerStats stats = chr.getStat();

        if (stats.getHp() <= 0) {
            return;
        }
        long now = System.currentTimeMillis();
        if ((healHP != 0) && (chr.canHP(now + 1000L))) {
            if (healHP > stats.getHealHP()) {
                healHP = (int) stats.getHealHP();
            }
            chr.addHP(healHP);
        }
        if ((healMP != 0) && (!GameConstants.isDemonSlayer(chr.getJob())) && (chr.canMP(now + 1000L))) {
            if (healMP > stats.getHealMP()) {
                healMP = (int) stats.getHealMP();
            }
            chr.addMP(healMP);
        }
	}

}
