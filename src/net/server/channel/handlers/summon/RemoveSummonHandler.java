package net.server.channel.handlers.summon;

import client.MapleBuffStat;
import client.MapleCharacter;
import client.MapleClient;
import net.AbstractMaplePacketHandler;
import net.RecvPacketOpcode;
import server.maps.MapleMapObject;
import server.maps.MapleMapObjectType;
import server.maps.MapleSummon;
import tools.data.LittleEndianAccessor;
import tools.packet.CField.SummonPacket;

public class RemoveSummonHandler extends AbstractMaplePacketHandler {

	public RemoveSummonHandler(RecvPacketOpcode recv) {
		super(recv);
	}

	@Override
	public void handlePacket(LittleEndianAccessor lea, MapleClient c, MapleCharacter chr) {
		final MapleMapObject obj = c.getPlayer().getMap().getMapObject(lea.readInt(), MapleMapObjectType.SUMMON);
        if (obj == null || !(obj instanceof MapleSummon)) {
            return;
        }
        final MapleSummon summon = (MapleSummon) obj;
        if (summon.getOwnerId() != c.getPlayer().getId() || summon.getSkillLevel() <= 0) {
            c.getPlayer().dropMessage(5, "Error.");
            return;
        }
        if (summon.getSkill() == 35111002 || summon.getSkill() == 35121010) { //rock n shock, amp
            return;
        }
        c.getPlayer().getMap().broadcastMessage(SummonPacket.removeSummon(summon, true));
        c.getPlayer().getMap().removeMapObject(summon);
        c.getPlayer().removeVisibleMapObject(summon);
        c.getPlayer().removeSummon(summon);
        if (summon.getSkill() != 35121011) {
            c.getPlayer().cancelEffectFromBuffStat(MapleBuffStat.SUMMON);
            //TODO: Multi Summoning, must do something about hack buffstat
        }
	}

}
