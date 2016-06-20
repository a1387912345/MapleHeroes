package net.server.channel.handler;

import java.lang.ref.WeakReference;

import client.MapleClient;
import client.anticheat.CheatingOffense;
import client.character.MapleCharacter;
import client.inventory.MapleInventoryType;
import constants.GameConstants;
import net.MaplePacketHandler;
import net.RecvPacketOpcode;
import net.netty.MaplePacketReader;
import net.packet.CField;
import server.Timer.CloneTimer;

public class FaceExpressionHandler extends MaplePacketHandler {

	public FaceExpressionHandler(RecvPacketOpcode recv) {
		super(recv);
	}

	@Override
	public void handlePacket(final MaplePacketReader mpr, final MapleClient c, MapleCharacter chr) {
		final int emote = mpr.readInt();
		
		if (emote > 7) {
            final int emoteid = 5159992 + emote;
            final MapleInventoryType type = GameConstants.getInventoryType(emoteid);
            if (chr.getInventory(type).findById(emoteid) == null) {
                chr.getCheatTracker().registerOffense(CheatingOffense.USING_UNAVAILABLE_ITEM, Integer.toString(emoteid));
                return;
            }
        }
        if (emote > 0 && chr != null && chr.getMap() != null && !chr.isHidden()) { //O_o
            chr.getMap().broadcastMessage(chr, CField.facialExpression(chr, emote), false);
            WeakReference<MapleCharacter>[] clones = chr.getClones();
            for (int i = 0; i < clones.length; i++) {
                if (clones[i].get() != null) {
                    final MapleCharacter clone = clones[i].get();
                    CloneTimer.getInstance().schedule(new Runnable() {
                        @Override
                        public void run() {
                            clone.getMap().broadcastMessage(CField.facialExpression(clone, emote));
                        }
                    }, 500 * i + 500);
                }
            }
        }
	}

}
