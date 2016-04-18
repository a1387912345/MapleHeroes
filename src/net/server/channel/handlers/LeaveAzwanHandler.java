package net.server.channel.handlers;

import client.MapleCharacter;
import client.MapleClient;
import net.AbstractMaplePacketHandler;
import net.RecvPacketOpcode;
import tools.data.LittleEndianAccessor;
import tools.packet.CField;
import tools.packet.CWvsContext;

public class LeaveAzwanHandler extends AbstractMaplePacketHandler {

	public LeaveAzwanHandler(RecvPacketOpcode recv) {
		super(recv);
	}

	@Override
	public void handlePacket(LittleEndianAccessor lea, MapleClient c, MapleCharacter chr) {
		if (c.getPlayer() == null || c.getPlayer().getMap() == null || !c.getPlayer().inAzwan()) {
            c.getSession().write(CField.pvpBlocked(6));
            c.getSession().write(CWvsContext.enableActions());
            return;
        }
        //c.getPlayer().cancelAllBuffs();
        //c.getPlayer().changeRemoval();
        //c.getPlayer().dispelDebuffs();
        //c.getPlayer().clearAllCooldowns();
        //c.getSession().write(CWvsContext.clearMidMsg());
        //c.getPlayer().changeMap(c.getChannelServer().getMapFactory().getMap(262000200));
        c.getSession().write(CField.showEffect("hillah/fail"));
        c.getSession().write(CField.UIPacket.sendAzwanResult());
        //c.getPlayer().getStats().recalcLocalStats(c.getPlayer());
        //c.getPlayer().getStats().heal(c.getPlayer());
	}

}
