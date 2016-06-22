package net.server.channel.handler.inventory;

import client.MapleClient;
import client.character.MapleCharacter;
import net.MaplePacketHandler;
import net.RecvPacketOpcode;
import net.netty.MaplePacketReader;
import net.server.channel.handler.deprecated.InventoryHandler;

public class UsePotentialScrollHandler extends MaplePacketHandler {

	public UsePotentialScrollHandler(RecvPacketOpcode recv) {
		super(recv);
	}

	@Override
	public void handlePacket(MaplePacketReader mpr, MapleClient c, MapleCharacter chr) {
		c.getCharacter().updateTick(mpr.readInt());
        InventoryHandler.UseUpgradeScroll(mpr.readShort(), mpr.readShort(), mpr.readShort(), c, c.getCharacter(), mpr.readByte() > 0);
	}

}
