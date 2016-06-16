package net.channel.handler;

import client.MapleCharacter;
import client.MapleClient;
import net.MaplePacketHandler;
import net.RecvPacketOpcode;
import net.netty.MaplePacketReader;
import net.packet.CWvsContext;
import net.packet.CWvsContext.AlliancePacket;
import net.world.World;
import net.world.guild.MapleGuild;

public class AllianceOperationHandler extends MaplePacketHandler {

	public AllianceOperationHandler(RecvPacketOpcode recv) {
		super(recv);
	}

	@Override
	public void handlePacket(MaplePacketReader inPacket, MapleClient client, MapleCharacter chr) {
		boolean denied = recv == RecvPacketOpcode.ALLIANCE_OPERATION ? false : true;
		
		if (client.getCharacter().getGuildId() <= 0) {
            client.sendPacket(CWvsContext.enableActions());
            return;
        }
        final MapleGuild gs = World.Guild.getGuild(client.getCharacter().getGuildId());
        if (gs == null) {
            client.sendPacket(CWvsContext.enableActions());
            return;
        }
        //System.out.println("Unhandled GuildAlliance \n" + inPacket.toString());
        byte op = inPacket.readByte();
        if (client.getCharacter().getGuildRank() != 1 && op != 1) { //only updating doesn't need guild leader
            return;
        }
        if (op == 22) {
            denied = true;
        }
        int leaderid = 0;
        if (gs.getAllianceId() > 0) {
            leaderid = World.Alliance.getAllianceLeader(gs.getAllianceId());
        }
        //accept invite, and deny invite don't need allianceid.
        if (op != 4 && !denied) {
            if (gs.getAllianceId() <= 0 || leaderid <= 0) {
                return;
            }
        } else if (leaderid > 0 || gs.getAllianceId() > 0) { //infact, if they have allianceid it's suspicious
            return;
        }
        if (denied) {
            denyInvite(client, gs);
            return;
        }
        int inviteid;
        switch (op) {
            case 1: //load... must be in world op

                for (byte[] pack : World.Alliance.getAllianceInfo(gs.getAllianceId(), false)) {
                    if (pack != null) {
                        client.sendPacket(pack);
                    }
                }
                break;
            case 3: //invite
                final int newGuild = World.Guild.getGuildLeader(inPacket.readMapleAsciiString());
                if (newGuild > 0 && client.getCharacter().getAllianceRank() == 1 && leaderid == client.getCharacter().getId()) {
                    chr = client.getChannelServer().getPlayerStorage().getCharacterById(newGuild);
                    if (chr != null && chr.getGuildId() > 0 && World.Alliance.canInvite(gs.getAllianceId())) {
                        chr.getClient().sendPacket(AlliancePacket.sendAllianceInvite(World.Alliance.getAlliance(gs.getAllianceId()).getName(), client.getCharacter()));
                        World.Guild.setInvitedId(chr.getGuildId(), gs.getAllianceId());
                    } else {
                        client.getCharacter().dropMessage(1, "Make sure the leader of the guild is online and in your channel.");
                    }
                } else {
                    client.getCharacter().dropMessage(1, "That Guild was not found. Please enter the correct Guild Name. (Not the player name)");
                }
                break;
            case 4: //accept invite... guildid that invited(int, a/b check) -> guildname that was invited? but we dont care about that
                inviteid = World.Guild.getInvitedId(client.getCharacter().getGuildId());
                if (inviteid > 0) {
                    if (!World.Alliance.addGuildToAlliance(inviteid, client.getCharacter().getGuildId())) {
                        client.getCharacter().dropMessage(5, "An error occured when adding guild.");
                    }
                    World.Guild.setInvitedId(client.getCharacter().getGuildId(), 0);
                }
                break;
            case 2: //leave; nothing
            case 6: //expel, guildid(int) -> allianceid(don't care, a/b check)
                final int gid;
                if (op == 6 && inPacket.available() >= 4) {
                    gid = inPacket.readInt();
                    if (inPacket.available() >= 4 && gs.getAllianceId() != inPacket.readInt()) {
                        break;
                    }
                } else {
                    gid = client.getCharacter().getGuildId();
                }
                if (client.getCharacter().getAllianceRank() <= 2 && (client.getCharacter().getAllianceRank() == 1 || client.getCharacter().getGuildId() == gid)) {
                    if (!World.Alliance.removeGuildFromAlliance(gs.getAllianceId(), gid, client.getCharacter().getGuildId() != gid)) {
                        client.getCharacter().dropMessage(5, "An error occured when removing guild.");
                    }
                }
                break;
            case 7: //change leader
                if (client.getCharacter().getAllianceRank() == 1 && leaderid == client.getCharacter().getId()) {
                    if (!World.Alliance.changeAllianceLeader(gs.getAllianceId(), inPacket.readInt())) {
                        client.getCharacter().dropMessage(5, "An error occured when changing leader.");
                    }
                }
                break;
            case 8: //title update
                if (client.getCharacter().getAllianceRank() == 1 && leaderid == client.getCharacter().getId()) {
                    String[] ranks = new String[5];
                    for (int i = 0; i < 5; i++) {
                        ranks[i] = inPacket.readMapleAsciiString();
                    }
                    World.Alliance.updateAllianceRanks(gs.getAllianceId(), ranks);
                }
                break;
            case 9:
                if (client.getCharacter().getAllianceRank() <= 2) {
                    if (!World.Alliance.changeAllianceRank(gs.getAllianceId(), inPacket.readInt(), inPacket.readByte())) {
                        client.getCharacter().dropMessage(5, "An error occured when changing rank.");
                    }
                }
                break;
            case 10: //notice update
                if (client.getCharacter().getAllianceRank() <= 2) {
                    final String notice = inPacket.readMapleAsciiString();
                    if (notice.length() > 100) {
                        break;
                    }
                    World.Alliance.updateAllianceNotice(gs.getAllianceId(), notice);
                }
                break;
            default:
                System.out.println("Unhandled GuildAlliance op: " + op + ", \n" + inPacket.toString());
                break;
        }
        //c.sendPacket(CWvsContext.enableActions());
	}
	
	private static final void denyInvite(MapleClient c, final MapleGuild gs) { //playername that invited -> guildname that was invited but we also don't care
        final int inviteid = World.Guild.getInvitedId(c.getCharacter().getGuildId());
        if (inviteid > 0) {
            final int newAlliance = World.Alliance.getAllianceLeader(inviteid);
            if (newAlliance > 0) {
                final MapleCharacter chr = c.getChannelServer().getPlayerStorage().getCharacterById(newAlliance);
                if (chr != null) {
                    chr.dropMessage(5, gs.getName() + " Guild has rejected the Guild Union invitation.");
                }
                World.Guild.setInvitedId(c.getCharacter().getGuildId(), 0);
            }
        }
        //c.sendPacket(CWvsContext.enableActions());
    }

}
