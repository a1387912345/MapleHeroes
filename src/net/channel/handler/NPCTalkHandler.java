package net.channel.handler;

import client.MapleCharacter;
import client.MapleClient;
import net.MaplePacketHandler;
import net.RecvPacketOpcode;
import net.netty.MaplePacketReader;
import scripting.npc.NPCScriptManager;
import server.life.MapleNPC;

public class NPCTalkHandler extends MaplePacketHandler {

	public NPCTalkHandler(RecvPacketOpcode recv) {
		super(recv);
	}

	@Override
	public void handlePacket(final MaplePacketReader lea, final MapleClient c, MapleCharacter chr) {
		
		if (chr == null || chr.getMap() == null) {
            return;
        }
        final MapleNPC npc = chr.getMap().getNPCByOid(lea.readInt());
        if (npc == null) {
            return;
        }
        if (chr.hasBlockedInventory()) {
            return;
        }
        if (NPCScriptManager.getInstance().hasScript(c, npc.getId(), null)) { //I want it to come before shop
            NPCScriptManager.getInstance().start(c, npc.getId(), null);
        } else if (npc.hasShop()) {
            chr.setConversation(1);
            npc.sendShop(c);
        } else {
            NPCScriptManager.getInstance().start(c, npc.getId(), null);
        }
	}

}
