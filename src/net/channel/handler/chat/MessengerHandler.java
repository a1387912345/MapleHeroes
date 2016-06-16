package net.channel.handler.chat;

import client.MapleCharacter;
import client.MapleCharacterUtil;
import client.MapleClient;
import net.MaplePacketHandler;
import net.RecvPacketOpcode;
import net.netty.MaplePacketReader;
import net.packet.CField;
import net.packet.CWvsContext;
import net.world.MapleMessenger;
import net.world.MapleMessengerCharacter;
import net.world.World;

public class MessengerHandler extends MaplePacketHandler {
	public MessengerHandler(RecvPacketOpcode recv) {
		super(recv);
	}

	public void handlePacket(final MaplePacketReader inPacket, final MapleClient client, MapleCharacter chr) {
		String input;
        MapleMessenger messenger = client.getCharacter().getMessenger();

        switch (inPacket.readByte()) {
            case 0x00: // open
                System.out.println("0");
                if (messenger == null) {
                    System.out.println("1");
                    inPacket.readByte();
                    byte mode = inPacket.readByte();
                    int messengerid = inPacket.readInt();
                    if (messengerid == 0) { // create
                        System.out.println("2");
                        client.getCharacter().setMessenger(World.Messenger.createMessenger(new MapleMessengerCharacter(client.getCharacter())));
                    } else { // join
                        System.out.println("3");
                        messenger = World.Messenger.getMessenger(messengerid);
                        if (messenger != null) {
                            System.out.println("4");
                            final int position = messenger.getLowestPosition();
                            if (position > -1 && position < 7) {
                                System.out.println("5");
                                client.getCharacter().setMessenger(messenger);
                                World.Messenger.joinMessenger(messenger.getId(), new MapleMessengerCharacter(client.getCharacter()), client.getCharacter().getName(), client.getChannel());
                            }
                        }
                    }
                }
                break;
            case 0x02: // exit
                if (messenger != null) {
                    final MapleMessengerCharacter messengerplayer = new MapleMessengerCharacter(client.getCharacter());
                    World.Messenger.leaveMessenger(messenger.getId(), messengerplayer);
                    client.getCharacter().setMessenger(null);
                }
                break;
            case 0x03: // invite
                if (messenger != null) {
                    final int position = messenger.getLowestPosition();
                    if (position <= -1 || position >= 7) {
                        return;
                    }
                    input = inPacket.readMapleAsciiString();
                    final MapleCharacter target = client.getChannelServer().getPlayerStorage().getCharacterByName(input);

                    if (target != null) {
                        if (target.getMessenger() == null){
                            if (!target.isIntern() || client.getCharacter().isIntern()){
                                client.sendPacket(CField.messengerNote(input, 4, 1));
                                target.getClient().sendPacket(CField.messengerInvite(client.getCharacter().getName(), messenger.getId()));
                            } else {
                                client.sendPacket(CField.messengerNote(input, 4, 0));
                            }
                        } else {
                            client.sendPacket(CField.messengerChat(client.getCharacter().getName(), " : " + target.getName() + " is already using Maple Messenger."));
                        }
                    } else {
                        if (World.isConnected(input)) {
                            World.Messenger.messengerInvite(client.getCharacter().getName(), messenger.getId(), input, client.getChannel(), client.getCharacter().isIntern());
                        } else {
                            client.sendPacket(CField.messengerNote(input, 4, 0));
                        }
                    }
                }
                break;
            case 0x05: // decline
                final String targeted = inPacket.readMapleAsciiString();
                final MapleCharacter target = client.getChannelServer().getPlayerStorage().getCharacterByName(targeted);
                if (target != null) { // This channel
                    if (target.getMessenger() != null) {
                        target.getClient().sendPacket(CField.messengerNote(client.getCharacter().getName(), 5, 0));
                    }
                } else { // Other channel
                    if (!client.getCharacter().isIntern()) {
                        World.Messenger.declineChat(targeted, client.getCharacter().getName());
                    }
                }
                break;
            case 0x06: // message
                if (messenger != null) {
                    final String charname = inPacket.readMapleAsciiString();
                    final String text = inPacket.readMapleAsciiString();
                    if (!client.getCharacter().isIntern() && text.length() >= 1000) {
                        return;
                    }
                    final String chattext = charname + "" + text;
                    World.Messenger.messengerChat(messenger.getId(), charname, text, client.getCharacter().getName());
                    if (messenger.isMonitored() && chattext.length() > client.getCharacter().getName().length() + 3) { //name : NOT name0 or name1
                    	World.Broadcast.broadcastGMMessage(
                                CWvsContext.broadcastMsg(
                                        6, "[GM Message] " + MapleCharacterUtil.makeMapleReadable(client.getCharacter().getName()) + "(Messenger: "
                                        + messenger.getMemberNamesDEBUG() + ") said: " + chattext));
                    }
                }
                break;
            case 0x09: //like
                if (messenger != null) {
                    String charname = inPacket.readMapleAsciiString();
                    //todo send like packet here
                }
                break;
            case 0x0A: //guidance
                if (messenger != null) {
                    inPacket.readByte();
                    String charname = inPacket.readMapleAsciiString();
                    String targetname = inPacket.readMapleAsciiString();
                    //todo send guide packet here
                }
                break;
            case 0x0B: //char info
                if (messenger != null) {
                    String charname = inPacket.readMapleAsciiString();
                    MapleCharacter character = client.getChannelServer().getPlayerStorage().getCharacterByName(charname);
                    client.sendPacket(CField.messengerCharInfo(character));
                }
                break;
            case 0x0E: //whisper
                if (messenger != null) {
                    String charname = inPacket.readMapleAsciiString();
                    //todo send whisper packet here
                }
                break;
        }
	}
}
