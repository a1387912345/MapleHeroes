package net.server.channel.handler;

import client.MapleClient;
import client.MonsterFamiliar;
import client.character.MapleCharacter;
import client.character.MapleCharacterUtil;
import net.MaplePacketHandler;
import net.RecvPacketOpcode;
import net.netty.MaplePacketReader;
import net.packet.CField;
import net.packet.CWvsContext;

public class RenameFamiliarHandler extends MaplePacketHandler {

	public RenameFamiliarHandler(RecvPacketOpcode recv) {
		super(recv);
	}

	@Override
	public void handlePacket(MaplePacketReader mpr, MapleClient c, MapleCharacter chr) {
		MonsterFamiliar mf = (MonsterFamiliar) c.getCharacter().getFamiliars().get(Integer.valueOf(mpr.readInt()));
        String newName = mpr.readMapleAsciiString();
        if ((mf != null) && (mf.getName().equals(mf.getOriginalName())) && (MapleCharacterUtil.isEligibleCharName(newName, false))) {
            mf.setName(newName);
            c.sendPacket(CField.renameFamiliar(mf));
        } else {
            chr.dropMessage(1, "Name was not eligible.");
        }
        c.sendPacket(CWvsContext.enableActions());
	}

}
