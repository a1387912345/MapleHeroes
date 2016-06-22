/*
 This file is part of the OdinMS Maple Story Server
 Copyright (C) 2008 ~ 2010 Patrick Huy <patrick.huy@frz.cc> 
 Matthias Butz <matze@odinms.de>
 Jan Christian Meyer <vimes@odinms.de>

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU Affero General Public License version 3
 as published by the Free Software Foundation. You may not use, modify
 or distribute this program under any other version of the
 GNU Affero General Public License.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU Affero General Public License for more details.

 You should have received a copy of the GNU Affero General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.server.login;

import client.MapleClient;
import net.packet.CWvsContext;
import net.packet.LoginPacket;
import net.server.channel.ChannelServer;

import java.util.Map;
import java.util.Map.Entry;
import server.Timer.PingTimer;

public class LoginWorker {

    private static long lastUpdate = 0;

    public static void registerClient(final MapleClient c) {
        if (LoginServer.getInstance().isAdminOnly() && !c.isGm()/* && !c.isLocalhost()*/) {
            c.sendPacket(CWvsContext.broadcastMsg(1, "We are currently set to closed Alpha.\r\nYou don't seem to have tester privileges.\r\nPlease try again later.\r\nWhen the public beta goes live."));
            c.sendPacket(LoginPacket.getLoginFailed(7));
            return;
        }

        if (System.currentTimeMillis() - lastUpdate > 600000) { // Update once every 10 minutes
            lastUpdate = System.currentTimeMillis();
            final Map<Integer, Integer> load = ChannelServer.getChannelLoad();
            int usersOn = 0;
            if (load == null || load.size() <= 0) { // In an unfortunate event that client logged in before load
                lastUpdate = 0;
                c.sendPacket(LoginPacket.getLoginFailed(7));
                return;
            }
            final double loadFactor = 1200 / ((double) LoginServer.getInstance().getUserLimit() / load.size());
            for (Entry<Integer, Integer> entry : load.entrySet()) {
                usersOn += entry.getValue();
                load.put(entry.getKey(), Math.max(1, (int) (entry.getValue() * loadFactor))); // This is the load number to display a channel's population/fullness percentage
            }
            LoginServer.getInstance().setLoad(load, usersOn);
            lastUpdate = System.currentTimeMillis();
        }

        if (c.finishLogin() == 0) {
            c.sendPacket(LoginPacket.getLoginSuccess(c));
//            c.setIdleTask(PingTimer.getInstance().schedule(new Runnable() {
//
//                @Override
//                public void run() {
//                    c.close();
//                }
//            }, 10 * 60 * 10000));
        } else {
            c.sendPacket(LoginPacket.getLoginFailed(7));
        }
    }
}
