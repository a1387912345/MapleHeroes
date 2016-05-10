package net.server.channel.handlers.summon;

import java.util.Iterator;

import client.MapleBuffStat;
import client.MapleCharacter;
import client.MapleClient;
import net.AbstractMaplePacketHandler;
import net.RecvPacketOpcode;
import server.maps.MapleSummon;
import tools.data.LittleEndianAccessor;
import tools.packet.CField.SummonPacket;

public class DamageSummonHandler extends AbstractMaplePacketHandler {

	public DamageSummonHandler(RecvPacketOpcode recv) {
		super(recv);
	}

	@Override
	public void handlePacket(final LittleEndianAccessor lea, final MapleClient c, final MapleCharacter chr) {
		lea.readInt();
		final int unkByte = lea.readByte();
        final int damage = lea.readInt();
        final int monsterIdFrom = lea.readInt();
        //       slea.readByte(); // stance

        final Iterator<MapleSummon> iter = chr.getSummonsReadLock().iterator();
        MapleSummon summon;
        boolean remove = false;
        try {
            while (iter.hasNext()) {
                summon = iter.next();
                if (summon.isPuppet() && summon.getOwnerId() == chr.getId() && damage > 0) { //We can only have one puppet(AFAIK O.O) so this check is safe.
                    summon.addHP((short) -damage);
                    if (summon.getHP() <= 0) {
                        remove = true;
                    }
                    chr.getMap().broadcastMessage(chr, SummonPacket.damageSummon(chr.getId(), summon.getSkill(), damage, unkByte, monsterIdFrom), summon.getTruePosition());
                    break;
                }
            }
        } finally {
            chr.unlockSummonsReadLock();
        }
        if (remove) {
            chr.cancelEffectFromBuffStat(MapleBuffStat.PUPPET);
        }
	}

}
