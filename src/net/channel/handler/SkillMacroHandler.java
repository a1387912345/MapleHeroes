package net.channel.handler;

import client.MapleCharacter;
import client.MapleClient;
import client.SkillMacro;
import net.MaplePacketHandler;
import net.RecvPacketOpcode;
import net.netty.MaplePacketReader;

public class SkillMacroHandler extends MaplePacketHandler {

	public SkillMacroHandler(RecvPacketOpcode recv) {
		super(recv);
	}

	@Override
	public void handlePacket(MaplePacketReader lea, MapleClient c, MapleCharacter chr) {
		int num = lea.readByte();

        for (int i = 0; i < num; i++) {
            String name = lea.readMapleAsciiString();
            int shout = lea.readByte();
            int skill1 = lea.readInt();
            int skill2 = lea.readInt();
            int skill3 = lea.readInt();

            SkillMacro macro = new SkillMacro(skill1, skill2, skill3, name, shout, i);
            chr.updateMacros(i, macro);
        }
	}

}
