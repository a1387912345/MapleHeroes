package net.server.channel.handler.inventory;

import client.MapleClient;
import client.character.MapleCharacter;
import net.MaplePacketHandler;
import net.RecvPacketOpcode;
import net.netty.MaplePacketReader;
import net.server.channel.handler.deprecated.InventoryHandler;

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
