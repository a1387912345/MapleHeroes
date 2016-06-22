package net.server.channel.handler.summon;

import client.MapleBuffStat;
import client.MapleClient;
import client.character.MapleCharacter;
import net.MaplePacketHandler;
import net.RecvPacketOpcode;
import net.netty.MaplePacketReader;
import net.packet.CField.SummonPacket;
import server.maps.MapleMapObject;
import server.maps.MapleMapObjectType;
import server.maps.MapleSummon;

public class RemoveSummonHandler extends MaplePacketHandler {

	public RemoveSummonHandler(RecvPacketOpcode recv) {
		super(recv);
	}

	@Override
	public void handlePacket(MaplePacketReader mpr, MapleClient c, MapleCharacter chr) {
		final MapleMapObject obj = c.getCharacter().getMap().getMapObject(mpr.readInt(), MapleMapObjectType.SUMMON);
        if (obj == null || !(obj instanceof MapleSummon)) {
            return;
        }
        final MapleSummon summon = (MapleSummon) obj;
        if (summon.getOwnerId() != c.getCharacter().getID() || summon.getSkillLevel() <= 0) {
            c.getCharacter().dropMessage(5, "Error.");
            return;
        }
        if (summon.getSkill() == 35111002 || summon.getSkill() == 35121010) { //rock n shock, amp
            return;
        }
        c.getCharacter().getMap().broadcastMessage(SummonPacket.removeSummon(summon, true));
        c.getCharacter().getMap().removeMapObject(summon);
        c.getCharacter().removeVisibleMapObject(summon);
        c.getCharacter().removeSummon(summon);
        if (summon.getSkill() != 35121011) {
            c.getCharacter().cancelEffectFromBuffStat(MapleBuffStat.SUMMON);
            //TODO: Multi Summoning, must do something about hack buffstat
        }
	}

}
