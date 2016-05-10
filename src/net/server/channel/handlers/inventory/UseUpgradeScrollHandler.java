package net.server.channel.handlers.inventory;

import client.MapleCharacter;
import client.MapleClient;
import net.AbstractMaplePacketHandler;
import net.RecvPacketOpcode;
import net.channel.handler.InventoryHandler;
import tools.data.LittleEndianAccessor;

public class UseUpgradeScrollHandler extends AbstractMaplePacketHandler {

	public UseUpgradeScrollHandler(RecvPacketOpcode recv) {
		super(recv);
	}

	@Override
	public void handlePacket(LittleEndianAccessor lea, MapleClient c, MapleCharacter chr) {
        chr.updateTick(lea.readInt());
        InventoryHandler.UseUpgradeScroll(lea.readShort(), lea.readShort(), lea.readShort(), c, c.getPlayer(), lea.readByte() > 0);
	}

}
