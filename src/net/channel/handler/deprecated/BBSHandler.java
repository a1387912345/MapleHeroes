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
package net.channel.handler.deprecated;

import client.MapleClient;
import net.netty.MaplePacketReader;
import net.packet.CWvsContext.GuildPacket;
import net.world.World;
import net.world.guild.MapleBBSThread;

import java.util.List;

public class BBSHandler {

    private static String correctLength(final String in, final int maxSize) {
        if (in.length() > maxSize) {
            return in.substring(0, maxSize);
        }
        return in;
    }

    public static final void BBSOperation(final MaplePacketReader inPacket, final MapleClient c) {
        if (c.getCharacter().getGuildId() <= 0) {
            return; // expelled while viewing bbs or hax
        }
        int localthreadid = 0;
        final byte action = inPacket.readByte();
        switch (action) {
            case 0: // start a new post
                if (!c.getCharacter().getCheatTracker().canBBS()) {
                    c.getCharacter().dropMessage(1, "You may only start a new thread every 60 seconds.");
                    return;
                }
                final boolean bEdit = inPacket.readByte() > 0;
                if (bEdit) {
                    localthreadid = inPacket.readInt();
                }
                final boolean bNotice = inPacket.readByte() > 0;
                final String title = correctLength(inPacket.readMapleAsciiString(), 25);
                String text = correctLength(inPacket.readMapleAsciiString(), 600);
                final int icon = inPacket.readInt();
                if (icon >= 0x64 && icon <= 0x6a) {
                    if (!c.getCharacter().haveItem(5290000 + icon - 0x64, 1, false, true)) {
                        return; // hax, using an nx icon that s/he doesn't have
                    }
                } else if (icon < 0 || icon > 2) {
                    return; // hax, using an invalid icon
                }
                if (!bEdit) {
                    newBBSThread(c, title, text, icon, bNotice);
                } else {
                    editBBSThread(c, title, text, icon, localthreadid);
                }
                break;
            case 1: // delete a thread
                localthreadid = inPacket.readInt();
                deleteBBSThread(c, localthreadid);
                break;
            case 2: // list threads
                int start = inPacket.readInt();
                listBBSThreads(c, start * 10);
                break;
            case 3: // list thread + reply, followed by id (int)
                localthreadid = inPacket.readInt();
                displayThread(c, localthreadid);
                break;
            case 4: // reply
                if (!c.getCharacter().getCheatTracker().canBBS()) {
                    c.getCharacter().dropMessage(1, "You may only start a new reply every 60 seconds.");
                    return;
                }
                localthreadid = inPacket.readInt();
                text = correctLength(inPacket.readMapleAsciiString(), 25);
                newBBSReply(c, localthreadid, text);
                break;
            case 5: // delete reply
                localthreadid = inPacket.readInt();
                int replyid = inPacket.readInt();
                deleteBBSReply(c, localthreadid, replyid);
                break;
        }
    }

    private static void listBBSThreads(MapleClient c, int start) {
        if (c.getCharacter().getGuildId() <= 0) {
            return;
        }
        c.sendPacket(GuildPacket.BBSThreadList(World.Guild.getBBS(c.getCharacter().getGuildId()), start));
    }

    private static void newBBSReply(final MapleClient c, final int localthreadid, final String text) {
        if (c.getCharacter().getGuildId() <= 0) {
            return;
        }
        World.Guild.addBBSReply(c.getCharacter().getGuildId(), localthreadid, text, c.getCharacter().getId());
        displayThread(c, localthreadid);
    }

    private static void editBBSThread(final MapleClient c, final String title, final String text, final int icon, final int localthreadid) {
        if (c.getCharacter().getGuildId() <= 0) {
            return; // expelled while viewing?
        }
        World.Guild.editBBSThread(c.getCharacter().getGuildId(), localthreadid, title, text, icon, c.getCharacter().getId(), c.getCharacter().getGuildRank());
        displayThread(c, localthreadid);
    }

    private static void newBBSThread(final MapleClient c, final String title, final String text, final int icon, final boolean bNotice) {
        if (c.getCharacter().getGuildId() <= 0) {
            return; // expelled while viewing?
        }
        displayThread(c, World.Guild.addBBSThread(c.getCharacter().getGuildId(), title, text, icon, bNotice, c.getCharacter().getId()));
        listBBSThreads(c, 0);
    }

    private static void deleteBBSThread(final MapleClient c, final int localthreadid) {
        if (c.getCharacter().getGuildId() <= 0) {
            return;
        }
        World.Guild.deleteBBSThread(c.getCharacter().getGuildId(), localthreadid, c.getCharacter().getId(), (int) c.getCharacter().getGuildRank());
    }

    private static void deleteBBSReply(final MapleClient c, final int localthreadid, final int replyid) {
        if (c.getCharacter().getGuildId() <= 0) {
            return;
        }

        World.Guild.deleteBBSReply(c.getCharacter().getGuildId(), localthreadid, replyid, c.getCharacter().getId(), (int) c.getCharacter().getGuildRank());
        displayThread(c, localthreadid);
    }

    private static void displayThread(final MapleClient c, final int localthreadid) {
        if (c.getCharacter().getGuildId() <= 0) {
            return;
        }
        final List<MapleBBSThread> bbsList = World.Guild.getBBS(c.getCharacter().getGuildId());
        if (bbsList != null) {
            for (MapleBBSThread t : bbsList) {
                if (t != null && t.localthreadID == localthreadid) {
                    c.sendPacket(GuildPacket.showThread(t));
                }
            }
        }
    }
}
