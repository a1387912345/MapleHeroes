package net.server.channel.handler.inventory;

import java.util.Map;

import client.MapleClient;
import client.Skill;
import client.SkillFactory;
import client.character.MapleCharacter;
import client.inventory.Item;
import constants.GameConstants;
import net.MaplePacketHandler;
import net.RecvPacketOpcode;
import net.netty.MaplePacketReader;
import net.packet.CWvsContext;
import net.server.channel.handler.deprecated.InventoryHandler;
import server.MapleInventoryManipulator;
import server.MapleItemInformationProvider;
import server.Randomizer;

public class UseSkillBookHandler extends MaplePacketHandler {

	public UseSkillBookHandler(RecvPacketOpcode recv) {
		super(recv);
	}

	@Override
	public void handlePacket(MaplePacketReader lea, MapleClient c, MapleCharacter chr) {
        chr.updateTick(lea.readInt());
        InventoryHandler.UseSkillBook((byte) lea.readShort(), lea.readInt(), c, c.getCharacter());

	}

}
