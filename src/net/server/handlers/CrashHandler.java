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
package net.server.handlers;

import client.MapleClient;
import net.MaplePacketHandler;
import net.SendPacketOpcode;
import tools.data.input.SeekableLittleEndianAccessor;

public class CrashHandler implements MaplePacketHandler {
    public void handlePacket(final SeekableLittleEndianAccessor slea, final MapleClient c) {
    	slea.skip(12);
    	short opcode = slea.readShort();
    	System.out.println("[" + SendPacketOpcode.getOpcodeName((int)opcode) + ":" + opcode + "] caused a client to crash.");
    	System.out.println("[" + SendPacketOpcode.getOpcodeName((int)opcode) + "] " + slea.toString());
    }

    public boolean validateState(final MapleClient c) {
        return true;
    }
}
