package net.channel.handler;

import client.MapleCharacter;
import client.MapleClient;
import net.MaplePacketHandler;
import net.RecvPacketOpcode;
import net.cashshop.CashShopServer;
import net.channel.ChannelServer;
import net.farm.FarmServer;
import net.netty.MaplePacketReader;
import net.packet.CField;
import net.packet.CWvsContext;
import net.packet.FarmPacket;
import net.world.CharacterTransfer;
import net.world.MapleMessengerCharacter;
import net.world.PlayerBuffStorage;
import net.world.World;

public class EnterCashShopHandler extends MaplePacketHandler {

	public EnterCashShopHandler(RecvPacketOpcode recv) {
		super(recv);
	}

	@Override
	public void handlePacket(MaplePacketReader lea, MapleClient c, MapleCharacter chr) {
		if (chr.hasBlockedInventory() || chr.getMap() == null || chr.getEventInstance() != null || c.getChannelServer() == null) {
            c.sendPacket(CField.serverBlocked(2));
            CharacterTransfer farmtransfer = FarmServer.getPlayerStorage().getPendingCharacter(chr.getId());
            if (farmtransfer != null) {
                c.sendPacket(FarmPacket.farmMessage("You cannot move into Cash Shop while visiting your farm, yet."));
            }
            c.sendPacket(CWvsContext.enableActions());
            return;
        }
        if (World.getPendingCharacterSize() >= 10) {
            chr.dropMessage(1, "The server is busy at the moment. Please try again in a minute or less.");
            c.sendPacket(CWvsContext.enableActions());
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
        World.ChannelChange_Data(new CharacterTransfer(chr), chr.getId(), -10);
        ch.removePlayer(chr);
        c.updateLoginState(3, c.getSessionIPAddress());
        chr.saveToDB(false, false);
        chr.getMap().removePlayer(chr);
        c.sendPacket(CField.getChannelChange(c, Integer.parseInt(CashShopServer.getIP().split(":")[1])));
        c.setCharacter(null);
        c.setReceiving(false);
	}

}