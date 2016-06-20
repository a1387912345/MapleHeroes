package net.server.channel.handler;

import java.awt.Point;

import client.MapleClient;
import client.SkillFactory;
import client.character.MapleCharacter;
import net.MaplePacketHandler;
import net.RecvPacketOpcode;
import net.netty.MaplePacketReader;
import server.maps.MapleMapObject;
import server.maps.MapleMapObjectType;
import server.maps.MapleSummon;
import server.maps.SummonMovementType;

public class HollyHandler extends MaplePacketHandler {

	public HollyHandler(RecvPacketOpcode recv) {
		super(recv);
	}

	@Override
	public void handlePacket(MaplePacketReader mpr, MapleClient c, MapleCharacter chr) {
		final MapleMapObject obj = c.getCharacter().getMap().getMapObject(mpr.readInt(), MapleMapObjectType.SUMMON);
        int skillid = mpr.readInt();
        if (skillid == 3121013) {
            final MapleSummon sum = (MapleSummon) obj;
            Point poss = c.getCharacter().getPosition();
                    final MapleSummon tosummon = new MapleSummon(c.getCharacter(), SkillFactory.getSkill(3121013).getEffect(sum.getSkillLevel()), new Point(sum.getTruePosition().x, sum.getTruePosition().y), SummonMovementType.STATIONARY);
                    c.getCharacter().getMap().spawnSummon(tosummon);
                    c.getCharacter().addSummon(tosummon);
            return;
        }
        int HP = SkillFactory.getSkill(3121013).getEffect(c.getCharacter().getSkillLevel(3121013)).getX();
        int hp = c.getCharacter().getStat().getMaxHp() * HP / 100;
        c.getCharacter().addHP(hp);
	}

}
