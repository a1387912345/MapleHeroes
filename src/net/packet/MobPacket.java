package net.packet;

import client.MapleCharacter;
import client.MonsterStatus;
import client.MonsterStatusEffect;
import net.SendPacketOpcode;
import net.netty.MaplePacketWriter;

import java.awt.Point;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import server.Randomizer;
import server.life.MapleMonster;
import server.life.MobSkill;
import server.maps.MapleMap;
import server.maps.MapleNodes;
import server.movement.LifeMovementFragment;
import tools.HexTool;
import tools.Pair;

public class MobPacket {

    public static byte[] damageMonster(int oid, long damage) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.DAMAGE_MONSTER);
		mplew.writeInt(oid);
        mplew.write(0);
        mplew.writeLong(damage);

        return mplew.getPacket();
    }

    public static byte[] damageFriendlyMob(MapleMonster mob, long damage, boolean display) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.DAMAGE_MONSTER);
		mplew.writeInt(mob.getObjectId());
        mplew.write(display ? 1 : 2);
        if (damage > 2147483647L) {
            mplew.writeInt(2147483647);
        } else {
            mplew.writeInt((int) damage);
        }
        if (mob.getHp() > 2147483647L) {
            mplew.writeInt((int) (mob.getHp() / mob.getMobMaxHp() * 2147483647.0D));
        } else {
            mplew.writeInt((int) mob.getHp());
        }
        if (mob.getMobMaxHp() > 2147483647L) {
            mplew.writeInt(2147483647);
        } else {
            mplew.writeInt((int) mob.getMobMaxHp());
        }

        return mplew.getPacket();
    }

    public static byte[] killMonster(int oid, int animation, boolean azwan) {
        MaplePacketWriter mplew;

        if (azwan) {
        	mplew = new MaplePacketWriter(SendPacketOpcode.AZWAN_KILL_MONSTER);
        } else {
        	mplew = new MaplePacketWriter(SendPacketOpcode.KILL_MONSTER);
        }
        boolean a = false; //idk
        boolean b = false; //idk
        if (azwan) {
            mplew.write(a ? 1 : 0);
            mplew.write(b ? 1 : 0);
        }
        mplew.writeInt(oid);
        if (azwan) {
            if (a) {
                mplew.write(0);
                if (b) {
                    //set mob temporary stat
                } else {
                    //set mob temporary stat
                }
            } else {
                if (b) {
                    //idk
                } else {
                    //idk
                }
            }
            return mplew.getPacket();
        }
        mplew.write(animation);
        if (animation == 4) {
            mplew.writeInt(-1);
        }

        return mplew.getPacket();
    }

    public static byte[] suckMonster(int oid, int chr) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.KILL_MONSTER);
		mplew.writeInt(oid);
        mplew.write(4);
        mplew.writeInt(chr);

        return mplew.getPacket();
    }

    public static byte[] healMonster(int oid, int heal) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.DAMAGE_MONSTER);
		mplew.writeInt(oid);
        mplew.write(0);
        mplew.writeInt(-heal);

        return mplew.getPacket();
    }
    
    public static byte[] ForbidMonsterAttack(int objectId, List<Byte> attacks) {
	    MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.MOB_REACTION);
		mplew.writeInt(objectId);
	    mplew.writeInt(attacks.size());
	    for (byte b = 0; b < attacks.size(); b++) {
	        mplew.writeInt(1);
	    }
	    return mplew.getPacket();
	}

    public static byte[] MobToMobDamage(int oid, int dmg, int mobid, boolean azwan) {
        MaplePacketWriter mplew;

        if (azwan) {
        	mplew = new MaplePacketWriter(SendPacketOpcode.AZWAN_MOB_TO_MOB_DAMAGE);
        } else {
        	mplew = new MaplePacketWriter(SendPacketOpcode.MOB_TO_MOB_DAMAGE);
        }
        mplew.writeInt(oid);
        mplew.write(0);
        mplew.writeInt(dmg);
        mplew.writeInt(mobid);
        mplew.write(1);

        return mplew.getPacket();
    }

    public static byte[] getMobSkillEffect(int oid, int skillid, int cid, int skilllevel) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.SKILL_EFFECT_MOB);
		mplew.writeInt(oid);
        mplew.writeInt(skillid);
        mplew.writeInt(cid);
        mplew.writeShort(skilllevel);

        return mplew.getPacket();
    }

    public static byte[] getMobCoolEffect(int oid, int itemid) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.ITEM_EFFECT_MOB);
		mplew.writeInt(oid);
        mplew.writeInt(itemid);

        return mplew.getPacket();
    }

    public static byte[] showMonsterHP(int oid, int remhppercentage) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.SHOW_MONSTER_HP);
		mplew.writeInt(oid);
        mplew.write(remhppercentage);

        return mplew.getPacket();
    }

    public static byte[] showCygnusAttack(int oid) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.CYGNUS_ATTACK);
		mplew.writeInt(oid);

        return mplew.getPacket();
    }

    public static byte[] showMonsterResist(int oid) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.MONSTER_RESIST);
		mplew.writeInt(oid);
        mplew.writeInt(0);
        mplew.writeShort(1);
        mplew.writeInt(0);

        return mplew.getPacket();
    }

    public static byte[] showBossHP(MapleMonster mob) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.BOSS_ENV);
		mplew.write(6);
        mplew.writeInt(mob.getId() == 9400589 ? 9300184 : mob.getId());
        if (mob.getHp() > 2147483647L) {
            mplew.writeInt((int) (mob.getHp() / mob.getMobMaxHp() * 2147483647.0D));
        } else {
            mplew.writeInt((int) mob.getHp());
        }
        if (mob.getMobMaxHp() > 2147483647L) {
            mplew.writeInt(2147483647);
        } else {
            mplew.writeInt((int) mob.getMobMaxHp());
        }
      //  SmartMobnotice(8840000, 1, 0, "Fuck", (byte) 1);
        mplew.write(mob.getStats().getTagColor());
        mplew.write(mob.getStats().getTagBgColor());

        return mplew.getPacket();
    }
    
    public static byte[] SmartMobnotice(int mobId, int type, int number, String message, byte color) {
	    MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.SMART_MOB_NOTICE);
		mplew.writeInt(color);//0:white, 1:yellow, 2:blue
	    mplew.writeInt(mobId);
	    mplew.writeInt(type);//1:attack, 2:skill, 3:change controller, 5:mobzone?
	    mplew.writeInt(number);//attack 1+, skill 0+
	    mplew.writeMapleAsciiString(message);
	
	    return mplew.getPacket();
    }

    public static byte[] showBossHP(int monsterId, long currentHp, long maxHp) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.BOSS_ENV);
		mplew.write(6);
        mplew.writeInt(monsterId);
        if (currentHp > 2147483647L) {
            mplew.writeInt((int) (currentHp / maxHp * 2147483647.0D));
        } else {
            mplew.writeInt((int) (currentHp <= 0L ? -1L : currentHp));
        }
        if (maxHp > 2147483647L) {
            mplew.writeInt(2147483647);
        } else {
            mplew.writeInt((int) maxHp);
        }
        mplew.write(6);
        mplew.write(5);

        return mplew.getPacket();
    }
    
    /*
    public static byte[] moveMonster(boolean useskill, int skill, int skillID, int skillLv, int option, int oid, Point startPos, List<LifeMovementFragment> moves, List<Integer> unk2, List<Pair<Integer, Integer>> unk3) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.MOVE_MONSTER);
		mplew.writeInt(oid);
        mplew.write(useskill ? 1 : 0);
        mplew.write(skill);
        mplew.write(skillID);
        mplew.write(skillLv);
        mplew.writeShort(option);
        mplew.write(unk3 == null ? 0 : unk3.size());
        if (unk3 != null) {
            for (Pair i : unk3) {
                mplew.writeShort(((Integer) i.left).intValue());
                mplew.writeShort(((Integer) i.right).intValue());
            }
        }
        mplew.write(unk2 == null ? 0 : unk2.size());
        if (unk2 != null) {
            for (Integer i : unk2) {
                mplew.writeShort(i.intValue());
            }
        }
        mplew.writeInt(0);
        mplew.writePos(startPos);
        mplew.writeShort(0);
        mplew.writeShort(0);
        PacketHelper.serializeMovementList(mplew, moves);
        mplew.write(0); // new
        return mplew.getPacket();
    } */

    public static byte[] moveMonster(boolean useskill, int skill, int skill1, int skill2, int skill3, int skill4, int oid, Point startPos, List<LifeMovementFragment> moves) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.MOVE_MONSTER);
		mplew.writeInt(oid);
        mplew.write(useskill ? 1 : 0);
        mplew.write(skill);
        mplew.write(skill1);
        mplew.write(skill2);
        mplew.write(skill3);
        mplew.write(skill4);
        mplew.write(0); // nSkillID
        mplew.write(0); // v21
        mplew.writeInt(0);
        mplew.writePos(startPos);
        mplew.writeInt(0); // v171
        //mplew.writeInt(Randomizer.nextInt());
        PacketHelper.serializeMovementList(mplew, moves);
        mplew.write(0); // v171
        
        return mplew.getPacket();
    }

    /*
    public static byte[] moveMonster(boolean useskill, int skill, int unk, int oid, Point startPos, List<LifeMovementFragment> moves) {
        return moveMonster(useskill, skill, unk, oid, startPos, moves, null, null);
    }
    
/*
    public static byte[] moveMonster(boolean useskill, int skill, int unk, int oid, Point startPos, List<LifeMovementFragment> moves, List<Integer> unk2, List<Pair<Integer, Integer>> unk3) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.MOVE_MONSTER);
		mplew.writeInt(oid);
        mplew.write(useskill ? 1 : 0);
        mplew.write(skill);
        mplew.writeInt(unk);
        mplew.write(unk3 == null ? 0 : unk3.size());
        if (unk3 != null) {
            for (Pair i : unk3) {
                mplew.writeShort(((Integer) i.left));
                mplew.writeShort(((Integer) i.right));
            }
        }
        mplew.write(unk2 == null ? 0 : unk2.size());
        if (unk2 != null) {
            for (Integer i : unk2) {
                mplew.writeShort(i);
            }
        }

        mplew.writeInt(0);
        mplew.writePos(startPos);
        mplew.writeInt(0);

        PacketHelper.serializeMovementList(mplew, moves);

        return mplew.getPacket();
    }*/

    public static byte[] moveMonsterResponse(int objectid, short moveid, int currentMp, boolean useSkills, int skillId, int skillLevel) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.MOVE_MONSTER_RESPONSE);
		mplew.writeInt(objectid);
        mplew.writeShort(moveid);
        mplew.write(useSkills ? 1 : 0);
        mplew.writeInt(currentMp);
    //    mplew.writeShort(0);
        mplew.write(skillId);
        mplew.write(skillLevel);
        mplew.writeInt(0);
     //   mplew.writeZeroBytes(50);

        return mplew.getPacket();
    }
    
    public static byte[] spawnMonster(MapleMonster life, int spawnType, int link, boolean azwan) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.SPAWN_MONSTER);
		mplew.write(0); // bSealedInsteadDead
        mplew.writeInt(life.getObjectId());
        mplew.write(1); // nCalcDamageIndex
        mplew.writeInt(life.getId());
        
        mplew.write(0); // Forced Mob Stat boolean
        
        int[] flag = new int[3];
        flag[1] |= 0x60000000;
        flag[2] |= 0x6000000;
        flag[2] |= 0x4000000;
        flag[2] |= 0xFF0000;
        flag[2] |= 0xF000;
        
        for (int i = 0; i < flag.length; i++) {
            mplew.writeInt(flag[i]);
        }

        short monstergen = (short) (life.getObjectId() / 2); // who knows
        for (int i = 0; i < 4; i++) {
            mplew.writeLong(0);
            mplew.writeShort(monstergen);
        }
        mplew.writeZeroBytes(119);
        
        //addMonsterStatus(mplew, life);
        mplew.writePos(life.getTruePosition());
        mplew.write(life.getStance());
        if (life.getId() == 8910000 || life.getId() == 8910100) {
            mplew.write(0);
        }
        mplew.writeShort(life.getFh());//was0
        mplew.writeShort(life.getFh());
        
        mplew.writeShort(-2); // -1 if used in controlMonster summonType
        mplew.write(-1); // team
        mplew.writeInt(life.getHp() > 2147483647 ? 2147483647 : (int) life.getHp());
        mplew.writeZeroBytes(21);
        
        mplew.writeInt(-1);
        mplew.writeInt(-1);
        mplew.writeInt(0);
        mplew.write(0);
        
        mplew.writeInt(100);
        mplew.writeInt(-1);
        
        mplew.writeInt(0);
        mplew.write(0);
        mplew.write(0);
        
        /*
        mplew.write(spawnType);
        if ((spawnType == -3) || (spawnType >= 0)) {
            mplew.writeInt(link);
        }
        */
        /*
        mplew.write(life.getCarnivalTeam());
        mplew.writeInt(life.getHp() > 2147483647 ? 2147483647 : (int) life.getHp());
        mplew.writeInt(0);//new 142
        mplew.writeZeroBytes(16);
        mplew.write(0);
        mplew.writeInt(-1);
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.write(0);
        mplew.write(-2);
        */
               
        return mplew.getPacket();
    }

    public static void addMonsterStatus(MaplePacketWriter mplew, MapleMonster life) {
        mplew.write(life.getChangedStats() != null);
        if (life.getChangedStats() != null) {
            mplew.writeInt(life.getChangedStats().hp > 2147483647L ? 2147483647 : (int) life.getChangedStats().hp);
            mplew.writeInt(life.getChangedStats().mp);
            mplew.writeInt(life.getChangedStats().exp);
            mplew.writeInt(life.getChangedStats().watk);
            mplew.writeInt(life.getChangedStats().matk);
            mplew.writeInt(life.getChangedStats().PDRate);
            mplew.writeInt(life.getChangedStats().MDRate);
            mplew.writeInt(life.getChangedStats().acc);
            mplew.writeInt(life.getChangedStats().eva);
            mplew.writeInt(life.getChangedStats().pushed);
            mplew.writeInt(life.getChangedStats().speed);//new 141?
            mplew.writeInt(life.getChangedStats().level);
        }
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.writeInt(0);

        mplew.writeShort(5088); // E0 13
        mplew.write(72);//0x48
        mplew.writeInt(0);
        mplew.write(0x88); // flag maybe
        short monstergen = (short) (life.getId() / 2); // who knows
        for (int i = 0; i < 4; ++i) {
            mplew.writeLong(0);
            mplew.writeShort(monstergen);
        }
        mplew.writeZeroBytes(19);
    }

    public static void addMonsterInformation(MaplePacketWriter mplew, MapleMonster life, boolean newSpawn, boolean summon, byte spawnType, int link) {
        mplew.writePos(life.getTruePosition());
        mplew.write(life.getStance());
        mplew.writeShort(0);
        mplew.writeShort(life.getFh());
        if (summon) {
            mplew.write(spawnType);
            if ((spawnType == -3) || (spawnType >= 0)) {
                mplew.writeInt(link);
            }
        } else {
            mplew.write(newSpawn ? -2 : life.isFake() ? -4 : -1);
        }
        mplew.write(life.getCarnivalTeam());
        mplew.writeInt(63000);
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.write(-1);
    }

    public static byte[] controlMonster(MapleMonster life, boolean newSpawn, boolean aggro, boolean azwan) {
        MaplePacketWriter mplew;

        if (azwan) {
        	mplew = new MaplePacketWriter(SendPacketOpcode.AZWAN_SPAWN_MONSTER_CONTROL);
        } else {
        	mplew = new MaplePacketWriter(SendPacketOpcode.SPAWN_MONSTER_CONTROL);
        }
        if (!azwan) {
            mplew.write(aggro ? 2 : 1);
        }

        mplew.writeInt(life.getObjectId());
        mplew.write(1);// 1 = Control normal, 5 = Control none?
        mplew.writeInt(life.getId());
        
        mplew.write(0); // Forced Mob Stat boolean
        
        int[] flag = new int[3];
        flag[1] |= 0x60000000;
        flag[2] |= 0x6000000;
        flag[2] |= 0x4000000;
        flag[2] |= 0xFF0000;
        flag[2] |= 0xF000;
        
        for (int i = 0; i < flag.length; i++) {
            mplew.writeInt(flag[i]);
        }

        short monstergen = (short) (life.getObjectId() / 2); // who knows
        for (int i = 0; i < 4; i++) {
            mplew.writeLong(0);
            mplew.writeShort(monstergen);
        }
        mplew.writeZeroBytes(119);
        
        //addMonsterStatus(mplew, life);
        mplew.writePos(life.getTruePosition());
        mplew.write(life.getStance());
        if (life.getId() == 8910000 || life.getId() == 8910100) {
            mplew.write(0);
        }
        mplew.writeShort(life.getFh());//was0
        mplew.writeShort(life.getFh());
        
        mplew.writeShort(-2); // -1 if used in controlMonster summonType
        mplew.write(-1); // team
        mplew.writeInt(life.getHp() > 2147483647 ? 2147483647 : (int) life.getHp());
        mplew.writeZeroBytes(21);
        
        mplew.writeInt(-1);
        mplew.writeInt(-1);
        mplew.writeInt(0);
        mplew.write(0);
        
        mplew.writeInt(100);
        mplew.writeInt(-1);
        
        mplew.writeInt(0);
        mplew.write(0);
        mplew.write(0);
        /*
        mplew.write(spawnType);
        if ((spawnType == -3) || (spawnType >= 0)) {
            mplew.writeInt(link);
        }
        */
        /*
        mplew.write(life.getCarnivalTeam());
        mplew.writeInt(life.getHp() > 2147483647 ? 2147483647 : (int) life.getHp());
        mplew.writeInt(0);//new 142
        mplew.writeZeroBytes(16);
        mplew.write(0);
        mplew.writeInt(-1);
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.write(0);
        mplew.write(-2);
        */
        return mplew.getPacket();
    }

    public static byte[] stopControllingMonster(MapleMonster life, boolean azwan) {
        MaplePacketWriter mplew;

        if (azwan) {
        	mplew = new MaplePacketWriter(SendPacketOpcode.AZWAN_SPAWN_MONSTER_CONTROL);
        } else {
        	mplew = new MaplePacketWriter(SendPacketOpcode.SPAWN_MONSTER_CONTROL);
        }
        if (!azwan) {
            mplew.write(0);
        }
        mplew.writeInt(life.getObjectId());
        if (azwan) {
            mplew.write(0);
            mplew.writeInt(0);
            mplew.write(0);
            addMonsterStatus(mplew, life);

            mplew.writePos(life.getTruePosition());
            mplew.write(life.getStance());
            mplew.writeShort(0);
            mplew.writeShort(life.getFh());
            mplew.write(life.isFake() ? -4 : -1);
            mplew.write(life.getCarnivalTeam());
            mplew.writeInt(63000);
            mplew.writeInt(0);
            mplew.writeInt(0);
            mplew.write(-1);
        }

        return mplew.getPacket();
    }

    public static byte[] makeMonsterReal(MapleMonster life, boolean azwan) {
        return spawnMonster(life, -1, 0, azwan);
    }

    public static byte[] makeMonsterFake(MapleMonster life, boolean azwan) {
        return spawnMonster(life, -4, 0, azwan);
    }

    public static byte[] makeMonsterEffect(MapleMonster life, int effect, boolean azwan) {
        return spawnMonster(life, effect, 0, azwan);
    }

    public static byte[] getMonsterSkill(int objectid) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.MONSTER_SKILL);
		mplew.writeInt(objectid);
        mplew.writeLong(0);

        return mplew.getPacket();
    }

    public static byte[] getMonsterTeleport(int objectid, int x, int y) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.TELE_MONSTER);
		mplew.writeInt(objectid);
        mplew.writeInt(x);
        mplew.writeInt(y);

        return mplew.getPacket();
    }

    private static void getLongMask_NoRef(MaplePacketWriter mplew, Collection<MonsterStatusEffect> ss, boolean ignore_imm) {
        int[] mask = new int[12];
        for (MonsterStatusEffect statup : ss) {
            if ((statup != null) && (statup.getStati() != MonsterStatus.WEAPON_DAMAGE_REFLECT) && (statup.getStati() != MonsterStatus.MAGIC_DAMAGE_REFLECT) && ((!ignore_imm) || ((statup.getStati() != MonsterStatus.WEAPON_IMMUNITY) && (statup.getStati() != MonsterStatus.MAGIC_IMMUNITY) && (statup.getStati() != MonsterStatus.DAMAGE_IMMUNITY)))) {
                mask[(statup.getStati().getPosition() - 1)] |= statup.getStati().getValue();
            }
        }
        for (int i = mask.length; i >= 1; i--) {
            mplew.writeInt(mask[(i - 1)]);
        }
    }

    public static byte[] applyMonsterStatus(int oid, MonsterStatus mse, int x, MobSkill skil) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.APPLY_MONSTER_STATUS);
		mplew.writeInt(oid);
        PacketHelper.writeSingleMask(mplew, mse);

        mplew.writeInt(x);
        mplew.writeShort(skil.getSkillId());
        mplew.writeShort(skil.getSkillLevel());
        mplew.writeShort(mse.isEmpty() ? 1 : 0);

        mplew.writeShort(0);
        mplew.write(2);//was 1
        mplew.writeZeroBytes(30);

        System.out.println("ams 2");
        return mplew.getPacket();
    }
   

    public static byte[] applyMonsterStatus(MapleMonster mons, MonsterStatusEffect ms, MapleCharacter chr) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.APPLY_MONSTER_STATUS);
		mplew.writeInt(mons.getObjectId());
        
        PacketHelper.writeMobMask(mplew, ms.getStati());
        
        
        mplew.writeInt(ms.getX().intValue());
        if (ms.isMonsterSkill()) {
            mplew.writeShort(ms.getMobSkill().getSkillId());
            mplew.writeShort(ms.getMobSkill().getSkillLevel());
        } else if (ms.getSkill() > 0) {
            mplew.writeInt(ms.getSkill());
        }
        mplew.writeShort((short) ((ms.getCancelTask() - System.currentTimeMillis()) / 1000));

        mplew.writeInt(chr.getId()); // Char ID
        /*
        mplew.writeInt(20000);
        mplew.writeInt(1000);
        mplew.writeInt(0); // Update tick? or CRC
        mplew.writeInt(7468);
        mplew.writeInt(6); //Duration?
        mplew.writeZeroBytes(20);
        mplew.writeInt(7850);
        mplew.writeShort(468);
        mplew.write(1);
        */
        
        mplew.write(HexTool.getByteArrayFromHexString("70 13 00 00 00 00 00 00 00 00 0B"));

        //mplew.write(HexTool.getByteArrayFromHexString("01 00 00 00 9B BA 3E 00 0C 00 01 61 87 9D 00 9B BA 3E 00 AA 1E 00 00 E8 03 00 00 1A 46 B7 15 2C 1D 00 00 06 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 AA 1E 00 00 D4 01 0A"));
        System.out.println("ams 3");
        System.out.println(mplew.toString());
        return mplew.getPacket();
    }

    public static byte[] applyMonsterStatus(MapleMonster mons, List<MonsterStatusEffect> mse) {
        if ((mse.size() <= 0) || (mse.get(0) == null)) {
            return CWvsContext.enableActions();
        }
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.APPLY_MONSTER_STATUS);
		mplew.writeInt(mons.getObjectId());
        MonsterStatusEffect ms = (MonsterStatusEffect) mse.get(0);
        if (ms.getStati() == MonsterStatus.POISON) {
            PacketHelper.writeMobMask(mplew, MonsterStatus.EMPTY);
            mplew.write(mse.size());
            for (MonsterStatusEffect m : mse) {
                mplew.writeInt(m.getFromID());
                if (m.isMonsterSkill()) {
                    mplew.writeShort(m.getMobSkill().getSkillId());
                    mplew.writeShort(m.getMobSkill().getSkillLevel());
                } else if (m.getSkill() > 0) {
                    mplew.writeInt(m.getSkill());
                }
                mplew.writeInt(m.getX().intValue());
                mplew.writeInt(1000);
                mplew.writeInt(0);
                mplew.writeInt(8000);//new v141
                mplew.writeInt(6);
                mplew.writeInt(0);
                mplew.writeZeroBytes(20);
                mplew.writeInt(7850);  
            }
            mplew.writeShort(1000);//was 300
            mplew.write(11);//was 1
            //mplew.write(1);
        } else {
            PacketHelper.writeMobMask(mplew, ms.getStati());

            mplew.writeInt(ms.getX().intValue());
            if (ms.isMonsterSkill()) {
                mplew.writeShort(ms.getMobSkill().getSkillId());
                mplew.writeShort(ms.getMobSkill().getSkillLevel());
            } else if (ms.getSkill() > 0) {
                mplew.writeInt(ms.getSkill());
            }
            mplew.writeShort((short) ((ms.getCancelTask() - System.currentTimeMillis()) / 1000));
            mplew.writeLong(0L);
            mplew.writeShort(0);
            mplew.write(1);
        }
//System.out.println("Monsterstatus3");
        System.out.println("ams 4");
        System.out.println(mplew.toString());
        return mplew.getPacket();
    }

    public static byte[] applyMonsterStatus(int oid, Map<MonsterStatus, Integer> stati, List<Integer> reflection, MobSkill skil) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.APPLY_MONSTER_STATUS);
		mplew.writeInt(oid);
        PacketHelper.writeMask(mplew, stati.keySet());

        for (Map.Entry mse : stati.entrySet()) {
            mplew.writeInt(((Integer) mse.getValue()).intValue());
            mplew.writeInt(skil.getSkillId());
            mplew.writeShort((short) skil.getDuration());
        }

        for (Integer ref : reflection) {
            mplew.writeInt(ref.intValue());
        }
        mplew.writeLong(0L);
        mplew.writeShort(0);

        int size = stati.size();
        if (reflection.size() > 0) {
            size /= 2;
        }
        mplew.write(size);
        
        System.out.println("ams 1");
        return mplew.getPacket();
    }

    public static byte[] applyPoison(MapleMonster mons, List<MonsterStatusEffect> mse) {
        if ((mse.size() <= 0) || (mse.get(0) == null)) {
            return CWvsContext.enableActions();
        }
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.APPLY_MONSTER_STATUS);
		mplew.writeInt(mons.getObjectId());
        PacketHelper.writeSingleMask(mplew, MonsterStatus.EMPTY);
        mplew.write(mse.size());
        for (MonsterStatusEffect m : mse) {
            mplew.writeInt(m.getFromID());
            if (m.isMonsterSkill()) {
                mplew.writeShort(m.getMobSkill().getSkillId());
                mplew.writeShort(m.getMobSkill().getSkillLevel());
            } else if (m.getSkill() > 0) {
                mplew.writeInt(m.getSkill());
            }
            mplew.writeInt(m.getX().intValue());
            mplew.writeInt(1000);
            mplew.writeInt(0);//600574518?
            mplew.writeInt(8000);//war 7000
            mplew.writeInt(6);//was 5
            mplew.writeInt(0);
        }
        mplew.writeShort(1000);//was 300
        mplew.write(2);//was 1
        //mplew.write(1);
//System.out.println("Monsterstatus5");
        return mplew.getPacket();
    }

    public static byte[] cancelMonsterStatus(int oid, MonsterStatus stat) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.CANCEL_MONSTER_STATUS);
		mplew.writeInt(oid);
        PacketHelper.writeMobMask(mplew, stat);
        mplew.writeInt(1);
        mplew.writeInt(1);
        mplew.writeInt(0); // Char ID
        mplew.write(HexTool.getByteArrayFromHexString("B0 A9 EB 03 0C"));

        System.out.println("cancel status");
        return mplew.getPacket();
    }

    public static byte[] cancelPoison(int oid, MonsterStatusEffect m) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.CANCEL_MONSTER_STATUS);
		mplew.writeInt(oid);
        PacketHelper.writeMobMask(mplew, MonsterStatus.EMPTY);
        mplew.writeInt(0);
        mplew.writeInt(1);
        mplew.writeInt(m.getFromID());
        if (m.isMonsterSkill()) {
            mplew.writeShort(m.getMobSkill().getSkillId());
            mplew.writeShort(m.getMobSkill().getSkillLevel());
        } else if (m.getSkill() > 0) {
            //mplew.writeInt(m.getSkill());
        }
        mplew.write(HexTool.getByteArrayFromHexString("B0 DC ED 03")); // Update tick?
        mplew.write(1); // This is just a counter. It increments by 1 each time APPLY or CANCEL_MONSTER_STATUS is sent.

        System.out.println("cancelpoison");
        return mplew.getPacket();
    }

    public static byte[] talkMonster(int oid, int itemId, String msg) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.TALK_MONSTER);
		mplew.writeInt(oid);
        mplew.writeInt(500);
        mplew.writeInt(itemId);
        mplew.write(itemId <= 0 ? 0 : 1);
        mplew.write((msg == null) || (msg.length() <= 0) ? 0 : 1);
        if ((msg != null) && (msg.length() > 0)) {
            mplew.writeMapleAsciiString(msg);
        }
        mplew.writeInt(1);

        return mplew.getPacket();
    }

    public static byte[] removeTalkMonster(int oid) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.REMOVE_TALK_MONSTER);
		mplew.writeInt(oid);

        return mplew.getPacket();
    }

    public static final byte[] getNodeProperties(MapleMonster objectid, MapleMap map) {
        if (objectid.getNodePacket() != null) {
            return objectid.getNodePacket();
        }
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.MONSTER_PROPERTIES);
		mplew.writeInt(objectid.getObjectId());
        mplew.writeInt(map.getNodes().size());
        mplew.writeInt(objectid.getPosition().x);
        mplew.writeInt(objectid.getPosition().y);
        for (MapleNodes.MapleNodeInfo mni : map.getNodes()) {
            mplew.writeInt(mni.x);
            mplew.writeInt(mni.y);
            mplew.writeInt(mni.attr);
            if (mni.attr == 2) {
                mplew.writeInt(500);
            }
        }
        mplew.writeInt(0);
        mplew.write(0);
        mplew.write(0);

        objectid.setNodePacket(mplew.getPacket());
        return objectid.getNodePacket();
    }

    public static byte[] showMagnet(int mobid, boolean success) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.SHOW_MAGNET);
		mplew.writeInt(mobid);
        mplew.write(success ? 1 : 0);
        mplew.write(0);

        return mplew.getPacket();
    }

    public static byte[] catchMonster(int mobid, int itemid, byte success) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.CATCH_MONSTER);
		mplew.writeInt(mobid);
        mplew.writeInt(itemid);
        mplew.write(success);

        return mplew.getPacket();
    }
}