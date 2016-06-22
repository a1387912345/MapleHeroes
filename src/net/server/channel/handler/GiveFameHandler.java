package net.server.channel.handler;

import client.MapleClient;
import client.MapleStat;
import client.character.MapleCharacter;
import net.MaplePacketHandler;
import net.RecvPacketOpcode;
import net.netty.MaplePacketReader;
import net.packet.CWvsContext;

public class GiveFameHandler extends MaplePacketHandler {

	public GiveFameHandler(RecvPacketOpcode recv) {
		super(recv);
	}

	@Override
	public void handlePacket(MaplePacketReader mpr, MapleClient c, MapleCharacter chr) {
		final int who = mpr.readInt();
        final int mode = mpr.readByte();

        final int famechange = mode == 0 ? -1 : 1;
        final MapleCharacter target = chr.getMap().getCharacterById(who);

        if (target == null || target == chr) { // faming self
            c.sendPacket(CWvsContext.giveFameErrorResponse(1));
            return;
        } else if (chr.getLevel() < 15) {
            c.sendPacket(CWvsContext.giveFameErrorResponse(2));
            return;
        }
        switch (chr.canGiveFame(target)) {
            case OK:
                if (Math.abs(target.getFame() + famechange) <= 99999) {
                    target.addFame(famechange);
                    target.updateSingleStat(MapleStat.FAME, target.getFame());
                }
                if (!chr.isGM()) {
                    chr.hasGivenFame(target);
                }
                c.sendPacket(CWvsContext.OnFameResult(0, target.getName(), famechange == 1, target.getFame()));
                target.getClient().sendPacket(CWvsContext.OnFameResult(5, chr.getName(), famechange == 1, 0));
                break;
            case NOT_TODAY:
                c.sendPacket(CWvsContext.giveFameErrorResponse(3));
                break;
            case NOT_THIS_MONTH:
                c.sendPacket(CWvsContext.giveFameErrorResponse(4));
                break;
        }
	}

}
