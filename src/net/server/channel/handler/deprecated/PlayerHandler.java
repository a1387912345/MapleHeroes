package net.server.channel.handler.deprecated;

import client.*;
import client.anticheat.CheatingOffense;
import client.character.MapleCharacter;
import client.inventory.Item;
import client.inventory.MapleInventory;
import client.inventory.MapleInventoryType;
import constants.GameConstants;
import net.netty.MaplePacketReader;
import net.packet.AdventurerPacket;
import net.packet.CField;
import net.packet.CSPacket;
import net.packet.CWvsContext;
import net.packet.JobPacket;
import net.packet.MobPacket;
import net.packet.JobPacket.AngelicPacket;
import net.server.channel.ChannelServer;
import net.world.World;

import java.awt.Point;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import server.*;
import server.Timer.CloneTimer;
import server.events.MapleEvent;
import server.events.MapleEventType;
import server.events.MapleSnowball;
import server.life.MapleLifeFactory;
import server.life.MapleMonster;
import server.life.MobAttackInfo;
import server.life.MobAttackInfoFactory;
import server.life.MobSkill;
import server.life.MobSkillFactory;
import server.maps.FieldLimitType;
import server.maps.MapleMap;
import server.maps.MapleMapObject;
import server.maps.MapleMapObjectType;
import server.movement.LifeMovementFragment;
import server.quest.MapleQuest;
import tools.AttackPair;
import tools.FileoutputUtil;
import tools.Pair;

public class PlayerHandler {

    public static int isFinisher(int skillid) {
        switch (skillid) {
            case 1111003:
                return 1;
            case 1111005:
                return 2;
            case 11111002:
                return 1;
            case 11111003:
                return 2;
        }
        return 0;
    }

    public static void UseTitle(int itemId, MapleClient c, MapleCharacter chr) {
        if ((chr == null) || (chr.getMap() == null)) {
            return;
        }
        Item toUse = chr.getInventory(MapleInventoryType.SETUP).findById(itemId);
        if (toUse == null) {
            return;
        }
        if (itemId <= 0) {
            chr.getQuestRemove(MapleQuest.getInstance(124000));
        } else {
            chr.getQuestNAdd(MapleQuest.getInstance(124000)).setCustomData(String.valueOf(itemId));
        }
        chr.getMap().broadcastMessage(chr, CField.showTitle(chr.getID(), itemId), false);
        c.sendPacket(CWvsContext.enableActions());
    }

    public static void AngelicChange(MaplePacketReader inPacket, MapleClient c, MapleCharacter chr) {
        if ((chr == null) || (chr.getMap() == null)) {
            return;
        }
        int transform = inPacket.readInt();
//        System.out.println("transform id " + transform);
        if (transform == 5010094) {
//            System.out.println("acvivate");
            chr.getMap().broadcastMessage(chr, CField.showAngelicBuster(chr.getID(), transform), false);
            chr.getMap().broadcastMessage(chr, CField.updateCharLook(chr, transform == 5010094), false);
            c.sendPacket(CWvsContext.enableActions());
//        System.out.println("acvivate done");
        } else {
//            System.out.println("deacvivate");
//        chr.getMap().broadcastMessage(chr, CField.showAngelicBuster(chr.getId(), transform), false);
//        chr.getMap().broadcastMessage(chr, CField.updateCharLook(chr, transform == 5010093), false);
//        c.sendPacket(CWvsContext.enableActions());
        }
    }

    public static void DressUpTime(MaplePacketReader inPacket, final MapleClient c, MapleCharacter chr) {
        byte type = inPacket.readByte();
//        System.out.println("abtype " + type);
        if (type == 1) {
//            PlayerHandler.AngelicChange(inPacket, c, chr);
            if (GameConstants.isAngelicBuster(c.getCharacter().getJob())) {
                c.sendPacket(JobPacket.AngelicPacket.DressUpTime(type));
                c.sendPacket(JobPacket.AngelicPacket.updateDress(5010094, c.getCharacter()));
                chr.getMap().broadcastMessage(chr, CField.updateCharLook(chr, true), false);//PLZ TEST ANGELIC CHANGE!
//        }
            } else {
                c.sendPacket(CWvsContext.enableActions());
//            return;
            }
        }
    }
//        if (type != 1) {// || !GameConstants.isAngelicBuster(c.getPlayer().getJob())
//            c.sendPacket(CWvsContext.enableActions());
//            return;
//        }
//        c.sendPacket(JobPacket.AngelicPacket.DressUpTime(type));
//    }

    public static void absorbingDF(MaplePacketReader inPacket, final MapleClient c) {
        int size = inPacket.readInt();
        int room = 0;
        byte unk = 0;
        int sn;
        for (int i = 0; i < size; i++) {
            room = GameConstants.isDemonAvenger(c.getCharacter().getJob()) || c.getCharacter().getJob() == 212 ? 0 : inPacket.readInt();
            unk = inPacket.readByte();
            sn = inPacket.readInt();
            if (GameConstants.isDemonSlayer(c.getCharacter().getJob())) {
//                c.getPlayer().addMP(c.getPlayer().getStat().getForce(room));
            }
            if (GameConstants.isAngelicBuster(c.getCharacter().getJob())) {
                boolean rand = Randomizer.isSuccess(80);
                if (sn > 0) {
                    if (rand) {
                        c.sendPacket(JobPacket.AngelicPacket.SoulSeekerRegen(c.getCharacter(), sn));
                    }
                }
            }
            if ((GameConstants.isDemonAvenger(c.getCharacter().getJob())) && inPacket.available() >= 8) {
//                c.getPlayer().getMap().broadcastMessage(MainPacketCreator.ShieldChacingRe(c.getPlayer().getId(), inPacket.readInt(), inPacket.readInt(), unk, c.getPlayer().getKeyValue2("schacing")));
            }
            if (c.getCharacter().getJob() == 212) {
//                c.getPlayer().getMap().broadcastMessage(MainPacketCreator.MegidoFlameRe(c.getPlayer().getId(), unk, inPacket.readInt()));
            }
        }
    }
    
    
    
        public static void LinkSkill(final MaplePacketReader inPacket, final MapleClient c, final MapleCharacter chr) {
        //inPacket: [76 7F 31 01] [35 00 00 00]
        c.getCharacter().dropMessage(1, "Beginning link skill.");
        int skill = inPacket.readInt();
        int cid = inPacket.readInt();
        boolean found = false;
        for (MapleCharacter chr2 : c.loadCharacters(c.getCharacter().getWorld())) {
            if (chr2.getID() == cid) {
                found = true;
            }
        }
        if (GameConstants.getLinkSkillByJob(chr.getJob()) != skill || !found || chr.getLevel() > 70) {
            c.getCharacter().dropMessage(1, "An error has occured.");
            c.sendPacket(CWvsContext.enableActions());
            return;
        }
        MapleCharacter.addLinkSkill(cid, skill);
    }

    public static void AdminCommand(MaplePacketReader inPacket, MapleClient c, MapleCharacter chr) {
        if (!c.getCharacter().isGM()) {
            return;
        }
        byte mode = inPacket.readByte();
        String victim;
        MapleCharacter target;
        switch (mode) {
            case 0x00: // Level1~Level8 & Package1~Package2
                int[][] toSpawn = MapleItemInformationProvider.getInstance().getSummonMobs(inPacket.readInt());
                for (int[] toSpawnChild : toSpawn) {
                    if (Randomizer.nextInt(101) <= toSpawnChild[1]) {
                        c.getCharacter().getMap().spawnMonsterOnGroudBelow(MapleLifeFactory.getMonster(toSpawnChild[0]), c.getCharacter().getPosition());
                    }
                }
                c.sendPacket(CWvsContext.enableActions());
                break;
            case 0x01: { // /d (inv)
                byte type = inPacket.readByte();
                MapleInventory in = c.getCharacter().getInventory(MapleInventoryType.getByType(type));
                for (byte i = 0; i < in.getSlotLimit(); i++) {
                    if (in.getItem(i) != null) {
                        MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.getByType(type), i, in.getItem(i).getQuantity(), false);
                    }
                    return;
                }
                break;
            }
            case 0x02: // Exp
                c.getCharacter().setExp(inPacket.readInt());
                break;
            case 0x03: // /ban <name>
                victim = inPacket.readMapleAsciiString();
                String reason = victim + " permanent banned by " + c.getCharacter().getName();
                target = c.getChannelServer().getPlayerStorage().getCharacterByName(victim);
                if (target != null) {
                    String readableTargetName = MapleCharacter.makeMapleReadable(target.getName());
                    String ip = target.getClient().getSocketChannel().remoteAddress().toString().split(":")[0];
                    reason += readableTargetName + " (IP: " + ip + ")";
                    target.ban(reason, false, false);
                    target.sendPolice("You have been blocked by #bMapleGM #kfor the HACK reason.");
                    c.sendPacket(CField.getGMEffect(4, (byte) 0));
                } else if (MapleCharacter.ban(victim, reason, false)) {
                    c.sendPacket(CField.getGMEffect(4, (byte) 0));
                } else {
                    c.sendPacket(CField.getGMEffect(6, (byte) 1));
                }
                break;
            case 0x04: // /block <name> <duration (in days)> <HACK/BOT/AD/HARASS/CURSE/SCAM/MISCONDUCT/SELL/ICASH/TEMP/GM/IPROGRAM/MEGAPHONE>
                victim = inPacket.readMapleAsciiString();
                int type = inPacket.readByte(); //reason
                int duration = inPacket.readInt();
                String description = inPacket.readMapleAsciiString();
                reason = c.getCharacter().getName() + " used /ban to ban";
                target = c.getChannelServer().getPlayerStorage().getCharacterByName(victim);
                if (target != null) {
                    String readableTargetName = MapleCharacter.makeMapleReadable(target.getName());
                    String ip = target.getClient().getSocketChannel().remoteAddress().toString().split(":")[0];
                    reason += readableTargetName + " (IP: " + ip + ")";
                    if (duration == -1) {
                        target.ban(description + " " + reason, true);
                    } else {
                        Calendar cal = Calendar.getInstance();
                        cal.add(Calendar.DATE, duration);
                        target.tempban(description, cal, type, false);
                        target.sendPolice(duration, reason, 6000);
                    }
                    c.sendPacket(CField.getGMEffect(4, (byte) 0));
                } else if (MapleCharacter.ban(victim, reason, false)) {
                    c.sendPacket(CField.getGMEffect(4, (byte) 0));
                } else {
                    c.sendPacket(CField.getGMEffect(6, (byte) 1));
                }
                break;
            case 0x10: // /h, information by vana (and tele mode f1) ... hide ofcourse
                if (inPacket.readByte() > 0) {
                    SkillFactory.getSkill(9101004).getEffect(1).applyTo(c.getCharacter());
                } else {
                    c.getCharacter().dispelBuff(9101004);
                }
                break;
            case 0x11: // Entering a map
                switch (inPacket.readByte()) {
                    case 0:// /u
                        StringBuilder sb = new StringBuilder("USERS ON THIS MAP: ");
                        for (MapleCharacter mc : c.getCharacter().getMap().getCharacters()) {
                            sb.append(mc.getName());
                            sb.append(" ");
                        }
                        c.getCharacter().dropMessage(5, sb.toString());
                        break;
                    case 12:// /uclip and entering a map
                        break;
                }
                break;
            case 0x12: // Send
                victim = inPacket.readMapleAsciiString();
                int mapId = inPacket.readInt();
                c.getChannelServer().getPlayerStorage().getCharacterByName(victim).changeMap(c.getChannelServer().getMapFactory().getMap(mapId));
                break;
            case 0x15: // Kill
                int mobToKill = inPacket.readInt();
                int amount = inPacket.readInt();
                List<MapleMapObject> monsterx = c.getCharacter().getMap().getMapObjectsInRange(c.getCharacter().getPosition(), Double.POSITIVE_INFINITY, Arrays.asList(MapleMapObjectType.MONSTER));
                for (int x = 0; x < amount; x++) {
                    MapleMonster monster = (MapleMonster) monsterx.get(x);
                    if (monster.getId() == mobToKill) {
                        c.getCharacter().getMap().killMonster(monster, c.getCharacter(), false, false, (byte) 1);
                    }
                }
                break;
            case 0x16: // Questreset
                MapleQuest.getInstance(inPacket.readShort()).forfeit(c.getCharacter());
                break;
            case 0x17: // Summon
                int mobId = inPacket.readInt();
                int quantity = inPacket.readInt();
                for (int i = 0; i < quantity; i++) {
                    c.getCharacter().getMap().spawnMonsterOnGroudBelow(MapleLifeFactory.getMonster(mobId), c.getCharacter().getPosition());
                }
                break;
            case 0x18: // Maple & Mobhp
                int mobHp = inPacket.readInt();
                c.getCharacter().dropMessage(5, "Monsters HP");
                List<MapleMapObject> monsters = c.getCharacter().getMap().getMapObjectsInRange(c.getCharacter().getPosition(), Double.POSITIVE_INFINITY, Arrays.asList(MapleMapObjectType.MONSTER));
                for (MapleMapObject mobs : monsters) {
                    MapleMonster monster = (MapleMonster) mobs;
                    if (monster.getId() == mobHp) {
                        c.getCharacter().dropMessage(5, monster.getName() + ": " + monster.getHp());
                    }
                }
                break;
            case 0x1E: // Warn
                victim = inPacket.readMapleAsciiString();
                String message = inPacket.readMapleAsciiString();
                target = c.getChannelServer().getPlayerStorage().getCharacterByName(victim);
                if (target != null) {
                    target.getClient().sendPacket(CWvsContext.broadcastMsg(1, message));
                    c.sendPacket(CField.getGMEffect(0x1E, (byte) 1));
                } else {
                    c.sendPacket(CField.getGMEffect(0x1E, (byte) 0));
                }
                break;
            case 0x24:// /Artifact Ranking
                break;
            case 0x77: //Testing purpose
                if (inPacket.available() == 4) {
                    System.out.println(inPacket.readInt());
                } else if (inPacket.available() == 2) {
                    System.out.println(inPacket.readShort());
                }
                break;
            default:
                System.out.println("New GM packet encountered (MODE : " + mode + ": " + inPacket.toString());
                break;
        }
    }

    public static void AranCombo(MapleClient c, MapleCharacter chr, int toAdd) {
        if ((chr != null) && (chr.getJob() >= 2000) && (chr.getJob() <= 2112)) {
            short combo = chr.getCombo();
            long curr = System.currentTimeMillis();

            if ((combo > 0) && (curr - chr.getLastCombo() > 7000L)) {
                combo = 0;
            }
            combo = (short) Math.min(30000, combo + toAdd);
            chr.setLastCombo(curr);
            chr.setCombo(combo);

            c.sendPacket(CField.updateCombo(combo));

            switch (combo) {
                case 10:
                case 20:
                case 30:
                case 40:
                case 50:
                case 60:
                case 70:
                case 80:
                case 90:
                case 100:
                    if (chr.getSkillLevel(21000000) < combo / 10) {
                        break;
                    }
                    SkillFactory.getSkill(21000000).getEffect(combo / 10).applyComboBuff(chr, combo);
                    break;
            }
        }
    }

    public static void RemoveAranCombo(MapleCharacter chr) {
        if (chr.getCombo() >=2) {    
        chr.minCombo((short) 1);
        } else {
            chr.setCombo((short) 1);
        }
    }

    public static void QuickSlot(MaplePacketReader inPacket, MapleCharacter chr) {
        if ((inPacket.available() == 32L) && (chr != null)) {
            StringBuilder ret = new StringBuilder();
            for (int i = 0; i < 8; i++) {
                ret.append(inPacket.readInt()).append(",");
            }
            ret.deleteCharAt(ret.length() - 1);
            chr.getQuestNAdd(MapleQuest.getInstance(123000)).setCustomData(ret.toString());
        }
    }

    public static void SkillEffect(MaplePacketReader inPacket, MapleCharacter chr) {
        int skillId = inPacket.readInt();
        if (skillId >= 91000000 && skillId < 100000000) {
            chr.getClient().sendPacket(CWvsContext.enableActions());
            return;
        }
        byte level = inPacket.readByte();
        short direction = inPacket.readShort();
        byte unk = inPacket.readByte();

        Skill skill = SkillFactory.getSkill(GameConstants.getLinkedAttackSkill(skillId));
        if ((chr == null) || (skill == null) || (chr.getMap() == null)) {
            return;
        }
        int skilllevel_serv = chr.getTotalSkillLevel(skill);

        if ((skilllevel_serv > 0) && (skilllevel_serv == level) && ((skillId == 33101005) || (skill.isChargeSkill()))) {
            chr.setKeyDownSkill_Time(System.currentTimeMillis());
            if (skillId == 33101005 || skillId == 27101202) {
                chr.setLinkMid(inPacket.readInt(), 0);
            }
            chr.getMap().broadcastMessage(chr, CField.skillEffect(chr, skillId, level, direction, unk), false);
        }
    }

    public static void changeAndroidEmotion(int emote, MapleCharacter chr) {
        if ((emote > 0) && (chr != null) && (chr.getMap() != null) && (!chr.isHidden()) && (emote <= 17) && (chr.getAndroid() != null)) {
            chr.getMap().broadcastMessage(CField.showAndroidEmotion(chr.getID(), (byte) emote));
        }
    }

    public static void snowBall(MaplePacketReader inPacket, MapleClient c) {
        c.sendPacket(CWvsContext.enableActions());
    }

    public static void leftKnockBack(MaplePacketReader inPacket, MapleClient c) {
        if (c.getCharacter().getMapId() / 10000 == 10906) {
            c.sendPacket(CField.leftKnockBack());
            c.sendPacket(CWvsContext.enableActions());
        }
    }

    public static void MessengerRanking(MaplePacketReader inPacket, MapleClient c, MapleCharacter chr) {
        if (chr == null) {
            return;
        }
        c.sendPacket(CField.messengerOpen(inPacket.readByte(), null));
    }
}
