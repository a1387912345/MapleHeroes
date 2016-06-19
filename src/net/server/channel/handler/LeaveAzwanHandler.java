package net.server.channel.handler;

import client.MapleClient;
import client.character.MapleCharacter;
import net.MaplePacketHandler;
import net.RecvPacketOpcode;
import net.netty.MaplePacketReader;
import net.packet.CField;
import net.packet.CWvsContext;

public class LeaveAzwanHandler extends MaplePacketHandler {

	public LeaveAzwanHandler(RecvPacketOpcode recv) {
		super(recv);
	}

	@Override
	public void handlePacket(MaplePacketReader lea, MapleClient c, MapleCharacter chr) {
		if (c.getCharacter() == null || c.getCharacter().getMap() == null || !c.getCharacter().inAzwan()) {
            c.sendPacket(CField.pvpBlocked(6));
            c.sendPacket(CWvsContext.enableActions());
            return;
        }
        //c.getPlayer().cancelAllBuffs();
        //c.getPlayer().changeRemoval();
        //c.getPlayer().dispelDebuffs();
        //c.getPlayer().clearAllCooldowns();
        //c.sendPacket(CWvsContext.clearMidMsg());
        //c.getPlayer().changeMap(c.getChannelServer().getMapFactory().getMap(262000200));
        c.sendPacket(CField.showEffect("hillah/fail"));
        c.sendPacket(CField.UIPacket.sendAzwanResult());
        //c.getPlayer().getStats().recalcLocalStats(c.getPlayer());
        //c.getPlayer().getStats().heal(c.getPlayer());
        c.sendPacket(CField.showEffect("hillah/fail"));  // Old handler sends these a second time
        c.sendPacket(CField.UIPacket.sendAzwanResult());
	}

}
