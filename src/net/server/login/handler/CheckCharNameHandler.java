package net.server.login.handler;

import client.MapleClient;
import client.character.MapleCharacter;
import client.character.MapleCharacterUtil;
import net.MaplePacketHandler;
import net.RecvPacketOpcode;
import net.netty.MaplePacketReader;
import net.packet.LoginPacket;
import net.server.login.LoginInformationProvider;
import tools.data.input.SeekableLittleEndianAccessor;

public class CheckCharNameHandler extends MaplePacketHandler {

	public CheckCharNameHandler(RecvPacketOpcode recv) {
		super(recv);
	}

	@Override
	public void handlePacket(final MaplePacketReader mpr, final MapleClient c, MapleCharacter chr) {
		final String name = mpr.readMapleAsciiString();
		boolean nameUsed = true;
		LoginInformationProvider li = LoginInformationProvider.getInstance();
        
        if (MapleCharacterUtil.canCreateChar(name, c.isGm())) {
            nameUsed = false;
        }
        if (li.isForbiddenName(name) && !c.isGm()) {
            nameUsed = false;
        }
        c.sendPacket(LoginPacket.charNameResponse(name, nameUsed));

	}

}
