package net.server.channel.handlers;

import client.MapleCharacter;
import client.MapleClient;
import net.AbstractMaplePacketHandler;
import net.RecvPacketOpcode;
import net.world.MaplePartyCharacter;
import tools.data.LittleEndianAccessor;
import tools.packet.CField;
import tools.packet.CWvsContext;

public class EnterAzwanHandler extends AbstractMaplePacketHandler {

	public EnterAzwanHandler(RecvPacketOpcode recv) {
		super(recv);
	}

	@Override
	public void handlePacket(LittleEndianAccessor lea, MapleClient c, MapleCharacter chr) {
		if (c.getPlayer() == null || c.getPlayer().getMap() == null || c.getPlayer().getMapId() != 262000300) {
            c.getSession().write(CField.pvpBlocked(1));
            c.getSession().write(CWvsContext.enableActions());
            return;
        }
        if (c.getPlayer().getLevel() < 40) {
            c.getSession().write(CField.pvpBlocked(1));
            c.getSession().write(CWvsContext.enableActions());
            return;
        }
        byte mode = lea.readByte();
        byte difficult = lea.readByte();
        byte party = lea.readByte();
        int mapid = 262020000 + (mode * 1000) + difficult; //Supply doesn't have difficult but it's always 0 so idc
        if (party == 1 && c.getPlayer().getParty() == null) {
            c.getSession().write(CField.pvpBlocked(9));
            c.getSession().write(CWvsContext.enableActions());
        }
        if (party == 1 && c.getPlayer().getParty() != null) {
            for (MaplePartyCharacter partymembers : c.getPlayer().getParty().getMembers()) {
                if (c.getChannelServer().getPlayerStorage().getCharacterById(partymembers.getId()).getMapId() != 262000300) {
                    c.getPlayer().dropMessage(1, "Please make sure all of your party members are in the same map.");
                    c.getSession().write(CWvsContext.enableActions());
                }
            }
        }
        if (party == 1 && c.getPlayer().getParty() != null) {
            for (MaplePartyCharacter partymember : c.getPlayer().getParty().getMembers()) {
                c.getChannelServer().getPlayerStorage().getCharacterById(partymember.getId()).changeMap(c.getChannelServer().getMapFactory().getMap(mapid));
            }
        } else {
            //party = 0;
            c.getPlayer().changeMap(c.getChannelServer().getMapFactory().getMap(mapid));
        }
        //EventManager em = c.getChannelServer().getEventSM().getEventManager("Azwan");
        //EventInstanceManager eim = em.newInstance("Azwan");
        //eim.setProperty("Global_StartMap", mapid + "");
        //eim.setProperty("Global_ExitMap", (party == 1 ? 262000100 : 262000200) + "");
        //eim.setProperty("Global_MinPerson", 1 + "");
        //eim.setProperty("Global_RewardMap", (party == 1 ? 262000100 : 262000200) + "");
        //eim.setProperty("CurrentStage", "1");
	}

}
