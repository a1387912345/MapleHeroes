package net.server.channel.handler.monster;

import client.MapleCharacterUtil;
import client.MapleClient;
import client.MonsterFamiliar;
import client.MonsterStatus;
import client.MonsterStatusEffect;
import client.SkillFactory;
import client.anticheat.CheatingOffense;
import client.character.MapleCharacter;
import client.inventory.Item;
import client.inventory.MapleInventoryType;
import constants.GameConstants;
import net.netty.MaplePacketReader;
import net.packet.CField;
import net.packet.CWvsContext;
import net.packet.MobPacket;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import server.MapleInventoryManipulator;
import server.MapleItemInformationProvider;
import server.Randomizer;
import server.StructFamiliar;
import server.Timer.WorldTimer;
import server.life.MapleLifeFactory;
import server.life.MapleMonster;
import server.life.MapleMonsterStats;
import server.life.MobSkill;
import server.life.MobSkillFactory;
import server.maps.MapleMap;
import server.maps.MapleNodes;
import server.movement.LifeMovementFragment;
import tools.FilePrinter;
import tools.FileoutputUtil;
import tools.Pair;
import tools.Triple;

public class MobHelper {

	/*
	public static final void MoveMonster(LittleEndianAccessor inPacket, MapleClient c, MapleCharacter chr) {
        if ((chr == null) || (chr.getMap() == null)) {
            return;
        }
        int oid = inPacket.readInt();
        MapleMonster monster = chr.getMap().getMonsterByOid(oid);
        inPacket.readByte(); // Azwan Boolean
        if (monster == null) {
            return;
        }
        if (monster.getLinkCID() > 0) {
            return;
        }
        short moveid = inPacket.readShort();
        boolean useSkill = inPacket.readByte() > 0;
        byte skill = inPacket.readByte();
        int skillID = inPacket.readByte() & 0xFF; 
        int skillLv = inPacket.readByte() & 0xFF; 
        short option = inPacket.readShort();  // Skill's effectDelay
        int realskill = 0;
        int level = 0;

        if (useSkill) {
            byte size = monster.getNoSkills();
            boolean used = false;

            if (size > 0) {
                final Pair<Integer, Integer> skillToUse = monster.getSkills().get((byte) Randomizer.nextInt(size));
                realskill = ((Integer) skillToUse.getLeft()).intValue();
                level = ((Integer) skillToUse.getRight()).intValue();

                MobSkill mobSkill = MobSkillFactory.getMobSkill(realskill, level);

                if ((mobSkill != null) && (!mobSkill.checkCurrentBuff(chr, monster))) {
                    long now = System.currentTimeMillis();
                    long ls = monster.getLastSkillUsed(realskill);

                    if ((ls == 0L) || ((now - ls > mobSkill.getCoolTime()) && (!mobSkill.onlyOnce()))) {
                        monster.setLastSkillUsed(realskill, now, mobSkill.getCoolTime());

                        int reqHp = (int) ((float) monster.getHp() / (float) monster.getMobMaxHp() * 100.0F);
                        if (reqHp <= mobSkill.getHP()) {
                            used = true;
                            mobSkill.applyEffect(chr, monster, true);
                        }
                    }
                }
            }
            if (!used) {
                realskill = 0;
                level = 0;
            }
        }
        final List<Pair<Integer, Integer>> unk3 = new ArrayList<>();
        byte size1 = inPacket.readByte();

        for (int i = 0; i < size1; i++) {
            unk3.add(new Pair(Integer.valueOf(inPacket.readShort()), Integer.valueOf(inPacket.readShort())));
        }
        final List<Integer> unk2 = new ArrayList<>();
        byte size = inPacket.readByte();
        for (int i = 0; i < size; i++) {
            unk2.add(Integer.valueOf(inPacket.readShort()));
        }
        inPacket.skip(1); // 00
        inPacket.skip(4); // 01 00 00 00
        inPacket.skip(4); // CC DD FF 00
        inPacket.skip(4); // CC DD FF 00
        inPacket.skip(4); // D8 07 09 06
        inPacket.skip(1); // 02 
        inPacket.skip(4); // 00 00 00 00
        inPacket.skip(4); // Client Sided Pos
        inPacket.skip(4); // Client Sided VPos
        Point startPos = monster.getPosition();
        List<LifeMovementFragment> res = null;
        try {
            res = MovementParse.parseMovement(inPacket, 2);
        } catch (ArrayIndexOutOfBoundsException e) {
            FilePrinter.printError(FilePrinter.MOVEMENT_EXCEPTION, e);
            FilePrinter.print(FilePrinter.MOVEMENT_EXCEPTION, "MOBID " + monster.getId() + ", AIOBE Type2:\n" + inPacket.toString(true));
            return;
        }
        if ((res != null) && (chr != null) && (res.size() > 0)) {
            MapleMap map = chr.getMap();
            /*for (LifeMovementFragment move : res) {
                if ((move instanceof AbsoluteLifeMovement)) {
                    Point endPos = ((LifeMovement) move).getPosition();
                    if ((endPos.x < map.getLeft() - 250) || (endPos.y < map.getTop() - 250) || (endPos.x > map.getRight() + 250) || (endPos.y > map.getBottom() + 250)) {
                        chr.getCheatTracker().checkMoveMonster(endPos);
                        return;
                    }
                }
            }*/ /*
            c.sendPacket(MobPacket.moveMonsterResponse(monster.getObjectId(), moveid, monster.getMp(), monster.isControllerHasAggro(), realskill, level));
            if (inPacket.available() != 37) {
                FilePrinter.print(FilePrinter.MOVEMENT_EXCEPTION, "inPacket.available != 37 (movement parsing error)\n" + inPacket.toString(true));
                return;
            }
            MovementParse.updatePosition(res, monster, -1);
            Point endPos = monster.getTruePosition();
            map.moveMonster(monster, endPos);
            map.broadcastMessage(chr, MobPacket.moveMonster(useSkill, skill, skillID, skillLv, option, monster.getObjectId(), startPos, res, unk2, unk3), endPos);
            chr.getCheatTracker().checkMoveMonster(endPos);
        }
    } */

    public static final void checkShammos(MapleCharacter chr, MapleMonster mobto, MapleMap map) {
        MapleMap mapp;
        if ((!mobto.isAlive()) && (mobto.getStats().isEscort())) {
            for (MapleCharacter chrz : map.getCharactersThreadsafe()) {
                if ((chrz.getParty() != null) && (chrz.getParty().getLeader().getId() == chrz.getID())) {
                    if (!chrz.haveItem(2022698)) {
                        break;
                    }
                    MapleInventoryManipulator.removeById(chrz.getClient(), MapleInventoryType.USE, 2022698, 1, false, true);
                    mobto.heal((int) mobto.getMobMaxHp(), mobto.getMobMaxMp(), true);
                    return;
                }

            }

            map.broadcastMessage(CWvsContext.broadcastMsg(6, "Your party has failed to protect the monster."));
            mapp = chr.getMap().getForcedReturnMap();
            for (MapleCharacter chrz : map.getCharactersThreadsafe()) {
                chrz.changeMap(mapp, mapp.getPortal(0));
            }
        } else if ((mobto.getStats().isEscort()) && (mobto.getEventInstance() != null)) {
            mobto.getEventInstance().setProperty("HP", String.valueOf(mobto.getHp()));
        }
    }

}
