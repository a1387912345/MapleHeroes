package net.server.channel.handlers;

import client.MapleCharacter;
import client.MapleClient;
import net.AbstractMaplePacketHandler;
import net.RecvPacketOpcode;
import net.channel.ChannelServer;
import net.server.farm.FarmServer;
import net.world.CharacterTransfer;
import net.world.MapleMessengerCharacter;
import net.world.PlayerBuffStorage;
import net.world.World;
import tools.data.LittleEndianAccessor;
import tools.packet.CField;
import tools.packet.CWvsContext;

public class EnterFarmHandler extends AbstractMaplePacketHandler {

	public EnterFarmHandler(RecvPacketOpcode recv) {
		super(recv);
	}

	@Override
	public void handlePacket(LittleEndianAccessor lea, MapleClient c, MapleCharacter chr) {
		if (chr.hasBlockedInventory() || chr.getMap() == null || chr.getEventInstance() != null || c.getChannelServer() == null) {
            c.getSession().write(CField.serverBlocked(2));
            c.getSession().write(CWvsContext.enableActions());
            return;
        }
        if (World.getPendingCharacterSize() >= 10) {
            chr.dropMessage(1, "The server is busy at the moment. Please try again in a minute or less.");
            c.getSession().write(CWvsContext.enableActions());
            return;
        }
        ChannelServer ch = ChannelServer.getInstance(c.getChannel());
        chr.changeRemoval();
        if (chr.getMessenger() != null) {
            MapleMessengerCharacter messengerplayer = new MapleMessengerCharacter(chr);
            World.Messenger.leaveMessenger(chr.getMessenger().getId(), messengerplayer);
        }
        PlayerBuffStorage.addBuffsToStorage(chr.getId(), chr.getAllBuffs());
        PlayerBuffStorage.addCooldownsToStorage(chr.getId(), chr.getCooldowns());
        PlayerBuffStorage.addDiseaseToStorage(chr.getId(), chr.getAllDiseases());
        World.ChannelChange_Data(new CharacterTransfer(chr), chr.getId(), -30);
        ch.removePlayer(chr);
        c.updateLoginState(3, c.getSessionIPAddress());
        chr.saveToDB(false, false);
        chr.getMap().removePlayer(chr);
        c.getSession().write(CField.getChannelChange(c, Integer.parseInt(FarmServer.getIP().split(":")[1])));
        c.setPlayer(null);
        c.setReceiving(false);
	}

}
