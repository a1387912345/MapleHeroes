/*
	This file is part of the OdinMS Maple Story Server
    Copyright (C) 2008 Patrick Huy <patrick.huy@frz.cc>
		       Matthias Butz <matze@odinms.de>
		       Jan Christian Meyer <vimes@odinms.de>

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as
    published by the Free Software Foundation version 3 as published by
    the Free Software Foundation. You may not use, modify or distribute
    this program under any other version of the GNU Affero General Public
    License.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package net.server.handler;

import client.MapleClient;
import client.character.MapleCharacter;
import net.MaplePacketHandler;
import net.RecvPacketOpcode;
import net.SendPacketOpcode;
import net.netty.MaplePacketReader;
import tools.HexTool;

public class CrashHandler extends MaplePacketHandler {

	public CrashHandler(RecvPacketOpcode recv) {
		super(recv);
	}
	
    public void handlePacket(final MaplePacketReader in, final MapleClient client, MapleCharacter chr) {
    	in.skip(12);
    	short opcode = in.readShort();
    	System.out.println("[Crash]\t" + SendPacketOpcode.getOpcodeName((int)opcode) + "\t|\t" + chr.getName() + "\t|\t" + HexTool.getOpcodeToString(opcode) + "\t|\t" + in.toString());
    }

    @Override
    public boolean validateState(final MapleClient c) {
        return true;
    }

}
