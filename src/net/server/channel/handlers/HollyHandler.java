package net.server.channel.handlers;

import java.awt.Point;

import client.MapleCharacter;
import client.MapleClient;
import client.SkillFactory;
import net.AbstractMaplePacketHandler;
import net.RecvPacketOpcode;
import server.maps.MapleMapObject;
import server.maps.MapleMapObjectType;
import server.maps.MapleSummon;
import server.maps.SummonMovementType;
import tools.data.LittleEndianAccessor;

public class HollyHandler extends AbstractMaplePacketHandler {

	public HollyHandler(RecvPacketOpcode recv) {
		super(recv);
	}

	@Override
	public void handlePacket(LittleEndianAccessor lea, MapleClient c, MapleCharacter chr) {
		final MapleMapObject obj = c.getPlayer().getMap().getMapObject(lea.readInt(), MapleMapObjectType.SUMMON);
        int skillid = lea.readInt();
        if (skillid == 3121013) {
            final MapleSummon sum = (MapleSummon) obj;
            Point poss = c.getPlayer().getPosition();
                    final MapleSummon tosummon = new MapleSummon(c.getPlayer(), SkillFactory.getSkill(3121013).getEffect(sum.getSkillLevel()), new Point(sum.getTruePosition().x, sum.getTruePosition().y), SummonMovementType.STATIONARY);
                    c.getPlayer().getMap().spawnSummon(tosummon);
                    c.getPlayer().addSummon(tosummon);
            return;
        }
        int HP = SkillFactory.getSkill(3121013).getEffect(c.getPlayer().getSkillLevel(3121013)).getX();
        int hp = c.getPlayer().getStat().getMaxHp() * HP / 100;
        c.getPlayer().addHP(hp);
	}

}
