package net.packet;

import client.*;
import client.MapleStat.Temp;
import client.inventory.*;
import constants.GameConstants;
import constants.Skills.Bishop;
import constants.Skills.IceLightningMage;
import constants.Skills.FirePoisonMage;
import net.SendPacketOpcode;
import net.channel.DojoRankingsData;
import net.channel.MapleGuildRanking;
import net.channel.MapleGeneralRanking.CandyRankingInfo;
import net.netty.MaplePacketWriter;
import net.world.MapleParty;
import net.world.MaplePartyCharacter;
import net.world.PartyOperation;
import net.world.World;
import net.world.exped.MapleExpedition;
import net.world.exped.PartySearch;
import net.world.exped.PartySearchType;
import net.world.family.MapleFamily;
import net.world.family.MapleFamilyBuff;
import net.world.family.MapleFamilyCharacter;
import net.world.guild.*;

import java.awt.Point;
import java.util.*;
import java.util.Map.Entry;
import server.MapleItemInformationProvider;
import server.MapleStatEffect;
import server.Randomizer;
import server.StructFamiliar;
import server.life.MapleMonster;
import server.life.PlayerNPC;
import server.stores.HiredMerchant;
import server.stores.MaplePlayerShopItem;
import tools.HexTool;
import tools.Pair;
import tools.StringUtil;

public class CWvsContext {

    public static byte[] enableActions() {
        return updatePlayerStats(new EnumMap<MapleStat, Long>(MapleStat.class), true, null);
    }

    public static byte[] updatePlayerStats(Map<MapleStat, Long> stats, MapleCharacter chr) {
        return updatePlayerStats(stats, false, chr);
    }

    public static byte[] updatePlayerStats(Map<MapleStat, Long> mystats, boolean itemReaction, MapleCharacter chr) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.UPDATE_STATS);
		mplew.write(itemReaction ? 1 : 0);
        long updateMask = 0L;
        //Added
        if (mystats.containsKey(MapleStat.MAXMP) && GameConstants.isDemonSlayer(chr.getJob())) {
            mystats.remove(MapleStat.MAXMP);
            mystats.put(MapleStat.MAXMP, (long) 10);
        }
        // End
        for (MapleStat statupdate : mystats.keySet()) {
            updateMask |= statupdate.getValue();
        }
        mplew.writeLong(updateMask);
        for (final Entry<MapleStat, Long> statupdate : mystats.entrySet()) {
            switch (statupdate.getKey()) {
                case SKIN:
                case LEVEL:
                case FATIGUE:
                case BATTLE_RANK:
                case ICE_GAGE:
                    mplew.write((statupdate.getValue()).byteValue());
                    break;
//                case JOB:
                case STR:
                case DEX:
                case INT:
                case LUK:
                case AVAILABLEAP:
                    mplew.writeShort((statupdate.getValue()).shortValue());
                    break;
                // Added
                case JOB:
                	mplew.writeShort((statupdate.getValue()).shortValue());
                    mplew.writeShort(chr.getSubcategory()); // not sure about it
                    break;
                // Ended
                case AVAILABLESP:
                    if (GameConstants.isSeparatedSp(chr.getJob())) {
                        mplew.write(chr.getRemainingSpSize());
                        for (int i = 0; i < chr.getRemainingSps().length; i++) {
                            if (chr.getRemainingSp(i) > 0) {
                                mplew.write(i + 1);
                                mplew.writeInt(chr.getRemainingSp(i));
                            }
                        }
                    } else {
                        mplew.writeShort(chr.getRemainingSp());
                    }
                    break;
                case TRAIT_LIMIT:
                    mplew.writeInt((statupdate.getValue()).intValue());
                    mplew.writeInt((statupdate.getValue()).intValue());
                    mplew.writeInt((statupdate.getValue()).intValue());
                    break;
                case EXP:
                case MESO:
                    mplew.writeLong((statupdate.getValue()).longValue());
                    break;
                case PET:
                    mplew.writeLong((statupdate.getValue()).intValue());
                    mplew.writeLong((statupdate.getValue()).intValue());
                    mplew.writeLong((statupdate.getValue()).intValue());
                    break;
                case BATTLE_POINTS:
                case VIRTUE:
                    mplew.writeLong((statupdate.getValue()).longValue());
                    break;
                default:
                    mplew.writeInt((statupdate.getValue()).intValue());
            }
        }

        if ((updateMask == 0L) && (!itemReaction)) {
            mplew.write(1);
        }
        mplew.write(-1); // nMixBaseHairColor
        mplew.write(0); // nMixAddHairColor
        mplew.write(0); // nMixHairBaseProb
        mplew.write(0); // aLevelQuest
        mplew.write(0); // battleRecoveryInfo
        
        return mplew.getPacket();
    }

    public static byte[] setTemporaryStats(short str, short dex, short _int, short luk, short watk, short matk, short acc, short avoid, short speed, short jump) {
        Map<Temp, Integer> stats = new EnumMap<>(MapleStat.Temp.class);

        stats.put(MapleStat.Temp.STR, Integer.valueOf(str));
        stats.put(MapleStat.Temp.DEX, Integer.valueOf(dex));
        stats.put(MapleStat.Temp.INT, Integer.valueOf(_int));
        stats.put(MapleStat.Temp.LUK, Integer.valueOf(luk));
        stats.put(MapleStat.Temp.WATK, Integer.valueOf(watk));
        stats.put(MapleStat.Temp.MATK, Integer.valueOf(matk));
        stats.put(MapleStat.Temp.ACC, Integer.valueOf(acc));
        stats.put(MapleStat.Temp.AVOID, Integer.valueOf(avoid));
        stats.put(MapleStat.Temp.SPEED, Integer.valueOf(speed));
        stats.put(MapleStat.Temp.JUMP, Integer.valueOf(jump));

        return temporaryStats(stats);
    }

    public static byte[] temporaryStats_Aran() {
        Map<Temp, Integer> stats = new EnumMap<>(MapleStat.Temp.class);

        stats.put(MapleStat.Temp.STR, Integer.valueOf(999));
        stats.put(MapleStat.Temp.DEX, Integer.valueOf(999));
        stats.put(MapleStat.Temp.INT, Integer.valueOf(999));
        stats.put(MapleStat.Temp.LUK, Integer.valueOf(999));
        stats.put(MapleStat.Temp.WATK, Integer.valueOf(255));
        stats.put(MapleStat.Temp.ACC, Integer.valueOf(999));
        stats.put(MapleStat.Temp.AVOID, Integer.valueOf(999));
        stats.put(MapleStat.Temp.SPEED, Integer.valueOf(140));
        stats.put(MapleStat.Temp.JUMP, Integer.valueOf(120));

        return temporaryStats(stats);
    }

    public static byte[] temporaryStats_Balrog(MapleCharacter chr) {
        Map<Temp, Integer> stats = new EnumMap<>(MapleStat.Temp.class);

        int offset = 1 + (chr.getLevel() - 90) / 20;
        stats.put(MapleStat.Temp.STR, Integer.valueOf(chr.getStat().getTotalStr() / offset));
        stats.put(MapleStat.Temp.DEX, Integer.valueOf(chr.getStat().getTotalDex() / offset));
        stats.put(MapleStat.Temp.INT, Integer.valueOf(chr.getStat().getTotalInt() / offset));
        stats.put(MapleStat.Temp.LUK, Integer.valueOf(chr.getStat().getTotalLuk() / offset));
        stats.put(MapleStat.Temp.WATK, Integer.valueOf(chr.getStat().getTotalWatk() / offset));
        stats.put(MapleStat.Temp.MATK, Integer.valueOf(chr.getStat().getTotalMagic() / offset));

        return temporaryStats(stats);
    }

    public static byte[] temporaryStats(Map<MapleStat.Temp, Integer> mystats) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.TEMP_STATS);
        int updateMask = 0;
        for (MapleStat.Temp statupdate : mystats.keySet()) {
            updateMask |= statupdate.getValue();
        }
        mplew.writeInt(updateMask);
        for (final Entry<MapleStat.Temp, Integer> statupdate : mystats.entrySet()) {
            switch (statupdate.getKey()) {
                case SPEED:
                case JUMP:
                case UNKNOWN:
                    mplew.write((statupdate.getValue()).byteValue());
                    break;
                default:
                    mplew.writeShort((statupdate.getValue()).shortValue());
            }
        }

        return mplew.getPacket();
    }

    public static byte[] temporaryStats_Reset() {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.TEMP_STATS_RESET);

        return mplew.getPacket();
    }

    public static byte[] updateSkills(Map<Skill, SkillEntry> update, boolean hyper) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.UPDATE_SKILLS);
		mplew.write(1);
        mplew.writeShort(0);//wasbyte142
        mplew.writeShort(update.size());
        for (Map.Entry z : update.entrySet()) {
            mplew.writeInt(((Skill) z.getKey()).getId());
            mplew.writeInt(((SkillEntry) z.getValue()).skillevel);
            mplew.writeInt(((SkillEntry) z.getValue()).masterlevel);
            PacketHelper.addExpirationTime(mplew, ((SkillEntry) z.getValue()).expiration);
        }
        mplew.write(/*hyper ? 0x0C : */4);
        
        return mplew.getPacket();
    }
    
    
    public static byte[] updateSkill(Map<Skill, SkillEntry> update, int skillid, int level, int masterlevel, long expiration) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.UPDATE_SKILLS);
		mplew.write(1);
        mplew.writeShort(0);//wasbyte142
        mplew.writeShort(update.size());
        for (Map.Entry z : update.entrySet()) {
            mplew.writeInt(((Skill) z.getKey()).getId());
            mplew.writeInt(((SkillEntry) z.getValue()).skillevel);
            mplew.writeInt(((SkillEntry) z.getValue()).masterlevel);
            PacketHelper.addExpirationTime(mplew, ((SkillEntry) z.getValue()).expiration);
        }
        mplew.write(/*hyper ? 0x0C : */4);
        
        return mplew.getPacket();
    }

    public static byte[] giveFameErrorResponse(int op) {
        return OnFameResult(op, null, true, 0);
    }

    public static byte[] OnFameResult(int op, String charname, boolean raise, int newFame) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.FAME_RESPONSE);
		mplew.write(op);
        if ((op == 0) || (op == 5)) {
            mplew.writeMapleAsciiString(charname == null ? "" : charname);
            mplew.write(raise ? 1 : 0);
            if (op == 0) {
                mplew.writeInt(newFame);
            }
        }

        return mplew.getPacket();
    }

    public static byte[] fullClientDownload() {
        //Opens "http://maplestory.nexon.net/support/game-download"
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.FULL_CLIENT_DOWNLOAD);

        return mplew.getPacket();
    }

    public static byte[] bombLieDetector(boolean error, int mapid, int channel) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.LIE_DETECTOR);
		mplew.write(error ? 2 : 1);
        mplew.writeInt(mapid);
        mplew.writeInt(channel);

        return mplew.getPacket();
    }

    public static byte[] sendLieDetector(final byte[] image) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.LIE_DETECTOR);
		mplew.write(6); // 1 = not attacking, 2 = tested, 3 = going through 

        mplew.write(4); // 2 give invalid pointer (suppose to be admin macro) 
        mplew.write(1); // the time >0 is always 1 minute 
        if (image == null) {
            mplew.writeInt(0);
            return mplew.getPacket();
        }
        mplew.writeInt(image.length);
        mplew.write(image);

        return mplew.getPacket();
    }

    public static byte[] LieDetectorResponse(final byte msg) {
        return LieDetectorResponse(msg, (byte) 0);
    }

    public static byte[] LieDetectorResponse(final byte msg, final byte msg2) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.LIE_DETECTOR);
		mplew.write(msg); // 1 = not attacking, 2 = tested, 3 = going through 
        mplew.write(msg2);

        return mplew.getPacket();
    }

    public static byte[] getLieDetector(byte type, String tester) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.LIE_DETECTOR); // 2A 00 01 00 00 00  
        mplew.write(type); // 1 = not attacking, 2 = tested, 3 = going through, 4 save screenshot 
        switch (type) {
            case 4: //save screen shot 
                mplew.write(0);
                mplew.writeMapleAsciiString(""); // file name 
                break;
            case 5:
                mplew.write(1); // 2 = save screen shot 
                mplew.writeMapleAsciiString(tester); // me or file name 
                break;
            case 6:
                mplew.write(4); // 2 or anything else, 2 = with maple admin picture, basicaly manager's skill? 
                mplew.write(1); // if > 0, then time = 60,000..maybe try < 0? 
                //mplew.writeInt(size);
                //mplew.write(byte); // bytes 
                break;
            case 7://send this if failed 
                // 2 = You have been appointed as a auto BOT program user and will be restrained. 
                mplew.write(4); // default 
                break;
            case 9:
                // 0 = passed lie detector test 
                // 1 = reward 5000 mesos for not botting. 
                // 2 = thank you for your cooperation with administrator. 
                mplew.write(0);
                break;
            case 8: // save screen shot.. it appears that you may be using a macro-assisted program
                mplew.write(0); // 2 or anything else , 2 = show msg, 0 = none 
                mplew.writeMapleAsciiString(""); // file name 
                break;
            case 10: // no save 
                mplew.write(0); // 2 or anything else, 2 = show msg 
                mplew.writeMapleAsciiString(""); // ?? // hi_You have passed the lie detector test 
                break;
            default:
                mplew.write(0);
                break;
        }
        return mplew.getPacket();
    }

    public static byte[] lieDetector(byte mode, byte action, byte[] image, String str1, String str2, String str3) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.LIE_DETECTOR);
		mplew.write(mode);
        mplew.write(action); //2 = show msg/save screenshot/maple admin picture(mode 6)
        if (mode == 6) {
            mplew.write(1); //if true time is 60:00
            PacketHelper.addImageInfo(mplew, image);
        }
        if (mode == 7 || mode == 9) {
        }
        if (mode == 4) { //save screenshot
            mplew.writeMapleAsciiString(str1); //file name
        }
        if (mode != 5) {
            if (mode == 10) {
                mplew.writeMapleAsciiString(str2); //passed lie detector message
            } else {
                if (mode != 8) {
                }
                mplew.writeMapleAsciiString(str2); //failed lie detector, file name (for screenshot)
            }
        }
        mplew.writeMapleAsciiString(str3); //file name for screenshot

        return mplew.getPacket();
    }

    public static byte[] report(int mode) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.REPORT_RESPONSE);
		mplew.write(mode);
        if (mode == 2) {
            mplew.write(0);
            mplew.writeInt(1); //times left to report
        }

        return mplew.getPacket();
    }

    public static byte[] OnSetClaimSvrAvailableTime(int from, int to) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.REPORT_TIME);
		mplew.write(from);
        mplew.write(to);

        return mplew.getPacket();
    }

    public static byte[] OnClaimSvrStatusChanged(boolean enable) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.REPORT_STATUS);
		mplew.write(enable ? 1 : 0);

        return mplew.getPacket();
    }

    public static byte[] updateMount(MapleCharacter chr, boolean levelup) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.UPDATE_MOUNT);
		mplew.writeInt(chr.getId());
        mplew.writeInt(chr.getMount().getLevel());
        mplew.writeInt(chr.getMount().getExp());
        mplew.writeInt(chr.getMount().getFatigue());
        mplew.write(levelup ? 1 : 0);

        return mplew.getPacket();
    }

    public static byte[] showQuestCompletion(int id) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.SHOW_QUEST_COMPLETION);
		mplew.writeShort(id);

        return mplew.getPacket();
    }

    public static byte[] useSkillBook(MapleCharacter chr, int skillid, int maxlevel, boolean canuse, boolean success) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.USE_SKILL_BOOK);
		mplew.write(0);
        mplew.writeInt(chr.getId());
        mplew.write(1);
        mplew.writeInt(skillid);
        mplew.writeInt(maxlevel);
        mplew.write(canuse ? 1 : 0);
        mplew.write(success ? 1 : 0);

        return mplew.getPacket();
    }

    public static byte[] useAPSPReset(boolean spReset, int cid) {
        MaplePacketWriter mplew = new MaplePacketWriter(spReset ? SendPacketOpcode.SP_RESET : SendPacketOpcode.AP_RESET);
		mplew.write(1);
        mplew.writeInt(cid);
        mplew.write(1);

        return mplew.getPacket();
    }

    public static byte[] expandCharacterSlots(int mode) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.EXPAND_CHARACTER_SLOTS);
		mplew.writeInt(mode);
        mplew.write(0);

        return mplew.getPacket();
    }

    public static byte[] finishedGather(int type) {
        return gatherSortItem(true, type);
    }

    public static byte[] finishedSort(int type) {
        return gatherSortItem(false, type);
    }

    public static byte[] gatherSortItem(boolean gather, int type) {
        MaplePacketWriter mplew = new MaplePacketWriter(gather ? SendPacketOpcode.FINISH_GATHER : SendPacketOpcode.FINISH_SORT);
		mplew.write(1);
        mplew.write(type);

        return mplew.getPacket();
    }

    public static byte[] updateExpPotion(int mode, int id, int itemId, boolean firstTime, int level, int potionDstLevel) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.EXP_POTION);
		mplew.write(mode);
        mplew.write(1); //bool for get_update_time
        mplew.writeInt(id);
        if (id != 0) {
            mplew.write(1); //not even being read how rude of nexon
            if (mode == 1) {
                mplew.writeInt(0);
            }
            if (mode == 2) {
                mplew.write(firstTime ? 1 : 0); //1 on first time then it turns 0
                mplew.writeInt(itemId);
                if (itemId != 0) {
                    mplew.writeInt(level); //level, confirmed
                    mplew.writeInt(potionDstLevel); //max level with potion
                    mplew.writeLong(384); //random, more like potion id
                }
            }
        }

        return mplew.getPacket();
    }

    public static byte[] updateGender(MapleCharacter chr) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.UPDATE_GENDER);
		mplew.write(chr.getGender());

        return mplew.getPacket();
    }

    public static byte[] charInfo(MapleCharacter chr, boolean isSelf) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.CHAR_INFO);
		mplew.writeInt(chr.getId());
        mplew.write(chr.getLevel());
        mplew.writeShort(chr.getJob());
        mplew.writeShort(chr.getSubcategory());
        mplew.write(chr.getStat().pvpRank);
        mplew.writeInt(chr.getFame());
        MapleMarriage marriage = chr.getMarriage();
        mplew.write(marriage != null && marriage.getId() != 0);
        if (marriage != null && marriage.getId() != 0) {
            mplew.writeInt(marriage.getId()); //marriage id
            mplew.writeInt(marriage.getHusbandId()); //husband char id
            mplew.writeInt(marriage.getWifeId()); //wife char id
            mplew.writeShort(3); //msg type
            mplew.writeInt(chr.getMarriageItemId()); //ring id husband
            mplew.writeInt(chr.getMarriageItemId()); //ring id wife
            mplew.writeAsciiString(marriage.getHusbandName(), 13); //husband name
            mplew.writeAsciiString(marriage.getWifeName(), 13); //wife name
        }
        List prof = chr.getProfessions();
        mplew.write(prof.size());
        for (Iterator i$ = prof.iterator(); i$.hasNext();) {
            int i = ((Integer) i$.next()).intValue();
            mplew.writeShort(i);
        }
        if (chr.getGuildId() <= 0) {
            mplew.writeMapleAsciiString("-");
            mplew.writeMapleAsciiString("");
        } else {
            MapleGuild gs = World.Guild.getGuild(chr.getGuildId());
            if (gs != null) {
                mplew.writeMapleAsciiString(gs.getName());
                if (gs.getAllianceId() > 0) {
                    MapleGuildAlliance allianceName = World.Alliance.getAlliance(gs.getAllianceId());
                    if (allianceName != null) {
                        mplew.writeMapleAsciiString(allianceName.getName());
                    } else {
                        mplew.writeMapleAsciiString("");
                    }
                } else {
                    mplew.writeMapleAsciiString("");
                }
            } else {
                mplew.writeMapleAsciiString("-");
                mplew.writeMapleAsciiString("");
            }
        }

        mplew.write(isSelf ? 1 : 0);
        mplew.write(0);


        byte index = 1;
        for (MaplePet pet : chr.getSummonedPets()) {
            if (index == 1) {   // please test if this doesn't d/c when viewing multipets
                mplew.write(index);
            }  
            mplew.writeInt(pet.getPetItemId());
            mplew.writeMapleAsciiString(pet.getName());
            mplew.write(pet.getLevel());
            mplew.writeShort(pet.getCloseness());
            mplew.write(pet.getFullness());
            mplew.writeShort(0);
            Item inv = chr.getInventory(MapleInventoryType.EQUIPPED).getItem((short) (byte) (index == 2 ? -130 : index == 1 ? -114 : -138));
            mplew.writeInt(inv == null ? 0 : inv.getItemId());
            mplew.writeInt(-1);//new v140
            mplew.write(chr.getSummonedPets().size() > index); //continue loop
            index++;
        }
        if (index == 1) { //index no change means no pets
            mplew.write(0);
        }
        /*if ((chr.getInventory(MapleInventoryType.EQUIPPED).getItem((byte) -18) != null) && (chr.getInventory(MapleInventoryType.EQUIPPED).getItem((byte) -19) != null)) {
         MapleMount mount = chr.getMount();
         mplew.write(1);
         mplew.writeInt(mount.getLevel());
         mplew.writeInt(mount.getExp());
         mplew.writeInt(mount.getFatigue());
         } else {
         mplew.write(0);
         }*/
        int wishlistSize = chr.getWishlistSize();
        mplew.write(wishlistSize);
        if (wishlistSize > 0) {
            int[] wishlist = chr.getWishlist();
            for (int x = 0; x < wishlistSize; x++) {
                mplew.writeInt(wishlist[x]);
            }
        }
        Item medal = chr.getInventory(MapleInventoryType.EQUIPPED).getItem((byte) -46);
        mplew.writeInt(medal == null ? 0 : medal.getItemId());
        List<Pair<Integer, Long>> medalQuests = chr.getCompletedMedals();
        mplew.writeShort(medalQuests.size());
        for (Pair x : medalQuests) {
            mplew.writeShort(((Integer) x.left).intValue());
            mplew.writeLong(((Long) x.right).longValue());
        }
        for (MapleTrait.MapleTraitType t : MapleTrait.MapleTraitType.values()) {
            mplew.write(chr.getTrait(t).getLevel());
        }

        mplew.writeInt(0); //farm id?
        PacketHelper.addFarmInfo(mplew, chr.getClient(), 0);

        mplew.writeInt(0);
        mplew.writeInt(0);

        List chairs = new ArrayList();
        for (Item i : chr.getInventory(MapleInventoryType.SETUP).newList()) {
            if ((i.getItemId() / 10000 == 301) && (!chairs.contains(Integer.valueOf(i.getItemId())))) {
                chairs.add(Integer.valueOf(i.getItemId()));
            }
        }
        mplew.writeInt(chairs.size());
        for (Iterator i$ = chairs.iterator(); i$.hasNext();) {
            int i = ((Integer) i$.next()).intValue();
            mplew.writeInt(i);
        }

        return mplew.getPacket();
    }

    public static byte[] getMonsterBookInfo(MapleCharacter chr) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.BOOK_INFO);
		mplew.writeInt(chr.getId());
        mplew.writeInt(chr.getLevel());
        chr.getMonsterBook().writeCharInfoPacket(mplew);

        return mplew.getPacket();
    }

    public static byte[] spawnPortal(int townId, int targetId, int skillId, Point pos) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.SPAWN_PORTAL);
		mplew.writeInt(townId);
        mplew.writeInt(targetId);
        if ((townId != 999999999) && (targetId != 999999999)) {
            mplew.writeInt(skillId);
            mplew.writePos(pos);
        }

        return mplew.getPacket();
    }

    public static byte[] mechPortal(Point pos) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.MECH_PORTAL);
		mplew.writePos(pos);

        return mplew.getPacket();
    }

    public static byte[] echoMegaphone(String name, String message) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.ECHO_MESSAGE);
		mplew.write(0);
        mplew.writeLong(PacketHelper.getTime(System.currentTimeMillis()));
        mplew.writeMapleAsciiString(name);
        mplew.writeMapleAsciiString(message);

        return mplew.getPacket();
    }

    public static byte[] showQuestMsg(String msg) {
        return broadcastMsg(5, msg);
    }

    public static byte[] Mulung_Pts(int recv, int total) {
        return showQuestMsg(new StringBuilder().append("You have received ").append(recv).append(" training points, for the accumulated total of ").append(total).append(" training points.").toString());
    }

    public static byte[] broadcastMsg(String message) {
        return broadcastMessage(4, 0, message, false);
    }

    public static byte[] broadcastMsg(int type, String message) {
        return broadcastMessage(type, 0, message, false);
    }

    public static byte[] broadcastMsg(int type, int channel, String message) {
        return broadcastMessage(type, channel, message, false);
    }

    public static byte[] broadcastMsg(int type, int channel, String message, boolean smegaEar) {
        return broadcastMessage(type, channel, message, smegaEar);
    }

    private static byte[] broadcastMessage(int type, int channel, String message, boolean megaEar) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.SERVERMESSAGE);
		mplew.write(type);
        if (type == 4) {
            mplew.write(1);
        }
        if ((type != 23) && (type != 24)) {
            mplew.writeMapleAsciiString(message);
        }
        switch (type) {
            case 3:
            case 22:
            case 25:
            case 26:
                mplew.write(channel - 1);
                mplew.write(megaEar ? 1 : 0);
                break;
            case 9:
                mplew.write(channel - 1);
                break;
            case 12:
                mplew.writeInt(channel);
                break;
            case 6:
            case 11:
            case 20:
                mplew.writeInt((channel >= 1000000) && (channel < 6000000) ? channel : 0);
                break;
            /*case 24:
                mplew.writeShort(0);
                break;*/
            case 4:
            case 5:
            case 7:
            case 8:
            case 10:
            case 13:
            case 14:
            case 15:
            case 16:
            case 17:
            case 18:
            case 19:
            case 21:
            case 23:
                break;
        }
        return mplew.getPacket();
    }

  public static byte[] getGachaponMega(String name, String message, Item item, byte rareness, String gacha) {
    return getGachaponMega(name, message, item, rareness, false, gacha);
  }

  public static byte[] getGachaponMega(String name, String message, Item item, byte rareness, boolean dragon, String gacha) {
    MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.SERVERMESSAGE);
		mplew.write(13);
    mplew.writeMapleAsciiString(new StringBuilder().append(name).append(message).toString());
    if (!dragon) {
      mplew.writeInt(0);
      mplew.writeInt(item.getItemId());
    }
    mplew.writeMapleAsciiString(gacha);
    PacketHelper.addItemInfo(mplew, item);

    return mplew.getPacket();
  }

    public static byte[] getEventEnvelope(int questID, int time) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.SERVERMESSAGE);
		mplew.write(23);
        mplew.writeShort(questID);
        mplew.writeInt(time);

        return mplew.getPacket();
    }

    public static byte[] tripleSmega(List<String> message, boolean ear, int channel) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.SERVERMESSAGE);
		mplew.write(10);
        if (message.get(0) != null) {
            mplew.writeMapleAsciiString((String) message.get(0));
        }
        mplew.write(message.size());
        for (int i = 1; i < message.size(); i++) {
            if (message.get(i) != null) {
                mplew.writeMapleAsciiString((String) message.get(i));
            }
        }
        mplew.write(channel - 1);
        mplew.write(ear ? 1 : 0);

        return mplew.getPacket();
    }

    public static byte[] itemMegaphone(String msg, boolean whisper, int channel, Item item) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.SERVERMESSAGE);
		mplew.write(9);
        mplew.writeMapleAsciiString(msg);
        mplew.write(channel - 1);
        mplew.write(whisper ? 1 : 0);
        PacketHelper.addItemPosition(mplew, item, true, false);
        if (item != null) {
            PacketHelper.addItemInfo(mplew, item);
        }

        return mplew.getPacket();
    }

    public static byte[] getPeanutResult(int itemId, short quantity, int itemId2, short quantity2, int ourItem) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.PIGMI_REWARD);
		mplew.writeInt(itemId);
        mplew.writeShort(quantity);
        mplew.writeInt(ourItem);
        mplew.writeInt(itemId2);
        mplew.writeInt(quantity2);
        mplew.write(0);
        mplew.write(0);

        return mplew.getPacket();
    }

    public static byte[] getOwlOpen() {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.OWL_OF_MINERVA);
		mplew.write(9);
        mplew.write(GameConstants.owlItems.length);
        for (int i : GameConstants.owlItems) {
            mplew.writeInt(i);
        }

        return mplew.getPacket();
    }

    public static byte[] getOwlSearched(int itemSearch, List<HiredMerchant> hms) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.OWL_OF_MINERVA);
		mplew.write(8);
        mplew.writeInt(0);
        mplew.writeInt(itemSearch);
        int size = 0;

        for (HiredMerchant hm : hms) {
            size += hm.searchItem(itemSearch).size();
        }

        mplew.writeInt(size);
        for (HiredMerchant hm : hms) {
            for (Iterator<HiredMerchant> i = hms.iterator(); i.hasNext();) {
                hm = (HiredMerchant) i.next();
                final List<MaplePlayerShopItem> items = hm.searchItem(itemSearch);
                for (MaplePlayerShopItem item : items) {
                    mplew.writeMapleAsciiString(hm.getOwnerName());
                    mplew.writeInt(hm.getMap().getId());
                    mplew.writeMapleAsciiString(hm.getDescription());
                    mplew.writeInt(item.item.getQuantity());
                    mplew.writeInt(item.bundles);
                    mplew.writeInt(item.price);
                    switch (2) {
                        case 0:
                            mplew.writeInt(hm.getOwnerId());
                            break;
                        case 1:
                            mplew.writeInt(hm.getStoreId());
                            break;
                        default:
                            mplew.writeInt(hm.getObjectId());
                    }

                    mplew.write(hm.getFreeSlot() == -1 ? 1 : 0);
                    mplew.write(GameConstants.getInventoryType(itemSearch).getType());
                    if (GameConstants.getInventoryType(itemSearch) == MapleInventoryType.EQUIP) {
                        PacketHelper.addItemInfo(mplew, item.item);
                    }
                }
            }
        }
        return mplew.getPacket();
    }

    public static byte[] getOwlMessage(int msg) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.OWL_RESULT);
		mplew.write(msg);

        return mplew.getPacket();
    }

    public static byte[] sendEngagementRequest(String name, int cid) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.ENGAGE_REQUEST);
		mplew.write(0);
        mplew.writeMapleAsciiString(name);
        mplew.writeInt(cid);

        return mplew.getPacket();
    }

    public static byte[] sendEngagement(byte msg, int item, MapleCharacter male, MapleCharacter female) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.ENGAGE_RESULT);
		mplew.write(msg);
        if (msg == 9 || msg >= 11 && msg <= 14) {
            mplew.writeInt(0);
            mplew.writeInt(male.getId());
            mplew.writeInt(female.getId());
            mplew.writeShort(1);
            mplew.writeInt(item);
            mplew.writeInt(item);
            mplew.writeAsciiString(male.getName(), 13);
            mplew.writeAsciiString(female.getName(), 13);
        } else if (msg == 10 || msg >= 15 && msg <= 16) {
            mplew.writeAsciiString("Male", 13);
            mplew.writeAsciiString("Female", 13);
            mplew.writeShort(0);
        }

        return mplew.getPacket();
    }

    public static byte[] sendWeddingGive() {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.WEDDING_GIFT);
		mplew.write(9);
        mplew.write(0);

        return mplew.getPacket();
    }

    public static byte[] sendWeddingReceive() {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.WEDDING_GIFT);
		mplew.write(10);
        mplew.writeLong(-1L);
        mplew.writeInt(0);
        mplew.write(0);

        return mplew.getPacket();
    }

    public static byte[] giveWeddingItem() {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.WEDDING_GIFT);
		mplew.write(11);
        mplew.write(0);
        mplew.writeLong(0L);
        mplew.write(0);

        return mplew.getPacket();
    }

    public static byte[] receiveWeddingItem() {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.WEDDING_GIFT);
		mplew.write(15);
        mplew.writeLong(0L);
        mplew.write(0);

        return mplew.getPacket();
    }

    public static byte[] sendCashPetFood(boolean success, byte index) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.USE_CASH_PET_FOOD);
		mplew.write(success ? 0 : 1);
        if (success) {
            mplew.write(index);
        }

        return mplew.getPacket();
    }

    public static byte[] yellowChat(String msg) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.YELLOW_CHAT);
		mplew.write(-1);
        mplew.writeMapleAsciiString(msg);

        return mplew.getPacket();
    }

    public static byte[] shopDiscount(int percent) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.SHOP_DISCOUNT);
		mplew.write(percent);

        return mplew.getPacket();
    }

    public static byte[] catchMob(int mobid, int itemid, byte success) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.CATCH_MOB);
		mplew.write(success);
        mplew.writeInt(itemid);
        mplew.writeInt(mobid);

        return mplew.getPacket();
    }

    public static byte[] spawnPlayerNPC(PlayerNPC npc) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.PLAYER_NPC);
		mplew.write(1);
        mplew.writeInt(npc.getId());
        mplew.writeMapleAsciiString(npc.getName());
        PacketHelper.addCharLook(mplew, npc, true, false);

        return mplew.getPacket();
    }

    public static byte[] disabledNPC(List<Integer> ids) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.DISABLE_NPC);
		mplew.write(ids.size());
        for (Integer i : ids) {
            mplew.writeInt(i.intValue());
        }

        return mplew.getPacket();
    }

    public static byte[] getCard(int cardid, int cardCount) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.MONSTER_BOOK_SET_CARD);
		mplew.write(cardid > 0 ? 1 : 0);
        if (cardid > 0) {
            mplew.writeInt(cardid);
            mplew.writeInt(cardCount);
        }
        return mplew.getPacket();
    }

    public static byte[] changeCardSet(int cardid) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.MONSTER_BOOK_SET_COVER);
		mplew.writeInt(cardid);

        return mplew.getPacket();
    }

    public static byte[] upgradeBook(Item book, MapleCharacter chr) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.BOOK_STATS);
		mplew.writeInt(book.getPosition());
        PacketHelper.addItemInfo(mplew, book, chr);

        return mplew.getPacket();
    }

    public static byte[] getCardDrops(int cardid, List<Integer> drops) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.CARD_DROPS);
		mplew.writeInt(cardid);
        mplew.writeShort(drops == null ? 0 : drops.size());
        if (drops != null) {
            for (Integer de : drops) {
                mplew.writeInt(de.intValue());
            }
        }

        return mplew.getPacket();
    }

    public static byte[] getFamiliarInfo(MapleCharacter chr) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.FAMILIAR_INFO);
		mplew.writeInt(chr.getFamiliars().size());
        for (MonsterFamiliar mf : chr.getFamiliars().values()) {
            mf.writeRegisterPacket(mplew, true);
        }
        List<Pair<Integer, Long>> size = new ArrayList<>();
        for (Item i : chr.getInventory(MapleInventoryType.USE).list()) {
            if (i.getItemId() / 10000 == 287) {
                StructFamiliar f = MapleItemInformationProvider.getInstance().getFamiliarByItem(i.getItemId());
                if (f != null) {
                    size.add(new Pair<>(f.familiar, i.getInventoryId()));
                }
            }
        }
        mplew.writeInt(size.size());
        for (Pair<?, ?> s : size) {
            mplew.writeInt(chr.getId());
            mplew.writeInt(((Integer) s.left));
            mplew.writeLong(((Long) s.right));
            mplew.write(0);
        }
        size.clear();

        return mplew.getPacket();
    }

    public static byte[] updateWebBoard(boolean result) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.WEB_BOARD_UPDATE);
		mplew.writeBoolean(result);

        return mplew.getPacket();
    }

    public static byte[] MulungEnergy(int energy) {
        return sendPyramidEnergy("energy", String.valueOf(energy));
    }

    public static byte[] sendPyramidEnergy(String type, String amount) {
        return sendString(1, type, amount);
    }

    public static byte[] sendGhostPoint(String type, String amount) {
        return sendString(2, type, amount);
    }

    public static byte[] sendGhostStatus(String type, String amount) {
        return sendString(3, type, amount);
    }

    public static byte[] sendString(int type, String object, String amount) {
        MaplePacketWriter mplew = null;

        switch (type) {
            case 1:
            	mplew = new MaplePacketWriter(SendPacketOpcode.SESSION_VALUE);
                break;
            case 2:
            	mplew = new MaplePacketWriter(SendPacketOpcode.PARTY_VALUE);
                break;
            case 3:
            	mplew = new MaplePacketWriter(SendPacketOpcode.MAP_VALUE);
        }

        mplew.writeMapleAsciiString(object);
        mplew.writeMapleAsciiString(amount);

        return mplew.getPacket();
    }

    public static byte[] fairyPendantMessage(int termStart, int incExpR) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.BONUS_EXP);
		mplew.writeInt(17);
        mplew.writeInt(0);

        mplew.writeInt(incExpR);

        return mplew.getPacket();
    }

    public static byte[] potionDiscountMessage(int type, int potionDiscR) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.POTION_BONUS);
		mplew.writeInt(type);
        mplew.writeInt(potionDiscR);

        return mplew.getPacket();
    }

    public static byte[] sendLevelup(boolean family, int level, String name) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.LEVEL_UPDATE);
		mplew.write(family ? 1 : 2);
        mplew.writeInt(level);
        mplew.writeMapleAsciiString(name);

        return mplew.getPacket();
    }

    public static byte[] sendMarriage(boolean family, String name) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.MARRIAGE_UPDATE);
		mplew.write(family ? 1 : 0);
        mplew.writeMapleAsciiString(name);

        return mplew.getPacket();
    }

    //mark packet
     public static byte[] giveMarkOfTheif(int cid, int oid, int skillid, List<MapleMonster> monsters, Point p1, Point p2, int javelin) {
        MaplePacketWriter packet = new MaplePacketWriter(SendPacketOpcode.GAIN_FORCE);
        packet.write(1);
        packet.writeInt(cid);
        packet.writeInt(oid);
        packet.writeInt(11); //type
        packet.write(1);
        packet.writeInt(monsters.size());
        for (MapleMonster monster : monsters) {
            packet.writeInt(monster.getObjectId());
        }
        packet.writeInt(skillid); //skillid
        for (int i = 0; i < monsters.size(); i++) {
            packet.write(1);
            packet.writeInt(i + 2);
            packet.writeInt(1);
            packet.writeInt(Randomizer.rand(0x2A, 0x2B));
            packet.writeInt(Randomizer.rand(0x03, 0x04));
            packet.writeInt(Randomizer.rand(0x43, 0xF5));
            packet.writeInt(200);
            packet.writeLong(0);
            packet.writeInt(Randomizer.nextInt());
            packet.writeInt(0);
        }
        packet.write(0);
        //for (Point p : pos) {
        packet.writeInt(p1.x);
        packet.writeInt(p1.y);
        packet.writeInt(p2.x);
        packet.writeInt(p2.y);
        //}
        packet.writeInt(javelin);
        //System.out.println(packet.toString());
        
         packet.writeZeroBytes(69); //We might need this =p
        return packet.getPacket();
    }
    
    //
    
    
    public static byte[] sendJobup(boolean family, int jobid, String name) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.JOB_UPDATE);
		mplew.write(family ? 1 : 0);
        mplew.writeInt(jobid);
        mplew.writeMapleAsciiString(new StringBuilder().append(!family ? "> " : "").append(name).toString());

        return mplew.getPacket();
    }

    public static byte[] getAvatarMega(MapleCharacter chr, int channel, int itemId, List<String> text, boolean ear) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.AVATAR_MEGA);
		mplew.writeInt(itemId);
        mplew.writeMapleAsciiString(chr.getName());
        for (String i : text) {
            mplew.writeMapleAsciiString(i);
        }
        mplew.writeInt(channel - 1);
        mplew.write(ear ? 1 : 0);
        PacketHelper.addCharLook(mplew, chr, true, false);

        return mplew.getPacket();
    }

    public static byte[] GMPoliceMessage(boolean dc) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.GM_POLICE);
		mplew.write(dc ? 10 : 0);

        return mplew.getPacket();
    }

    public static byte[] GMPoliceMessage(String msg) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.MAPLE_ADMIN_MSG);
		mplew.writeMapleAsciiString(msg);

        return mplew.getPacket();
    }

    public static byte[] pendantSlot(boolean p) { //slot -59
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.SLOT_UPDATE);
		mplew.write(p ? 1 : 0);
        
        return mplew.getPacket();
    }

    public static byte[] followRequest(int chrid) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.FOLLOW_REQUEST);
		mplew.writeInt(chrid);

        return mplew.getPacket();
    }

    public static byte[] getTopMsg(String msg) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.TOP_MSG);
		mplew.writeMapleAsciiString(msg);

        return mplew.getPacket();
    }

    public static byte[] showMidMsg(String s, int l) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.MID_MSG);
		mplew.write(l);
        mplew.writeMapleAsciiString(s);
        mplew.write(s.length() > 0 ? 0 : 1);

        return mplew.getPacket();
    }

    public static byte[] getMidMsg(String msg, boolean keep, int index) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.MID_MSG);
		mplew.write(index);
        mplew.writeMapleAsciiString(msg);
        mplew.write(keep ? 0 : 1);

        return mplew.getPacket();
    }

    public static byte[] clearMidMsg() {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.CLEAR_MID_MSG);

        return mplew.getPacket();
    }

    public static byte[] getSpecialMsg(String msg, int type, boolean show) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.SPECIAL_MSG);
		mplew.writeMapleAsciiString(msg);
        mplew.writeInt(type);
        mplew.writeInt(show ? 0 : 1);

        return mplew.getPacket();
    }
    
    public static byte[] getSpecialMsg(String msg, int type, int duration, boolean show) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.SPECIAL_MSG);
		mplew.writeMapleAsciiString(msg);
        mplew.writeInt(type);
        mplew.writeInt(duration);
        mplew.writeInt(show ? 0 : 1);

        return mplew.getPacket();
    }

    public static byte[] CakePieMsg() {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.CAKE_VS_PIE_MSG);

        return mplew.getPacket();
    }
    
    public static byte[] gmBoard(int increnement, String url) { //Test if it DOES work with the open_UI packet.
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.OPEN_UI);
		mplew.write(SendPacketOpcode.GM_STORY_BOARD.getOpcode());//was 0x1D
        mplew.writeShort(1234); //random token        
        mplew.writeShort(Randomizer.nextInt(Short.MAX_VALUE)); //random token        
        mplew.writeMapleAsciiString(url);        
        return mplew.getPacket();    
    }  

    public static byte[] gmBoard2(int increnement, String url) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.GM_STORY_BOARD);
		mplew.writeInt(increnement); //Increnement number
        mplew.writeMapleAsciiString(url);

        return mplew.getPacket();
    }

    public static byte[] updateJaguar(MapleCharacter from) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.WILD_HUNTER_INFO);
        PacketHelper.addJaguarInfo(mplew, from);

        return mplew.getPacket();
    }

    public static byte[] loadInformation(byte mode, int location, int birthday, int favoriteAction, int favoriteLocation, boolean success) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.YOUR_INFORMATION);
		mplew.write(mode);
        if (mode == 2) {
            mplew.writeInt(location);
            mplew.writeInt(birthday);
            mplew.writeInt(favoriteAction);
            mplew.writeInt(favoriteLocation);
        } else if (mode == 4) {
            mplew.write(success ? 1 : 0);
        }

        return mplew.getPacket();
    }

    public static byte[] saveInformation(boolean fail) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.YOUR_INFORMATION);
		mplew.write(4);
        mplew.write(fail ? 0 : 1);

        return mplew.getPacket();
    }

    public static byte[] myInfoResult() {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.FIND_FRIEND);
		mplew.write(6);
        mplew.writeInt(0);
        mplew.writeInt(0);

        return mplew.getPacket();
    }

    public static byte[] findFriendResult(byte mode, List<MapleCharacter> friends, int error, MapleCharacter chr) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.FIND_FRIEND);
		mplew.write(mode);
        switch (mode) {
            case 6:
                mplew.writeInt(0);
                mplew.writeInt(0);
                break;
            case 8:
                mplew.writeShort(friends.size());
                for (MapleCharacter mc : friends) {
                    mplew.writeInt(mc.getId());
                    mplew.writeMapleAsciiString(mc.getName());
                    mplew.write(mc.getLevel());
                    mplew.writeShort(mc.getJob());
                    mplew.writeInt(0);
                    mplew.writeInt(0);
                }
                break;
            case 9:
                mplew.write(error);
                break;
            case 11:
                mplew.writeInt(chr.getId());
                PacketHelper.addCharLook(mplew, chr, true, false);
                break;
        }

        return mplew.getPacket();
    }

    public static byte[] showBackgroundEffect(String eff, int value) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.VISITOR);
		mplew.writeMapleAsciiString(eff);
        mplew.write(value);

        return mplew.getPacket();
    }

    public static byte[] sendPinkBeanChoco() {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.PINKBEAN_CHOCO);
		mplew.writeInt(0);
        mplew.write(1);
        mplew.write(0);
        mplew.write(0);
        mplew.writeInt(0);

        return mplew.getPacket();
    }

    public static byte[] changeChannelMsg(int channel, String msg) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.AUTO_CC_MSG);
		mplew.writeInt(channel);
        mplew.writeMapleAsciiString(msg);

        return mplew.getPacket();
    }

    public static byte[] pamSongUI() {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.PAM_SONG);
        return mplew.getPacket();
    }

    public static byte[] ultimateExplorer() {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.ULTIMATE_EXPLORER);

        return mplew.getPacket();
    }

    public static byte[] professionInfo(String skil, int level1, int level2, int chance) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.SPECIAL_STAT);
		mplew.writeMapleAsciiString(skil);
        mplew.writeInt(level1);
        mplew.writeInt(level2);
        mplew.write(1);
        mplew.writeInt((skil.startsWith("9200")) || (skil.startsWith("9201")) ? 100 : chance);

        return mplew.getPacket();
    }

    public static byte[] updateAzwanFame(int level, int fame, boolean levelup) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.UPDATE_HONOUR);
		mplew.writeInt(level);
        mplew.writeInt(fame);
        mplew.write(levelup ? 1 : 0);

        return mplew.getPacket();
    }

    public static byte[] showAzwanKilled() {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.AZWAN_KILLED);

        return mplew.getPacket();
    }

    public static byte[] showSilentCrusadeMsg(byte type, short chapter) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.SILENT_CRUSADE_MSG);
		mplew.write(type);
        mplew.writeShort(chapter - 1);

        /* type:
         * 0 - open ui (short is chapter)
         * 2 - not enough inventory space
         * 3 - failed due to unknown error
         */
        return mplew.getPacket();
    }

    public static byte[] getSilentCrusadeMsg(byte type) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.SILENT_CRUSADE_SHOP);
		mplew.write(type);

        return mplew.getPacket();
    }

    public static byte[] showSCShopMsg(byte type) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.SILENT_CRUSADE_SHOP);
		mplew.write(type);

        return mplew.getPacket();
    }

    public static byte[] updateImpTime() {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.UPDATE_IMP_TIME);
		mplew.writeInt(0);
        mplew.writeLong(0L);

        return mplew.getPacket();
    }

    public static byte[] updateImp(MapleImp imp, int mask, int index, boolean login) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.ITEM_POT);
		mplew.write(login ? 0 : 1);
        mplew.writeInt(index + 1);
        mplew.writeInt(mask);
        if ((mask & MapleImp.ImpFlag.SUMMONED.getValue()) != 0) {
            Pair<?, ?> i = MapleItemInformationProvider.getInstance().getPot(imp.getItemId());
            if (i == null) {
                return enableActions();
            }
            mplew.writeInt(((Integer) i.left).intValue());
            mplew.write(imp.getLevel());
        }
        if (((mask & MapleImp.ImpFlag.SUMMONED.getValue()) != 0) || ((mask & MapleImp.ImpFlag.STATE.getValue()) != 0)) {
            mplew.write(imp.getState());
        }
        if (((mask & MapleImp.ImpFlag.SUMMONED.getValue()) != 0) || ((mask & MapleImp.ImpFlag.FULLNESS.getValue()) != 0)) {
            mplew.writeInt(imp.getFullness());
        }
        if (((mask & MapleImp.ImpFlag.SUMMONED.getValue()) != 0) || ((mask & MapleImp.ImpFlag.CLOSENESS.getValue()) != 0)) {
            mplew.writeInt(imp.getCloseness());
        }
        if (((mask & MapleImp.ImpFlag.SUMMONED.getValue()) != 0) || ((mask & MapleImp.ImpFlag.CLOSENESS_LEFT.getValue()) != 0)) {
            mplew.writeInt(1);
        }
        if (((mask & MapleImp.ImpFlag.SUMMONED.getValue()) != 0) || ((mask & MapleImp.ImpFlag.MINUTES_LEFT.getValue()) != 0)) {
            mplew.writeInt(0);
        }
        if (((mask & MapleImp.ImpFlag.SUMMONED.getValue()) != 0) || ((mask & MapleImp.ImpFlag.LEVEL.getValue()) != 0)) {
            mplew.write(1);
        }
        if (((mask & MapleImp.ImpFlag.SUMMONED.getValue()) != 0) || ((mask & MapleImp.ImpFlag.FULLNESS_2.getValue()) != 0)) {
            mplew.writeInt(imp.getFullness());
        }
        if (((mask & MapleImp.ImpFlag.SUMMONED.getValue()) != 0) || ((mask & MapleImp.ImpFlag.UPDATE_TIME.getValue()) != 0)) {
            mplew.writeLong(PacketHelper.getTime(System.currentTimeMillis()));
        }
        if (((mask & MapleImp.ImpFlag.SUMMONED.getValue()) != 0) || ((mask & MapleImp.ImpFlag.CREATE_TIME.getValue()) != 0)) {
            mplew.writeLong(PacketHelper.getTime(System.currentTimeMillis()));
        }
        if (((mask & MapleImp.ImpFlag.SUMMONED.getValue()) != 0) || ((mask & MapleImp.ImpFlag.AWAKE_TIME.getValue()) != 0)) {
            mplew.writeLong(PacketHelper.getTime(System.currentTimeMillis()));
        }
        if (((mask & MapleImp.ImpFlag.SUMMONED.getValue()) != 0) || ((mask & MapleImp.ImpFlag.SLEEP_TIME.getValue()) != 0)) {
            mplew.writeLong(PacketHelper.getTime(System.currentTimeMillis()));
        }
        if (((mask & MapleImp.ImpFlag.SUMMONED.getValue()) != 0) || ((mask & MapleImp.ImpFlag.MAX_CLOSENESS.getValue()) != 0)) {
            mplew.writeInt(100);
        }
        if (((mask & MapleImp.ImpFlag.SUMMONED.getValue()) != 0) || ((mask & MapleImp.ImpFlag.MAX_DELAY.getValue()) != 0)) {
            mplew.writeInt(1000);
        }
        if (((mask & MapleImp.ImpFlag.SUMMONED.getValue()) != 0) || ((mask & MapleImp.ImpFlag.MAX_FULLNESS.getValue()) != 0)) {
            mplew.writeInt(1000);
        }
        if (((mask & MapleImp.ImpFlag.SUMMONED.getValue()) != 0) || ((mask & MapleImp.ImpFlag.MAX_ALIVE.getValue()) != 0)) {
            mplew.writeInt(1);
        }
        if (((mask & MapleImp.ImpFlag.SUMMONED.getValue()) != 0) || ((mask & MapleImp.ImpFlag.MAX_MINUTES.getValue()) != 0)) {
            mplew.writeInt(10);
        }
        mplew.write(0);

        return mplew.getPacket();
    }

//    public static byte[] getMulungRanking(MapleClient c, List<DojoRankingInfo> all) {
//        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(SendPacketOpcode.MULUNG_DOJO_RANKING);
//        MapleDojoRanking data = MapleDojoRanking.getInstance();
//        mplew.writeInt(all.size()); // size
//        for (DojoRankingInfo info : all) {
//            mplew.writeShort(info.getRank());
//            mplew.writeMapleAsciiString(info.getName());
//            mplew.writeLong(info.getTime());
//        }
//        return mplew.getPacket();
//    }
    public static byte[] getMulungRanking() {
        final MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.MULUNG_DOJO_RANKING);
        DojoRankingsData data = DojoRankingsData.loadLeaderboard();
        mplew.writeInt(data.totalCharacters); // size
        for (int i = 0; i < data.totalCharacters; i++) {
            mplew.writeShort(data.ranks[i]); // rank
            mplew.writeMapleAsciiString(data.names[i]); // Character name
            mplew.writeLong(data.times[i]); // time in seconds
        }
        return mplew.getPacket();
    }

    public static byte[] getMulungMessage(boolean dc, String msg) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.MULUNG_MESSAGE);
		mplew.write(dc ? 1 : 0);
        mplew.writeMapleAsciiString(msg);

        return mplew.getPacket();
    }

    public static byte[] getCandyRanking(MapleClient c, List<CandyRankingInfo> all) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.CANDY_RANKING);
		mplew.writeInt(all.size());
        for (CandyRankingInfo info : all) {
            mplew.writeShort(info.getRank());
            mplew.writeMapleAsciiString(info.getName());
        }
        return mplew.getPacket();
    }

    public static byte[] showForeignDamageSkin(MapleCharacter chr, int skinid) {
    	MaplePacketWriter packet = new MaplePacketWriter(SendPacketOpcode.SHOW_DAMAGE_SKIN);
        packet.writeInt(chr.getId());
        packet.writeInt(skinid);
        return packet.getPacket();
    }

    public static class AlliancePacket {

        public static byte[] getAllianceInfo(MapleGuildAlliance alliance) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.ALLIANCE_OPERATION);
            mplew.write(12);
            mplew.write(alliance == null ? 0 : 1);
            if (alliance != null) {
                addAllianceInfo(mplew, alliance);
            }

            return mplew.getPacket();
        }

        private static void addAllianceInfo(MaplePacketWriter mplew, MapleGuildAlliance alliance) {
            mplew.writeInt(alliance.getId());
            mplew.writeMapleAsciiString(alliance.getName());
            for (int i = 1; i <= 5; i++) {
                mplew.writeMapleAsciiString(alliance.getRank(i));
            }
            mplew.write(alliance.getNoGuilds());
            for (int i = 0; i < alliance.getNoGuilds(); i++) {
                mplew.writeInt(alliance.getGuildId(i));
            }
            mplew.writeInt(alliance.getCapacity());
            mplew.writeMapleAsciiString(alliance.getNotice());
        }

        public static byte[] getGuildAlliance(MapleGuildAlliance alliance) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.ALLIANCE_OPERATION);
            mplew.write(13);
            if (alliance == null) {
                mplew.writeInt(0);
                return mplew.getPacket();
            }
            int noGuilds = alliance.getNoGuilds();
            MapleGuild[] g = new MapleGuild[noGuilds];
            for (int i = 0; i < alliance.getNoGuilds(); i++) {
                g[i] = World.Guild.getGuild(alliance.getGuildId(i));
                if (g[i] == null) {
                    return CWvsContext.enableActions();
                }
            }
            mplew.writeInt(noGuilds);
            for (MapleGuild gg : g) {
                CWvsContext.GuildPacket.getGuildInfo(mplew, gg);
            }
            return mplew.getPacket();
        }

        public static byte[] allianceMemberOnline(int alliance, int gid, int id, boolean online) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.ALLIANCE_OPERATION);
            mplew.write(14);
            mplew.writeInt(alliance);
            mplew.writeInt(gid);
            mplew.writeInt(id);
            mplew.write(online ? 1 : 0);

            return mplew.getPacket();
        }

        public static byte[] removeGuildFromAlliance(MapleGuildAlliance alliance, MapleGuild expelledGuild, boolean expelled) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.ALLIANCE_OPERATION);
            mplew.write(16);
            addAllianceInfo(mplew, alliance);
            CWvsContext.GuildPacket.getGuildInfo(mplew, expelledGuild);
            mplew.write(expelled ? 1 : 0);

            return mplew.getPacket();
        }

        public static byte[] addGuildToAlliance(MapleGuildAlliance alliance, MapleGuild newGuild) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.ALLIANCE_OPERATION);
            mplew.write(18);
            addAllianceInfo(mplew, alliance);
            mplew.writeInt(newGuild.getId());
            CWvsContext.GuildPacket.getGuildInfo(mplew, newGuild);
            mplew.write(0);

            return mplew.getPacket();
        }

        public static byte[] sendAllianceInvite(String allianceName, MapleCharacter inviter) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.ALLIANCE_OPERATION);
            mplew.write(3);
            mplew.writeInt(inviter.getGuildId());
            mplew.writeMapleAsciiString(inviter.getName());
            mplew.writeMapleAsciiString(allianceName);

            return mplew.getPacket();
        }

        public static byte[] getAllianceUpdate(MapleGuildAlliance alliance) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.ALLIANCE_OPERATION);
            mplew.write(23);
            addAllianceInfo(mplew, alliance);

            return mplew.getPacket();
        }

        public static byte[] createGuildAlliance(MapleGuildAlliance alliance) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.ALLIANCE_OPERATION);
            mplew.write(15);
            addAllianceInfo(mplew, alliance);
            int noGuilds = alliance.getNoGuilds();
            MapleGuild[] g = new MapleGuild[noGuilds];
            for (int i = 0; i < alliance.getNoGuilds(); i++) {
                g[i] = World.Guild.getGuild(alliance.getGuildId(i));
                if (g[i] == null) {
                    return CWvsContext.enableActions();
                }
            }
            for (MapleGuild gg : g) {
                CWvsContext.GuildPacket.getGuildInfo(mplew, gg);
            }
            return mplew.getPacket();
        }

        public static byte[] updateAlliance(MapleGuildCharacter mgc, int allianceid) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.ALLIANCE_OPERATION);
            mplew.write(24);
            mplew.writeInt(allianceid);
            mplew.writeInt(mgc.getGuildId());
            mplew.writeInt(mgc.getId());
            mplew.writeInt(mgc.getLevel());
            mplew.writeInt(mgc.getJobId());

            return mplew.getPacket();
        }

        public static byte[] updateAllianceLeader(int allianceid, int newLeader, int oldLeader) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.ALLIANCE_OPERATION);
            mplew.write(25);
            mplew.writeInt(allianceid);
            mplew.writeInt(oldLeader);
            mplew.writeInt(newLeader);

            return mplew.getPacket();
        }

        public static byte[] allianceRankChange(int aid, String[] ranks) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.GUILD_OPERATION);
            mplew.write(26);
            mplew.writeInt(aid);
            for (String r : ranks) {
                mplew.writeMapleAsciiString(r);
            }

            return mplew.getPacket();
        }

        public static byte[] updateAllianceRank(MapleGuildCharacter mgc) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.ALLIANCE_OPERATION);
            mplew.write(27);
            mplew.writeInt(mgc.getId());
            mplew.write(mgc.getAllianceRank());

            return mplew.getPacket();
        }

        public static byte[] changeAllianceNotice(int allianceid, String notice) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.ALLIANCE_OPERATION);
            mplew.write(28);
            mplew.writeInt(allianceid);
            mplew.writeMapleAsciiString(notice);

            return mplew.getPacket();
        }

        public static byte[] disbandAlliance(int alliance) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.ALLIANCE_OPERATION);
            mplew.write(29);
            mplew.writeInt(alliance);

            return mplew.getPacket();
        }

        public static byte[] changeAlliance(MapleGuildAlliance alliance, boolean in) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.ALLIANCE_OPERATION);
            mplew.write(1);
            mplew.write(in ? 1 : 0);
            mplew.writeInt(in ? alliance.getId() : 0);
            int noGuilds = alliance.getNoGuilds();
            MapleGuild[] g = new MapleGuild[noGuilds];
            for (int i = 0; i < noGuilds; i++) {
                g[i] = World.Guild.getGuild(alliance.getGuildId(i));
                if (g[i] == null) {
                    return CWvsContext.enableActions();
                }
            }
            mplew.write(noGuilds);
            for (int i = 0; i < noGuilds; i++) {
                mplew.writeInt(g[i].getId());

                Collection<MapleGuildCharacter> members = g[i].getMembers();
                mplew.writeInt(members.size());
                for (MapleGuildCharacter mgc : members) {
                    mplew.writeInt(mgc.getId());
                    mplew.write(in ? mgc.getAllianceRank() : 0);
                }
            }

            return mplew.getPacket();
        }

        public static byte[] changeAllianceLeader(int allianceid, int newLeader, int oldLeader) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.ALLIANCE_OPERATION);
            mplew.write(2);
            mplew.writeInt(allianceid);
            mplew.writeInt(oldLeader);
            mplew.writeInt(newLeader);

            return mplew.getPacket();
        }

        public static byte[] changeGuildInAlliance(MapleGuildAlliance alliance, MapleGuild guild, boolean add) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.ALLIANCE_OPERATION);
            mplew.write(4);
            mplew.writeInt(add ? alliance.getId() : 0);
            mplew.writeInt(guild.getId());
            Collection<MapleGuildCharacter> members = guild.getMembers();
            mplew.writeInt(members.size());
            for (MapleGuildCharacter mgc : members) {
                mplew.writeInt(mgc.getId());
                mplew.write(add ? mgc.getAllianceRank() : 0);
            }

            return mplew.getPacket();
        }

        public static byte[] changeAllianceRank(int allianceid, MapleGuildCharacter player) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.ALLIANCE_OPERATION);
            mplew.write(5);
            mplew.writeInt(allianceid);
            mplew.writeInt(player.getId());
            mplew.writeInt(player.getAllianceRank());

            return mplew.getPacket();
        }
    }

    public static class FamilyPacket {

        public static byte[] getFamilyData() {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.FAMILY);
            MapleFamilyBuff[] entries = MapleFamilyBuff.values();
            mplew.writeInt(entries.length);

            for (MapleFamilyBuff entry : entries) {
                mplew.write(entry.type);
                mplew.writeInt(entry.rep);
                mplew.writeInt(1);
                mplew.writeMapleAsciiString(entry.name);
                mplew.writeMapleAsciiString(entry.desc);
            }
            return mplew.getPacket();
        }

        public static byte[] getFamilyInfo(MapleCharacter chr) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.OPEN_FAMILY);
            mplew.writeInt(chr.getCurrentRep());
            mplew.writeInt(chr.getTotalRep());
            mplew.writeInt(chr.getTotalRep());
            mplew.writeShort(chr.getNoJuniors());
            mplew.writeShort(2);
            mplew.writeShort(chr.getNoJuniors());
            MapleFamily family = World.Family.getFamily(chr.getFamilyId());
            if (family != null) {
                mplew.writeInt(family.getLeaderId());
                mplew.writeMapleAsciiString(family.getLeaderName());
                mplew.writeMapleAsciiString(family.getNotice());
            } else {
                mplew.writeLong(0L);
            }
            List<?> b = chr.usedBuffs();
            mplew.writeInt(b.size());
            for (Iterator<?> i$ = b.iterator(); i$.hasNext();) {
                int ii = ((Integer) i$.next()).intValue();
                mplew.writeInt(ii);
                mplew.writeInt(1);
            }

            return mplew.getPacket();
        }

        public static void addFamilyCharInfo(MapleFamilyCharacter ldr, MaplePacketWriter mplew) {
            mplew.writeInt(ldr.getId());
            mplew.writeInt(ldr.getSeniorId());
            mplew.writeShort(ldr.getJobId());
            mplew.writeShort(0);
            mplew.write(ldr.getLevel());
            mplew.write(ldr.isOnline() ? 1 : 0);
            mplew.writeInt(ldr.getCurrentRep());
            mplew.writeInt(ldr.getTotalRep());
            mplew.writeInt(ldr.getTotalRep());
            mplew.writeInt(ldr.getTotalRep());
            mplew.writeInt(Math.max(ldr.getChannel(), 0));
            mplew.writeInt(0);
            mplew.writeMapleAsciiString(ldr.getName());
        }

        public static byte[] getFamilyPedigree(MapleCharacter chr) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.SEND_PEDIGREE);
            mplew.writeInt(chr.getId());
            MapleFamily family = World.Family.getFamily(chr.getFamilyId());

            int descendants = 2;
            int gens = 0;
            int generations = 0;
            if (family == null) {
                mplew.writeInt(2);
                addFamilyCharInfo(new MapleFamilyCharacter(chr, 0, 0, 0, 0), mplew);
            } else {
                mplew.writeInt(family.getMFC(chr.getId()).getPedigree().size() + 1);
                addFamilyCharInfo(family.getMFC(family.getLeaderId()), mplew);

                if (chr.getSeniorId() > 0) {
                    MapleFamilyCharacter senior = family.getMFC(chr.getSeniorId());
                    if (senior != null) {
                        if (senior.getSeniorId() > 0) {
                            addFamilyCharInfo(family.getMFC(senior.getSeniorId()), mplew);
                        }
                        addFamilyCharInfo(senior, mplew);
                    }
                }
            }
            addFamilyCharInfo(chr.getMFC() == null ? new MapleFamilyCharacter(chr, 0, 0, 0, 0) : chr.getMFC(), mplew);
            if (family != null) {
                if (chr.getSeniorId() > 0) {
                    MapleFamilyCharacter senior = family.getMFC(chr.getSeniorId());
                    if (senior != null) {
                        if ((senior.getJunior1() > 0) && (senior.getJunior1() != chr.getId())) {
                            addFamilyCharInfo(family.getMFC(senior.getJunior1()), mplew);
                        } else if ((senior.getJunior2() > 0) && (senior.getJunior2() != chr.getId())) {
                            addFamilyCharInfo(family.getMFC(senior.getJunior2()), mplew);
                        }

                    }

                }

                if (chr.getJunior1() > 0) {
                    MapleFamilyCharacter junior = family.getMFC(chr.getJunior1());
                    if (junior != null) {
                        addFamilyCharInfo(junior, mplew);
                    }
                }
                if (chr.getJunior2() > 0) {
                    MapleFamilyCharacter junior = family.getMFC(chr.getJunior2());
                    if (junior != null) {
                        addFamilyCharInfo(junior, mplew);
                    }
                }
                if (chr.getJunior1() > 0) {
                    MapleFamilyCharacter junior = family.getMFC(chr.getJunior1());
                    if (junior != null) {
                        if ((junior.getJunior1() > 0) && (family.getMFC(junior.getJunior1()) != null)) {
                            gens++;
                            addFamilyCharInfo(family.getMFC(junior.getJunior1()), mplew);
                        }
                        if ((junior.getJunior2() > 0) && (family.getMFC(junior.getJunior2()) != null)) {
                            gens++;
                            addFamilyCharInfo(family.getMFC(junior.getJunior2()), mplew);
                        }
                    }
                }
                if (chr.getJunior2() > 0) {
                    MapleFamilyCharacter junior = family.getMFC(chr.getJunior2());
                    if (junior != null) {
                        if ((junior.getJunior1() > 0) && (family.getMFC(junior.getJunior1()) != null)) {
                            gens++;
                            addFamilyCharInfo(family.getMFC(junior.getJunior1()), mplew);
                        }
                        if ((junior.getJunior2() > 0) && (family.getMFC(junior.getJunior2()) != null)) {
                            gens++;
                            addFamilyCharInfo(family.getMFC(junior.getJunior2()), mplew);
                        }
                    }
                }
                generations = family.getMemberSize();
            }
            mplew.writeLong(gens);
            mplew.writeInt(0);
            mplew.writeInt(-1);
            mplew.writeInt(generations);

            if (family != null) {
                if (chr.getJunior1() > 0) {
                    MapleFamilyCharacter junior = family.getMFC(chr.getJunior1());
                    if (junior != null) {
                        if ((junior.getJunior1() > 0) && (family.getMFC(junior.getJunior1()) != null)) {
                            mplew.writeInt(junior.getJunior1());
                            mplew.writeInt(family.getMFC(junior.getJunior1()).getDescendants());
                        } else {
                            mplew.writeInt(0);
                        }
                        if ((junior.getJunior2() > 0) && (family.getMFC(junior.getJunior2()) != null)) {
                            mplew.writeInt(junior.getJunior2());
                            mplew.writeInt(family.getMFC(junior.getJunior2()).getDescendants());
                        } else {
                            mplew.writeInt(0);
                        }
                    }
                }
                if (chr.getJunior2() > 0) {
                    MapleFamilyCharacter junior = family.getMFC(chr.getJunior2());
                    if (junior != null) {
                        if ((junior.getJunior1() > 0) && (family.getMFC(junior.getJunior1()) != null)) {
                            mplew.writeInt(junior.getJunior1());
                            mplew.writeInt(family.getMFC(junior.getJunior1()).getDescendants());
                        } else {
                            mplew.writeInt(0);
                        }
                        if ((junior.getJunior2() > 0) && (family.getMFC(junior.getJunior2()) != null)) {
                            mplew.writeInt(junior.getJunior2());
                            mplew.writeInt(family.getMFC(junior.getJunior2()).getDescendants());
                        } else {
                            mplew.writeInt(0);
                        }
                    }
                }
            }

            List<?> b = chr.usedBuffs();
            mplew.writeInt(b.size());
            for (Iterator<?> i$ = b.iterator(); i$.hasNext();) {
                int ii = ((Integer) i$.next()).intValue();
                mplew.writeInt(ii);
                mplew.writeInt(1);
            }
            mplew.writeShort(2);

            return mplew.getPacket();
        }

        public static byte[] getFamilyMsg(byte type, int meso) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.FAMILY_MESSAGE);
            mplew.writeInt(type);
            mplew.writeInt(meso);

            return mplew.getPacket();
        }

        public static byte[] sendFamilyInvite(int cid, int otherLevel, int otherJob, String inviter) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.FAMILY_INVITE);
            mplew.writeInt(cid);
            mplew.writeInt(otherLevel);
            mplew.writeInt(otherJob);
            mplew.writeInt(0);
            mplew.writeMapleAsciiString(inviter);
            return mplew.getPacket();
        }

        public static byte[] sendFamilyJoinResponse(boolean accepted, String added) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.FAMILY_INVITE_RESPONSE);
            mplew.write(accepted ? 1 : 0);
            mplew.writeMapleAsciiString(added);

            return mplew.getPacket();
        }

        public static byte[] getSeniorMessage(String name) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.SENIOR_MESSAGE);
            mplew.writeMapleAsciiString(name);

            return mplew.getPacket();
        }

        public static byte[] changeRep(int r, String name) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.REP_INCREASE);
            mplew.writeInt(r);
            mplew.writeMapleAsciiString(name);

            return mplew.getPacket();
        }

        public static byte[] familyLoggedIn(boolean online, String name) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.FAMILY_LOGGEDIN);
            mplew.write(online ? 1 : 0);
            mplew.writeMapleAsciiString(name);

            return mplew.getPacket();
        }

        public static byte[] familyBuff(int type, int buffnr, int amount, int time) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.FAMILY_BUFF);
            mplew.write(type);
            if ((type >= 2) && (type <= 4)) {
                mplew.writeInt(buffnr);

                mplew.writeInt(type == 3 ? 0 : amount);
                mplew.writeInt(type == 2 ? 0 : amount);
                mplew.write(0);
                mplew.writeInt(time);
            }
            return mplew.getPacket();
        }

        public static byte[] cancelFamilyBuff() {
            return familyBuff(0, 0, 0, 0);
        }

        public static byte[] familySummonRequest(String name, String mapname) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.FAMILY_USE_REQUEST);
            mplew.writeMapleAsciiString(name);
            mplew.writeMapleAsciiString(mapname);

            return mplew.getPacket();
        }
    }

    public static class BuddylistPacket {

        public static byte[] updateBuddylist(Collection<BuddylistEntry> buddylist) {
            return updateBuddylist(buddylist, 7);
        }

        public static byte[] updateBuddylist(Collection<BuddylistEntry> buddylist, int deleted) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.BUDDYLIST);
            mplew.write(deleted);
            mplew.write(buddylist.size());
            for (BuddylistEntry buddy : buddylist) {
                mplew.writeInt(buddy.getCharacterId());
                mplew.writeAsciiString(buddy.getName(), 13);
                mplew.write(buddy.isVisible() ? 0 : 1);//if adding = 2
                mplew.writeInt(buddy.getChannel() == -1 ? -1 : buddy.getChannel());
                mplew.writeAsciiString(buddy.getGroup(), 17);
            }
            for (int x = 0; x < buddylist.size(); x++) {
                mplew.writeInt(0);
            }

            return mplew.getPacket();
        }

        public static byte[] updateBuddyChannel(int characterid, int channel) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.BUDDYLIST);
            mplew.write(20);
            mplew.writeInt(characterid);
            mplew.write(0);
            mplew.writeInt(channel);

            return mplew.getPacket();
        }

        public static byte[] requestBuddylistAdd(int cidFrom, String nameFrom, int levelFrom, int jobFrom) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.BUDDYLIST);
            mplew.write(9);
            mplew.writeInt(cidFrom);
            mplew.writeMapleAsciiString(nameFrom);
            mplew.writeInt(levelFrom);
            mplew.writeInt(jobFrom);
            mplew.writeInt(0);//v115
            mplew.writeInt(cidFrom);
            mplew.writeAsciiString(nameFrom, 13);
            mplew.write(1);
            mplew.writeInt(0);
            mplew.writeAsciiString("ETC", 16);
            mplew.writeShort(0);//was1

            return mplew.getPacket();
        }

        public static byte[] updateBuddyCapacity(int capacity) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.BUDDYLIST);
            mplew.write(21);
            mplew.write(capacity);

            return mplew.getPacket();
        }

        public static byte[] buddylistMessage(byte message) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.BUDDYLIST);
            mplew.write(message);

            return mplew.getPacket();
        }
    }

    public static byte[] giveKilling(int x) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.GIVE_BUFF);
        PacketHelper.writeSingleMask(mplew, MapleBuffStat.KILL_COUNT);
//        mplew.writeInt(0);
//        mplew.write(0);
//        mplew.writeInt(x);
//        mplew.writeZeroBytes(6);
        mplew.writeShort(0);
        mplew.write(0);
        mplew.writeInt(x);
        return mplew.getPacket();
    }

    public static class ExpeditionPacket {

        public static byte[] expeditionStatus(MapleExpedition me, boolean created, boolean silent) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.EXPEDITION_OPERATION);
            mplew.write(created ? 86 : silent ? 72 : 76);//74
            mplew.writeInt(me.getType().exped);
            mplew.writeInt(0);
            for (int i = 0; i < 6; i++) {
                if (i < me.getParties().size()) {
                    MapleParty party = World.Party.getParty((me.getParties().get(i)).intValue());

                    CWvsContext.PartyPacket.addPartyStatus(-1, party, mplew, false, true);
                } else {
                    CWvsContext.PartyPacket.addPartyStatus(-1, null, mplew, false, true);
                }

            }

            return mplew.getPacket();
        }

        public static byte[] expeditionError(int errcode, String name) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.EXPEDITION_OPERATION);
            mplew.write(100);//88
            mplew.writeInt(errcode);
            mplew.writeMapleAsciiString(name);

            return mplew.getPacket();
        }

        public static byte[] expeditionMessage(int code) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.EXPEDITION_OPERATION);
            mplew.write(code);

            return mplew.getPacket();
        }

        public static byte[] expeditionJoined(String name) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.EXPEDITION_OPERATION);
            mplew.write(87);//75
            mplew.writeMapleAsciiString(name);

            return mplew.getPacket();
        }

        public static byte[] expeditionLeft(String name) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.EXPEDITION_OPERATION);
            mplew.write(92);//79
            mplew.writeMapleAsciiString(name);

            return mplew.getPacket();
        }

        public static byte[] expeditionLeaderChanged(int newLeader) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.EXPEDITION_OPERATION);
            mplew.write(96);//84
            mplew.writeInt(newLeader);

            return mplew.getPacket();
        }

        public static byte[] expeditionUpdate(int partyIndex, MapleParty party) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.EXPEDITION_OPERATION);
            mplew.write(97);//85
            mplew.writeInt(0);
            mplew.writeInt(partyIndex);

            CWvsContext.PartyPacket.addPartyStatus(-1, party, mplew, false, true);

            return mplew.getPacket();
        }

        public static byte[] expeditionInvite(MapleCharacter from, int exped) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.EXPEDITION_OPERATION);
            mplew.write(99);//87
            mplew.writeInt(from.getLevel());
            mplew.writeInt(from.getJob());
            mplew.writeInt(0);
            mplew.writeMapleAsciiString(from.getName());
            mplew.writeInt(exped);

            return mplew.getPacket();
        }
    }

    public static class PartyPacket {

        public static byte[] partyCreated(int partyid, String partyName) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.PARTY_OPERATION);
            mplew.write(16); // This value updates with versions
            mplew.writeInt(partyid);
            mplew.writeInt(999999999);
            mplew.writeInt(999999999);
            mplew.writeInt(0);
            mplew.writeShort(0);
            mplew.writeShort(0);
            mplew.write(0);
            mplew.write(1);
            mplew.writeMapleAsciiString(partyName);

            return mplew.getPacket();
        }
        
        public static byte[] partyCreated(int partyid) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.PARTY_OPERATION);
            mplew.write(16); // This value updates with versions
            mplew.writeInt(partyid);
            mplew.writeInt(999999999);
            mplew.writeInt(999999999);
            mplew.writeInt(0);
            mplew.writeShort(0);
            mplew.writeShort(0);
            mplew.write(0);
            mplew.write(1);

            return mplew.getPacket();
        }

        public static byte[] partyInvite(MapleCharacter from) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.PARTY_OPERATION);
            mplew.write(4);
            //mplew.writeInt(from.getParty() == null ? 0 : from.getParty().getId());
            mplew.writeInt(from.getParty() == null? 0 : from.getId());
            mplew.writeMapleAsciiString(from.getName());
            mplew.writeInt(from.getLevel());
            mplew.writeInt(from.getJob());
            mplew.writeShort(0);
            mplew.writeInt(0);
            
            return mplew.getPacket();
        }

        public static byte[] partyRequestInvite(MapleCharacter to) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.PARTY_OPERATION);
            mplew.write(33);
            mplew.writeMapleAsciiString(to.getName());

            return mplew.getPacket();
        }

        /**
         * 10: A beginner can't create a party. 
         * 1/11/14/19: Your request for a party didn't work due to an unexpected error. 
         * 13: You have yet to join a party.
         * 16: Already have joined a party. 
         * 17: The party you're trying to join is already in full capacity. 
         * 19: Unable to find the requested character in this channel.
         *
         * @param message
         * @return
         */
        public static byte[] partyStatusMessage(int message, String charname) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.PARTY_OPERATION);
            mplew.write(message);
            if ((message == 30) || (message == 52)) {
                mplew.writeMapleAsciiString(charname);
            } else if (message == 45) {
                mplew.write(0);
            }

            System.out.println("[PartyStatusMessage] " + mplew.toString());
            return mplew.getPacket();
        }

        public static void addPartyStatus(int forchannel, MapleParty party, MaplePacketWriter mplew, boolean leaving) {
            addPartyStatus(forchannel, party, mplew, leaving, false);
        }

        public static void addPartyStatus(int forchannel, MapleParty party, MaplePacketWriter mplew, boolean leaving, boolean exped) {
            List<MaplePartyCharacter> partymembers;
            if (party == null) {
                partymembers = new ArrayList();
            } else {
                partymembers = new ArrayList(party.getMembers());
            }
            while (partymembers.size() < 6) {
                partymembers.add(new MaplePartyCharacter());
            }
            for (MaplePartyCharacter partychar : partymembers) {
                mplew.writeInt(partychar.getId());
            }
            for (MaplePartyCharacter partychar : partymembers) {
                mplew.writeAsciiString(partychar.getName(), 13);
            }
            for (MaplePartyCharacter partychar : partymembers) {
                mplew.writeInt(partychar.getJobId());
            }
            for (MaplePartyCharacter partychar : partymembers) {
                mplew.writeInt(0);
            }
            for (MaplePartyCharacter partychar : partymembers) {
                mplew.writeInt(partychar.getLevel());
            }
            for (MaplePartyCharacter partychar : partymembers) {
                mplew.writeInt(partychar.isOnline() ? partychar.getChannel() - 1 : -2);
            }
            for (MaplePartyCharacter partychar : partymembers) {
                mplew.writeInt(0);
            }

            mplew.writeInt(party == null ? 0 : party.getLeader().getId());
            if (exped) {
                return;
            }
            for (MaplePartyCharacter partychar : partymembers) {
                mplew.writeInt(partychar.getChannel() == forchannel ? partychar.getMapid() : 0);
            }
            for (MaplePartyCharacter partychar : partymembers) {
                if ((partychar.getChannel() == forchannel) && (!leaving)) {
                    mplew.writeInt(partychar.getDoorTown());
                    mplew.writeInt(partychar.getDoorTarget());
                    mplew.writeInt(partychar.getDoorSkill());
                    mplew.writeInt(partychar.getDoorPosition().x);
                    mplew.writeInt(partychar.getDoorPosition().y);
                } else {
                    mplew.writeInt(leaving ? 999999999 : 0);
                    mplew.writeLong(leaving ? 999999999L : 0L);
                    mplew.writeLong(leaving ? -1L : 0L);
                }
            }
            mplew.write(1);
            mplew.writeMapleAsciiString(party.getName());
        }

        public static byte[] updateParty(int forChannel, MapleParty party, PartyOperation op, MaplePartyCharacter target) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.PARTY_OPERATION);
            switch (op) {
                case DISBAND:
                case EXPEL:
                case LEAVE:
                    mplew.write(21);
                    mplew.writeInt(party.getId());
                    mplew.writeInt(target.getId());
                    mplew.write(op == PartyOperation.DISBAND ? 0 : 1);
                    if (op == PartyOperation.DISBAND) {
                    	mplew.writeInt(target.getId());
                        break;
                    }
                    mplew.write(op == PartyOperation.EXPEL ? 1 : 0);
                    mplew.writeMapleAsciiString(target.getName());
                    addPartyStatus(forChannel, party, mplew, op == PartyOperation.LEAVE);
                    break;
                case JOIN:
                    mplew.write(24);
                    mplew.writeInt(party.getId());
                    mplew.writeMapleAsciiString(target.getName());
                    addPartyStatus(forChannel, party, mplew, false);
                    break;
                case SILENT_UPDATE:
                case LOG_ONOFF:
                    mplew.write(15);
                    mplew.writeInt(party.getId());
                    addPartyStatus(forChannel, party, mplew, op == PartyOperation.LOG_ONOFF);
                    break;
                case CHANGE_LEADER:
                case CHANGE_LEADER_DC:
                    mplew.write(48);
                    mplew.writeInt(target.getId());
                    mplew.write(op == PartyOperation.CHANGE_LEADER_DC ? 1 : 0);
                    break;
                case CHANGE_LEADER_DC_2:
                	mplew.write(57);
                	mplew.write(party.getId());
                	addPartyStatus(forChannel, party, mplew, op == PartyOperation.CHANGE_LEADER_DC_2);
            }
            
            System.out.println("[UpdateParty " + op + "] " + mplew.toString());
            return mplew.getPacket();
        }

        public static byte[] partyPortal(int townId, int targetId, int skillId, Point position, boolean animation) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.PARTY_OPERATION);
            mplew.write(61);
            mplew.write(animation ? 0 : 1);
            mplew.writeInt(townId);
            mplew.writeInt(targetId);
            mplew.writeInt(skillId);
            mplew.writePos(position);

            return mplew.getPacket();
        }

        public static byte[] getPartyListing(PartySearchType pst) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.PARTY_OPERATION);
            mplew.write(GameConstants.GMS ? 147 : 77);
            mplew.writeInt(pst.id);
            final List<PartySearch> parties = World.Party.searchParty(pst);
            mplew.writeInt(parties.size());
            for (PartySearch party : parties) {
                mplew.writeInt(0);
                mplew.writeInt(2);
                if (pst.exped) {
                    MapleExpedition me = World.Party.getExped(party.getId());
                    mplew.writeInt(me.getType().maxMembers);
                    mplew.writeInt(party.getId());
                    mplew.writeAsciiString(party.getName(), 48);
                    for (int i = 0; i < 5; i++) {
                        if (i < me.getParties().size()) {
                            MapleParty part = World.Party.getParty((me.getParties().get(i)).intValue());
                            if (part != null) {
                                addPartyStatus(-1, part, mplew, false, true);
                            } else {
                                mplew.writeZeroBytes(202);
                            }
                        } else {
                            mplew.writeZeroBytes(202);
                        }
                    }
                } else {
                    mplew.writeInt(0);
                    mplew.writeInt(party.getId());
                    mplew.writeAsciiString(party.getName(), 48);
                    addPartyStatus(-1, World.Party.getParty(party.getId()), mplew, false, true);
                }

                mplew.writeShort(0);
            }

            return mplew.getPacket();
        }

        public static byte[] partyListingAdded(PartySearch ps) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.PARTY_OPERATION);
            mplew.write(93);
            mplew.writeInt(ps.getType().id);
            mplew.writeInt(0);
            mplew.writeInt(1);
            if (ps.getType().exped) {
                MapleExpedition me = World.Party.getExped(ps.getId());
                mplew.writeInt(me.getType().maxMembers);
                mplew.writeInt(ps.getId());
                mplew.writeAsciiString(ps.getName(), 48);
                for (int i = 0; i < 5; i++) {
                    if (i < me.getParties().size()) {
                        MapleParty party = World.Party.getParty((me.getParties().get(i)).intValue());
                        if (party != null) {
                            addPartyStatus(-1, party, mplew, false, true);
                        } else {
                            mplew.writeZeroBytes(202);
                        }
                    } else {
                        mplew.writeZeroBytes(202);
                    }
                }
            } else {
                mplew.writeInt(0);
                mplew.writeInt(ps.getId());
                mplew.writeAsciiString(ps.getName(), 48);
                addPartyStatus(-1, World.Party.getParty(ps.getId()), mplew, false, true);
            }
            mplew.writeShort(0);

            return mplew.getPacket();
        }

        public static byte[] showMemberSearch(List<MapleCharacter> chr) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.MEMBER_SEARCH);
            mplew.write(chr.size());
            for (MapleCharacter c : chr) {
                mplew.writeInt(c.getId());
                mplew.writeMapleAsciiString(c.getName());
                mplew.writeShort(c.getJob());
                mplew.write(c.getLevel());
            }
            return mplew.getPacket();
        }

        public static byte[] showPartySearch(List<MapleParty> chr) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.PARTY_SEARCH);
            mplew.write(chr.size());
            for (MapleParty c : chr) {
                mplew.writeInt(c.getId());
                mplew.writeMapleAsciiString(c.getLeader().getName());
                mplew.write(c.getLeader().getLevel());
                mplew.write(c.getLeader().isOnline() ? 1 : 0);
                mplew.write(c.getMembers().size());
                for (MaplePartyCharacter ch : c.getMembers()) {
                    mplew.writeInt(ch.getId());
                    mplew.writeMapleAsciiString(ch.getName());
                    mplew.writeShort(ch.getJobId());
                    mplew.write(ch.getLevel());
                    mplew.write(ch.isOnline() ? 1 : 0);
                }
            }
            return mplew.getPacket();
        }
    }

    public static class GuildPacket {
    	
    	public static byte[] approveGuildName(String guildName) {
    		MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.GUILD_OPERATION);
    		mplew.write(5);  // Last updated: v172.2
    		mplew.writeMapleAsciiString(guildName);
    		
    		return mplew.getPacket();
    	}

        public static byte[] guildInvite(int gid, String charName, int levelFrom, int jobFrom) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.GUILD_OPERATION);
            mplew.write(7);  // Last updated: v172.2
            mplew.writeInt(gid);
            mplew.writeMapleAsciiString(charName);
            mplew.writeInt(levelFrom);
            mplew.writeInt(jobFrom);
            mplew.writeInt(0);
            
            return mplew.getPacket();
        }

        public static byte[] showGuildInfo(MapleCharacter chr) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.GUILD_OPERATION);
            mplew.write(49); // Last updated: v172.2
            if ((chr == null) || (chr.getMGC() == null)) {
                mplew.write(0);
                return mplew.getPacket();
            }
            MapleGuild g = World.Guild.getGuild(chr.getGuildId());
            if (g == null) {
                mplew.write(0);
                return mplew.getPacket();
            }
            mplew.write(0);
            mplew.write(1);
            getGuildInfo(mplew, g);

            return mplew.getPacket();
        }

        public static void getGuildInfo(MaplePacketWriter mplew, MapleGuild guild) {
            mplew.writeInt(guild.getId());
            mplew.writeMapleAsciiString(guild.getName());
            for (int i = 1; i <= 5; i++) {
                mplew.writeMapleAsciiString(guild.getRankTitle(i));
            }
            guild.addMemberData(mplew);
            mplew.writeInt(guild.getCapacity());
            mplew.writeShort(guild.getLogoBG());
            mplew.write(guild.getLogoBGColor());
            mplew.writeShort(guild.getLogo());
            mplew.write(guild.getLogoColor());
            mplew.writeMapleAsciiString(guild.getNotice());
            mplew.writeInt(guild.getHonorExp());
            mplew.writeInt(guild.getHonorExp()); // This honor exp value is a little higher than the previous one.
            mplew.writeInt(guild.getAllianceId() > 0 ? guild.getAllianceId() : 0);
            mplew.write(guild.getLevel());
            mplew.writeShort(0); // Guild Ranking
            mplew.writeInt(guild.getGP());
            mplew.write(0); // idk
            mplew.writeShort(0); // idk
            mplew.writeInt(guild.getSkills().size());
            for (MapleGuildSkill i : guild.getSkills()) {
                mplew.writeInt(i.skillID);
                //mplew.writeShort(i.level);
                //mplew.writeLong(PacketHelper.getTime(i.timestamp));
                //mplew.writeMapleAsciiString(i.purchaser);
                //mplew.writeMapleAsciiString(i.activator);
            }
        }

        public static byte[] newGuildInfo(MapleCharacter c) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.GUILD_OPERATION);
            mplew.write(38);
            if ((c == null) || (c.getMGC() == null)) {
                return genericGuildMessage((byte) 37);
            }
            MapleGuild g = World.Guild.getGuild(c.getGuildId());
            if (g == null) {
                return genericGuildMessage((byte) 37);
            }
            getGuildInfo(mplew, g);

            return mplew.getPacket();
        }

        public static byte[] newGuildMember(MapleGuildCharacter mgc) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.GUILD_OPERATION);
            mplew.write(63); // Last updated: v172.2
            mplew.writeInt(mgc.getGuildId());
            mplew.writeInt(mgc.getId());
            mplew.writeAsciiString(mgc.getName(), 13);
            mplew.writeInt(mgc.getJobId());
            mplew.writeInt(mgc.getLevel());
            mplew.writeInt(mgc.getGuildRank());
            mplew.writeInt(mgc.isOnline() ? 1 : 0);
            mplew.writeInt(mgc.getAllianceRank());
            mplew.writeInt(mgc.getGuildContribution());
            mplew.writeInt(mgc.getGuildContribution());
            mplew.writeInt(0); // IGP
            mplew.writeLong(PacketHelper.getTime(System.currentTimeMillis()));

            return mplew.getPacket();
        }

        public static byte[] memberLeft(MapleGuildCharacter mgc, boolean bExpelled) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.GUILD_OPERATION);
            mplew.write(bExpelled ? 78 : 75); // Last updated: v172.2
            mplew.writeInt(mgc.getGuildId());
            mplew.writeInt(mgc.getId());
            mplew.writeMapleAsciiString(mgc.getName());

            return mplew.getPacket();
        }

        public static byte[] guildDisband(int gid) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.GUILD_OPERATION);
            mplew.write(81); // Last updated: v172.2
            mplew.writeInt(gid);
            mplew.write(1);

            return mplew.getPacket();
        }

        public static byte[] guildCapacityChange(int gid, int capacity) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.GUILD_OPERATION);
            mplew.write(64);
            mplew.writeInt(gid);
            mplew.write(capacity);

            return mplew.getPacket();
        }

        public static byte[] guildContribution(int guildid, int charid, int contribution, int individualGP) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.GUILD_OPERATION);
            mplew.write(98); // Last updated: v172.2
            mplew.writeInt(guildid);
            mplew.writeInt(charid);
            mplew.writeInt(contribution); // Contribution
            mplew.writeInt((int)(contribution * 0.3)); // Guild Point    // Sidenote: Not sure if we need to create an actual GP column for the characters in the SQL database.
            mplew.writeInt(individualGP); // Individual Guild Point (IGP)
            mplew.writeLong(PacketHelper.getTime(System.currentTimeMillis()));

            return mplew.getPacket();
        }

        public static byte[] changeRank(MapleGuildCharacter mgc) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.GUILD_OPERATION);
            mplew.write(96); // Last updated: v172.2
            mplew.writeInt(mgc.getGuildId());
            mplew.writeInt(mgc.getId());
            mplew.write(mgc.getGuildRank());

            return mplew.getPacket();
        }

        public static byte[] rankTitleChange(int gid, String[] ranks) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.GUILD_OPERATION);
            mplew.write(94); // Last updated: v172.2
            mplew.writeInt(gid);
            for (String r : ranks) {
                mplew.writeMapleAsciiString(r);
            }

            return mplew.getPacket();
        }

        public static byte[] guildEmblemChange(int gid, short bg, byte bgcolor, short logo, byte logocolor) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.GUILD_OPERATION);
            mplew.write(99); // Last updated: v172.2
            mplew.writeInt(gid);
            mplew.writeShort(bg);
            mplew.write(bgcolor);
            mplew.writeShort(logo);
            mplew.write(logocolor);

            return mplew.getPacket();
        }

        public static byte[] updateGP(int guildId, int honorexp, int GP, int guildLevel) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.GUILD_OPERATION);
            mplew.write(105); // Last updated: v172.2
            mplew.writeInt(guildId);
            mplew.writeInt(honorexp);
            mplew.writeInt(guildLevel);
            mplew.writeInt(GP);

            return mplew.getPacket();
        }

        public static byte[] guildNotice(int gid, String notice) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.GUILD_OPERATION);
            mplew.write(75);
            mplew.writeInt(gid);
            mplew.writeMapleAsciiString(notice);

            return mplew.getPacket();
        }

        public static byte[] guildMemberLevelJobUpdate(MapleGuildCharacter mgc) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.GUILD_OPERATION);
            mplew.write(92); // Last updated: v172.2
            mplew.writeInt(mgc.getGuildId());
            mplew.writeInt(mgc.getId());
            mplew.writeInt(mgc.getLevel());
            mplew.writeInt(mgc.getJobId());

            return mplew.getPacket();
        }

        public static byte[] guildMemberOnline(int guildId, int charId, boolean bOnline) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.GUILD_OPERATION);
            mplew.write(93); // Last updated: v172.2
            mplew.writeInt(guildId);
            mplew.writeInt(charId);
            mplew.write(bOnline ? 1 : 0);
            mplew.write(1); // Unknown boolean

            return mplew.getPacket();
        }

        public static byte[] showGuildRanks(int npcid, List<MapleGuildRanking.GuildRankingInfo> all) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.GUILD_OPERATION);
            mplew.write(80);
            mplew.writeInt(npcid);
            mplew.writeInt(all.size());
            for (MapleGuildRanking.GuildRankingInfo info : all) {
                mplew.writeShort(0);
                mplew.writeMapleAsciiString(info.getName());
                mplew.writeInt(info.getGP());
                mplew.writeInt(info.getLogo());
                mplew.writeInt(info.getLogoColor());
                mplew.writeInt(info.getLogoBg());
                mplew.writeInt(info.getLogoBgColor());
            }

            return mplew.getPacket();
        }

        public static byte[] guildSkillPurchased(int gid, int sid, int level, long expiration, String purchase, String activate) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.GUILD_OPERATION);
            mplew.write(85);
            mplew.writeInt(gid);
            mplew.writeInt(sid);
            mplew.writeShort(level);
            mplew.writeLong(PacketHelper.getTime(expiration));
            mplew.writeMapleAsciiString(purchase);
            mplew.writeMapleAsciiString(activate);

            return mplew.getPacket();
        }

        public static byte[] guildLeaderChanged(int gid, int oldLeader, int newLeader, int allianceId) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.GUILD_OPERATION);
            mplew.write(89);
            mplew.writeInt(gid);
            mplew.writeInt(oldLeader);
            mplew.writeInt(newLeader);
            mplew.write(1);
            mplew.writeInt(allianceId);

            return mplew.getPacket();
        }

        public static byte[] denyGuildInvitation(String charname) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.GUILD_OPERATION);
            mplew.write(61);
            mplew.writeMapleAsciiString(charname);

            return mplew.getPacket();
        }

        public static byte[] genericGuildMessage(byte code) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.GUILD_OPERATION);
            mplew.write(code);//30 = cant find in ch
            if (code == 87) {
                mplew.writeInt(0);
            }
            /*
            if ((code == 3) || (code == 59) || (code == 60) || (code == 61) || (code == 84) || (code == 87)) {
                mplew.writeMapleAsciiString("");
            }
            */
            
            return mplew.getPacket();
        }

        public static byte[] BBSThreadList(List<MapleBBSThread> bbs, int start) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.BBS_OPERATION);
            mplew.write(6);
            if (bbs == null) {
                mplew.write(0);
                mplew.writeLong(0L);
                return mplew.getPacket();
            }
            int threadCount = bbs.size();
            MapleBBSThread notice = null;
            for (MapleBBSThread b : bbs) {
                if (b.isNotice()) {
                    notice = b;
                    break;
                }
            }
            mplew.write(notice == null ? 0 : 1);
            if (notice != null) {
                addThread(mplew, notice);
            }
            if (threadCount < start) {
                start = 0;
            }
            mplew.writeInt(threadCount);
            int pages = Math.min(10, threadCount - start);
            mplew.writeInt(pages);
            for (int i = 0; i < pages; i++) {
                addThread(mplew, (MapleBBSThread) bbs.get(start + i));
            }

            return mplew.getPacket();
        }

        private static void addThread(MaplePacketWriter mplew, MapleBBSThread rs) {
            mplew.writeInt(rs.localthreadID);
            mplew.writeInt(rs.ownerID);
            mplew.writeMapleAsciiString(rs.name);
            mplew.writeLong(PacketHelper.getKoreanTimestamp(rs.timestamp));
            mplew.writeInt(rs.icon);
            mplew.writeInt(rs.getReplyCount());
        }

        public static byte[] showThread(MapleBBSThread thread) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.BBS_OPERATION);
            mplew.write(7);
            mplew.writeInt(thread.localthreadID);
            mplew.writeInt(thread.ownerID);
            mplew.writeLong(PacketHelper.getKoreanTimestamp(thread.timestamp));
            mplew.writeMapleAsciiString(thread.name);
            mplew.writeMapleAsciiString(thread.text);
            mplew.writeInt(thread.icon);
            mplew.writeInt(thread.getReplyCount());
            for (MapleBBSThread.MapleBBSReply reply : thread.replies.values()) {
                mplew.writeInt(reply.replyid);
                mplew.writeInt(reply.ownerID);
                mplew.writeLong(PacketHelper.getKoreanTimestamp(reply.timestamp));
                mplew.writeMapleAsciiString(reply.content);
            }

            return mplew.getPacket();
        }
    }

    public static class InfoPacket {

        public static byte[] showMesoGain(long gain, boolean inChat) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.SHOW_STATUS_INFO);
            if (!inChat) {
                mplew.write(0);
                mplew.write(1);
                mplew.write(0);
                mplew.writeLong(gain);
                mplew.writeShort(0);
            } else {
                mplew.write(6);
                mplew.writeLong(gain);
                mplew.writeInt(-1);
            }

            return mplew.getPacket();
        }

        public static byte[] getShowInventoryStatus(int mode) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.SHOW_STATUS_INFO);
            mplew.write(0);
            mplew.write(mode);
            mplew.writeInt(0);
            mplew.writeInt(0);

            return mplew.getPacket();
        }

        public static byte[] getShowItemGain(int itemId, short quantity) {
            return getShowItemGain(itemId, quantity, false);
        }

        public static byte[] getShowItemGain(int itemId, short quantity, boolean inChat) {
            MaplePacketWriter mplew;

            if (inChat) {
            	mplew = new MaplePacketWriter(SendPacketOpcode.SHOW_SPECIAL_EFFECT);
                mplew.write(7);
                mplew.write(1);
                mplew.writeInt(itemId);
                mplew.writeInt(quantity);
            } else {
            	mplew = new MaplePacketWriter(SendPacketOpcode.SHOW_STATUS_INFO);
                mplew.writeShort(0);
                mplew.writeInt(itemId);
                mplew.writeInt(quantity);
            }

            return mplew.getPacket();
        }

        public static byte[] updateQuest(MapleQuestStatus quest, String status) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.SHOW_STATUS_INFO);
            mplew.write(1);
            mplew.writeInt(quest.getQuest().getId());
            mplew.write(quest.getStatus());
            switch (quest.getStatus()) {
                case 0:
                    mplew.write(0);
                    break;
                case 1:
                    mplew.writeMapleAsciiString(quest.getCustomData() != null ? quest.getCustomData() : status);
                    break;
                case 2:
                    mplew.writeLong(PacketHelper.getTime(System.currentTimeMillis()));
            }

            return mplew.getPacket();
        }
        
        public static byte[] updateQuest(int questid, String status) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.SHOW_STATUS_INFO);
            mplew.write(1);
            mplew.writeInt(questid);
            mplew.write(1);
            mplew.writeMapleAsciiString(status);

            return mplew.getPacket();
        }

        public static byte[] updateQuestMobKills(MapleQuestStatus status) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.SHOW_STATUS_INFO);
            mplew.write(1);
            mplew.writeShort(status.getQuest().getId());
            mplew.write(1);
            StringBuilder sb = new StringBuilder();
            for (Iterator<?> i$ = status.getMobKills().values().iterator(); i$.hasNext();) {
                int kills = ((Integer) i$.next()).intValue();
                sb.append(StringUtil.getLeftPaddedStr(String.valueOf(kills), '0', 3));
            }
            mplew.writeMapleAsciiString(sb.toString());
            mplew.writeLong(0L);

            return mplew.getPacket();
        }

        public static byte[] itemExpired(int itemid) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.SHOW_STATUS_INFO);
            mplew.write(2);
            mplew.writeInt(itemid);

            return mplew.getPacket();
        }

        /**
         * Sends a gain experience message with additional exp status lines based on the exp mask. The type of exp stats that can be used
         * are found in the class {@code MapleExpStatus}.
         * @param exp
         * @return
         * 
         * @see CWvsContext::OnIncEXPMessage()
         */
        public static byte[] gainExpMessage(MapleExp exp) {
        	MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.SHOW_STATUS_INFO);
        	mplew.write(3);
        	mplew.write(exp.isLastHit());
        	mplew.writeInt(exp.getExp());
        	mplew.write(exp.isQuest());
        	
        	PacketHelper.writeExpMask(mplew, exp.getExpStats());
        	
        	/* not implemented yet, just a place holder
        	if (flag & 1 != 0) {
        		mplew.writeInt(0); // nSelectedMobBonusExp
        	}
        	if (flag & 4 != 0) {
        		mplew.write(0); // nPartyBonusPercentage
        	}
        	*/
        	if (exp.isPartyBonus()) { // if (flag & 4 != 0) { i think
        		mplew.write(exp.getPartyBonusExpRate()); // nPartyBonusPercentage
        	}
        	if (exp.isQuest()) {
        		mplew.write(exp.getQuestBonusExpRate()); // nQuestBonusRate
        	}
        	if (exp.getQuestBonusExpRate() > 0) {
        		mplew.write(0); // nQuestBonusRemainCount
        	}
        	for (Map.Entry<MapleExpStatus, Integer> expStat : exp.getExpStats().entrySet()) {
        		if (expStat.getKey() == MapleExpStatus.BURNING_FIELD) {
        			mplew.writeInt(exp.getBurningFieldBonusExp());
        			mplew.writeInt(exp.getBurningFieldExpRate());
        		} else {
        			mplew.writeInt(expStat.getValue());
        		}
        	}

        	System.out.println("[Exp Packet] " + mplew.toString());
        	return mplew.getPacket();
        }
        
        // Old dysfunctional one
        public static byte[] gainExpMessage(int gain, boolean white, int partyinc, int Class_Bonus_EXP, int Equipment_Bonus_EXP, int Premium_Bonus_EXP) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.SHOW_STATUS_INFO);
            mplew.write(3);
            mplew.write(white ? 1 : 0);
            mplew.writeInt(gain);
            mplew.write(0);
            mplew.writeInt(0);
            mplew.write(0);
            mplew.write(0);
            mplew.writeInt(0);
            mplew.write(0);
            mplew.writeInt(partyinc);

            mplew.writeInt(Equipment_Bonus_EXP);
            mplew.writeInt(0);
            mplew.writeInt(0);
            mplew.write(0);
            mplew.writeInt(0);
            mplew.writeInt(0);
            mplew.writeInt(0);

            mplew.writeInt(0);
            mplew.writeInt(0);
            mplew.writeInt(0);
            mplew.writeInt(Premium_Bonus_EXP);
            mplew.writeInt(0);
            mplew.writeInt(0);
            mplew.writeInt(0);
            mplew.writeInt(0);
            mplew.writeInt(0);
            
            return mplew.getPacket();
        }

        public static byte[] GainEXP_Others(long gain, boolean inChat, boolean white) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.SHOW_STATUS_INFO);
            mplew.write(3);
            mplew.write(white ? 1 : 0);
            mplew.writeLong(gain);
            mplew.write(inChat ? 1 : 0);
            mplew.writeInt(0);
            mplew.writeInt(0);
            mplew.writeInt(0);
            mplew.writeInt(0);
            mplew.writeInt(0);
            mplew.writeInt(0);
            mplew.writeInt(0);
            if (inChat) {
                mplew.writeLong(0L);
            } else {
                mplew.writeShort(0);
                mplew.write(0);
            }
            mplew.writeInt(0);
            mplew.writeInt(0);
            mplew.writeInt(0);
            mplew.writeInt(0);
            mplew.writeInt(0);
            mplew.writeInt(0);
            mplew.writeInt(0);
            mplew.writeInt(0);
            mplew.write(0);
            return mplew.getPacket();
        }

        public static byte[] getSPMsg(byte sp, short job) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.SHOW_STATUS_INFO);
            mplew.write(4);
            mplew.writeShort(job);
            mplew.write(sp);

            return mplew.getPacket();
        }

        public static byte[] getShowFameGain(int gain) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.SHOW_STATUS_INFO);
            mplew.write(5);
            mplew.writeInt(gain);

            return mplew.getPacket();
        }

        public static byte[] getGPMsg(int itemid) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.SHOW_STATUS_INFO);
            mplew.write(7);
            mplew.writeInt(itemid);

            return mplew.getPacket();
        }

        public static byte[] getGPContribution(int itemid) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.SHOW_STATUS_INFO);
            mplew.write(8);
            mplew.writeInt(itemid);

            return mplew.getPacket();
        }

        public static byte[] getStatusMsg(int itemid) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.SHOW_STATUS_INFO);
            mplew.write(9);
            mplew.writeInt(itemid);

            return mplew.getPacket();
        }

        public static byte[] showInfo(String info) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.SHOW_STATUS_INFO);
            mplew.write(11);
            mplew.writeMapleAsciiString(info);

            return mplew.getPacket();
        }

        public static byte[] updateInfoQuest(int quest, String data) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.SHOW_STATUS_INFO);
            mplew.write(12);
            mplew.writeInt(quest);
            mplew.writeMapleAsciiString(data);
//            System.err.println("infoquest " + mplew.toString());
            return mplew.getPacket();
        }

        public static byte[] showItemReplaceMessage(List<String> message) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.SHOW_STATUS_INFO);
            mplew.write(14);
            mplew.write(message.size());
            for (String x : message) {
                mplew.writeMapleAsciiString(x);
            }

            return mplew.getPacket();
        }

        public static byte[] showTraitGain(MapleTrait.MapleTraitType trait, int amount) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.SHOW_STATUS_INFO);
            mplew.write(18); // Last updated: v173.1
            mplew.writeLong(trait.getStat().getValue());
            mplew.writeInt(amount);

            return mplew.getPacket();
        }

        public static byte[] showTraitMaxed(MapleTrait.MapleTraitType trait) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.SHOW_STATUS_INFO);
            mplew.write(17);
            mplew.writeLong(trait.getStat().getValue());

            return mplew.getPacket();
        }

        public static byte[] getBPMsg(int amount) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.SHOW_STATUS_INFO);
            mplew.write(21);
            mplew.writeInt(amount);
            mplew.writeInt(0);

            return mplew.getPacket();
        }

        public static byte[] showExpireMessage(byte type, List<Integer> item) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.SHOW_STATUS_INFO);
            mplew.write(type);
            mplew.write(item.size());
            for (Integer it : item) {
                mplew.writeInt(it.intValue());
            }

            return mplew.getPacket();
        }

        public static byte[] showStatusMessage(int mode, String info, String data) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.SHOW_STATUS_INFO);
            mplew.write(mode);
            if (mode == 22) {
                mplew.writeMapleAsciiString(info);
                mplew.writeMapleAsciiString(data);
            }

            return mplew.getPacket();
        }

        public static byte[] showReturnStone(int act) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.SHOW_STATUS_INFO);
            mplew.write(23);
            mplew.write(act);

            return mplew.getPacket();
        }

        public static byte[] showItemBox() {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.SHOW_STATUS_INFO);
            mplew.write(1);
            mplew.write(0x7A);
            mplew.write(0x1C);
            mplew.write(1);
            mplew.write(1);
            mplew.write(0);
            mplew.write(0x30);
            return mplew.getPacket();
        }

        public static byte[] getShowCoreGain(int core, int quantity) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.SHOW_STATUS_INFO);
            mplew.write(30);
            mplew.write(22);
            mplew.writeInt(core);
            mplew.writeInt(quantity);

            return mplew.getPacket();
        }
    }

    public static class BuffPacket {

        public static byte[] giveDice(int buffid, int skillid, int duration, Map<MapleBuffStat, Integer> statups) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.GIVE_BUFF);
            PacketHelper.writeBuffMask(mplew, statups);

            mplew.writeShort(Math.max(buffid / 100, Math.max(buffid / 10, buffid % 10))); // 1-6

            mplew.writeInt(skillid); // skillid
            mplew.writeInt(duration);
            mplew.writeShort(0);
            mplew.write(0);
            mplew.writeInt(GameConstants.getDiceStat(buffid, 3));
            mplew.writeInt(GameConstants.getDiceStat(buffid, 3));
            mplew.writeInt(GameConstants.getDiceStat(buffid, 4));
            mplew.writeZeroBytes(20); //idk
            mplew.writeInt(GameConstants.getDiceStat(buffid, 2));
            mplew.writeZeroBytes(12); //idk
            mplew.writeInt(GameConstants.getDiceStat(buffid, 5));
            mplew.writeZeroBytes(16); //idk
            mplew.writeInt(GameConstants.getDiceStat(buffid, 6));
            mplew.writeZeroBytes(16);
            mplew.writeZeroBytes(6);//new 143
            mplew.writeInt(1000);//new 143
            mplew.write(1);
            mplew.writeInt(0);//new143
//            mplew.write(4); // Total buffed times
//            mplew.write(0);//v112
            return mplew.getPacket();
        }

        public static byte[] giveHoming(int skillid, int mobid, int x) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.GIVE_BUFF);
            PacketHelper.writeSingleMask(mplew, MapleBuffStat.HOMING_BEACON);
            mplew.writeShort(0);
            mplew.write(0);
            mplew.writeInt(1);
            mplew.writeLong(skillid);
            mplew.write(0);
            mplew.writeLong(mobid);
            mplew.writeShort(0);
            mplew.writeShort(0);
            mplew.write(0);
            mplew.write(0);//v112
            return mplew.getPacket();
        }

      public static byte[] giveMount(int buffid, int skillid, Map<MapleBuffStat, Integer> statups) {
        MaplePacketWriter packet = new MaplePacketWriter(SendPacketOpcode.GIVE_BUFF);
        packet.writeLong(MapleBuffStat.MONSTER_RIDING.getValue());
        packet.writeLong(0);
        packet.writeLong(0);
        packet.writeLong(0);
        packet.writeLong(0); //v192 //144
        packet.writeInt(0);
        packet.writeInt(10);
        packet.writeInt(10);
        packet.writeInt(skillid); // skillid
        packet.write(HexTool.getByteArrayFromHexString("00 C2 EB 0B"));
        packet.writeInt(10);
        packet.writeInt(skillid); // skillid
        packet.write(HexTool.getByteArrayFromHexString("00 C2 EB 0B"));
        packet.writeInt(0);
        packet.writeInt(0);
        packet.write(0);
        packet.writeInt(buffid); // 1902000 saddle
        packet.writeInt(skillid); // skillid
        packet.write(0);
        packet.writeInt(0);
        packet.writeInt(0);
        packet.write(1);
        packet.writeInt(4);
        packet.write(0);
        return packet.getPacket();
    }
        
      
    public static byte[] showMonsterRiding(int cid, Map<MapleBuffStat, Integer> statups, int buffid, int skillId) {
    	MaplePacketWriter packet = new MaplePacketWriter(SendPacketOpcode.GIVE_FOREIGN_BUFF);
        packet.writeInt(cid);
        packet.writeLong(MapleBuffStat.MONSTER_RIDING.getValue());
        packet.writeLong(0);
        packet.writeLong(0);
        packet.writeLong(0);
        packet.writeZeroBytes(39); //v192 4byte. /144
        packet.writeInt(buffid); // 1902000 saddle
        packet.writeInt(skillId); // skillid
        packet.writeZeroBytes(7);
        return packet.getPacket();
    }

        public static byte[] givePirate(Map<MapleBuffStat, Integer> statups, int duration, int skillid) {
            return giveForeignPirate(statups, duration, -1, skillid);
        }

        public static byte[] giveForeignPirate(Map<MapleBuffStat, Integer> statups, int duration, int cid, int skillid) {
            final boolean infusion = skillid == 5121009 || skillid == 15111005;
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.GIVE_FOREIGN_BUFF);
            mplew.writeInt(cid);
            PacketHelper.writeBuffMask(mplew, statups);
            mplew.writeShort(0);
            mplew.write(0);
            for (Integer stat : statups.values()) {
                mplew.writeInt(stat.intValue());
                mplew.writeLong(skillid);
                mplew.writeZeroBytes(infusion ? 6 : 1);
                mplew.writeShort(duration);//duration... seconds
            }
            mplew.writeShort(0);
            mplew.writeShort(0);
            mplew.write(1);
            mplew.write(1);
            return mplew.getPacket();
        }

        public static byte[] giveArcane(int skillid, Map<Integer, Integer> statups) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.GIVE_BUFF);
            PacketHelper.writeSingleMask(mplew, MapleBuffStat.MANY_USES);
            mplew.writeShort(statups.size());
            mplew.writeInt(skillid);
            mplew.writeInt(5000);
            mplew.writeShort(0);
            mplew.write(0);
            mplew.writeShort(0);
            mplew.writeShort(0);
            mplew.write(0);
            mplew.write(0);
            mplew.writeZeroBytes(9);
            return mplew.getPacket();
        }

        public static byte[] giveEnergyChargeTest(int bar, int bufflength) {
            return giveEnergyChargeTest(-1, bar, bufflength);
        }

        public static byte[] giveEnergyChargeTest(int cid, int bar, int bufflength) {
            if (true) {
                return CWvsContext.enableActions();
            }
            MaplePacketWriter mplew;

            if (cid == -1) {
            	mplew = new MaplePacketWriter(SendPacketOpcode.GIVE_BUFF);
            } else {
            	mplew = new MaplePacketWriter(SendPacketOpcode.GIVE_FOREIGN_BUFF);
                mplew.writeInt(cid);
            }
            PacketHelper.writeSingleMask(mplew, MapleBuffStat.ENERGY_CHARGE);
            mplew.writeShort(0);
            mplew.write(0);
            mplew.writeInt(Math.min(bar, 10000));
            mplew.writeLong(0L);
            mplew.write(0);

            mplew.writeInt(bar >= 10000 ? bufflength : 0);
            mplew.write(0);
            mplew.write(6);
            return mplew.getPacket();
        }
        
        public static byte[] giveBuff(int buffid, int bufflength, Map<MapleBuffStat, Integer> statups, MapleStatEffect effect) {
        	MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.GIVE_BUFF);
        	PacketHelper.writeBuffMask(mplew, statups);

            for (Map.Entry<MapleBuffStat, Integer> stat : statups.entrySet()) {
                if (!stat.getKey().canStack()) {
                    if (GameConstants.isSpecialBuff(buffid)) {
                        mplew.writeInt(stat.getValue());
                    } else {
                        mplew.writeShort(stat.getValue());
                    }
                    mplew.writeInt(buffid);
                    mplew.writeInt(bufflength);
                }
            }

            mplew.writeShort(0); // Size for a for loop
            mplew.write(0); // nDefenseAtt
            mplew.write(0); // nDefenseState
            mplew.write(0); // nPVPDamage
            
            if (buffid == Bishop.DIVINE_PROTECTION) {
            	mplew.write(1);
            } else if (buffid == FirePoisonMage.ELEMENTAL_ADAPATION || buffid == IceLightningMage.ELEMENTAL_ADAPTATION) {
            	mplew.write(0);
            }
            
            mplew.writeInt(0);
            
            for (Map.Entry<MapleBuffStat, Integer> stat : statups.entrySet()) {
                if (stat.getKey().canStack()) {
                    mplew.writeInt(1); // stacks size
                    mplew.writeInt(buffid);
                    mplew.writeInt(stat.getValue());
                    mplew.writeInt((int) (System.currentTimeMillis() % 1000000000)); // ?
                    mplew.writeInt(1); 
                    mplew.writeInt(bufflength);
                }
            }
            
            mplew.writeInt(0);
            
            if (statups.containsKey(MapleBuffStat.MAPLE_WARRIOR) || statups.containsKey(MapleBuffStat.SPEED)) {
            	mplew.write(0);
            }
            if (statups.containsKey(MapleBuffStat.DARKSIGHT) || statups.containsKey(MapleBuffStat.ADVANCED_BLESSING)) {
            	mplew.writeInt(0);
            }
     
            mplew.writeShort(1); // Buff count. Used 1 as a placeholder for now.
            mplew.write(0); // nSubID
            mplew.write(0); // bJustBuffCheck
            mplew.write(0); // bFirstSet
            
            System.out.println(mplew.toString());
        	return mplew.getPacket();
        }
        
        
        /* Original
        public static byte[] giveBuff(int buffid, int bufflength, Map<MapleBuffStat, Integer> statups, MapleStatEffect effect) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(SendPacketOpcode.GIVE_BUFF);
            
            PacketHelper.writeBuffMask(mplew, statups);//48
            boolean stacked = false;
            boolean isAura = false;
            boolean iscombo = false;
            
            for (Map.Entry<MapleBuffStat, Integer> stat : statups.entrySet()) {
                isAura = GameConstants.isAuraBuff(stat.getKey());
                if (stat.getKey() == MapleBuffStat.COMBO) {
                    iscombo = true;
                }
                if (!stat.getKey().canStack()) {
                    boolean specialBuff = GameConstants.isSpecialBuff(stat.getKey());
                    if (specialBuff) {
                        mplew.writeInt(stat.getValue());
                    } else {
                        mplew.writeShort(stat.getValue());
                    }
                    mplew.writeInt(buffid);
                    mplew.writeInt(bufflength);
                    if (stat.getKey() == MapleBuffStat.HOLY_SHIELD) {
                        mplew.writeInt(0);
                    }
                    if (stat.getKey() == MapleBuffStat.TEMPEST_BLADES) {
                        mplew.writeZeroBytes(5);
                        mplew.writeInt(buffid == 61101002 ? 1 : 2);
                        mplew.writeInt(buffid == 61101002 ? 3 : 5);
                        mplew.writeInt(effect.getWeapon()); //weapon
                        mplew.writeInt(buffid == 61101002 ? 3 : 5);
                        if (buffid == 61120007) {
                            mplew.writeZeroBytes(8);
                        }
                    }
                }
            }

            for (Map.Entry<MapleBuffStat, Integer> stat : statups.entrySet()) {
                if (stat.getKey().canStack()) {
                    if (!stacked) {
                        mplew.writeZeroBytes(5);
                        mplew.writeZeroBytes(4); //new v143?
                        if (GameConstants.isSpecialStackBuff(stat.getKey())) {
                            mplew.writeZeroBytes(1); //not sure where this part comes
                        }
                        stacked = true;
                    }
                    mplew.writeInt(1); //amount of the same buffstat
                    //for each of the same buffstats:
                    mplew.writeInt(buffid);
                    mplew.writeInt(stat);
		mplew.writeInt(Integer.MAX_VALUE); //some kind of time
                    mplew.writeInt(0);
                    mplew.writeInt(bufflength);
                    if (stat.getKey() == MapleBuffStat.DAMAGE_CAP_INCREASE) {
                        mplew.writeInt(1000);
                    }
                }
            }
            if (buffid == 24121004) {// Priere D'Aria
                mplew.writeZeroBytes(3);
                mplew.writeShort(0);
                mplew.write(0);
            }  
           if (buffid == 2321054) {
            mplew.writeInt(0);
            }
            if (buffid == 32001003 || buffid == 32120013 || buffid == 32101003 || buffid == 32120014 || buffid == 32111012 || buffid == 32120015 || buffid == 2221054 || buffid == 36121003 || buffid == 11101022 || buffid == 11111022 || buffid == 2311012 || buffid == 100001263 || buffid == 100001264) {
            mplew.write(1);
            }
            if (!isAura) {
                mplew.writeShort(0);
                if (effect != null) {
                    if (effect.isDivineShield()) {
                        mplew.writeInt(effect.getEnhancedWatk());
                    } else if (effect.getCharColor() > 0) {
                        mplew.writeInt(effect.getCharColor());
                    } else if (effect.isInflation()) {
                        mplew.writeInt(effect.getInflation());
                    }
                }
            }
            mplew.writeShort(0);
            if (buffid == 32110000 || buffid == 32111012 || buffid == 2221054 || buffid == 11101022 || buffid == 11111022 || buffid == 2311012) {
                mplew.write(1);
            }  else if (buffid == 27121005) {
            mplew.writeInt(effect.getX());
        }      if (buffid == 15001022 && effect.getY() > 0) {
            mplew.writeInt(effect.getY());
        }  
            
            //mplew.writeZeroBytes((buffid != 36111003 && buffid != 101120109 && buffid != 27121005) ? 3 : 0); // 197

        //       if (buffid == 31011001) {// Overload Release?? idk
        //          mplew.writeInt(effect.getDuration());
        //         JobPacket.AvengerPacket.cancelExceed();
        //        }
            if (buffid == 31211004) {// Recovery
                mplew.writeInt(effect.getDuration());
                JobPacket.AvengerPacket.cancelExceed();
                CWvsContext.enableActions();
            }
     /*       if (buffid == 27111004) {
                mplew.write(0);
                mplew.writeShort(1000);
                mplew.writeShort(0);
            }*/
        /*
            if (buffid == 27110007) {
                mplew.write(0);
                mplew.writeShort(25);
            }
            if (buffid == 27101202) {
                mplew.writeZeroBytes(10);
            }
            if (iscombo) {
                mplew.writeShort(258);
                mplew.writeShort(600);
            } else {
                mplew.write(0);
                mplew.write((effect != null) && (effect.isShadow()) ? 1 : 2);
            }
            if (isAura) {
                mplew.writeInt(0);
            }
            if ((statups.containsKey(MapleBuffStat.JUMP)) || (statups.containsKey(MapleBuffStat.SPEED)) || (statups.containsKey(MapleBuffStat.MORPH)) || (statups.containsKey(MapleBuffStat.GHOST_MORPH)) /*|| (statups.containsKey(MapleBuffStat.MAPLE_WARRIOR))*/ /*|| (statups.containsKey(MapleBuffStat.MONSTER_RIDING)) || (statups.containsKey(MapleBuffStat.DASH_SPEED)) || (statups.containsKey(MapleBuffStat.DASH_JUMP)) || (statups.containsKey(MapleBuffStat.SOARING)) || (statups.containsKey(MapleBuffStat.YELLOW_AURA)) || (statups.containsKey(MapleBuffStat.SNATCH)) || (statups.containsKey(MapleBuffStat.INDIE_SPEED)) || (statups.containsKey(MapleBuffStat.ANGEL_JUMP)) || (statups.containsKey(MapleBuffStat.ENERGY_CHARGE)) || (statups.containsKey(MapleBuffStat.MECH_CHANGE))) {
                mplew.write(4);
            }
            if (buffid == 23111004) {//ignis roar
                mplew.write(0);
                mplew.writeShort(1000);
            } else {
                mplew.writeShort(0);

            }
            if (statups.containsKey(MapleBuffStat.MAPLE_WARRIOR)) {
                mplew.write(HexTool.getByteArrayFromHexString("00 E8 03 00 00 00 13 00 00 00 00"));
                return mplew.getPacket();
            }
            if (statups.containsKey(MapleBuffStat.KAISER_COMBO)) { //this too
                mplew.writeZeroBytes(8);
            }
            mplew.writeZeroBytes(69); //make sure no dc incase not enough length
            
            //new v142
            mplew.writeShort(1);
            mplew.write(0);
            mplew.write(0);
            mplew.write(0);
            System.out.println("sent Buff with buffid: " + buffid +"  as packet: "+ mplew.toString());
            return mplew.getPacket();
        } */
        
        public static byte[] giveDebuff(MapleDisease statups, int x, int skillid, int level, int duration) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.GIVE_BUFF);
            PacketHelper.writeSingleMask(mplew, statups);
            mplew.writeShort(x);
            mplew.writeShort(skillid);
            mplew.writeShort(level);
            mplew.writeInt(duration);
            mplew.writeShort(0);
            mplew.writeShort(0);
            //mplew.write(1);
            mplew.write(0);
            //mplew.write(1);
            mplew.writeZeroBytes(30);
            //System.out.println(HexTool.toString(mplew.getPacket()));
            return mplew.getPacket();
        }

   /*     public static byte[] cancelBuff(List<MapleBuffStat> statups) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.CANCEL_BUFF);

            PacketHelper.writeMask(mplew, statups);
            for (MapleBuffStat z : statups) {
                if (z.canStack()) {
                    mplew.writeInt(0); //amount of buffs still in the stack? dunno mans
                }
            }
            mplew.write(3);
            mplew.write(1);
            mplew.writeLong(0);
            mplew.writeLong(0);
            mplew.writeLong(0);
            mplew.write(0);
            return mplew.getPacket();
        }*/
        
        public static byte[] cancelBuff(List<MapleBuffStat> statups) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.CANCEL_BUFF);

            PacketHelper.writeMask(mplew, statups);
            for (MapleBuffStat stat : statups) {
                if (stat.canStack()) {
                    mplew.writeInt(0);
                }
            }
            /*
            if ((statups.contains(MapleBuffStat.JUMP)) || (statups.contains(MapleBuffStat.SPEED))) {
            	mplew.write(1);
            }
            if ((statups.contains(MapleBuffStat.HOLY_SHIELD)) || (statups.contains(MapleBuffStat.MP_BOOST))) {
            	mplew.writeLong(0);
            }
            if ((statups.contains(MapleBuffStat.MAPLE_WARRIOR))) {
            	mplew.write(0);
            }
            */
            mplew.writeLong(0);
            mplew.writeLong(0);
            
            System.out.println(mplew.toString());
            return mplew.getPacket();
        }

        public static byte[] cancelDebuff(MapleDisease mask) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.CANCEL_BUFF);

            PacketHelper.writeSingleMask(mplew, mask);
            mplew.write(3);
            mplew.write(1);
            mplew.writeLong(0);
            mplew.write(0);//v112
            return mplew.getPacket();
        }

        public static byte[] cancelHoming() {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.CANCEL_BUFF);

            PacketHelper.writeSingleMask(mplew, MapleBuffStat.HOMING_BEACON);
            mplew.write(0);//v112

            return mplew.getPacket();
        }
        
        public static byte[] giveAriaBuff(Map<MapleBuffStat, Integer> statups, int bufflevel, int buffid, int bufflength) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.GIVE_BUFF);
            PacketHelper.writeBuffMask(mplew, statups);
            // mplew.write(HexTool.getByteArrayFromHexString("00 00 00 00 00 00 00 80 00 00 00 00 00 00 00 00 00 00 40 00 00 00 00 00 00 00 00 00 00 00 00 00"));         
            for (Map.Entry stat : statups.entrySet()) {
                mplew.writeShort(((Integer) stat.getValue()).intValue());
                mplew.writeInt(buffid);
                mplew.writeInt(bufflength);
            }
            mplew.writeZeroBytes(3);
            mplew.writeShort(0); // not sure..
            mplew.write(0);
            mplew.writeShort(0);
            return mplew.getPacket();
        }
        
     public static byte[] giveForeignBuff(int cid, Map<MapleBuffStat, Integer> statups, MapleStatEffect effect) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.GIVE_FOREIGN_BUFF);
		mplew.writeInt(cid);
        PacketHelper.writeBuffMask(mplew, statups);
        for (Entry<MapleBuffStat, Integer> statup : statups.entrySet()) {
            if (statup.getKey() == MapleBuffStat.SHADOWPARTNER || statup.getKey() == MapleBuffStat.MECH_CHANGE || statup.getKey() == MapleBuffStat.DARK_AURA || statup.getKey() == MapleBuffStat.YELLOW_AURA || statup.getKey() == MapleBuffStat.BLUE_AURA || statup.getKey() == MapleBuffStat.GIANT_POTION || statup.getKey() == MapleBuffStat.SPIRIT_LINK || statup.getKey() == MapleBuffStat.PYRAMID_PQ || statup.getKey() == MapleBuffStat.WK_CHARGE || statup.getKey() == MapleBuffStat.DAMAGE_R || statup.getKey() == MapleBuffStat.MORPH || statup.getKey() == MapleBuffStat.WATER_SHIELD || statup.getKey() == MapleBuffStat.DARK_METAMORPHOSIS) {
                mplew.writeShort(statup.getValue().shortValue());
                mplew.writeInt(effect.isSkill() ? effect.getSourceId() : -effect.getSourceId());
            } else if (statup.getKey() == MapleBuffStat.FAMILIAR_SHADOW) {
                mplew.writeInt(statup.getValue());
                mplew.writeInt(effect.getCharColor());
            } else {
                mplew.writeShort(statup.getValue().shortValue());
            }
        }
        mplew.writeShort(0);
        mplew.write(0);
        if (effect.getSourceId() == 13101024) {
            mplew.writeLong(0);
            mplew.writeLong(0);
            mplew.writeLong(0);
            mplew.writeZeroBytes(6);
            mplew.write(1);
            mplew.writeZeroBytes(22);
        } else if (effect.getSourceId() == 4001003) { // Dark Sight
            mplew.writeLong(0);
            mplew.writeLong(0);
            mplew.writeZeroBytes(4);
        } else if (/*effect.getSourceId() == 1101013 || effect.getSourceId() == 1120003 ||*/ effect.getSourceId() == 11111001 || effect.getSourceId() == 11110005) { 
            mplew.writeLong(1);
            mplew.writeLong(0);
            mplew.writeZeroBytes(4);
         } else if (effect.getSourceId() == 15001004) {
            mplew.writeInt(0);
            mplew.write(0);
            mplew.writeShort(23);
            mplew.writeShort(20);
            mplew.write(0);
            mplew.write(HexTool.getByteArrayFromHexString("AB E5 E4 00"));
            mplew.writeInt(0);
            mplew.write(0);
            mplew.writeShort(23);
            mplew.writeShort(0);
        } else if (effect.getSourceId() == 61120008 || effect.getSourceId() == 61111008 || effect.getSourceId() == 61121053) {// KAISER BUFFS!
           /* mplew.writeLong(0); // old kaiser
            mplew.writeLong(0);
            mplew.writeZeroBytes(5);*/
            mplew.writeInt(2);
            mplew.writeZeroBytes(13);
            mplew.writeShort(600);
            mplew.writeZeroBytes(20);//ourstory method
        } else if (effect.getSourceId() == 21101006) {
            mplew.writeShort(0);
            mplew.write(7);
            mplew.writeLong(0);
            mplew.writeLong(0);
            mplew.write(208);
            mplew.write(2);
        }/* else if (effect.getSourceId() == 3101004 || effect.getSourceId() == 3201004 || effect.getSourceId() == 13101003 || effect.getSourceId() == 33101003) {
            mplew.writeLong(0);
            mplew.writeLong(0);
        }*/ else if (effect.getSourceId() == 30001001 || effect.getSourceId() == 30011001 || effect.getSourceId() == 2311009) {
            mplew.writeLong(0);
            mplew.writeLong(0);
            mplew.write(0);
        } else if (effect.getSourceId() == 1221004 || effect.getSourceId() == 1211006 || effect.getSourceId() == 1211008 || effect.getSourceId() == 1211004) {
            mplew.writeShort(0);
            mplew.writeLong(4);
            mplew.writeLong(0);
            mplew.write(0);
            mplew.writeShort(602);
        } else if (effect.getSourceId() == 32120000 || effect.getSourceId() == 32001003 || effect.getSourceId() == 32110000 || effect.getSourceId() == 32111012 || effect.getSourceId() == 32120001 || effect.getSourceId() == 32101003) { //ì˜¤ë�¼
            mplew.writeLong(0);
            mplew.writeLong(0);
            mplew.writeZeroBytes(5);
        } else {
            mplew.writeLong(0);
            mplew.writeLong(0);
            mplew.writeZeroBytes(6);
        }
        System.out.println("Sent foreign Efftect: "+effect.getSourceId()+" as packet: "+mplew.toString());
        return mplew.getPacket();
    }

        public static byte[] giveForeignDebuff(int cid, final MapleDisease statups, int skillid, int level, int x) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.GIVE_FOREIGN_BUFF);
		mplew.writeInt(cid);

            PacketHelper.writeSingleMask(mplew, statups);
            if (skillid == 125) {
                mplew.writeShort(0);
                mplew.write(0); //todo test
            }
            mplew.writeShort(x);
            mplew.writeShort(skillid);
            mplew.writeShort(level);
            mplew.writeShort(0); // same as give_buff
            mplew.writeShort(0); //Delay
            mplew.write(1);
            mplew.write(1);
            mplew.write(0);//v112
            mplew.writeZeroBytes(20);
            return mplew.getPacket();
        }
        
        public static byte[] cancelForeignBuff(int cid, List<MapleBuffStat> statups) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.CANCEL_FOREIGN_BUFF);
		mplew.writeInt(cid);
            PacketHelper.writeMask(mplew, statups);
            //mplew.write(3);
            mplew.write(1);
            //mplew.write(0);
            //mplew.writeZeroBytes(20);

            return mplew.getPacket();
        }
        
        public static byte[] cancelForeignRiding(int cid, List<MapleBuffStat> statups) {
        MaplePacketWriter packet = new MaplePacketWriter(SendPacketOpcode.CANCEL_FOREIGN_BUFF);
        packet.writeInt(cid);
        packet.writeLong(MapleBuffStat.MONSTER_RIDING.getValue());
        packet.writeLong(0);
        packet.writeLong(0);
        packet.writeLong(0);
        packet.writeLong(0); // v181
        packet.write(1);
        return packet.getPacket();
    }

        public static byte[] cancelForeignDebuff(int cid, MapleDisease mask) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.CANCEL_FOREIGN_BUFF);
		mplew.writeInt(cid);

            PacketHelper.writeSingleMask(mplew, mask);//48 bytes
            //mplew.write(3);
            mplew.write(1);
            //mplew.write(0);//v112
            return mplew.getPacket();
        }

        public static byte[] giveCard(int cid, int oid, int skillid) {
            MaplePacketWriter writer = new MaplePacketWriter(SendPacketOpcode.GAIN_FORCE);
            writer.write(0);
            writer.writeInt(cid);
            writer.writeInt(1);
            writer.writeInt(oid);
            writer.writeInt(skillid);
            writer.write(1);
            writer.writeInt(2);
            writer.writeInt(1);
            writer.writeInt(21);
            writer.writeInt(8);
            writer.writeInt(8);
            writer.write(0);
            return writer.getPacket();
        }
    }

    public static class InventoryPacket {

        public static byte[] addInventorySlot(MapleInventoryType type, Item item) {
            return addInventorySlot(type, item, false);
        }

        public static byte[] addInventorySlot(MapleInventoryType type, Item item, boolean fromDrop) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.INVENTORY_OPERATION);
            mplew.write(fromDrop ? 1 : 0);
            mplew.write(1);
            mplew.write(0);

            mplew.write(GameConstants.isInBag(item.getPosition(), type.getType()) ? 9 : 0);
            mplew.write(type.getType());
            mplew.writeShort(item.getPosition());
            PacketHelper.addItemInfo(mplew, item);
            return mplew.getPacket();
        }

        public static byte[] updateInventorySlot(MapleInventoryType type, Item item, boolean fromDrop) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.INVENTORY_OPERATION);
            mplew.write(fromDrop ? 1 : 0);
            mplew.write(1);
            mplew.write(0);

            mplew.write(GameConstants.isInBag(item.getPosition(), type.getType()) ? 6 : 1);
            mplew.write(type.getType());
            mplew.writeShort(item.getPosition());
            mplew.writeShort(item.getQuantity());

            return mplew.getPacket();
        }

        public static byte[] moveInventoryItem(MapleInventoryType type, short src, short dst, boolean bag, boolean bothBag) {
            return moveInventoryItem(type, src, dst, (byte) -1, bag, bothBag);
        }

        public static byte[] moveInventoryItem(MapleInventoryType type, short src, short dst, short equipIndicator, boolean bag, boolean bothBag) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.INVENTORY_OPERATION);
            mplew.write(1);
            mplew.write(1);
            mplew.write(0);

            mplew.write(bag ? 5 : bothBag ? 8 : 2);
            mplew.write(type.getType());
            mplew.writeShort(src);
            mplew.writeShort(dst);
            if (bag) {
                mplew.writeShort(0);
            }
            if (equipIndicator != -1) {
                mplew.write(equipIndicator);
            }

            return mplew.getPacket();
        }

        public static byte[] moveAndMergeInventoryItem(MapleInventoryType type, short src, short dst, short total, boolean bag, boolean switchSrcDst, boolean bothBag) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.INVENTORY_OPERATION);
            mplew.write(1);
            mplew.write(2);
            mplew.write(0);

            mplew.write((bag) && ((switchSrcDst) || (bothBag)) ? 7 : 3);
            mplew.write(type.getType());
            mplew.writeShort(src);

            mplew.write((bag) && ((!switchSrcDst) || (bothBag)) ? 6 : 1);
            mplew.write(type.getType());
            mplew.writeShort(dst);
            mplew.writeShort(total);

            return mplew.getPacket();
        }

        public static byte[] moveAndMergeWithRestInventoryItem(MapleInventoryType type, short src, short dst, short srcQ, short dstQ, boolean bag, boolean switchSrcDst, boolean bothBag) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.INVENTORY_OPERATION);
            mplew.write(1);
            mplew.write(2);
            mplew.write(0);

            mplew.write((bag) && ((switchSrcDst) || (bothBag)) ? 6 : 1);
            mplew.write(type.getType());
            mplew.writeShort(src);
            mplew.writeShort(srcQ);

            mplew.write((bag) && ((!switchSrcDst) || (bothBag)) ? 6 : 1);
            mplew.write(type.getType());
            mplew.writeShort(dst);
            mplew.writeShort(dstQ);

            return mplew.getPacket();
        }

        public static byte[] clearInventoryItem(MapleInventoryType type, short slot, boolean fromDrop) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.INVENTORY_OPERATION);
            mplew.write(fromDrop ? 1 : 0);
            mplew.write(1);
            mplew.write(0);

            mplew.write((slot > 100) && (type == MapleInventoryType.ETC) ? 7 : 3);
            mplew.write(type.getType());
            mplew.writeShort(slot);

            return mplew.getPacket();
        }

        public static byte[] updateSpecialItemUse(Item item, byte invType, MapleCharacter chr) {
            return updateSpecialItemUse(item, invType, item.getPosition(), false, chr);
        }

        public static byte[] updateSpecialItemUse(Item item, byte invType, short pos, boolean theShort, MapleCharacter chr) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.INVENTORY_OPERATION);
            mplew.write(0);
            mplew.write(2);
            mplew.write(0);

            mplew.write(GameConstants.isInBag(pos, invType) ? 7 : 3);
            mplew.write(invType);
            mplew.writeShort(pos);

            mplew.write(0);
            mplew.write(invType);
            if ((item.getType() == 1) || (theShort)) {
                mplew.writeShort(pos);
            } else {
                mplew.write(pos);
            }
            PacketHelper.addItemInfo(mplew, item, chr);
            if (pos < 0) {
                mplew.write(2);
            }

            return mplew.getPacket();
        }

        public static byte[] updateSpecialItemUse_(Item item, byte invType, MapleCharacter chr) {
            return updateSpecialItemUse_(item, invType, item.getPosition(), chr);
        }

        public static byte[] updateSpecialItemUse_(Item item, byte invType, short pos, MapleCharacter chr) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.INVENTORY_OPERATION);
            mplew.write(0);
            mplew.write(1);
            mplew.write(0);

            mplew.write(0);
            mplew.write(invType);
            if (item.getType() == 1) {
                mplew.writeShort(pos);
            } else {
                mplew.write(pos);
            }
            PacketHelper.addItemInfo(mplew, item, chr);
            if (pos < 0) {
                mplew.write(1);
            }

            return mplew.getPacket();
        }

        public static byte[] updateEquippedItem(MapleCharacter chr, Equip eq, short pos) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.INVENTORY_OPERATION);
            mplew.write(0);
            mplew.write(1);
            mplew.write(0);

            mplew.write(0);
            mplew.write(1);
            mplew.writeShort(pos);
            PacketHelper.addItemInfo(mplew, eq, chr);

            return mplew.getPacket();
        }

        public static byte[] scrolledItem(Item scroll, MapleInventoryType inv, Item item, boolean destroyed, boolean potential, boolean equipped) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.INVENTORY_OPERATION);
            mplew.write(1);
            mplew.write(destroyed ? 2 : 3);
            mplew.write(0);

            mplew.write(scroll.getQuantity() > 0 ? 1 : 3);
            mplew.write(GameConstants.getInventoryType(scroll.getItemId()).getType());
            mplew.writeShort(scroll.getPosition());
            if (scroll.getQuantity() > 0) {
                mplew.writeShort(scroll.getQuantity());
            }

            mplew.write(3);
            mplew.write(inv.getType());
            mplew.writeShort(item.getPosition());
            if (!destroyed) {
                mplew.write(0);
                mplew.write(inv.getType());
                mplew.writeShort(item.getPosition());
                PacketHelper.addItemInfo(mplew, item);
            }
            if (!potential) {
                mplew.write(1);
            }
            if (equipped) {
                mplew.write(8);
            }

            return mplew.getPacket();
        }

        public static byte[] moveAndUpgradeItem(MapleInventoryType type, Item item, short oldpos, short newpos, MapleCharacter chr) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.INVENTORY_OPERATION);
            mplew.write(1);
            mplew.write(3);
            mplew.write(0);

            mplew.write(GameConstants.isInBag(newpos, type.getType()) ? 7 : 3);
            mplew.write(type.getType());
            mplew.writeShort(oldpos);

            mplew.write(0);
            mplew.write(1);
            mplew.writeShort(oldpos);
            PacketHelper.addItemInfo(mplew, item, chr);

            mplew.write(2);
            mplew.write(type.getType());
            mplew.writeShort(oldpos);
            mplew.writeShort(newpos);
            mplew.write(0);

            return mplew.getPacket();
        }

        public static byte[] dropInventoryItem(MapleInventoryType type, short src) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.INVENTORY_OPERATION);
            mplew.write(1);
            mplew.write(1);
            mplew.write(0);

            mplew.write(3);
            mplew.write(type.getType());
            mplew.writeShort(src);
            if (src < 0) {
                mplew.write(1);
            }

            return mplew.getPacket();
        }

        public static byte[] dropInventoryItemUpdate(MapleInventoryType type, Item item) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.INVENTORY_OPERATION);
            mplew.write(1);
            mplew.write(1);
            mplew.write(0);

            mplew.write(1);
            mplew.write(type.getType());
            mplew.writeShort(item.getPosition());
            mplew.writeShort(item.getQuantity());

            return mplew.getPacket();
        }

        public static byte[] getInventoryFull() {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.INVENTORY_OPERATION);
            mplew.write(1);
            mplew.write(0);
            mplew.write(0);

            return mplew.getPacket();
        }

        public static byte[] getInventoryStatus() {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.INVENTORY_OPERATION);
            mplew.write(0);
            mplew.write(0);
            mplew.write(0);

            return mplew.getPacket();
        }

        public static byte[] getSlotUpdate(byte invType, byte newSlots) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.INVENTORY_GROW);
            mplew.write(invType);
            mplew.write(newSlots);

            return mplew.getPacket();
        }

        public static byte[] getShowInventoryFull() {
            return CWvsContext.InfoPacket.getShowInventoryStatus(255);
        }

        public static byte[] showItemUnavailable() {
            return CWvsContext.InfoPacket.getShowInventoryStatus(254);
        }
    }

    public static byte[] updateHyperSp(int mode, int remainSp) {
        return updateSpecialStat("hyper", 0x1C, mode, remainSp);
    }

    public static byte[] updateSpecialStat(String stat, int array, int mode, int amount) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.SPECIAL_STAT);
		mplew.writeMapleAsciiString(stat);
        mplew.writeInt(array);
        mplew.writeInt(mode);
        mplew.write(1);
        mplew.writeInt(amount);

        return mplew.getPacket();
    }

    public static byte[] updateMaplePoint(int mp) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.MAPLE_POINT);
		mplew.writeInt(mp);

        return mplew.getPacket();
    }

    public static byte[] updateCrowns(int[] titles) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.EVENT_CROWN);
        for (int i = 0; i < 5; i++) {
            mplew.writeMapleAsciiString("");
            if (titles.length < i + 1) {
                mplew.write(-1);
            } else {
                mplew.write(titles[i]);
            }
        }

        return mplew.getPacket();
    }

    public static byte[] magicWheel(int type, List<Integer> items, String data, int endSlot) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.MAGIC_WHEEL);
		mplew.write(type);
        switch (type) {
            case 3:
                mplew.write(items.size());
                for (int item : items) {
                    mplew.writeInt(item);
                }
                mplew.writeMapleAsciiString(data); // nexon encrypt the item and then send the string
                mplew.write(endSlot);
                break;
            case 5:
                //<Character Name> got <Item Name>.
                break;
            case 6:
                //You don't have a Magic Gachapon Wheel in your Inventory.
                break;
            case 7:
                //You don't have any Inventory Space.\r\n You must have 2 or more slots available\r\n in each of your tabs.
                break;
            case 8:
                //Please try this again later.
                break;
            case 9:
                //Failed to delete Magic Gachapon Wheel item.
                break;
            case 0xA:
                //Failed to receive Magic Gachapon Wheel item.
                break;
            case 0xB:
                //You cannot move while Magic Wheel window is open.
                break;
        }

        return mplew.getPacket();
    }

    public static class Reward {

        public static byte[] receiveReward(int id, byte mode, int quantity) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.REWARD);
		mplew.write(mode); // mode
            switch (mode) { // mode
                case 9:
                    mplew.writeInt(0);
                    break;
                case 0x0B:
                    mplew.writeInt(id);
                    mplew.writeInt(quantity); //quantity
                    //Popup: You have received the Maple Points.\r\n( %d maple point )
                    break;
                case 0x0C:
                    mplew.writeInt(id);
                    //Popup You have received the Game item.
                    break;
                case 0x0E:
                    mplew.writeInt(id);
                    mplew.writeInt(quantity); //quantity
                    //Popup: You have received the Mesos.\r\n( %d meso )
                    break;
                case 0x0F:
                    mplew.writeInt(id);
                    mplew.writeInt(quantity); //quantity
                    //Popup: You have received the Exp.\r\n( %d exp )
                    break;
                case 0x14:
                    //Popup: Failed to receive the Maple Points.
                    break;
                case 0x15:
                    mplew.write(0);
                    //Popup: Failed to receive the Game Item.
                    break;
                case 0x16:
                    mplew.write(0);
                    //Popup: Failed to receive the Game Item.
                    break;
                case 0x17:
                    //Popup: Failed to receive the Mesos.
                    break;
                case 0x18:
                    //Popup: Failed to receive the Exp.
                    break;
                case 0x21:
                    mplew.write(0); //66
                    //No inventory space
                    break;
            }

            return mplew.getPacket();
        }

        public static byte[] updateReward(int id, byte mode, List<MapleReward> rewards, int option) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.REWARD);
		mplew.write(mode); // mode
            switch (mode) { // mode
                case 9:
                    mplew.writeInt(rewards.size());
                    if (rewards.size() > 0) {
                        for (int i = 0; i < rewards.size(); i++) {
                            MapleReward reward = rewards.get(i);
                            boolean empty = reward.getId() < 1;
                            mplew.writeInt(empty ? 0 : reward.getId()); // 0 = blank 1+ = gift
                            if (!empty) {
                                if ((option & 1) != 0) {
                                    mplew.writeLong(reward.getReceiveDate()); //start time
                                    mplew.writeLong(reward.getExpireDate()); //end time
                                    mplew.writeLong(reward.getReceiveDate()); //start time
                                    mplew.writeLong(reward.getExpireDate()); //end time
                                }
                                if ((option & 2) != 0) { //nexon do here a3 & 2 when a3 is 9
                                    mplew.writeInt(0);
                                    mplew.writeInt(0);
                                    mplew.writeInt(0);
                                    mplew.writeInt(0);
                                    mplew.writeInt(0);
                                    mplew.writeInt(0);
                                    mplew.writeMapleAsciiString("");
                                    mplew.writeMapleAsciiString("");
                                    mplew.writeMapleAsciiString("");
                                }
                                mplew.writeInt(reward.getType()); //type 3 = maple point 4 = mesos 5 = exp
                                mplew.writeInt(reward.getItem()); // item id
                                mplew.writeInt(/*itemQ*/reward.getItem() > 0 ? 1 : 0); // item quantity (?)
                                mplew.writeInt(0);
                                mplew.writeLong(0L);
                                mplew.writeInt(0);
                                mplew.writeInt(reward.getMaplePoints()); // maple point amount
                                mplew.writeInt(reward.getMeso()); // mesos amount
                                mplew.writeInt(reward.getExp()); // exp amount
                                mplew.writeInt(0);
                                mplew.writeInt(0);
                                mplew.writeMapleAsciiString("");
                                mplew.writeMapleAsciiString("");
                                mplew.writeMapleAsciiString("");
                                mplew.writeMapleAsciiString(reward.getDesc());
                            }
                        }
                    }
                    break;
                case 0x0B:
                    mplew.writeInt(id);
                    mplew.writeInt(0); //quantity
                    //Popup: You have received the Maple Points.\r\n( %d maple point )
                    break;
                case 0x0C:
                    mplew.writeInt(id);
                    //Popup You have received the Game item.
                    break;
                case 0x0E:
                    mplew.writeInt(id);
                    mplew.writeInt(0); //quantity
                    //Popup: You have received the Mesos.\r\n( %d meso )
                    break;
                case 0x0F:
                    mplew.writeInt(id);
                    mplew.writeInt(0); //quantity
                    //Popup: You have received the Exp.\r\n( %d exp )
                    break;
                case 0x14:
                    //Popup: Failed to receive the Maple Points.
                    break;
                case 0x15:
                    mplew.write(0);
                    //Popup: Failed to receive the Game Item.
                    break;
                case 0x16:
                    mplew.write(0);
                    //Popup: Failed to receive the Game Item.
                    break;
                case 0x17:
                    //Popup: Failed to receive the Mesos.
                    break;
                case 0x18:
                    //Popup: Failed to receive the Exp.
                    break;
                case 0x21:
                    mplew.write(0); //66
                    //No inventory space
                    break;
            }

            return mplew.getPacket();
        }
    }
}