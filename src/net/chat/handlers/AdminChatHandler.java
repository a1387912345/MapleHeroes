package net.chat.handlers;

import client.MapleCharacter;
import client.MapleClient;
import net.AbstractMaplePacketHandler;
import net.world.World;
import tools.data.LittleEndianAccessor;
import tools.packet.CWvsContext;

public class AdminChatHandler extends AbstractMaplePacketHandler
{
	public void handlePacket(final LittleEndianAccessor lea, final MapleClient c)
	{
		final MapleCharacter chr = c.getPlayer();
		if (!c.getPlayer().isGM()) 
		{//if ( (signed int)CWvsContext::GetAdminLevel((void *)v294) > 2 )
            return;
        }
        byte mode = lea.readByte();
        //not saving slides...
        byte[] packet = CWvsContext.broadcastMsg(lea.readByte(), lea.readMapleAsciiString());//maybe I should make a check for the slea.readByte()... but I just hope gm's don't fuck things up :)
        switch (mode) 
        {
            case 0:// /alertall, /noticeall, /slideall
                World.Broadcast.broadcastMessage(packet);
                break;
            case 1:// /alertch, /noticech, /slidech
                c.getChannelServer().broadcastMessage(packet);
                break;
            case 2:// /alertm /alertmap, /noticem /noticemap, /slidem /slidemap
                c.getPlayer().getMap().broadcastMessage(packet);
                break;

        }
	}
}
