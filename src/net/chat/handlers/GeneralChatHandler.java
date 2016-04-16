package net.chat.handlers;

import client.MapleClient;
import net.MaplePacketHandler;
import constants.ServerConstants.CommandType;
import server.commands.CommandProcessor;
import tools.data.LittleEndianAccessor;
import tools.packet.CField;
import tools.packet.CWvsContext;
import client.MapleCharacter;
import client.MapleCharacterUtil;
import net.channel.ChannelServer;
import net.world.MapleMessenger;
import net.world.MapleMessengerCharacter;
import net.world.World;

public class GeneralChatHandler implements MaplePacketHandler
{
	@Override
	public void handlePacket(final LittleEndianAccessor lea, final MapleClient c) 
	{
		final String text = lea.readMapleAsciiString();
		final byte unk = lea.readByte();
		final MapleCharacter chr = c.getPlayer();
		System.out.println(lea.toString());
        if (c.getPlayer() != null && c.getPlayer().getMap() != null) 
        {
            c.getPlayer().updateTick(lea.readInt());
        
            if (text.length() > 0 && chr != null && chr.getMap() != null && !CommandProcessor.processCommand(c, text, CommandType.NORMAL)) 
            {
            	if (!chr.isIntern() && text.length() >= 80) 
            	{
            		return;
            	}
            	if (chr.getCanTalk() || chr.isStaff()) 
            	{
	            //Note: This patch is needed to prevent chat packet from being broadcast to people who might be packet sniffing.
            		if (chr.isHidden()) 
            		{
            			if (chr.isIntern() && !chr.isSuperGM() && unk == 0) 
            			{
            				chr.getMap().broadcastGMMessage(chr, CField.getChatText(chr.getId(), text, c.getPlayer().isSuperGM(), (byte) 1), true);
            				if (unk == 0) 
            				{
            					chr.getMap().broadcastGMMessage(chr, CWvsContext.broadcastMsg(2, chr.getName() + " : " + text), true);
            				}
            			} 
            			else 
	                	{
            				chr.getMap().broadcastGMMessage(chr, CField.getChatText(chr.getId(), text, c.getPlayer().isSuperGM(), unk), true);
	                	}
            		} 
            		else 
            		{
            			chr.getCheatTracker().checkMsg();
            			if (chr.isIntern() && !chr.isSuperGM() && unk == 0) 
            			{
            				//chr.getMap().broadcastMessage(CField.getChatText(chr.getId(), text, false, (byte) 1), c.getPlayer().getTruePosition());
            				chr.getMap().broadcastMessage(ColourChat(chr, text, unk, chr.getChatType()));
            				chr.getMap().broadcastMessage(CField.getChatText(chr.getId(), text, c.getPlayer().isSuperGM(), 1));
            				/*if (unk == 0) {
	                     	//chr.getMap().broadcastMessage(CWvsContext.broadcastMsg(2, chr.getName() + " : " + text), c.getPlayer().getTruePosition());
	                     	chr.getMap().broadcastMessage(ColourChat(chr, text, unk, chr.getChatType()));
	                     	chr.getMap().broadcastMessage(CField.getChatText(chr.getId(), text, false, 1));
	                     	}*/
            			}
            			else
            			{
            				//chr.getMap().broadcastMessage(CField.getChatText(chr.getId(), text, c.getPlayer().isSuperGM(), unk), c.getPlayer().getTruePosition());
            				chr.getMap().broadcastMessage(ColourChat(chr, text, unk, chr.getChatType()));
            				chr.getMap().broadcastMessage(CField.getChatText(chr.getId(), text, c.getPlayer().isSuperGM(), 1));//was1
            			}
            		}
            		if (text.equalsIgnoreCase(c.getChannelServer().getServerName() + " rocks")) 
            		{
            			chr.finishAchievement(11);
            		}
            	} 
            	else 
            	{
            		c.getSession().write(CWvsContext.broadcastMsg(6, "You have been muted and are therefore unable to talk."));
            	}
            }	
        }
	}
	public static byte[] ColourChat(final MapleCharacter chr, String text, final byte unk, short colour) 
	{
        String rank = "";
        switch (chr.getGMLevel()) 
        {
            case 1:
                rank = "[Intern] ";
                //colour = 5;
                break;
            case 2:
                rank = "[GM] ";
                //colour = 1;
                break;
            case 3:
                rank = "[Head GM] ";
                //colour = 9;
                break;
            case 4:
                rank = "[Admin] ";
                //colour = 8;
                break;
        }
        if (chr.isDonator()) 
        {
            colour = 13;
            if (rank.isEmpty()) 
            {
                rank = "[Donor] ";
            }
        }
        byte[] packet;
        switch (colour) 
        {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
            case 10:
            case 11:
            case 12:
            case 13:
                packet = CField.getGameMessage(colour, rank + chr.getName() + " : " + text);
                break;
            default:
                packet = CField.getChatText(chr.getId(), text, false, unk);
        }
        return packet;
    }
	@Override
	public boolean validateState(MapleClient c) 
	{
		// TODO Auto-generated method stub
		return false;
	}
}
