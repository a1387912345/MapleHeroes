package net.server.channel.handlers;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import client.MapleCharacter;
import client.MapleClient;
import client.MapleStat;
import net.AbstractMaplePacketHandler;
import net.RecvPacketOpcode;
import tools.Pair;
import tools.data.LittleEndianAccessor;
import tools.packet.CField;

public class RespawnPVPHandler extends AbstractMaplePacketHandler {

	public RespawnPVPHandler(RecvPacketOpcode recv) {
		super(recv);
	}

	@Override
	public void handlePacket(LittleEndianAccessor lea, MapleClient c, MapleCharacter chr) {
		final Lock ThreadLock = new ReentrantLock();
	    /*if (c.getPlayer() == null || c.getPlayer().getMap() == null || !c.getPlayer().inPVP() || c.getPlayer().isAlive()) {
	     c.getSession().write(CWvsContext.enableActions());
	     return;
	     }*/
	    final int type = Integer.parseInt(c.getPlayer().getEventInstance().getProperty("type"));
	    byte lvl = 0;
	    c.getPlayer().getStat().heal_noUpdate(c.getPlayer());
	    c.getPlayer().updateSingleStat(MapleStat.MP, c.getPlayer().getStat().getMp());
	    //c.getPlayer().getEventInstance().schedule("broadcastType", 500);   
	    ThreadLock.lock();
	    try {
	        c.getPlayer().getEventInstance().schedule("updateScoreboard", 500);
	    } finally {
	        ThreadLock.unlock();
	    }
	    c.getPlayer().changeMap(c.getPlayer().getMap(), c.getPlayer().getMap().getPortal(type == 0 ? 0 : (type == 3 ? (c.getPlayer().getTeam() == 0 ? 3 : 1) : (c.getPlayer().getTeam() == 0 ? 2 : 3))));
	    c.getSession().write(CField.getPVPScore(Integer.parseInt(c.getPlayer().getEventInstance().getProperty(String.valueOf(c.getPlayer().getId()))), false));
	
	    if (c.getPlayer().getLevel() >= 30 && c.getPlayer().getLevel() < 70) {
	        lvl = 0;
	    } else if (c.getPlayer().getLevel() >= 70 && c.getPlayer().getLevel() < 120) {
	        lvl = 1;
	    } else if (c.getPlayer().getLevel() >= 120 && c.getPlayer().getLevel() < 180) {
	        lvl = 2;
	    } else if (c.getPlayer().getLevel() >= 180) {
	        lvl = 3;
	    }
	
	    List<MapleCharacter> players = c.getPlayer().getEventInstance().getPlayers();
	    List<Pair<Integer, String>> players1 = new LinkedList<>();
	    for (int xx = 0; xx < players.size(); xx++) {
	        players1.add(new Pair<>(players.get(xx).getId(), players.get(xx).getName()));
	    }
	    c.getSession().write(CField.getPVPType(type, players1, c.getPlayer().getTeam(), true, lvl));
	    c.getSession().write(CField.enablePVP(true));
	}

}
