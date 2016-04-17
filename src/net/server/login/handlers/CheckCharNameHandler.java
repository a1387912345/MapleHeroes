package net.server.login.handlers;

import client.MapleCharacterUtil;
import client.MapleClient;
import net.AbstractMaplePacketHandler;
import net.RecvPacketOpcode;
import net.login.LoginInformationProvider;
import tools.data.LittleEndianAccessor;
import tools.data.input.SeekableLittleEndianAccessor;
import tools.packet.LoginPacket;

public class CheckCharNameHandler extends AbstractMaplePacketHandler {

	public CheckCharNameHandler(RecvPacketOpcode recv) {
		super(recv);
	}

	@Override
	public void handlePacket(final LittleEndianAccessor lea, final MapleClient c) {
		final String name = lea.readMapleAsciiString();
		boolean nameUsed = true;
		LoginInformationProvider li = LoginInformationProvider.getInstance();
        
        if (MapleCharacterUtil.canCreateChar(name, c.isGm())) {
            nameUsed = false;
        }
        if (li.isForbiddenName(name) && !c.isGm()) {
            nameUsed = false;
        }
        c.getSession().write(LoginPacket.charNameResponse(name, nameUsed));

	}

}
