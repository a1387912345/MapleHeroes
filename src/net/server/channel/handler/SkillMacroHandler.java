package net.server.channel.handler;

import client.MapleClient;
import client.SkillMacro;
import client.character.MapleCharacter;
import net.MaplePacketHandler;
import net.RecvPacketOpcode;
import net.netty.MaplePacketReader;

public class SkillMacroHandler extends MaplePacketHandler {

	public SkillMacroHandler(RecvPacketOpcode recv) {
		super(recv);
	}

	@Override
	public void handlePacket(MaplePacketReader mpr, MapleClient c, MapleCharacter chr) {
		int num = mpr.readByte();

        for (int i = 0; i < num; i++) {
            String name = mpr.readMapleAsciiString();
            int shout = mpr.readByte();
            int skill1 = mpr.readInt();
            int skill2 = mpr.readInt();
            int skill3 = mpr.readInt();

            SkillMacro macro = new SkillMacro(skill1, skill2, skill3, name, shout, i);
            chr.updateMacros(i, macro);
        }
	}

}
