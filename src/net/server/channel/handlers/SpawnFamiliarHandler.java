package net.server.channel.handlers;

import client.MapleCharacter;
import client.MapleClient;
import client.MonsterFamiliar;
import net.AbstractMaplePacketHandler;
import net.RecvPacketOpcode;
import tools.data.LittleEndianAccessor;
import tools.packet.CWvsContext;

public class SpawnFamiliarHandler extends AbstractMaplePacketHandler {

	public SpawnFamiliarHandler(RecvPacketOpcode recv) {
		super(recv);
	}

	@Override
	public void handlePacket(LittleEndianAccessor lea, MapleClient c, MapleCharacter chr) {
		c.getPlayer().updateTick(lea.readInt());
        int mId = lea.readInt();
        c.getSession().write(CWvsContext.enableActions());
        c.getPlayer().removeFamiliar();
        if ((c.getPlayer().getFamiliars().containsKey(Integer.valueOf(mId))) && (lea.readByte() > 0)) {
            MonsterFamiliar mf = (MonsterFamiliar) c.getPlayer().getFamiliars().get(Integer.valueOf(mId));
            if (mf.getFatigue() > 0) {
                c.getPlayer().dropMessage(1, "Please wait " + mf.getFatigue() + " seconds to summon it.");
            } else {
                c.getPlayer().spawnFamiliar(mf, false);
            }
        }
	}

}
