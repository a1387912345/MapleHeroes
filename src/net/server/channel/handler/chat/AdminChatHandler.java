package net.server.channel.handler.chat;

import client.MapleClient;
import client.character.MapleCharacter;
import net.MaplePacketHandler;
import net.RecvPacketOpcode;
import net.netty.MaplePacketReader;
import net.packet.CWvsContext;
import net.world.World;

public class AdminChatHandler extends MaplePacketHandler {
	public AdminChatHandler(RecvPacketOpcode recv) {
		super(recv);
	}

	public void handlePacket(final MaplePacketReader inPacket, final MapleClient c, MapleCharacter chr) {
		if (!c.getCharacter().isGM()) {//if ( (signed int)CWvsContext::GetAdminLevel((void *)v294) > 2 )
            return;
        }
        byte mode = inPacket.readByte();
        //not saving slides...
        byte[] packet = CWvsContext.broadcastMsg(inPacket.readByte(), inPacket.readMapleAsciiString());//maybe I should make a check for the inPacket.readByte()... but I just hope gm's don't fuck things up :)
        switch (mode) {
            case 0:// /alertall, /noticeall, /slideall
                World.Broadcast.broadcastMessage(packet);
                break;
            case 1:// /alertch, /noticech, /slidech
                c.getChannelServer().broadcastMessage(packet);
                break;
            case 2:// /alertm /alertmap, /noticem /noticemap, /slidem /slidemap
                c.getCharacter().getMap().broadcastMessage(packet);
                break;

        }
	}
}
