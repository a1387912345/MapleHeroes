package net.server.channel.handler;

import client.MapleClient;
import client.character.MapleCharacter;
import net.MaplePacketHandler;
import net.RecvPacketOpcode;
import net.netty.MaplePacketReader;
import net.packet.CField;
import net.packet.CWvsContext;
import net.world.MaplePartyCharacter;

public class EnterAzwanHandler extends MaplePacketHandler {

	public EnterAzwanHandler(RecvPacketOpcode recv) {
		super(recv);
	}

	@Override
	public void handlePacket(MaplePacketReader lea, MapleClient c, MapleCharacter chr) {
		if (c.getCharacter() == null || c.getCharacter().getMap() == null || c.getCharacter().getMapId() != 262000300) {
            c.sendPacket(CField.pvpBlocked(1));
            c.sendPacket(CWvsContext.enableActions());
            return;
        }
        if (c.getCharacter().getLevel() < 40) {
            c.sendPacket(CField.pvpBlocked(1));
            c.sendPacket(CWvsContext.enableActions());
            return;
        }
        byte mode = lea.readByte();
        byte difficult = lea.readByte();
        byte party = lea.readByte();
        int mapid = 262020000 + (mode * 1000) + difficult; //Supply doesn't have difficult but it's always 0 so idc
        if (party == 1 && c.getCharacter().getParty() == null) {
            c.sendPacket(CField.pvpBlocked(9));
            c.sendPacket(CWvsContext.enableActions());
        }
        if (party == 1 && c.getCharacter().getParty() != null) {
            for (MaplePartyCharacter partymembers : c.getCharacter().getParty().getMembers()) {
                if (c.getChannelServer().getPlayerStorage().getCharacterById(partymembers.getId()).getMapId() != 262000300) {
                    c.getCharacter().dropMessage(1, "Please make sure all of your party members are in the same map.");
                    c.sendPacket(CWvsContext.enableActions());
                }
            }
        }
        if (party == 1 && c.getCharacter().getParty() != null) {
            for (MaplePartyCharacter partymember : c.getCharacter().getParty().getMembers()) {
                c.getChannelServer().getPlayerStorage().getCharacterById(partymember.getId()).changeMap(c.getChannelServer().getMapFactory().getMap(mapid));
            }
        } else {
            //party = 0;
            c.getCharacter().changeMap(c.getChannelServer().getMapFactory().getMap(mapid));
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
