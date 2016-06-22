package net.server.channel.handler;

import client.MapleClient;
import client.character.MapleCharacter;
import net.MaplePacketHandler;
import net.RecvPacketOpcode;
import net.netty.MaplePacketReader;
import net.packet.CWvsContext;
import scripting.npc.NPCConversationManager;
import scripting.npc.NPCScriptManager;
import server.maps.MapScriptMethods;

public class NPCTalkMoreHandler extends MaplePacketHandler {

	public NPCTalkMoreHandler(RecvPacketOpcode recv) {
		super(recv);
	}

	@Override
	public void handlePacket(final MaplePacketReader mpr, final MapleClient c, MapleCharacter chr) {
		final byte lastMsg = mpr.readByte(); // 00 (last msg type I think)
		
        if (lastMsg == 9 && mpr.available() >= 4) {
            mpr.readShort();
        }
        final byte action = mpr.readByte(); // 00 = end chat, 01 == follow
        byte disposeByte;
        switch(lastMsg) {
	        case 3: 
	        case 4:
	        case 5:
	        case 9:
	        	disposeByte = 0;
	        	break;
        	default:
        		disposeByte = (byte)0xFF;
        		break;
        }

        if (((lastMsg == 0x12 && c.getCharacter().getDirection() >= 0) || (lastMsg == 0x12 && c.getCharacter().getDirection() == -1)) && action == 1) {
            byte lastbyte = mpr.readByte(); // 00 = end chat, 01 == follow
            if (lastbyte == 0) {
                c.sendPacket(CWvsContext.enableActions());
            } else {
                MapScriptMethods.startDirectionInfo(c.getCharacter(), lastMsg == 0x13);
                c.sendPacket(CWvsContext.enableActions());
            }
            return;
        }
        final NPCConversationManager cm = NPCScriptManager.getInstance().getCM(c);

        if(action == disposeByte) {
        	cm.dispose();
        	return;
        } 
        /*if (cm != null && lastMsg == 0x17) {
            c.getPlayer().handleDemonJob(inPacket.readInt());
            return;
        }*/
        if (cm == null || c.getCharacter().getConversation() == 0 || cm.getLastMsg() != lastMsg) {
            return;
        }
        cm.setLastMsg((byte) -1);
        if (lastMsg == 1) {
            NPCScriptManager.getInstance().action(c, action, lastMsg, -1);
        } else if (lastMsg == 3) {
            if (action != 0) {
                cm.setGetText(mpr.readMapleAsciiString());
                if (cm.getType() == 0) {
                    NPCScriptManager.getInstance().startQuest(c, action, lastMsg, -1);
                } else if (cm.getType() == 1) {
                    NPCScriptManager.getInstance().endQuest(c, action, lastMsg, -1);
                } else {
                    NPCScriptManager.getInstance().action(c, action, lastMsg, -1);
                }
            } else {
                cm.dispose();
            }
        } else if (lastMsg == 0x17) {
            NPCScriptManager.getInstance().action(c, (byte) 1, lastMsg, action);
        } else if (lastMsg == 0x16) {
            NPCScriptManager.getInstance().action(c, (byte) 1, lastMsg, action);
        } else {
            int selection = -1;
            if (mpr.available() >= 4) {
                selection = mpr.readInt();
            } else if (mpr.available() > 0) {
                selection = mpr.readByte();
            }
            if (lastMsg == 4 && selection == -1) {
                cm.dispose();
                return;//h4x
            }
            if (selection >= -1 && action != -1) {
                if (cm.getType() == 0) {
                    NPCScriptManager.getInstance().startQuest(c, action, lastMsg, selection);
                } else if (cm.getType() == 1) {
                    NPCScriptManager.getInstance().endQuest(c, action, lastMsg, selection);
                } else {
                    NPCScriptManager.getInstance().action(c, action, lastMsg, selection);
                }
            } else {
                cm.dispose();
            }
        }
        
             
        
	}	
}
