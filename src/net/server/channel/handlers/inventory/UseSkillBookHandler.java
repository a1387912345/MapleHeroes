package net.server.channel.handlers.inventory;

import java.util.Map;

import client.MapleCharacter;
import client.MapleClient;
import client.Skill;
import client.SkillFactory;
import client.inventory.Item;
import constants.GameConstants;
import net.AbstractMaplePacketHandler;
import net.RecvPacketOpcode;
import net.channel.handler.InventoryHandler;
import server.MapleInventoryManipulator;
import server.MapleItemInformationProvider;
import server.Randomizer;
import tools.data.LittleEndianAccessor;
import tools.packet.CWvsContext;

public class UseSkillBookHandler extends AbstractMaplePacketHandler {

	public UseSkillBookHandler(RecvPacketOpcode recv) {
		super(recv);
	}

	@Override
	public void handlePacket(LittleEndianAccessor lea, MapleClient c, MapleCharacter chr) {
        chr.updateTick(lea.readInt());
        InventoryHandler.UseSkillBook((byte) lea.readShort(), lea.readInt(), c, c.getPlayer());

	}

}
