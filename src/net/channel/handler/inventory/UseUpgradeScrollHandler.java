package net.channel.handler.inventory;

import client.MapleCharacter;
import client.MapleClient;
import net.MaplePacketHandler;
import net.RecvPacketOpcode;
import net.channel.handler.deprecated.InventoryHandler;
import net.netty.MaplePacketReader;

public class UseUpgradeScrollHandler extends MaplePacketHandler {

	public UseUpgradeScrollHandler(RecvPacketOpcode recv) {
		super(recv);
	}

	@Override
	public void handlePacket(MaplePacketReader lea, MapleClient c, MapleCharacter chr) {
        chr.updateTick(lea.readInt());
        InventoryHandler.UseUpgradeScroll(lea.readShort(), lea.readShort(), lea.readShort(), c, c.getCharacter(), lea.readByte() > 0);
	}

}
