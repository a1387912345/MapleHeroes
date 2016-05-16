package net.server.channel.handlers;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import client.MapleCharacter;
import client.MapleClient;
import client.Skill;
import client.SkillFactory;
import net.AbstractMaplePacketHandler;
import net.RecvPacketOpcode;
import net.world.World;
import net.world.guild.MapleGuild;
import net.world.guild.MapleGuildResponse;
import server.MapleStatEffect;
import tools.Pair;
import tools.data.LittleEndianAccessor;
import tools.packet.CField;
import tools.packet.CWvsContext.GuildPacket;

public class GuildOperationHandler extends AbstractMaplePacketHandler {

	private static final Map<String, Pair<Integer, Long>> invited = new HashMap<>();
    private static long nextPruneTime = System.currentTimeMillis() + 5 * 60 * 1000;
    
	public GuildOperationHandler(RecvPacketOpcode recv) {
		super(recv);
	}

	@Override
	public void handlePacket(LittleEndianAccessor lea, MapleClient c, MapleCharacter chr) {
		final long currentTime = System.currentTimeMillis();
		int guildId, charid;
		String charName;
        if (currentTime >= nextPruneTime) {
            Iterator<Entry<String, Pair<Integer, Long>>> itr = getInvited().entrySet().iterator();
            Entry<String, Pair<Integer, Long>> inv;
            while (itr.hasNext()) {
                inv = itr.next();
                if (currentTime >= inv.getValue().right) {
                    itr.remove();
                }
            }
            nextPruneTime += 5 * 60 * 1000;
        }

        switch (lea.readByte()) {
        	case 1: // Accept Guild Invitation
	            if (c.getPlayer().getGuildId() > 0) {
	            	c.getPlayer().dropMessage(1, "You have already joined a guild.");
	                return;
	            }
	            guildId = lea.readInt(); // guild leader id or inviter id?

	            charName = c.getPlayer().getName().toLowerCase();
	            Pair<Integer, Long> gid = getInvited().remove(charName);
	            if (gid != null && guildId == gid.left) {
	                c.getPlayer().setGuildId(guildId);
	                c.getPlayer().setGuildRank((byte) 5);
	                int s = World.Guild.addGuildMember(c.getPlayer().getMGC());
	                if (s == 0) {
	                    c.getPlayer().dropMessage(1, "The guild you are trying to join is already full.");
	                    c.getPlayer().setGuildId(0);
	                    return;
	                }
	                c.getSession().write(GuildPacket.showGuildInfo(c.getPlayer()));
	                final MapleGuild gs = World.Guild.getGuild(guildId);
	                for (byte[] pack : World.Alliance.getAllianceInfo(gs.getAllianceId(), true)) {
	                    if (pack != null) {
	                        c.getSession().write(pack);
	                    }
	                }
	                c.getPlayer().saveGuildStatus();
	                respawnPlayer(c.getPlayer());
	            }
	            break;
            case 4: // Create Guild
                if (c.getPlayer().getGuildId() > 0 || c.getPlayer().getMapId() != 200000301) {
                    c.getPlayer().dropMessage(1, "You cannot create a new guild while in one.");
                    return;
                } else if (c.getPlayer().getMeso() < 500000) {
                    c.getPlayer().dropMessage(1, "You do not have enough mesos to create a guild.");
                    return;
                }
                final String guildName = lea.readMapleAsciiString();

                if (!isGuildNameAcceptable(guildName)) {
                    c.getPlayer().dropMessage(1, "The guild name you have chosen is not acceptable.");
                    return;
                }
                guildId = World.Guild.createGuild(c.getPlayer().getId(), guildName);
                if (guildId == 0) {
                    c.getPlayer().dropMessage(1, "An error occured when trying to create a new guild. Please try again.");
                    return;
                }
                c.getPlayer().gainMeso(-5000000, true, true);
                c.getPlayer().setGuildId(guildId);
                c.getPlayer().setGuildRank((byte) 1);
                c.getPlayer().saveGuildStatus();
                c.getPlayer().finishAchievement(35);
                World.Guild.setGuildMemberOnline(c.getPlayer().getMGC(), true, c.getChannel());
                //c.getSession().write(GuildPacket.showGuildInfo(c.getPlayer()));
                c.getSession().write(GuildPacket.newGuildInfo(c.getPlayer()));
                World.Guild.gainGP(c.getPlayer().getGuildId(), 500, c.getPlayer().getId());
                //c.getPlayer().dropMessage(1, "You have successfully created a Guild.");
                respawnPlayer(c.getPlayer());
                break;
            case 7: // Invite Player to Guild
                if (c.getPlayer().getGuildId() <= 0 || c.getPlayer().getGuildRank() > 2) { // 1 == guild master, 2 == jr
                    return;
                }
                charName = lea.readMapleAsciiString().toLowerCase();
                if (getInvited().containsKey(charName)) {
                    c.getPlayer().dropMessage(5, "The player is currently handling an invitation.");
                    //return;
                }
                final MapleGuildResponse mgr = MapleGuild.sendInvite(c, charName);

                if (mgr != null) {
                    c.getSession().write(mgr.getPacket());
                } else {
                	Pair<Integer, Long> put = getInvited().put(charName, new Pair<>(c.getPlayer().getGuildId(), currentTime + (20 * 60000))); //20 mins expire
                }
                break;
            
            case 11: // Leave Guild
                charid = lea.readInt();
                charName = lea.readMapleAsciiString();

                if (charid != c.getPlayer().getId() || !charName.equals(c.getPlayer().getName()) || c.getPlayer().getGuildId() <= 0) {
                    return;
                }
                World.Guild.leaveGuild(c.getPlayer().getMGC());
                respawnPlayer(c.getPlayer());
                //c.getSession().write(GuildPacket.showGuildInfo(null));
                break;
            case 12: // Expel Member from Guild
                charid = lea.readInt();
                charName = lea.readMapleAsciiString();

                if (c.getPlayer().getGuildRank() > 2 || c.getPlayer().getGuildId() <= 0) {
                    return;
                }
                World.Guild.expelMember(c.getPlayer().getMGC(), charName, charid);
                break;
            case 18: // Change Guild Rank Titles
                if (c.getPlayer().getGuildId() <= 0 || c.getPlayer().getGuildRank() != 1) {
                    return;
                }
                String ranks[] = new String[5];
                for (int i = 0; i < 5; i++) {
                    ranks[i] = lea.readMapleAsciiString();
                }

                World.Guild.changeRankTitle(c.getPlayer().getGuildId(), ranks);
                break;
            case 19: // Change Guild Member's Rank
                charid = lea.readInt();
                byte newRank = lea.readByte();

                if ((newRank <= 1 || newRank > 5) || c.getPlayer().getGuildRank() > 2 || (newRank <= 2 && c.getPlayer().getGuildRank() != 1) || c.getPlayer().getGuildId() <= 0) {
                    return;
                }

                World.Guild.changeRank(c.getPlayer().getGuildId(), charid, newRank);
                break;
            case 20: // Change Guild Emblem
                if (c.getPlayer().getGuildId() <= 0 || c.getPlayer().getGuildRank() != 1) {
                    return;
                }

                if (c.getPlayer().getMeso() < 1500000) {
                    c.getPlayer().dropMessage(1, "You do not have enough mesos to create an emblem.");
                    return;
                }
                final short bg = lea.readShort();
                final byte bgcolor = lea.readByte();
                final short logo = lea.readShort();
                final byte logocolor = lea.readByte();

                World.Guild.setGuildEmblem(c.getPlayer().getGuildId(), bg, bgcolor, logo, logocolor);

                c.getPlayer().gainMeso(-1500000, true, true);
                respawnPlayer(c.getPlayer());
                break;
            case 0x11: // guild notice change
                final String notice = lea.readMapleAsciiString();
                if (notice.length() > 100 || c.getPlayer().getGuildId() <= 0 || c.getPlayer().getGuildRank() > 2) {
                    return;
                }
                World.Guild.setGuildNotice(c.getPlayer().getGuildId(), notice);
                break;
            case 0x1d: //guild skill purchase
                Skill skilli = SkillFactory.getSkill(lea.readInt());
                if (c.getPlayer().getGuildId() <= 0 || skilli == null || skilli.getId() < 91000000) {
                    return;
                }
                int eff = World.Guild.getSkillLevel(c.getPlayer().getGuildId(), skilli.getId()) + 1;
                if (eff > skilli.getMaxLevel()) {
                    return;
                }
                final MapleStatEffect skillid = skilli.getEffect(eff);
                if (skillid.getReqGuildLevel() <= 0 || c.getPlayer().getMeso() < skillid.getPrice()) {
                    return;
                }
                if (World.Guild.purchaseSkill(c.getPlayer().getGuildId(), skillid.getSourceId(), c.getPlayer().getName(), c.getPlayer().getId())) {
                    c.getPlayer().gainMeso(-skillid.getPrice(), true);
                }
                break;
            case 0x1e: //guild skill activation
                skilli = SkillFactory.getSkill(lea.readInt());
                if (c.getPlayer().getGuildId() <= 0 || skilli == null) {
                    return;
                }
                eff = World.Guild.getSkillLevel(c.getPlayer().getGuildId(), skilli.getId());
                if (eff <= 0) {
                    return;
                }
                final MapleStatEffect skillii = skilli.getEffect(eff);
                if (skillii.getReqGuildLevel() < 0 || c.getPlayer().getMeso() < skillii.getExtendPrice()) {
                    return;
                }
                if (World.Guild.activateSkill(c.getPlayer().getGuildId(), skillii.getSourceId(), c.getPlayer().getName())) {
                    c.getPlayer().gainMeso(-skillii.getExtendPrice(), true);
                }
                break;
            case 0x1f: //guild leader change
                charid = lea.readInt();
                if (c.getPlayer().getGuildId() <= 0 || c.getPlayer().getGuildRank() > 1) {
                    return;
                }
                World.Guild.setGuildLeader(c.getPlayer().getGuildId(), charid);
                break;
        }
	}
	
	private static boolean isGuildNameAcceptable(final String name) {
        if (name.length() < 3 || name.length() > 12) {
            return false;
        }
        for (int i = 0; i < name.length(); i++) {
            if (!Character.isLowerCase(name.charAt(i)) && !Character.isUpperCase(name.charAt(i))) {
                return false;
            }
        }
        return true;
    }
	
	private static void respawnPlayer(final MapleCharacter mc) {
        if (mc.getMap() == null) {
            return;
        }
        mc.getMap().broadcastMessage(CField.loadGuildName(mc));
        mc.getMap().broadcastMessage(CField.loadGuildIcon(mc));
    }

	public static Map<String, Pair<Integer, Long>> getInvited() {
		return invited;
	}

}
