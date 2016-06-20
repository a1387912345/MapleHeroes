package net.server.channel.handler;

import client.MapleClient;
import client.character.MapleCharacter;
import net.MaplePacketHandler;
import net.RecvPacketOpcode;
import net.netty.MaplePacketReader;
import net.packet.CField;
import server.life.MapleMonster;

public class TouchFamiliarHandler extends MaplePacketHandler {

	public TouchFamiliarHandler(RecvPacketOpcode recv) {
		super(recv);
	}

	@Override
	public void handlePacket(MaplePacketReader mpr, MapleClient c, MapleCharacter chr) {
		if (chr.getSummonedFamiliar() == null) {
            return;
        }
        mpr.skip(6);
        byte unk = mpr.readByte();

        MapleMonster target = chr.getMap().getMonsterByOid(mpr.readInt());
        if (target == null) {
            return;
        }
        int type = mpr.readInt();
        mpr.skip(4);
        int damage = mpr.readInt();
        int maxDamage = chr.getSummonedFamiliar().getOriginalStats().getPhysicalAttack() * 5;
        if (damage < maxDamage) {
            damage = maxDamage;
        }
        if ((!target.getStats().isFriendly()) && (chr.getCheatTracker().checkFamiliarAttack(chr))) {
            chr.getMap().broadcastMessage(chr, CField.touchFamiliar(chr.getID(), unk, target.getObjectId(), type, 600, damage), chr.getTruePosition());
            target.damage(chr, damage, true);
            chr.getSummonedFamiliar().addFatigue(chr);
        }
	}

}
