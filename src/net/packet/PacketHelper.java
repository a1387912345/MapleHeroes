package net.packet;

import client.*;
import client.inventory.*;
import constants.GameConstants;
import constants.ServerConstants;
import net.Buffstat;
import net.netty.MaplePacketWriter;
import net.world.MapleCharacterLook;

import java.util.*;
import java.util.Map.Entry;
import server.CashItem;
import server.MapleItemInformationProvider;
import server.shops.MapleShop;
import server.shops.MapleShopItem;
import server.movement.LifeMovementFragment;
import server.quest.MapleQuest;
import server.stores.AbstractPlayerStore;
import server.stores.IMaplePlayerShop;
import tools.BitTools;
import tools.HexTool;
import tools.KoreanDateUtil;
import tools.Pair;
import tools.StringUtil;
import tools.Triple;

public class PacketHelper {

    public static final long FT_UT_OFFSET = 116444592000000000L;
    public static final long MAX_TIME = 150842304000000000L;
    public static final long ZERO_TIME = 94354848000000000L;
    public static final long PERMANENT = 150841440000000000L;

    public static long getKoreanTimestamp(long realTimestamp) {
        return getTime(realTimestamp);
    }

    public static long getTime(long realTimestamp) {
        if (realTimestamp == -1L) { // 00 80 05 BB 46 E6 17 02, 1/1/2079
            return MAX_TIME;
        }
        if (realTimestamp == -2L) { // 00 40 E0 FD 3B 37 4F 01, 1/1/1900
            return ZERO_TIME;
        }
        if (realTimestamp == -3L) {
            return PERMANENT;
        }
        return realTimestamp * 10000L + 116444592000000000L;
    }

    public static long decodeTime(long fakeTimestamp) {
        if (fakeTimestamp == 150842304000000000L) {
            return -1L;
        }
        if (fakeTimestamp == 94354848000000000L) {
            return -2L;
        }
        if (fakeTimestamp == 150841440000000000L) {
            return -3L;
        }
        return (fakeTimestamp - 116444592000000000L) / 10000L;
    }

    public static long getFileTimestamp(long timeStampinMillis, boolean roundToMinutes) {
        if (SimpleTimeZone.getDefault().inDaylightTime(new Date())) {
            timeStampinMillis -= 3600000L;
        }
        long time;

        if (roundToMinutes) {
            time = timeStampinMillis / 1000L / 60L * 600000000L;
        } else {
            time = timeStampinMillis * 10000L;
        }
        return time + 116444592000000000L;
    }

    public static void addImageInfo(MaplePacketWriter mplew, byte[] image) {
        mplew.writeInt(image.length);
        mplew.write(image);
    }

    public static void addStartedQuestInfo(MaplePacketWriter mplew, MapleCharacter chr) {
        mplew.write(1);
        final List<MapleQuestStatus> started = chr.getStartedQuests();
        mplew.writeShort(started.size());
        for (MapleQuestStatus q : started) {
            mplew.writeInt(q.getQuest().getId());
            if (q.hasMobKills()) {
                StringBuilder sb = new StringBuilder();
                for (Iterator i$ = q.getMobKills().values().iterator(); i$.hasNext();) {
                    int kills = ((Integer) i$.next()).intValue();
                    sb.append(StringUtil.getLeftPaddedStr(String.valueOf(kills), '0', 3));
                }
                mplew.writeMapleAsciiString(sb.toString());
            } else {
                mplew.writeMapleAsciiString(q.getCustomData() == null ? "" : q.getCustomData());
            }
        }
        addNXQuestInfo(mplew, chr);
    }

    public static void addNXQuestInfo(MaplePacketWriter mplew, MapleCharacter chr) {
    	mplew.writeShort(0);
        /*
         mplew.writeShort(7);
         mplew.writeMapleAsciiString("1NX5211068");
         mplew.writeMapleAsciiString("1");
         mplew.writeMapleAsciiString("SE20130619");
         mplew.writeMapleAsciiString("20130626060823");
         mplew.writeMapleAsciiString("99NX5533018");
         mplew.writeMapleAsciiString("1");
         mplew.writeMapleAsciiString("1NX1003792");
         mplew.writeMapleAsciiString("1");
         mplew.writeMapleAsciiString("1NX1702337");
         mplew.writeMapleAsciiString("1");
         mplew.writeMapleAsciiString("1NX9102857");
         mplew.writeMapleAsciiString("1");
         mplew.writeMapleAsciiString("SE20130116");
         mplew.writeMapleAsciiString("1");
         */
    }

    public static void addCompletedQuestInfo(MaplePacketWriter mplew, MapleCharacter chr) {
        mplew.write(1);
        final List<MapleQuestStatus> completed = chr.getCompletedQuests();
        mplew.writeShort(completed.size());
        for (MapleQuestStatus q : completed) {
            mplew.writeInt(q.getQuest().getId());
            mplew.writeInt(KoreanDateUtil.getQuestTimestamp(q.getCompletionTime()));
            //v139 changed from long to int
        }
    }

    public static void addSkillInfo(MaplePacketWriter mplew, MapleCharacter chr) {
        mplew.write(1);
        mplew.writeShort(0); // Skills Count
        /*MaplePacketLittleEndianWriter mplew1 =  new MaplePacketLittleEndianWriter();
         final Map<Skill, SkillEntry> skills = chr.getSkills();
         mplew1.write(1);
         int hyper = 0;
         //for (Skill skill : skills.keySet()) {
         //    if (skill.isHyper()) hyper++;
         //}
         mplew1.writeShort(skills.size() - hyper);
         boolean follow = false;
        
         for (Map.Entry<Skill, SkillEntry> skill : skills.entrySet()) {
         //if (((Skill) skill.getKey()).isHyper()) continue;
            
         if (follow) {
         follow = false;
         if (!GameConstants.isHyperSkill((Skill) skill.getKey()))
         mplew1.writeInt(skill.getKey().getId());
         }
         mplew1.writeInt(skill.getKey().getId());
         mplew1.writeInt(((SkillEntry) skill.getValue()).skillevel);
         addExpirationTime(mplew1, ((SkillEntry) skill.getValue()).expiration);

         if (GameConstants.isHyperSkill((Skill) skill.getKey())) {
         // mplew1.writeInt(1110009);
         follow = true;
         } else if (((Skill) skill.getKey()).isFourthJob()) {
         mplew1.writeInt(((SkillEntry) skill.getValue()).masterlevel);
         }
         //  addSingleSkill(mplew, skill.getKey(), skill.getValue());
         }
         mplew.write(mplew1.getPacket());
         System.out.println(HexTool.toString(mplew1.getPacket()));
         */
    }

//    public static void addSingleSkill(MaplePacketLittleEndianWriter mplew, Skill skill, SkillEntry ske) {
//        try {
//            // if (skill.getId() != 1001008) return;
//
//            MaplePacketLittleEndianWriter mplew1 = new MaplePacketLittleEndianWriter();
//
//            mplew1.writeInt(skill.getId());
//            mplew1.writeInt(ske.skillevel);
//            addExpirationTime(mplew1, ske.expiration);
//
//            if (GameConstants.isHyperSkill(skill)) {
//                //System.out.println("HYPER: " + ((Skill) skill.getKey()).getId());
//                mplew1.writeInt(0);
//            } else if (((Skill) skill).isFourthJob()) {
//                mplew1.writeInt(((SkillEntry) ske).masterlevel);
//            }
//            if (skill.getId() == 1001008) {
//                System.out.println(HexTool.toString(mplew1.getPacket()));
//            }
//            mplew.write(mplew1.getPacket());
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//    }
    public static void addCoolDownInfo(MaplePacketWriter mplew, MapleCharacter chr) {
        final List<MapleCoolDownValueHolder> cd = chr.getCooldowns();

        mplew.writeShort(cd.size());
        for (MapleCoolDownValueHolder cooling : cd) {
            mplew.writeInt(cooling.skillId);
            mplew.writeInt((int) (cooling.length + cooling.startTime - System.currentTimeMillis()) / 1000);
        }
        if (cd.isEmpty()) {
            mplew.writeShort(0);
        }
    }

    public static void addRocksInfo(MaplePacketWriter mplew, MapleCharacter chr) {
        int[] mapz = chr.getRegRocks();
        for (int i = 0; i < 5; i++) {
            mplew.writeInt(mapz[i]);
        }

        int[] map = chr.getRocks();
        for (int i = 0; i < 10; i++) {
            mplew.writeInt(map[i]);
        }

        int[] maps = chr.getHyperRocks();
        for (int i = 0; i < 13; i++) {
            mplew.writeInt(maps[i]);
        }
        for (int i = 0; i < 13; i++) {
            mplew.writeInt(maps[i]);
        }
    }

    public static void addMiniGameRecordsInfo(MaplePacketWriter mplew, MapleCharacter chr) {
        short size = 0;
        mplew.writeShort(size);
        for (int i = 0; i < size; i++) {
            mplew.writeInt(0);
            mplew.writeInt(0);
            mplew.writeInt(0);
            mplew.writeInt(0);
            mplew.writeInt(0);
        }
    }

    public static void addRingInfo(MaplePacketWriter mplew, MapleCharacter chr) {
        Triple<List<MapleRing>, List<MapleRing>, List<MapleRing>> aRing = chr.getRings(true);
        List<MapleRing> cRing = aRing.getLeft();
        mplew.writeShort(cRing.size());
        for (MapleRing ring : cRing) {
            mplew.writeInt(ring.getPartnerChrId());
            mplew.writeAsciiString(ring.getPartnerName(), 13);
            mplew.writeLong(ring.getRingId());
            mplew.writeLong(ring.getPartnerRingId());
        }
        List<MapleRing> fRing = aRing.getMid();
        mplew.writeShort(fRing.size());
        for (MapleRing ring : fRing) {
            mplew.writeInt(ring.getPartnerChrId());
            mplew.writeAsciiString(ring.getPartnerName(), 13);
            mplew.writeLong(ring.getRingId());
            mplew.writeLong(ring.getPartnerRingId());
            mplew.writeInt(ring.getItemId());
        }
        List<MapleRing> mRing = aRing.getRight();
        mplew.writeShort(mRing.size());
        int marriageId = 30000;
        for (MapleRing ring : mRing) {
            mplew.writeInt(marriageId);
            mplew.writeInt(chr.getId());
            mplew.writeInt(ring.getPartnerChrId());
            mplew.writeShort(3);
            mplew.writeInt(ring.getItemId());
            mplew.writeInt(ring.getItemId());
            mplew.writeAsciiString(chr.getName(), 13);
            mplew.writeAsciiString(ring.getPartnerName(), 13);
        }
    }

    public static void addMoneyInfo(MaplePacketWriter mplew, MapleCharacter chr) {
        mplew.writeLong(chr.getMeso()); //changed to long v139
    }

    public static void addInventoryInfo(MaplePacketWriter mplew, MapleCharacter chr) {
        mplew.writeInt(0);
        addPotionPotInfo(mplew, chr);
        //RED stuff:
        mplew.writeInt(0);
        mplew.writeInt(chr.getId());
        mplew.writeInt(0); 
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.writeInt(0);

        mplew.writeInt(0);

        mplew.write(0);
        mplew.write(0);
        mplew.write(0);

        mplew.write(chr.getInventory(MapleInventoryType.EQUIP).getSlotLimit());
        mplew.write(chr.getInventory(MapleInventoryType.USE).getSlotLimit());
        mplew.write(chr.getInventory(MapleInventoryType.SETUP).getSlotLimit());
        mplew.write(chr.getInventory(MapleInventoryType.ETC).getSlotLimit());
        mplew.write(chr.getInventory(MapleInventoryType.CASH).getSlotLimit());

        MapleQuestStatus stat = chr.getQuestNoAdd(MapleQuest.getInstance(122700));
        if ((stat != null) && (stat.getCustomData() != null) && (Long.parseLong(stat.getCustomData()) > System.currentTimeMillis())) {
            mplew.writeLong(getTime(Long.parseLong(stat.getCustomData())));
        } else {
            mplew.writeLong(getTime(-2L));
        }
        mplew.write(0);
        
        MapleInventory iv = chr.getInventory(MapleInventoryType.EQUIPPED);
        final List<Item> equipped = iv.newList();
        Collections.sort(equipped);
        
        
        for (Item item : equipped) {
            if ((item.getPosition() < 0) && (item.getPosition() > -100)) {
                addItemPosition(mplew, item, false, false);
                addItemInfo(mplew, item, chr);
            }
        }
        mplew.writeShort(0); // equipped equipment
        
        for (Item item : equipped) { 
            if ((item.getPosition() <= -100) && (item.getPosition() > -1000)) {
                System.out.println("Item Position " + item.getPosition());
                addItemPosition(mplew, item, false, false);
                addItemInfo(mplew, item, chr);
            }
        }
        mplew.writeShort(0);// cash equip
        iv = chr.getInventory(MapleInventoryType.EQUIP);
        for (Item item : iv.list()) { 
            addItemPosition(mplew, item, false, false);
            addItemInfo(mplew, item, chr);
        }
        mplew.writeShort(0);// equipment
        for (Item item : equipped) { 
            if ((item.getPosition() <= -1000) && (item.getPosition() > -1100)) {
                addItemPosition(mplew, item, false, false);
                addItemInfo(mplew, item, chr);
            }
        }
        mplew.writeShort(0);// unknown
        for (Item item : equipped) { 
            if ((item.getPosition() <= -1100) && (item.getPosition() > -1200)) {
                addItemPosition(mplew, item, false, false);
                addItemInfo(mplew, item, chr);
            }
        }
        mplew.writeShort(0); // dragon
        mplew.writeShort(0); // mech
        for (Item item : equipped) {
            if (item.getPosition() <= -1200) {
                addItemPosition(mplew, item, false, false);
                addItemInfo(mplew, item, chr);
            }
        }
        mplew.writeShort(0); // android
        mplew.writeShort(0); // unknown
        mplew.writeShort(0); // unknown
        mplew.writeShort(0); // unknown
        for (Item item : equipped) {
            if ((item.getPosition() <= -5000) && (item.getPosition() >= -5003)) {
                addItemPosition(mplew, item, false, false);
                addItemInfo(mplew, item, chr);
            }
        }      
        mplew.writeShort(0); // totem
        mplew.writeShort(0); // unknown
        mplew.writeShort(0); // unknown
        mplew.writeShort(0); // unknown
        mplew.writeShort(0); // unknown
        
        iv = chr.getInventory(MapleInventoryType.USE);
        for (Item item : iv.list()) {
            addItemPosition(mplew, item, false, false);
            addItemInfo(mplew, item, chr);
        }
        mplew.write(0);
             
        iv = chr.getInventory(MapleInventoryType.SETUP);
        for (Item item : iv.list()) {
            addItemPosition(mplew, item, false, false);
            addItemInfo(mplew, item, chr);
        }
        mplew.write(0);
        iv = chr.getInventory(MapleInventoryType.ETC);
        for (Item item : iv.list()) {
            if (item.getPosition() < 100) {
                addItemPosition(mplew, item, false, false);
                addItemInfo(mplew, item, chr);
            }
        }
        mplew.write(0);
        iv = chr.getInventory(MapleInventoryType.CASH);
        for (Item item : iv.list()) {
            addItemPosition(mplew, item, false, false);
            addItemInfo(mplew, item, chr);
        }
        mplew.write(0);
//        for (int i = 0; i < chr.getExtendedSlots().size(); i++) {
//            mplew.writeInt(i);
//            mplew.writeInt(chr.getExtendedSlot(i));
//            for (Item item : chr.getInventory(MapleInventoryType.ETC).list()) {
//                if ((item.getPosition() > i * 100 + 100) && (item.getPosition() < i * 100 + 200)) {
//                    addItemPosition(mplew, item, false, true);
//                    addItemInfo(mplew, item, chr);
//                }
//            }
//            mplew.writeInt(-1);
//        }
        mplew.writeZeroBytes(21);//was17
        
    }

    public static void addPotionPotInfo(MaplePacketWriter mplew, MapleCharacter chr) {
        if (chr.getPotionPots() == null) {
            mplew.writeInt(0);
            return;
        }
        mplew.writeInt(chr.getPotionPots().size());
        for (MaplePotionPot p : chr.getPotionPots()) {
            mplew.writeInt(p.getId());
            mplew.writeInt(p.getMaxValue());
            mplew.writeInt(p.getHp());
            mplew.writeInt(0);
            mplew.writeInt(p.getMp());

            mplew.writeLong(PacketHelper.getTime(p.getStartDate()));
            mplew.writeLong(PacketHelper.getTime(p.getEndDate()));
        }
    }

    public static void addCharStats(MaplePacketWriter mplew, MapleCharacter chr) {
    	mplew.writeInt(chr.getId());
        mplew.writeInt(chr.getId()); // dwCharacterIDForLog
        mplew.writeInt(0); // [FF FF FF 7F] dwWorldIDForLog
        mplew.writeAsciiString(chr.getName(), 13);
        mplew.write(chr.getGender());
        mplew.write(chr.getSkinColor());
        mplew.writeInt(chr.getFace());
        mplew.writeInt(chr.getHair());
        mplew.write(-1); // nMixHairBaseColor
        mplew.write(0);  // nMixAddHairColor
        mplew.write(0);  // nMixHairBaseProb
        mplew.write(chr.getLevel());
        mplew.writeShort(chr.getJob());
        chr.getStat().connectData(mplew);
        mplew.writeShort(chr.getRemainingAp());
        if (GameConstants.isSeparatedSp(chr.getJob())) {
            int size = chr.getRemainingSpSize();
            mplew.write(size);
            for (int i = 0; i < chr.getRemainingSps().length; i++) {
                if (chr.getRemainingSp(i) > 0) {
                    mplew.write(i + 1);
                    mplew.writeInt(chr.getRemainingSp(i));
                }
            }
            System.out.println("Byte");
        } else {
            mplew.writeShort(chr.getRemainingSp());
            System.out.println("Short");
        }
        mplew.writeLong(chr.getExp());
        mplew.writeInt(chr.getFame());
        mplew.writeInt(chr.getGachExp());
        mplew.writeInt(0); // migration data nWP?
        mplew.writeInt(chr.getMapId());
        mplew.write(chr.getInitialSpawnpoint());
        mplew.writeInt(0);
        mplew.writeShort(chr.getSubcategory());
        if (GameConstants.isDemonSlayer(chr.getJob()) || GameConstants.isXenon(chr.getJob()) || GameConstants.isDemonAvenger(chr.getJob()) || GameConstants.isBeastTamer(chr.getJob())) {
            mplew.writeInt(chr.getFaceMarking());
        }
        mplew.write(chr.getFatigue());
        mplew.writeInt(GameConstants.getCurrentDate()); // nLastFatigueUpdateTime
        
        for (MapleTrait.MapleTraitType t : MapleTrait.MapleTraitType.values()) {
            mplew.writeInt(chr.getTrait(t).getTotalExp());
        }
        
        for (MapleTrait.MapleTraitType t : MapleTrait.MapleTraitType.values()) {
            mplew.writeShort(0); //today's stats
        }
        mplew.write(0);
        mplew.writeLong(getTime(System.currentTimeMillis()));
        mplew.writeInt(chr.getStat().pvpExp);
        mplew.write(chr.getStat().pvpRank);
        mplew.writeInt(chr.getBattlePoints());
        mplew.write(0); // nPvPModeLevel
        mplew.write(0); // nPvPModeType
        mplew.writeInt(0); // nEventPoint
        addPartTimeJob(mplew, MapleCharacter.getPartTime(chr.getId()));
        for (int i = 0; i < 9; i++) {
            mplew.writeInt(0);
            mplew.write(0);
            mplew.writeInt(0);
        }
        mplew.writeReversedLong(getTime(System.currentTimeMillis())); // stAccountLastLogout
        mplew.write(0); // bBurning
    }

    public static void addCharLook(MaplePacketWriter mplew, MapleCharacterLook chr, boolean mega, boolean second) {
    	mplew.write(second ? chr.getSecondGender() : chr.getGender());
        mplew.write(second ? chr.getSecondSkinColor() : chr.getSkinColor());
        mplew.writeInt(second ? chr.getSecondFace() : chr.getFace());
        mplew.writeInt(chr.getJob());
        mplew.write(mega ? 0 : 1);
        mplew.writeInt(second ? chr.getSecondHair() : chr.getHair());

        final Map<Byte, Integer> myEquip = new LinkedHashMap<>();
        final Map<Byte, Integer> maskedEquip = new LinkedHashMap<>();
        final Map<Byte, Integer> totemEquip = new LinkedHashMap<>();
        final Map<Byte, Integer> equip = second ? chr.getSecondEquips(true) : chr.getEquips(true);
        for (final Entry<Byte, Integer> item : equip.entrySet()) {
            if (item.getKey() < -127) {
            	continue;
            }
        byte pos = (byte) (item.getKey() * -1);

        if ((pos < 100) && (myEquip.get(pos) == null)) {
            myEquip.put(pos, item.getValue());
        } else if ((pos > 100) && (pos != 111)) {
            pos = (byte) (pos - 100);
            if (myEquip.get(pos) != null) {
            	maskedEquip.put(pos, myEquip.get(pos));
            	totemEquip.put(pos, item.getValue());
            }
            myEquip.put(pos, item.getValue());
            totemEquip.put(pos, item.getValue());
        } else if (myEquip.get(pos) != null) {
            maskedEquip.put(pos, item.getValue());
            totemEquip.put(pos, item.getValue());
            }
        }
        for (final Entry<Byte, Integer> totem : chr.getTotems().entrySet()) {
            byte pos = (byte) (totem.getKey() * -1);
                if (pos < 0 || pos > 2) { //3 totem slots
                continue;
            }
	        if (totem.getValue() < 1200000 || totem.getValue() >= 1210000) {
	            continue;
	        }
	        totemEquip.put(pos, totem.getValue());
        }

        for (Map.Entry entry : myEquip.entrySet()) {
            int weapon = ((Integer) entry.getValue()).intValue();
	        if (GameConstants.getWeaponType(weapon) == (second ? MapleWeaponType.LONG_SWORD : MapleWeaponType.BIG_SWORD)) {
	            continue;
	        }
	        mplew.write(((Byte) entry.getKey()).byteValue());
	        mplew.writeInt(((Integer) entry.getValue()).intValue());
        }
        mplew.write(255);

        for (Map.Entry entry : maskedEquip.entrySet()) {
            mplew.write(((Byte) entry.getKey()).byteValue());
            mplew.writeInt(((Integer) entry.getValue()).intValue());
        }
        mplew.write(255);

        for (Map.Entry entry : totemEquip.entrySet()) {
            mplew.write(((Byte) entry.getKey()).byteValue());
            mplew.writeInt(((Integer) entry.getValue()).intValue());
        }
        mplew.write(255); //new v140

        Integer cWeapon = equip.get(Byte.valueOf((byte) -111));
        mplew.writeInt(cWeapon != null ? cWeapon.intValue() : 0);
        Integer Weapon = equip.get(Byte.valueOf((byte) -11));
        mplew.writeInt(Weapon != null ? Weapon.intValue() : 0); //new v139
        boolean zero = GameConstants.isZero(chr.getJob());
        Integer Shield = equip.get(Byte.valueOf((byte) -10)); // (byte) -110));
        
        mplew.writeInt(!zero && Shield != null ? Shield.intValue() : 0); //new v139
        mplew.write(/*GameConstants.isMercedes(chr.getJob()) ? 1 : */0); // Mercedes/Elf Ears
        for(int i = 0; i < 3; i++) {
        	mplew.writeInt(0);   // Pet Item IDs, add in later
        }
        if (GameConstants.isDemonSlayer(chr.getJob()) || GameConstants.isXenon(chr.getJob()) || GameConstants.isDemonAvenger(chr.getJob()) || GameConstants.isBeastTamer(chr.getJob())) {
        	mplew.writeInt(chr.getFaceMarking());
        } else if (GameConstants.isZero(chr.getJob())) {
        	mplew.write(1);
        }
        if (GameConstants.isBeastTamer(chr.getJob())) {
            mplew.write(1);
            mplew.writeInt(chr.getEars());
            mplew.write(1);
            mplew.writeInt(chr.getTail());
        }
        mplew.write(0); // nMixedHairColor
        mplew.write(0); // nMixHairPercent
    }

    public static void addExpirationTime(MaplePacketWriter mplew, long time) {
        mplew.writeLong(getTime(time));
    }

    public static void addItemPosition(MaplePacketWriter mplew, Item item, boolean trade, boolean bagSlot) {
        if (item == null) {
            mplew.write(0);
            return;
        }
        short pos = item.getPosition();
        if (pos <= -1) {
            pos = (short) (pos * -1);
            if ((pos > 100) && (pos < 1000)) {
                pos = (short) (pos - 100);
            }
        }
        if (bagSlot) {
            mplew.writeInt(pos % 100 - 1);
        } else if ((!trade) && (item.getType() == 1)) {
            mplew.writeShort(pos);
        } else {
            mplew.write(pos);
        }
        System.out.println("Cash item positon " + pos);
    }

    public static void addItemInfo(MaplePacketWriter mplew, Item item) {
        addItemInfo(mplew, item, null);
    }

    public static void addItemInfo(final MaplePacketWriter mplew, final Item item, final MapleCharacter chr) {
        mplew.write(item.getPet() != null ? 3 : item.getType());
        mplew.writeInt(item.getItemId());
        boolean hasUniqueId = item.getUniqueId() > 0 && !GameConstants.isMarriageRing(item.getItemId()) && item.getItemId() / 10000 != 166;
        //marriage rings arent cash items so dont have uniqueids, but we assign them anyway for the sake of rings
        mplew.write(hasUniqueId ? 1 : 0);
        if (hasUniqueId) {
            mplew.writeLong(item.getUniqueId());
        }
        if (item.getPet() != null) { // Pet
            addPetItemInfo(mplew, item, item.getPet(), true);
        } else {
            addExpirationTime(mplew, item.getExpiration());
            mplew.writeInt(chr == null ? -1 : chr.getExtendedSlots().indexOf(item.getItemId()));
            if (item.getType() == 1) {
                final Equip equip = Equip.calculateEquipStats((Equip) item);
                //final Equip equip = Equip.calculateEquipStatsTest((Equip) item);
                addEquipStats(mplew, equip);
                //addEquipStatsTest(mplew, equip);
                addEquipBonusStats(mplew, equip, hasUniqueId);
            } else {
                mplew.writeShort(item.getQuantity());
                mplew.writeMapleAsciiString(item.getOwner());
                mplew.writeShort(item.getFlag());
                if (GameConstants.isThrowingStar(item.getItemId()) || GameConstants.isBullet(item.getItemId()) || item.getItemId() / 10000 == 287) {
                    mplew.writeLong(item.getInventoryId() <= 0 ? -1 : item.getInventoryId());
                }
            }
        }
    }

    public static void addEquipStatsTest(MaplePacketWriter mplew, Equip equip) {
        int mask;
        int masklength = 2;
        for (int i = 1; i <= masklength; i++) {
            mask = 0;
            if (equip.getStatsTest().size() > 0) {
                for (EquipStat stat : equip.getStatsTest().keySet()) {
                    if (stat.getPosition() == i) {
                        mask += stat.getValue();
                    }
                }
            }
            mplew.writeInt(mask);
            if (mask != 0) {
                for (EquipStat stat : equip.getStatsTest().keySet()) {
                    if (stat.getDatatype() == 8) {
                        mplew.writeLong(equip.getStatsTest().get(stat));
                    } else if (stat.getDatatype() == 4) {
                        mplew.writeInt(equip.getStatsTest().get(stat).intValue());
                    } else if (stat.getDatatype() == 2) {
                        mplew.writeShort(equip.getStatsTest().get(stat).shortValue());
                    } else if (stat.getDatatype() == 1) {
                        mplew.write(equip.getStatsTest().get(stat).byteValue());
                    }
                }
            }
        }
    }

    public static void addEquipStats(MaplePacketWriter mplew, Equip equip) {
        int head = 0;
        if (equip.getStats().size() > 0) {
            for (EquipStat stat : equip.getStats()) {
                head |= stat.getValue();
            }
        }
        mplew.writeInt(head);
        if (head != 0) {
            if (equip.getStats().contains(EquipStat.SLOTS)) {
                mplew.write(equip.getUpgradeSlots());
            }
            if (equip.getStats().contains(EquipStat.LEVEL)) {
                mplew.write(equip.getLevel());
            }
            if (equip.getStats().contains(EquipStat.STR)) {
                mplew.writeShort(equip.getStr());
            }
            if (equip.getStats().contains(EquipStat.DEX)) {
                mplew.writeShort(equip.getDex());
            }
            if (equip.getStats().contains(EquipStat.INT)) {
                mplew.writeShort(equip.getInt());
            }
            if (equip.getStats().contains(EquipStat.LUK)) {
                mplew.writeShort(equip.getLuk());
            }
            if (equip.getStats().contains(EquipStat.MHP)) {
                mplew.writeShort(equip.getHp());
            }
            if (equip.getStats().contains(EquipStat.MMP)) {
                mplew.writeShort(equip.getMp());
            }
            if (equip.getStats().contains(EquipStat.WATK)) {
                mplew.writeShort(equip.getWatk());
            }
            if (equip.getStats().contains(EquipStat.MATK)) {
                mplew.writeShort(equip.getMatk());
            }
            if (equip.getStats().contains(EquipStat.WDEF)) {
                mplew.writeShort(equip.getWdef());
            }
            if (equip.getStats().contains(EquipStat.MDEF)) {
                mplew.writeShort(equip.getMdef());
            }
            if (equip.getStats().contains(EquipStat.ACC)) {
                mplew.writeShort(equip.getAcc());
            }
            if (equip.getStats().contains(EquipStat.AVOID)) {
                mplew.writeShort(equip.getAvoid());
            }
            if (equip.getStats().contains(EquipStat.HANDS)) {
                mplew.writeShort(equip.getHands());
            }
            if (equip.getStats().contains(EquipStat.SPEED)) {
                mplew.writeShort(equip.getSpeed());
            }
            if (equip.getStats().contains(EquipStat.JUMP)) {
                mplew.writeShort(equip.getJump());
            }
            if (equip.getStats().contains(EquipStat.FLAG)) {
                mplew.writeShort(equip.getFlag());
            }
            if (equip.getStats().contains(EquipStat.INC_SKILL)) {
                mplew.write(equip.getIncSkill() > 0 ? 1 : 0);
            }
            if (equip.getStats().contains(EquipStat.ITEM_LEVEL)) {
                mplew.write(Math.max(equip.getBaseLevel(), equip.getEquipLevel())); // Item level
            }
            if (equip.getStats().contains(EquipStat.ITEM_EXP)) {
                mplew.writeLong(equip.getExpPercentage() * 100000); // Item Exp... 10000000 = 100%
            }
            if (equip.getStats().contains(EquipStat.DURABILITY)) {
                mplew.writeInt(equip.getDurability());
            }
            if (equip.getStats().contains(EquipStat.VICIOUS_HAMMER)) {
                mplew.writeInt(equip.getViciousHammer());
            }
            if (equip.getStats().contains(EquipStat.PVP_DAMAGE)) {
                mplew.writeShort(equip.getPVPDamage());
            }
            if (equip.getStats().contains(EquipStat.ENHANCT_BUFF)) {
                mplew.writeShort(equip.getEnhanctBuff());
            }
            if (equip.getStats().contains(EquipStat.DURABILITY_SPECIAL)) {
                mplew.writeInt(equip.getDurability());
            }
            if (equip.getStats().contains(EquipStat.REQUIRED_LEVEL)) {
                mplew.write(equip.getReqLevel());
            }
            if (equip.getStats().contains(EquipStat.YGGDRASIL_WISDOM)) {
                mplew.write(equip.getYggdrasilWisdom());
            }
            if (equip.getStats().contains(EquipStat.FINAL_STRIKE)) {
                mplew.write(equip.getFinalStrike());
            }
            if (equip.getStats().contains(EquipStat.BOSS_DAMAGE)) {
                mplew.write(equip.getBossDamage());
            }
            if (equip.getStats().contains(EquipStat.IGNORE_PDR)) {
                mplew.write(equip.getIgnorePDR());
            }
        } else {
            /*
             *   if ( v3 >= 0 )
             *     v36 = 0;
             *   else
             *     v36 = (unsigned __int8)CInPacket::Decode1(a2);
             */
//            mplew.write(0); //unknown
        }
        addEquipSpecialStats(mplew, equip);
    }

    public static void addEquipSpecialStats(MaplePacketWriter mplew, Equip equip) {
        int head = 0;
        if (equip.getSpecialStats().size() > 0) {
            for (EquipSpecialStat stat : equip.getSpecialStats()) {
                head |= stat.getValue();
            }
        }
        mplew.writeInt(head);
//        System.out.println("mask " + head);

        if (head != 0) {
            if (equip.getSpecialStats().contains(EquipSpecialStat.TOTAL_DAMAGE)) {
//                System.out.println("TOTAL_DAMAGE " + equip.getTotalDamage());
                mplew.write(equip.getTotalDamage());
            }
            if (equip.getSpecialStats().contains(EquipSpecialStat.ALL_STAT)) {
//                System.out.println("ALL_STAT " + equip.getAllStat());
                mplew.write(equip.getAllStat());
            }
            if (equip.getSpecialStats().contains(EquipSpecialStat.KARMA_COUNT)) {
//                System.out.println("KARMA_COUNT " + equip.getKarmaCount());
                mplew.write(equip.getKarmaCount());
            }
            if (equip.getSpecialStats().contains(EquipSpecialStat.UNK8)) {
//                System.out.println("unk8 " + System.currentTimeMillis());
                mplew.writeLong(System.currentTimeMillis());
            }
            if (equip.getSpecialStats().contains(EquipSpecialStat.UNK10)) {
//                System.out.println("unk10 " + 1);
                mplew.writeInt(0);
            }
        }
    }

//    public static void addEquipBonusStats(MaplePacketLittleEndianWriter mplew, Equip equip, boolean hasUniqueId) {
//        mplew.writeMapleAsciiString(equip.getOwner());
//        mplew.write(equip.getState()); // 17 = rare, 18 = epic, 19 = unique, 20 = legendary, potential flags. special grade is 14 but it crashes
//        mplew.write(equip.getEnhance());
//        mplew.writeShort(equip.getPotential1());
//        mplew.writeShort(equip.getPotential2());
//        mplew.writeShort(equip.getPotential3());
//        mplew.writeShort(equip.getBonusPotential1());
//        mplew.writeShort(equip.getBonusPotential2());
//        mplew.writeShort(equip.getBonusPotential3());
//        mplew.writeShort(equip.getFusionAnvil() % 100000);
//        mplew.writeShort(equip.getSocketState());
//        mplew.writeShort(equip.getSocket1() % 10000); // > 0 = mounted, 0 = empty, -1 = none.
//        mplew.writeShort(equip.getSocket2() % 10000);
//        mplew.writeShort(equip.getSocket3() % 10000);
//        if (!hasUniqueId) {
//            mplew.writeLong(equip.getInventoryId() <= 0 ? -1 : equip.getInventoryId()); //some tracking ID
//        }
//        mplew.writeLong(getTime(-2));
//        mplew.writeInt(-1); //?
//        
//    }
    public static void addEquipBonusStats(MaplePacketWriter mplew, Equip equip, boolean hasUniqueId) {
        mplew.writeMapleAsciiString(equip.getOwner());
        mplew.write(equip.getState()); // 17 = rare, 18 = epic, 19 = unique, 20 = legendary, potential flags. special grade is 14 but it crashes
        mplew.write(equip.getEnhance());
        mplew.writeShort(equip.getPotential1());
        mplew.writeShort(equip.getPotential2());
        mplew.writeShort(equip.getPotential3());
        mplew.writeShort(equip.getBonusPotential1());
        mplew.writeShort(equip.getBonusPotential2());
        mplew.writeShort(equip.getBonusPotential3());
        mplew.writeShort(equip.getFusionAnvil() % 100000);
        mplew.writeShort(equip.getSocketState());
        mplew.writeShort(equip.getSocket1() % 10000); // > 0 = mounted, 0 = empty, -1 = none.
        mplew.writeShort(equip.getSocket2() % 10000);
        mplew.writeShort(equip.getSocket3() % 10000);
        if (!hasUniqueId) {
            mplew.writeLong(equip.getInventoryId() <= 0 ? -1 : equip.getInventoryId()); //some tracking ID
        }
        mplew.writeLong(getTime(-2));
        mplew.writeInt(-1); //?
        // new 142
        mplew.writeLong(0);
        mplew.writeLong(getTime(-2));
        mplew.writeLong(0);
        mplew.writeLong(0);
        mplew.writeZeroBytes(6);
    }

    public static void serializeMovementList(MaplePacketWriter mlew, List<LifeMovementFragment> moves) {
        mlew.write(moves.size());
        for (LifeMovementFragment move : moves) {
            move.serialize(mlew);
        }
    }

    public static void addAnnounceBox(MaplePacketWriter mplew, MapleCharacter chr) {
        if ((chr.getPlayerShop() != null) && (chr.getPlayerShop().isOwner(chr)) && (chr.getPlayerShop().getShopType() != 1) && (chr.getPlayerShop().isAvailable())) {
            addInteraction(mplew, chr.getPlayerShop());
        } else {
            mplew.write(0);
        }
    }

    public static void addInteraction(MaplePacketWriter mplew, IMaplePlayerShop shop) {
        mplew.write(shop.getGameType());
        mplew.writeInt(((AbstractPlayerStore) shop).getObjectId());
        mplew.writeMapleAsciiString(shop.getDescription());
        if (shop.getShopType() != 1) {
            mplew.write(shop.getPassword().length() > 0 ? 1 : 0);
        }
        mplew.write(shop.getItemId() % 10);
        mplew.write(shop.getSize());
        mplew.write(shop.getMaxSize());
        if (shop.getShopType() != 1) {
            mplew.write(shop.isOpen() ? 0 : 1);
        }
    }

    public static void addCharacterInfo(MaplePacketWriter mplew, MapleCharacter chr) {
    	long flag = -1;
    	
    	mplew.writeLong(flag);
        mplew.write(0); // nCombatOrders
        
        for(int i = 0; i < 3; i++) { // Seeds
        	mplew.writeInt(-4); // FC FF FF FF //aPetActiveSkillCoolTime
        }
        mplew.write(0); // nPvPExp_CS
        mplew.write(0); // nKey
        mplew.writeInt(0); // unk bool
        mplew.write(0);
        
        if ((flag & 1) != 0) {
            addCharStats(mplew, chr);
            mplew.write(chr.getBuddylist().getCapacity());
            mplew.write(chr.getBlessOfFairyOrigin() != null);
            if (chr.getBlessOfFairyOrigin() != null) {
                mplew.writeMapleAsciiString(chr.getBlessOfFairyOrigin());
            }
            mplew.write(chr.getBlessOfEmpressOrigin() != null);
            if (chr.getBlessOfEmpressOrigin() != null) {
                mplew.writeMapleAsciiString(chr.getBlessOfEmpressOrigin());
            }
            MapleQuestStatus ultExplorer = chr.getQuestNoAdd(MapleQuest.getInstance(GameConstants.ULT_EXPLORER));
            mplew.write((ultExplorer != null) && (ultExplorer.getCustomData() != null));
            if ((ultExplorer != null) && (ultExplorer.getCustomData() != null)) {
                mplew.writeMapleAsciiString(ultExplorer.getCustomData());
            }
        }
        
        if ((flag & 2) != 0) {
            addMoneyInfo(mplew, chr);
        }
        if ((flag & 8) != 0) {
            addInventoryInfo(mplew, chr);
        }
        if ((flag & 0x100) != 0) {
            addSkillInfo(mplew, chr);
        }
        if ((flag & 0x8000) != 0) {
            addCoolDownInfo(mplew, chr);
        }
        if ((flag & 0x200) != 0) {
            addStartedQuestInfo(mplew, chr);
        }
        if ((flag & 0x4000) != 0) {
            addCompletedQuestInfo(mplew, chr);
        }
        if ((flag & 0x400) != 0) {
            addMiniGameRecordsInfo(mplew, chr);
        }
        if ((flag & 0x800) != 0) {
            addRingInfo(mplew, chr);
        }
        if ((flag & 0x1000) != 0) {
            addRocksInfo(mplew, chr);
        }
        if ((flag & 0x20000) != 0) {
            mplew.writeInt(0); //nSlotHyper
        }
        if ((flag & 0x10000) != 0) {
            addMonsterBookInfo(mplew, chr);
        }

        if ((flag & 0x80000) != 0) {
	        mplew.writeShort(0);
	        int count = 0;
	        mplew.writeInt(count);
	        for(int i = 0; i < count; i++) {
	        	mplew.writeInt(chr.getId());
	        	mplew.writeInt(0);
	        	mplew.writeMapleAsciiString("");
	        	mplew.write(1);
	        	mplew.writeLong(0);
	        	mplew.writeInt(0);
	        	mplew.writeMapleAsciiString("");
	        	mplew.write(0);
	        	mplew.write(0);
	        	mplew.writeLong(0);
	        	mplew.writeMapleAsciiString("");
	        }
        }
        
        mplew.writeShort(0);
        
        if ((flag & 0x40000) != 0) {
            chr.QuestInfoPacket(mplew);
        }
        
        if ((flag & 0x2000) != 0) { // Androids
        	int count = 0;
        	mplew.writeShort(count);
	        for(int i = 0; i < count; i++) {
	        	mplew.writeInt(0);
	        	addCharLook(mplew, chr, false, false);
	        }
        }
        
        if ((flag & 0x1000) != 0) { // nWillEXPCS
        	int count = 0;
        	mplew.writeInt(count);
        	for(int i = 0; i < count; i++) {
        		mplew.writeInt(0);
        		mplew.writeInt(0);
        	}
        }
        
        if ((flag & 0x200000) != 0) {
            if ((chr.getJob() >= 3300) && (chr.getJob() <= 3312)) {
                addJaguarInfo(mplew, chr);
            }
        }
        
        if (GameConstants.isZero(chr.getJob())) {
            addZeroInfo(mplew, chr);
        }
        
        if ((flag & 0x4000000) != 0) {
        	mplew.writeShort(0); // NPCShopBuyLimit
        	// TODO
        }
        
        if ((flag & 0x20000000) != 0) {
            //addStealSkills(mplew, chr);
        	mplew.writeInt(0);
            mplew.writeInt(0);
            mplew.writeInt(0);
            mplew.writeInt(0);
        }
        
        if ((flag & 0x10000000) != 0) {
        	mplew.writeInt(0);
            mplew.writeInt(0);
            mplew.writeInt(0);
            mplew.writeInt(0);
        }
        
        if ((flag & 0x80000000) != 0) {
            addAbilityInfo(mplew, chr);
        }
        
        if ((flag & 0x10000) != 0) { // GW_SoulCollection
        	int count = 0;
        	mplew.writeShort(count);
        	for(int i = 0; i < count; i++) {
        		mplew.writeInt(0);
        		mplew.writeInt(0);
        	}
        }
        
        int mlcount = 0;
        mplew.writeInt(mlcount); // Monsterlife
        for(int i = 0; i < mlcount; i++) {
        	mplew.writeMapleAsciiString("");
        	mplew.writeInt(0);
        	mplew.writeMapleAsciiString("");
        	int count = 0;
        	mplew.writeInt(count);
        	for(int j = 0; j < count; j++) {
        		mplew.write(0);
        	}
        	
        }
        
        //addHonorInfo(mplew, chr);
        mplew.writeLong(0); // 1
        mplew.writeZeroBytes(3); // 2
        
        mplew.write(0); // Return effect info

        if (GameConstants.isAngelicBuster(chr.getJob())) {
	        mplew.writeInt(1);
	        mplew.writeInt(21173); //face
	        mplew.writeInt(37141); //hair
	        mplew.writeInt(1051291); // dressup suit cant unequip
	        mplew.writeInt(0);
	        mplew.writeInt(0);
	        mplew.write(0);
        } else {
	        mplew.writeZeroBytes(25);
        }
        
        mplew.writeInt(1);
        mplew.writeInt(47);
        mplew.writeLong(1);
        mplew.writeShort(0);
        mplew.writeInt(0);
        
        mplew.write(0);
        mplew.writeShort(0);
        
        
        mplew.writeInt(-1);
        mplew.writeLong(0);
        mplew.writeLong(1);
        mplew.writeLong(getTime(-2));
        mplew.writeZeroBytes(14);
        
        //addEvolutionInfo(mplew, chr);
        //mplew.writeZeroBytes(3);//new 144
        //mplew.write(0); //farm monsters length


        
        if((flag & 0x40) != 0) {
        	addFarmInfo(mplew, chr.getClient(), 0);
        	mplew.writeInt(-1); //v146 can be 5 tho...
            mplew.writeInt(0);
        }

        mplew.write(0); // Memorial Cube Info
       
        mplew.writeInt(0); // nEXP64
        mplew.writeLong(getTime(-2));
        mplew.writeInt(0);
        
        // Runner Game Record
        mplew.writeInt(chr.getId());
        mplew.writeInt(0); // Last Score
        mplew.writeInt(0); // High Score
        mplew.writeInt(0); // Runner Point
        mplew.writeLong(getTime(-2)); // Last Played
        mplew.writeInt(10); // Total Left
        mplew.writeShort(1); // Count
        mplew.writeInt(9); // Slot
        mplew.writeMapleAsciiString("check1=0;cDate=16/04/20");
        
        mplew.write(0);
        
        mplew.writeInt(0); // Decode Text Equip Info
        
        mplew.write(HexTool.getByteArrayFromHexString("01 00 01 00 00 00 00 00 00 00 64 00 00 00 80 73 10 EF 29 9C D1 01 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 80 29 60 76 F1 9C D1 01 00 01 00 00"));
        
        /*
        if((flag & 0x4000000) != 0) {
        	mplew.writeShort(1);
            mplew.writeInt(1);
            mplew.writeInt(0);
            mplew.writeInt(100);
            mplew.writeLong(getTime(-2));
            mplew.writeShort(0);
            mplew.writeShort(0);
        }
        mplew.write(0);
        */
        /*
        //mplew.writeZeroBytes(9);//v146
        if ((flag & 0x2000) != 0) {
            addCoreAura(mplew, chr);
        }
        */
        
        mplew.writeInt(chr.getClient().getAccID());
        mplew.writeInt(chr.getId());
        mplew.writeInt(4);
        mplew.writeInt(0);
        addRedLeafInfo(mplew, chr);
        
    }

    public static int getSkillBook(final int i) {
        switch (i) {
            case 1:
            case 2:
                return 4;
            case 3:
                return 3;
            case 4:
                return 2;
        }
        return 0;
    }

    public static void addAbilityInfo(final MaplePacketWriter mplew, MapleCharacter chr) {
        final List<InnerSkillValueHolder> skills = chr.getInnerSkills();
        mplew.writeShort(skills.size());
        for (int i = 0; i < skills.size(); ++i) {
            mplew.write(i + 1); // key
            mplew.writeInt(skills.get(i).getSkillId()); //d 7000000 id ++, 71 = char cards
            mplew.write(skills.get(i).getSkillLevel()); // level
            mplew.write(skills.get(i).getRank()); //rank, C, B, A, and S
        }

    }

    public static void addHonorInfo(final MaplePacketWriter mplew, MapleCharacter chr) {
        mplew.writeInt(chr.getHonorLevel()); //honor lvl
        mplew.writeInt(chr.getHonourExp()); //honor exp
    }

    public static void addEvolutionInfo(final MaplePacketWriter mplew, MapleCharacter chr) {
        mplew.writeShort(0);
        mplew.writeShort(0);
    }

    public static void addCoreAura(MaplePacketWriter mplew, MapleCharacter chr) {
        MapleCoreAura aura = chr.getCoreAura();
        mplew.writeInt(aura.getId()); //nvr change
        mplew.writeInt(chr.getId());
        int level = chr.getSkillLevel(80001151) > 0 ? chr.getSkillLevel(80001151) : chr.getSkillLevel(1214);
        mplew.writeInt(level);
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.writeInt(aura.getExpire());//timer
        mplew.writeInt(0);
        mplew.writeInt(aura.getAtt());//wep att
        mplew.writeInt(aura.getDex());//dex
        mplew.writeInt(aura.getLuk());//luk
        mplew.writeInt(aura.getMagic());//magic att
        mplew.writeInt(aura.getInt());//int
        mplew.writeInt(aura.getStr());//str
        mplew.writeInt(0);
        mplew.writeInt(aura.getTotal());//max
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.writeLong(getTime(System.currentTimeMillis() + 86400000L));
        mplew.write(0);
        mplew.write(GameConstants.isJett(chr.getJob()) ? 1 : 0);
    }

    public static void addStolenSkills(MaplePacketWriter mplew, MapleCharacter chr, int jobNum, boolean writeJob) {
        if (writeJob) {
            mplew.writeInt(jobNum);
        }
        int count = 0;
        if (chr.getStolenSkills() != null) {
            for (Pair<Integer, Boolean> sk : chr.getStolenSkills()) {
                if (GameConstants.getJobNumber(sk.left / 10000) == jobNum) {
                    mplew.writeInt(sk.left);
                    count++;
                    if (count >= GameConstants.getNumSteal(jobNum)) {
                        break;
                    }
                }
            }
        }
        while (count < GameConstants.getNumSteal(jobNum)) { //for now?
            mplew.writeInt(0);
            count++;
        }
    }

    public static void addChosenSkills(MaplePacketWriter mplew, MapleCharacter chr) {
        for (int i = 1; i <= 4; i++) {
            boolean found = false;
            if (chr.getStolenSkills() != null) {
                for (Pair<Integer, Boolean> sk : chr.getStolenSkills()) {
                    if (GameConstants.getJobNumber(sk.left / 10000) == i && sk.right) {
                        mplew.writeInt(sk.left);
                        found = true;
                        break;
                    }
                }
            }
            if (!found) {
                mplew.writeInt(0);
            }
        }
    }

    public static void addStealSkills(final MaplePacketWriter mplew, final MapleCharacter chr) {
        for (int i = 1; i <= 4; i++) {
            addStolenSkills(mplew, chr, i, false); // 52
        }
        addChosenSkills(mplew, chr); // 16
    }

    public static void addMonsterBookInfo(MaplePacketWriter mplew, MapleCharacter chr) {
        if (chr.getMonsterBook().getSetScore() > 0) {
            chr.getMonsterBook().writeFinished(mplew);
        } else {
            chr.getMonsterBook().writeUnfinished(mplew);
        }

        mplew.writeInt(chr.getMonsterBook().getSet());
    }

    public static void addPetItemInfo(MaplePacketWriter mplew, Item item, MaplePet pet, boolean active) {
        if (item == null) {
            mplew.writeLong(PacketHelper.getKoreanTimestamp((long) (System.currentTimeMillis() * 1.5)));
        } else {
            addExpirationTime(mplew, item.getExpiration() <= System.currentTimeMillis() ? -1L : item.getExpiration());
        }
        mplew.writeInt(-1);
        mplew.writeAsciiString(pet.getName(), 13);
        mplew.write(pet.getLevel());
        mplew.writeShort(pet.getCloseness());
        mplew.write(pet.getFullness());
        if (item == null) {
            mplew.writeLong(PacketHelper.getKoreanTimestamp((long) (System.currentTimeMillis() * 1.5)));
        } else {
            addExpirationTime(mplew, item.getExpiration() <= System.currentTimeMillis() ? -1L : item.getExpiration());
        }
        mplew.writeShort(0);
        mplew.writeShort(pet.getFlags());
        mplew.writeInt((pet.getPetItemId() == 5000054) && (pet.getSecondsLeft() > 0) ? pet.getSecondsLeft() : 0);
        mplew.writeShort(0);
        mplew.write(active ? 0 : pet.getSummoned() ? pet.getSummonedValue() : 0);
        for (int i = 0; i < 4; i++) {
            mplew.write(0);
        }
        mplew.writeInt(-1); //new v140
        mplew.writeShort(100); //new v140
    }

    public static void addShopInfo(MaplePacketWriter mplew, MapleShop shop, MapleClient c) {
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        mplew.write(shop.getRanks().size() > 0 ? 1 : 0);

        mplew.writeInt(0);
        
        if (shop.getRanks().size() > 0) {
            mplew.write(shop.getRanks().size());
            for (Pair s : shop.getRanks()) {
                mplew.writeInt(((Integer) s.left).intValue());
                mplew.writeMapleAsciiString((String) s.right);
            }
        }
        mplew.writeShort(shop.getItems().size() + c.getCharacter().getRebuy().size());
        for (MapleShopItem item : shop.getItems()) {
            addShopItemInfo(mplew, item, shop, ii, null, c.getCharacter());
        }
        for (Item i : c.getCharacter().getRebuy()) {
            addShopItemInfo(mplew, new MapleShopItem(i.getItemId(), (int) ii.getPrice(i.getItemId()), i.getQuantity(), i.getPosition()), shop, ii, i, c.getCharacter());
        }
   
        /*
        if (shop.getRanks().size() > 0) {
            mplew.write(shop.getRanks().size());
            for (Pair s : shop.getRanks()) {
                mplew.writeInt(((Integer) s.left).intValue());
                mplew.writeMapleAsciiString((String) s.right);
            }
        }
        mplew.writeShort(shop.getItems().size() + c.getPlayer().getRebuy().size());
        for (MapleShopItem item : shop.getItems()) {
            addShopItemInfo(mplew, item, shop, ii, null, c.getPlayer());
        }
        for (Item i : c.getPlayer().getRebuy()) {
            addShopItemInfo(mplew, new MapleShopItem(i.getItemId(), (int) ii.getPrice(i.getItemId()), i.getQuantity(), i.getPosition()), shop, ii, i, c.getPlayer());
        }
        */
    }

    /*
     * Categories:
     * 0 - No Tab
     * 1 - Equip
     * 2 - Use
     * 3 - Setup
     * 4 - Etc
     * 5 - Recipe
     * 6 - Scroll
     * 7 - Special
     * 8 - 8th Anniversary
     * 9 - Button
     * 10 - Invitation Ticket
     * 11 - Materials
     * 12 - Maple
     * 13 - Homecoming
     * 14 - Cores
     * 80 - JoeJoe
     * 81 - Hermoninny
     * 82 - Little Dragon
     * 83 - Ika
     */
    public static void addShopItemInfo(MaplePacketWriter mplew, MapleShopItem item, MapleShop shop, MapleItemInformationProvider ii, Item i, MapleCharacter chr) {
        mplew.writeInt(item.getItemId());
        mplew.writeInt(item.getPrice());
        mplew.write(ServerConstants.SHOP_DISCOUNT); //Discount
        mplew.writeInt(item.getReqItem());
        mplew.writeInt(item.getReqItemQ());
        mplew.writeInt(1440 * item.getExpiration());
        mplew.writeInt(item.getMinLevel());
        mplew.writeInt(0);
        mplew.writeZeroBytes(26);
        mplew.writeLong(getTime(-2L)); //new v140 1900
        mplew.writeLong(getTime(-1L)); //new v140 2079
        
        mplew.writeInt(item.getCategory());
        if (GameConstants.isEquip(item.getItemId())) {
            mplew.write(item.hasPotential() ? 1 : 0);
        } else {
            mplew.write(0);
        }
        mplew.writeInt(item.getExpiration() > 0 ? 1 : 0);
        mplew.write(0);//new 144
        mplew.writeInt(0);
        if ((!GameConstants.isThrowingStar(item.getItemId())) && (!GameConstants.isBullet(item.getItemId()))) {
        	mplew.writeShort(item.getBuyable()); //buyable
        	mplew.writeShort(item.getQuantity()); //quantity of item to buy
        } else {
            mplew.writeAsciiString("333333");
            mplew.writeShort(BitTools.doubleToShortBits(ii.getPrice(item.getItemId())));
			//mplew.writeShort(ItemInformation.getInstance().getSlotMax(c, item.getItemId()));
            mplew.writeShort(ii.getSlotMax(item.getItemId()));
        
        /*
             mplew.writeInt(0);
             mplew.writeShort(0);
             mplew.writeShort(BitTools.doubleToShortBits(ii.getPrice(item.getItemId())));
             */
//            mplew.writeZeroBytes(8);
//            mplew.writeShort(ii.getSlotMax(item.getItemId()));
        }
    
        
        mplew.write(i == null ? 0 : 1);
        if (i != null) {
            addItemInfo(mplew, i);
        }
        if (shop.getRanks().size() > 0) {
            mplew.write(item.getRank() >= 0 ? 1 : 0);
            if (item.getRank() >= 0) {
                mplew.write(item.getRank());
            }
        }
        for (int j = 0; j < 4; j++) {
            mplew.writeInt(0); //red leaf high price probably
        }
        
        //mplew.write(HexTool.getByteArrayFromHexString("00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00"));
        addRedLeafInfo(mplew, chr);
    }

    public static void addJaguarInfo(MaplePacketWriter mplew, MapleCharacter chr) {
        mplew.write(chr.getIntNoRecord(GameConstants.JAGUAR));
        for (int i = 0; i < 5; i++) {
            mplew.writeInt(0);
        }
    }

    public static void addZeroInfo(MaplePacketWriter mplew, MapleCharacter chr) {
        short mask = 0;
        mplew.writeShort(mask);
        if ((mask & 1) != 0) {
            mplew.write(0); //bool
        }
        if ((mask & 2) != 0) {
            mplew.writeInt(0);
        }
        if ((mask & 4) != 0) {
            mplew.writeInt(0);
        }
        if ((mask & 8) != 0) {
            mplew.write(0);
        }
        if ((mask & 10) != 0) {
            mplew.writeInt(0);
        }
        if ((mask & 20) != 0) {
            mplew.writeInt(0);
        }
        if ((mask & 40) != 0) {
            mplew.writeInt(0);
        }
        if (mask < 0) {
            mplew.writeInt(0);
        }
        if ((mask & 100) != 0) {
            mplew.writeInt(0);
        }
    }

    public static void addFarmInfo(MaplePacketWriter mplew, MapleClient c, int idk) {
        mplew.writeMapleAsciiString(c.getFarm().getName());
        mplew.writeInt(c.getFarm().getWaru());
        mplew.writeInt(c.getFarm().getLevel());
        mplew.writeInt(c.getFarm().getExp());
        mplew.writeInt(c.getFarm().getAestheticPoints());
        mplew.writeInt(0); //gems

        mplew.write((byte) idk);
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.writeInt(1);
    }

public static void addRedLeafInfo(MaplePacketWriter mplew, MapleCharacter chr) {      
            for (int i = 0; i < 4; i++) {
            mplew.writeInt(9410165 + i);//v146 -2
            mplew.writeInt(chr.getFriendShipPoints()[i]);
        }

    }

    public static void addLuckyLogoutInfo(MaplePacketWriter mplew, boolean enable, CashItem item0, CashItem item1, CashItem item2) {
        mplew.writeInt(enable ? 1 : 0);
        if (enable) {
            CSPacket.addCSItemInfo(mplew, item0);
            CSPacket.addCSItemInfo(mplew, item1);
            CSPacket.addCSItemInfo(mplew, item2);
        }
    }

    public static void addPartTimeJob(MaplePacketWriter mplew, PartTimeJob parttime) {
        mplew.write(parttime.getJob());
        if (parttime.getJob() > 0 && parttime.getJob() <= 5) {
            mplew.writeReversedLong(parttime.getTime());
        } else {
            mplew.writeReversedLong(System.currentTimeMillis());
        }
        mplew.writeInt(parttime.getReward());
        mplew.write(parttime.getReward() > 0);
    }

    public static <E extends Buffstat> void writeMobMask(MaplePacketWriter mplew, E statup) {
        for (int i = 1; i <= 3; i++) {
            mplew.writeInt(i == statup.getPosition() ? statup.getValue() : 0);
        }
    }
    
    /**
     * Writes a long exp mask to {@code mplew} from {@code expStats}.
     * @param mplew
     * @param expStats
     */
    public static void writeExpMask(MaplePacketWriter mplew, Map<MapleExpStatus, Integer> expStats) {
    	long mask = 0;
    	
    	for (MapleExpStatus expStat : expStats.keySet()) {
    		mask += expStat.getFlag();
    	}
    	
    	mplew.writeLong(mask);
    }
    
    public static <E extends Buffstat> void writeSingleMask(MaplePacketWriter mplew, E statup) {
        for (int i = GameConstants.MAX_BUFFSTAT; i >= 1; i--) {
            mplew.writeInt(i == statup.getPosition() ? statup.getValue() : 0);
        }
    }
    
    /*
    public static <E extends Buffstat> void writeMobStatFlag(MaplePacketLittleEndianWriter mplew, MonsterStatus mse) {
    	int[] flag = new int[GameConstants.MAX_MOBSTAT];
    	
    	for (Buffstat statup : mse) {
            flag[(statup.getPosition() - 1)] += statup.getValue();
        }
        for(int i = 0; i < flag.length; i++) {
        	mplew.writeInt(flag[i]);
        }
    }
    */

   public static <E extends Buffstat> void writeMask(MaplePacketWriter mplew, Collection<E> statups) {
        int[] mask = new int[GameConstants.MAX_BUFFSTAT];
        /*
        if (!statups.contains(MapleBuffStat.MONSTER_RIDING)) {
            mask = new int[12];
        }
        */
        for (Buffstat statup : statups) {
            mask[(statup.getPosition() - 1)] += statup.getValue();
            System.out.println(statup.getValue());
        }
        for(int i = 0; i < mask.length; i++) {
        	mplew.writeInt(mask[i]);
        }
    }

   /* Original v146
    public static <E extends Buffstat> void writeBuffMask(MaplePacketLittleEndianWriter mplew, Collection<Pair<E, Integer>> statups) {
        int[] mask = new int[10];
        if (!statups.contains(MapleBuffStat.MONSTER_RIDING)) {
            mask = new int[12];
        }
        for (Pair statup : statups) {
            mask[(((Buffstat) statup.left).getPosition() - 1)] |= ((Buffstat) statup.left).getValue();
        }
        for (int i = mask.length; i >= 1; i--) {
            mplew.writeInt(mask[(i - 1)]);
        }
    }
    */

   public static <E extends Buffstat> void writeBuffMask(MaplePacketWriter mplew, Collection<Pair<E, Integer>> statups) {
	   int[] mask = new int[GameConstants.MAX_BUFFSTAT];
	   /*
       if (!statups.contains(MapleBuffStat.MONSTER_RIDING)) {
           mask = new int[12];
       }
       */
       for (Pair statup : statups) {
           mask[(((Buffstat) statup.left).getPosition() - 1)] |= ((Buffstat) statup.left).getValue();
       }
       for(int i = 0; i < mask.length; i++) {
    	   mplew.writeInt(mask[i]);
       }
   }
   
    /* Original v146
    public static <E extends Buffstat> void writeBuffMask(MaplePacketLittleEndianWriter mplew, Map<E, Integer> statups) {
        int[] mask = new int[10];
        if (!statups.containsKey(MapleBuffStat.MONSTER_RIDING)) {
            mask = new int[12];
        }
        for (Buffstat statup : statups.keySet()) {
            mask[(statup.getPosition() - 1)] |= statup.getValue();
        }
        for (int i = mask.length; i >= 1; i--) {
            mplew.writeInt(mask[(i - 1)]);
        }
    }
    */
    
    public static <E extends Buffstat> void writeBuffMask(MaplePacketWriter mplew, Map<E, Integer> statups) {
        int[] mask = new int[GameConstants.MAX_BUFFSTAT];
        /*
        if (!statups.containsKey(MapleBuffStat.MONSTER_RIDING)) {
            flag = new int[12];
        }
        */
        for (Buffstat statup : statups.keySet()) {
            mask[(statup.getPosition() - 1)] += statup.getValue();
        }
        for(int i = 0; i < mask.length; i++) {
        	mplew.writeInt(mask[i]);
        }
    }
}