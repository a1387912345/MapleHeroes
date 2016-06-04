package net.server.channel.handlers;

import client.MapleCharacter;
import client.MapleClient;
import constants.GameConstants;
import net.AbstractMaplePacketHandler;
import net.RecvPacketOpcode;
import net.world.MapleParty;
import net.world.MaplePartyCharacter;
import net.world.PartyOperation;
import net.world.World;
import server.maps.Event_DojoAgent;
import server.quest.MapleQuest;
import tools.data.LittleEndianAccessor;
import tools.packet.CWvsContext;

public class PartyOperationHandler extends AbstractMaplePacketHandler {

	public PartyOperationHandler(RecvPacketOpcode recv) {
		super(recv);
	}

	@Override
	public void handlePacket(final LittleEndianAccessor lea, final MapleClient c, final MapleCharacter chr) {
		int operation = lea.readByte();
        MapleParty party = c.getPlayer().getParty();
        MaplePartyCharacter player = new MaplePartyCharacter(c.getPlayer());

        switch (operation) {
            case 1: // Create party
            	lea.skip(1);
            	String partyName = lea.readMapleAsciiString();
                if (party == null) {
                    party = World.Party.createParty(player, partyName);
                    c.getPlayer().setParty(party);
                    c.getSession().write(CWvsContext.PartyPacket.partyCreated(party.getId(), party.getName()));
                } else {
                    if (party.getExpeditionId() > 0) {
                        c.getPlayer().dropMessage(5, "You may not create a party while in a raid.");
                        return;
                    }
                    if ((player.equals(party.getLeader())) && (party.getMembers().size() == 1)) {
                        c.getSession().write(CWvsContext.PartyPacket.partyCreated(party.getId(), party.getName()));
                    } else {
                        c.getPlayer().dropMessage(5, "You can't create a party because you are already in one.");
                    }
                }
                break;
            case 2: // Disband or leave party
                if (party == null) {
                    break;
                }
                if (party.getExpeditionId() > 0) {
                    c.getPlayer().dropMessage(5, "You may not leave a party while in a raid.");
                    return;
                }
                if (player.equals(party.getLeader()) && party.getMembers().size() > 1) {
                	//int leaderid = partyMember.getId();
                	//MaplePartyCharacter newLeader = null;
                	for(MaplePartyCharacter member : party.getMembers()) {
                		if(member.getId() != player.getId() && member.getId() != 0) {
                			//newLeader = member;
                			World.Party.updateParty(party.getId(), PartyOperation.CHANGE_LEADER, member);
                			break;
                		}
                	}
                    //World.Party.updateParty(party.getId(), PartyOperation.CHANGE_LEADER, newLeader);
                    World.Party.updateParty(party.getId(), PartyOperation.LEAVE, player);
                    if (c.getPlayer().getEventInstance() != null) {
                        c.getPlayer().getEventInstance().leftParty(c.getPlayer());
                    }
                } else if (player.equals(party.getLeader())) {
                    if (GameConstants.isDojo(c.getPlayer().getMapId())) {
                        Event_DojoAgent.failed(c.getPlayer());
                    }
                    if (c.getPlayer().getPyramidSubway() != null) {
                        c.getPlayer().getPyramidSubway().fail(c.getPlayer());
                    }
                    World.Party.updateParty(party.getId(), PartyOperation.DISBAND, player);
                    if (c.getPlayer().getEventInstance() != null) {
                        c.getPlayer().getEventInstance().disbandParty();
                    }
                } else {
                    if (GameConstants.isDojo(c.getPlayer().getMapId())) {
                        Event_DojoAgent.failed(c.getPlayer());
                    }
                    if (c.getPlayer().getPyramidSubway() != null) {
                        c.getPlayer().getPyramidSubway().fail(c.getPlayer());
                    }
                    World.Party.updateParty(party.getId(), PartyOperation.LEAVE, player);
                    if (c.getPlayer().getEventInstance() != null) {
                        c.getPlayer().getEventInstance().leftParty(c.getPlayer());
                    }
                }
                c.getPlayer().setParty(null);
                break;
            case 3:
                int partyid = lea.readInt();
                if (party == null) {
                    party = World.Party.getParty(partyid);
                    if (party != null) {
                        if (party.getExpeditionId() > 0) {
                            c.getPlayer().dropMessage(5, "You may not do party operations while in a raid.");
                            return;
                        }
                        if ((party.getMembers().size() < 8) && (c.getPlayer().getQuestNoAdd(MapleQuest.getInstance(122901)) == null)) {
                            c.getPlayer().setParty(party);
                            World.Party.updateParty(party.getId(), PartyOperation.JOIN, player);
                            c.getPlayer().receivePartyMemberHP();
                            c.getPlayer().updatePartyMemberHP();
                        } else {
                            c.getSession().write(CWvsContext.PartyPacket.partyStatusMessage(22, null));
                        }
                    } else {
                        c.getPlayer().dropMessage(5, "The party you are trying to join does not exist");
                    }
                } else {
                    c.getPlayer().dropMessage(5, "You can't join the party as you are already in one");
                }
                break;
            case 4: // Party invitation
                if (party == null) {
                    party = World.Party.createParty(player);
                    c.getPlayer().setParty(party);
                    c.getSession().write(CWvsContext.PartyPacket.partyCreated(party.getId(), party.getName()));
                }

                String inviteName = lea.readMapleAsciiString();
                MapleCharacter partyInvitee = World.getCharacterFromPlayerStorage(inviteName);
                if (partyInvitee != null) {
                	if ((partyInvitee.getParty() == null) && (partyInvitee.getQuestNoAdd(MapleQuest.getInstance(122901)) == null)) {
                        if (party.getExpeditionId() > 0) {
                            c.getPlayer().dropMessage(5, "You may not do party operations while in a raid.");
                            return;
                        }
                        if (party.getMembers().size() < 8) {
                            //c.getSession().write(CWvsContext.PartyPacket.partyStatusMessage(30, partyInvitee.getName()));
                        	c.getSession().write(CWvsContext.PartyPacket.partyRequestInvite(partyInvitee));
                            partyInvitee.getClient().getSession().write(CWvsContext.PartyPacket.partyInvite(c.getPlayer()));
                        } else {
                            c.getSession().write(CWvsContext.PartyPacket.partyStatusMessage(22, null));
                        }
                    } else {
                        //c.getPlayer().dropMessage(5, partyInvitee.getName() + " is already in a party.");
                        c.getPlayer().showMessage(11, partyInvitee.getName() + " is already in a party.");
                    }
                } else {
                    c.getSession().write(CWvsContext.PartyPacket.partyStatusMessage(17, null));
                }
                break;
            case 6: // Expel party member
                if ((party == null) || (player == null) || (!player.equals(party.getLeader()))) {
                    break;
                }
                if (party.getExpeditionId() > 0) {
                    c.getPlayer().dropMessage(5, "You may not do party operations while in a raid.");
                    return;
                }
                MaplePartyCharacter expelled = party.getMemberById(lea.readInt());
                if (expelled != null) {
                    if ((GameConstants.isDojo(c.getPlayer().getMapId())) && (expelled.isOnline())) {
                        Event_DojoAgent.failed(c.getPlayer());
                    }
                    if ((c.getPlayer().getPyramidSubway() != null) && (expelled.isOnline())) {
                        c.getPlayer().getPyramidSubway().fail(c.getPlayer());
                    }
                    World.Party.updateParty(party.getId(), PartyOperation.EXPEL, expelled);
                    if (c.getPlayer().getEventInstance() != null) {
                        if (expelled.isOnline()) {
                            c.getPlayer().getEventInstance().disbandParty();
                        }
                    }
                }
                break;
            case 7: // Change leader
                if (party == null) {
                    break;
                }
                if (party.getExpeditionId() > 0) {
                    c.getPlayer().dropMessage(5, "You may not do party operations while in a raid.");
                    return;
                }
                MaplePartyCharacter newLeader = party.getMemberById(lea.readInt());
                if ((newLeader != null) && (player.equals(party.getLeader()))) {
                    World.Party.updateParty(party.getId(), PartyOperation.CHANGE_LEADER, newLeader);
                }
                break;
            case 66://was 7
                if (party != null) {
                    if ((c.getPlayer().getEventInstance() != null) || (c.getPlayer().getPyramidSubway() != null) || (party.getExpeditionId() > 0) || (GameConstants.isDojo(c.getPlayer().getMapId()))) {
                        c.getPlayer().dropMessage(5, "You may not do party operations while in a raid.");
                        return;
                    }
                    if (player.equals(party.getLeader())) {
                        World.Party.updateParty(party.getId(), PartyOperation.DISBAND, player);
                    } else {
                        World.Party.updateParty(party.getId(), PartyOperation.LEAVE, player);
                    }
                    c.getPlayer().setParty(null);
                }
                int partyid_ = lea.readInt();
                party = World.Party.getParty(partyid_);
                if ((party == null) || (party.getMembers().size() >= 8)) {
                    break;
                }
                if (party.getExpeditionId() > 0) {
                    c.getPlayer().dropMessage(5, "You may not do party operations while in a raid.");
                    return;
                }
                MapleCharacter cfrom = c.getPlayer().getMap().getCharacterById(party.getLeader().getId());
                if ((cfrom != null) && (cfrom.getQuestNoAdd(MapleQuest.getInstance(122900)) == null)) {
                    c.getSession().write(CWvsContext.PartyPacket.partyStatusMessage(50, c.getPlayer().getName()));
                    cfrom.getClient().getSession().write(CWvsContext.PartyPacket.partyRequestInvite(c.getPlayer()));
                } else {
                    c.getPlayer().dropMessage(5, "Player was not found or player is not accepting party requests.");
                }
                break;
            case 8:
                if (lea.readByte() > 0) {
                    c.getPlayer().getQuestRemove(MapleQuest.getInstance(122900));
                } else {
                    c.getPlayer().getQuestNAdd(MapleQuest.getInstance(122900));
                }
                break;
            default:
                System.out.println("Unhandled Party function." + operation);
        }
	}

}
