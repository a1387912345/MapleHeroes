package net.channel.handler;

import client.MapleCharacter;
import client.MapleClient;
import net.MaplePacketHandler;
import net.RecvPacketOpcode;
import net.channel.ChannelServer;
import net.netty.MaplePacketReader;
import net.packet.CWvsContext;
import net.world.World;
import server.maps.FieldLimitType;
import server.maps.MapleMap;

public class ChangeChannelHandler extends MaplePacketHandler {

	public ChangeChannelHandler(RecvPacketOpcode recv) {
		super(recv);
	}

	@Override
	public void handlePacket(final MaplePacketReader lea, final MapleClient c, MapleCharacter chr) {
		final boolean room = getRecvOpcode() == RecvPacketOpcode.CHANGE_ROOM_CHANNEL;
		
		if (chr == null || chr.hasBlockedInventory() || chr.getEventInstance() != null || chr.getMap() == null || chr.isInBlockedMap() || FieldLimitType.ChannelSwitch.check(chr.getMap().getFieldLimit())) {
            c.sendPacket(CWvsContext.enableActions());
            return;
        }
        if (World.getPendingCharacterSize() >= 10) {
            chr.dropMessage(1, "The server is busy at the moment. Please try again in less than a minute.");
            c.sendPacket(CWvsContext.enableActions());
            return;
        }
        final int chc = lea.readByte() + 1;
        int mapid = 0;
        if (room) {
            mapid = lea.readInt();
        }
        chr.updateTick(lea.readInt());
        if (!World.isChannelAvailable(chc, chr.getWorld())) {
            chr.dropMessage(1, "Request denied due to an unknown error.");
            c.sendPacket(CWvsContext.enableActions());
            return;
        }
        if (room && (mapid < 910000001 || mapid > 910000022)) {
            chr.dropMessage(1, "Request denied due to an unknown error.");
            c.sendPacket(CWvsContext.enableActions());
            return;
        }
        if (room) {
            if (chr.getMapId() == mapid) {
                if (c.getChannel() == chc) {
                    chr.dropMessage(1, "You are already in " + chr.getMap().getMapName());
                    c.sendPacket(CWvsContext.enableActions());
                } else { // diff channel
                    chr.changeChannel(chc);
                }
            } else { // diff map
                if (c.getChannel() != chc) {
                    chr.changeChannel(chc);
                }
                final MapleMap warpz = ChannelServer.getInstance(c.getChannel()).getMapFactory().getMap(mapid);
                if (warpz != null) {
                    chr.changeMap(warpz, warpz.getPortal("out00"));
                } else {
                    chr.dropMessage(1, "Request denied due to an unknown error.");
                    c.sendPacket(CWvsContext.enableActions());
                }
            }
        } else {
            chr.changeChannel(chc);
        }
	}

}
