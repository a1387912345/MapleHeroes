package net.packet;

import client.*;
import client.inventory.*;
import constants.GameConstants;
import constants.Interaction;
import constants.QuickMove.QuickMoveNPC;
import net.SendPacketOpcode;
import net.channel.handler.deprecated.AttackInfo;
import net.netty.MaplePacketWriter;
import net.world.World;
import net.world.guild.MapleGuild;
import net.world.guild.MapleGuildAlliance;

import java.awt.Point;
import java.util.*;
import server.MaplePackageActions;
import server.MapleTrade;
import server.Randomizer;
import server.events.MapleSnowball;
import server.life.MapleMonster;
import server.life.MapleNPC;
import server.maps.*;
import server.movement.LifeMovementFragment;
import server.quest.MapleQuest;
import server.shops.MapleShop;
import tools.AttackPair;
import tools.HexTool;
import tools.Pair;
import tools.Triple;

public class CField {

    public static int DEFAULT_BUFFMASK = 0;
    public static final byte[] Nexon_IP = new byte[]{(byte) 8, (byte) 31, (byte) 99, (byte) 141};  //current ip
    public static final byte[] MapleTalk_IP = new byte[]{(byte) 8, (byte) 31, (byte) 99, (byte) 133};
    
    public static byte[] getPacketFromHexString(String hex) {
        return HexTool.getByteArrayFromHexString(hex);
    }

    public static byte[] getServerIP(MapleClient c, int port, int worldId, int charId) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.SERVER_IP);
		mplew.writeShort(0);
        mplew.write(Nexon_IP);
        mplew.writeShort(port);
        mplew.write(MapleTalk_IP); // MapleTalk IP
        mplew.writeShort(8785 + worldId); // 8785 + World_ID
        mplew.writeInt(charId);
        mplew.writeZeroBytes(15);

        return mplew.getPacket();
    }

    public static byte[] getChannelChange(MapleClient c, int port) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.CHANGE_CHANNEL);
		mplew.write(1);
        mplew.write(Nexon_IP);
        mplew.writeShort(port);
        mplew.writeInt(0);

        return mplew.getPacket();
    }

    public static byte[] getPVPType(int type, List<Pair<Integer, String>> players1, int team, boolean enabled, int lvl) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.PVP_TYPE);
		mplew.write(type);
        mplew.write(lvl);
        mplew.write(enabled ? 1 : 0);
        mplew.write(0);
        if (type > 0) {
            mplew.write(team);
            mplew.writeInt(players1.size());
            for (Pair pl : players1) {
                mplew.writeInt(((Integer) pl.left).intValue());
                mplew.writeMapleAsciiString((String) pl.right);
                mplew.writeShort(2660);
            }
        }

        return mplew.getPacket();
    }

    public static byte[] getPVPTransform(int type) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.PVP_TRANSFORM);
		mplew.write(type);

        return mplew.getPacket();
    }

    public static byte[] getPVPDetails(List<Pair<Integer, Integer>> players) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.PVP_DETAILS);
		mplew.write(1);
        mplew.write(0);
        mplew.writeInt(players.size());
        for (Pair pl : players) {
            mplew.writeInt(((Integer) pl.left).intValue());
            mplew.write(((Integer) pl.right).intValue());
        }

        return mplew.getPacket();
    }

    public static byte[] enablePVP(boolean enabled) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.PVP_ENABLED);
		mplew.write(enabled ? 1 : 2);

        return mplew.getPacket();
    }

    public static byte[] getPVPScore(int score, boolean kill) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.PVP_SCORE);
		mplew.writeInt(score);
        mplew.write(kill ? 1 : 0);

        return mplew.getPacket();
    }

    public static byte[] getPVPResult(List<Pair<Integer, MapleCharacter>> flags, int exp, int winningTeam, int playerTeam) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.PVP_RESULT);
		mplew.writeInt(flags.size());
        for (Pair f : flags) {
            mplew.writeInt(((MapleCharacter) f.right).getId());
            mplew.writeMapleAsciiString(((MapleCharacter) f.right).getName());
            mplew.writeInt(((Integer) f.left).intValue());
            mplew.writeShort(((MapleCharacter) f.right).getTeam() + 1);
            mplew.writeInt(0);
            mplew.writeInt(0);
        }
        mplew.writeZeroBytes(24);
        mplew.writeInt(exp);
        mplew.write(0);
        mplew.writeShort(100);
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.write(winningTeam);
        mplew.write(playerTeam);

        return mplew.getPacket();
    }

    public static byte[] getPVPTeam(List<Pair<Integer, String>> players) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.PVP_TEAM);
		mplew.writeInt(players.size());
        for (Pair pl : players) {
            mplew.writeInt(((Integer) pl.left).intValue());
            mplew.writeMapleAsciiString((String) pl.right);
            mplew.writeShort(2660);
        }

        return mplew.getPacket();
    }

    public static byte[] getPVPScoreboard(List<Pair<Integer, MapleCharacter>> flags, int type) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.PVP_SCOREBOARD);
		mplew.writeShort(flags.size());
        for (Pair f : flags) {
            mplew.writeInt(((MapleCharacter) f.right).getId());
            mplew.writeMapleAsciiString(((MapleCharacter) f.right).getName());
            mplew.writeInt(((Integer) f.left).intValue());
            mplew.write(type == 0 ? 0 : ((MapleCharacter) f.right).getTeam() + 1);
        }

        return mplew.getPacket();
    }

    public static byte[] getPVPPoints(int p1, int p2) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.PVP_POINTS);
		mplew.writeInt(p1);
        mplew.writeInt(p2);

        return mplew.getPacket();
    }

    public static byte[] getPVPKilled(String lastWords) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.PVP_KILLED);
		mplew.writeMapleAsciiString(lastWords);

        return mplew.getPacket();
    }

    public static byte[] getPVPMode(int mode) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.PVP_MODE);
		mplew.write(mode);

        return mplew.getPacket();
    }

    public static byte[] getPVPIceHPBar(int hp, int maxHp) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.PVP_ICEKNIGHT);
		mplew.writeInt(hp);
        mplew.writeInt(maxHp);

        return mplew.getPacket();
    }

    public static byte[] getCaptureFlags(MapleMap map) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.CAPTURE_FLAGS);
		mplew.writeRect(map.getArea(0));
        mplew.writeInt(((Point) ((Pair) map.getGuardians().get(0)).left).x);
        mplew.writeInt(((Point) ((Pair) map.getGuardians().get(0)).left).y);
        mplew.writeRect(map.getArea(1));
        mplew.writeInt(((Point) ((Pair) map.getGuardians().get(1)).left).x);
        mplew.writeInt(((Point) ((Pair) map.getGuardians().get(1)).left).y);

        return mplew.getPacket();
    }

    public static byte[] getCapturePosition(MapleMap map) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.CAPTURE_POSITION);
        Point p1 = map.getPointOfItem(2910000);
        Point p2 = map.getPointOfItem(2910001);
		mplew.write(p1 == null ? 0 : 1);
        if (p1 != null) {
            mplew.writeInt(p1.x);
            mplew.writeInt(p1.y);
        }
        mplew.write(p2 == null ? 0 : 1);
        if (p2 != null) {
            mplew.writeInt(p2.x);
            mplew.writeInt(p2.y);
        }

        return mplew.getPacket();
    }

    public static byte[] resetCapture() {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.CAPTURE_RESET);

        return mplew.getPacket();
    }

    public static byte[] getMacros(SkillMacro[] macros) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.SKILL_MACRO);
        int count = 0;
        for (int i = 0; i < 5; i++) {
            if (macros[i] != null) {
                count++;
            }
        }
        mplew.write(count);
        for (int i = 0; i < 5; i++) {
            SkillMacro macro = macros[i];
            if (macro != null) {
                mplew.writeMapleAsciiString(macro.getName());
                mplew.write(macro.getShout());
                mplew.writeInt(macro.getSkill1());
                mplew.writeInt(macro.getSkill2());
                mplew.writeInt(macro.getSkill3());
            }
        }

        return mplew.getPacket();
    }

    public static byte[] gameMsg(String msg) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.GAME_MSG);
		mplew.writeAsciiString(msg);
        mplew.write(1);

        return mplew.getPacket();
    }

    public static byte[] innerPotentialMsg(String msg) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.INNER_ABILITY_MSG);
		mplew.writeMapleAsciiString(msg);

        return mplew.getPacket();
    }

    public static byte[] updateInnerPotential(byte ability, int skill, int level, int rank) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.ENABLE_INNER_ABILITY);
		mplew.write(1); //unlock
        mplew.write(1); //0 = no update
        mplew.writeShort(ability); //1-3
        mplew.writeInt(skill); //skill id (7000000+)
        mplew.writeShort(level); //level, 0 = blank inner ability
        mplew.writeShort(rank); //rank
        mplew.write(1); //0 = no update

        return mplew.getPacket();
    }

    public static byte[] innerPotentialResetMessage() {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.INNER_ABILITY_RESET_MSG);
		mplew.write(HexTool.getByteArrayFromHexString("26 00 49 6E 6E 65 72 20 50 6F 74 65 6E 74 69 61 6C 20 68 61 73 20 62 65 65 6E 20 72 65 63 6F 6E 66 69 67 75 72 65 64 2E 01"));

        return mplew.getPacket();
    }

    public static byte[] updateHonour(int honourLevel, int honourExp, boolean levelup) {
        /*
         * data:
         * 03 00 00 00
         * 69 00 00 00
         * 01
         */
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.UPDATE_HONOUR);
		mplew.writeInt(honourLevel);
        mplew.writeInt(honourExp);
        mplew.write(levelup ? 1 : 0); //shows level up effect

        return mplew.getPacket();
    }

    public static byte[] getCharInfo(MapleCharacter chr) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.WARP_TO_MAP);
		mplew.writeShort(2);
        mplew.writeLong(1L);
        mplew.writeLong(2L);
        mplew.writeInt(chr.getClient().getChannel() - 1);
        mplew.write(0); // bDev
        mplew.writeInt(0); // dwOldDriverId
        mplew.write(1); // bPopupDlg
        mplew.writeInt(0); // skip
        mplew.writeInt(0); // 45 05 00 00 nFieldWidth
        mplew.writeInt(0); // 49 03 00 00 nFieldHeight
        mplew.write(1); // bCharacterData
        mplew.writeShort(0);
        chr.CRand().connectData(mplew);
        PacketHelper.addCharacterInfo(mplew, chr);
        //PacketHelper.addLuckyLogoutInfo(mplew, false, null, null, null);
        mplew.writeZeroBytes(6); //lucky logout + another int
        mplew.writeLong(PacketHelper.getTime(System.currentTimeMillis()));
        mplew.writeInt(100);
        mplew.writeShort(0);
        mplew.write(1);
        mplew.writeZeroBytes(20);
        
        return mplew.getPacket();
    }

    public static byte[] getWarpToMap(MapleMap to, int spawnPoint, MapleCharacter chr) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.WARP_TO_MAP);
		mplew.writeShort(2);
        mplew.writeLong(1L);
        mplew.writeLong(2L);
        mplew.writeLong(chr.getClient().getChannel() - 1);
        mplew.write(0);
        mplew.write(2);//was8
        mplew.writeInt(0);
        mplew.writeInt(0); // 1298
        mplew.writeInt(0); // 330
        mplew.writeInt(0);
        mplew.writeInt(to.getId());
        mplew.write(spawnPoint);
        mplew.writeInt(chr.getStat().getHp());
        mplew.writeShort(0);
        mplew.write(0);
        mplew.writeLong(PacketHelper.getTime(System.currentTimeMillis()));
        mplew.writeInt(100);
        mplew.writeShort(0);//new143
        mplew.write(1);//new143
        mplew.writeZeroBytes(20);
       if (to.getFieldType().equals("63")) {
            mplew.write(0);
        }
       
       return mplew.getPacket();
    }

    public static byte[] removeBGLayer(boolean remove, int map, byte layer, int duration) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.REMOVE_BG_LAYER);
		mplew.write(remove ? 1 : 0); //Boolean show or remove
        mplew.writeInt(map);
        mplew.write(layer); //Layer to show/remove
        mplew.writeInt(duration);

        return mplew.getPacket();
    }

    public static byte[] setMapObjectVisible(List<Pair<String, Byte>> objects) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.SET_MAP_OBJECT_VISIBLE);
		mplew.write(objects.size());
        for (Pair<String, Byte> object : objects) {
            mplew.writeMapleAsciiString(object.getLeft());
            mplew.write(object.getRight());
        }

        return mplew.getPacket();
    }

    public static byte[] spawnFlags(List<Pair<String, Integer>> flags) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.CHANGE_BACKGROUND);
		mplew.write(flags == null ? 0 : flags.size());
        if (flags != null) {
            for (Pair f : flags) {
                mplew.writeMapleAsciiString((String) f.left);
                mplew.write(((Integer) f.right).intValue());
            }
        }

        return mplew.getPacket();
    }

    public static byte[] serverBlocked(int type) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.SERVER_BLOCKED);
		mplew.write(type);

        return mplew.getPacket();
    }

    public static byte[] pvpBlocked(int type) {
        MaplePacketWriter mplew = new MaplePacketWriter();

        mplew.write(type);

        return mplew.getPacket();
    }

    public static byte[] showEquipEffect() {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.SHOW_EQUIP_EFFECT);

        return mplew.getPacket();
    }

    public static byte[] showEquipEffect(int team) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.SHOW_EQUIP_EFFECT);
		mplew.writeShort(team);

        return mplew.getPacket();
    }

    public static byte[] multiChat(String name, String chattext, int mode) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.MULTICHAT);
		mplew.write(mode);
        mplew.writeMapleAsciiString(name);
        mplew.writeMapleAsciiString(chattext);

        return mplew.getPacket();
    }

    public static byte[] getFindReplyWithCS(String target, boolean buddy) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.WHISPER);
		mplew.write(buddy ? 72 : 9);
        mplew.writeMapleAsciiString(target);
        mplew.write(2);
        mplew.writeInt(-1);

        return mplew.getPacket();
    }

    public static byte[] getWhisper(String sender, int channel, String text) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.WHISPER);
		mplew.write(18);
        mplew.writeMapleAsciiString(sender);
        mplew.writeShort(channel - 1);
        mplew.writeMapleAsciiString(text);

        return mplew.getPacket();
    }

    public static byte[] getWhisperReply(String target, byte reply) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.WHISPER);
		mplew.write(10);
        mplew.writeMapleAsciiString(target);
        mplew.write(reply);

        return mplew.getPacket();
    }

    public static byte[] getFindReplyWithMap(String target, int mapid, boolean buddy) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.WHISPER);
		mplew.write(buddy ? 72 : 9);
        mplew.writeMapleAsciiString(target);
        mplew.write(3);//was1
        mplew.writeInt(mapid);//mapid);
//        mplew.writeZeroBytes(8);

        return mplew.getPacket();
    }

    public static byte[] getFindReply(String target, int channel, boolean buddy) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.WHISPER);
		mplew.write(buddy ? 72 : 9);
        mplew.writeMapleAsciiString(target);
        mplew.write(3);
        mplew.writeInt(channel - 1);

        return mplew.getPacket();
    }

    public static byte[] MapEff(String path) {
        return environmentChange(path, 4);//was 3
    }

    public static byte[] MapNameDisplay(int mapid) {
        return environmentChange("maplemap/enter/" + mapid, 4);
    }

    public static byte[] Aran_Start() {
        return environmentChange("Aran/balloon", 4);
    }

    public static byte[] musicChange(String song) {
        return environmentChange(song, 7);//was 6
    }

    public static byte[] showEffect(String effect) {
        return environmentChange(effect, 4);//was 3
    }

    public static byte[] playSound(String sound) {
        return environmentChange(sound, 5);//was 4
    }

    public static byte[] environmentChange(String env, int mode) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.BOSS_ENV);
		mplew.write(mode);
        mplew.writeMapleAsciiString(env);
        mplew.writeInt(0);

        return mplew.getPacket();
    }

    public static byte[] trembleEffect(int type, int delay) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.BOSS_ENV);
		mplew.write(1);
        mplew.write(type);
        mplew.writeInt(delay);
        mplew.writeShort(30);
        // mplew.writeInt(0);

        return mplew.getPacket();
    }

    public static byte[] environmentMove(String env, int mode) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.MOVE_ENV);
		mplew.writeMapleAsciiString(env);
        mplew.writeInt(mode);

        return mplew.getPacket();
    }

    public static byte[] getUpdateEnvironment(MapleMap map) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.UPDATE_ENV);
		mplew.writeInt(map.getEnvironment().size());
        for (Map.Entry mp : map.getEnvironment().entrySet()) {
            mplew.writeMapleAsciiString((String) mp.getKey());
            mplew.writeInt(((Integer) mp.getValue()).intValue());
        }

        return mplew.getPacket();
    }

    public static byte[] startMapEffect(String msg, int itemid, boolean active) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.MAP_EFFECT);
        //mplew.write(active ? 0 : 1);

        mplew.writeInt(itemid);
        if (active) {
            mplew.writeMapleAsciiString(msg);
        }
        return mplew.getPacket();
    }

    public static byte[] removeMapEffect() {
        return startMapEffect(null, 0, false);
    }

    public static byte[] getGMEffect(int value, int mode) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.GM_EFFECT);
		mplew.write(value);
        mplew.writeZeroBytes(17);

        return mplew.getPacket();
    }

    public static byte[] showOXQuiz(int questionSet, int questionId, boolean askQuestion) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.OX_QUIZ);
		mplew.write(askQuestion ? 1 : 0);
        mplew.write(questionSet);
        mplew.writeShort(questionId);

        return mplew.getPacket();
    }

    public static byte[] showEventInstructions() {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.GMEVENT_INSTRUCTIONS);
		mplew.write(0);

        return mplew.getPacket();
    }

    public static byte[] getPVPClock(int type, int time) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.CLOCK);
		mplew.write(3);
        mplew.write(type);
        mplew.writeInt(time);

        return mplew.getPacket();
    }
    
        public static byte[] getBanBanClock(int time, int direction) {
        MaplePacketWriter outPacket = new MaplePacketWriter(SendPacketOpcode.CLOCK);
        outPacket.write(5);
        outPacket.write(direction); //0:?????? 1:????
        outPacket.writeInt(time);
        return outPacket.getPacket();
    }

    public static byte[] getClock(int time) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.CLOCK);
		mplew.write(2);
        mplew.writeInt(time);

        return mplew.getPacket();
    }

    public static byte[] getClockTime(int hour, int min, int sec) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.CLOCK);
		mplew.write(1);
        mplew.write(hour);
        mplew.write(min);
        mplew.write(sec);

        return mplew.getPacket();
    }

    public static byte[] boatPacket(int effect, int mode) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.BOAT_MOVE);
		mplew.write(effect);
        mplew.write(mode);

        return mplew.getPacket();
    }

    public static byte[] setBoatState(int effect) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.BOAT_STATE);
		mplew.write(effect);
        mplew.write(0);

        return mplew.getPacket();
    }

    public static byte[] stopClock() {
        return getPacketFromHexString(Integer.toHexString(SendPacketOpcode.STOP_CLOCK.getOpcode()) + " 00");
    }

    public static byte[] showAriantScoreBoard() {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.ARIANT_SCOREBOARD);

        return mplew.getPacket();
    }

    public static byte[] sendPyramidUpdate(int amount) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.PYRAMID_UPDATE);
		mplew.writeInt(amount);

        return mplew.getPacket();
    }

    public static byte[] sendPyramidResult(byte rank, int amount) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.PYRAMID_RESULT);
		mplew.write(rank);
        mplew.writeInt(amount);

        return mplew.getPacket();
    }

    public static byte[] quickSlot(String skil) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.QUICK_SLOT);
		mplew.write(skil == null ? 0 : 1);
        if (skil != null) {
            String[] slots = skil.split(",");
            for (int i = 0; i < 8; i++) {
                mplew.writeInt(Integer.parseInt(slots[i]));
            }
        }

        return mplew.getPacket();
    }

    public static byte[] getMovingPlatforms(MapleMap map) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.MOVE_PLATFORM);
		mplew.writeInt(map.getPlatforms().size());
        for (MapleNodes.MaplePlatform mp : map.getPlatforms()) {
            mplew.writeMapleAsciiString(mp.name);
            mplew.writeInt(mp.start);
            mplew.writeInt(mp.SN.size());
            for (int x = 0; x < mp.SN.size(); x++) {
                mplew.writeInt((mp.SN.get(x)).intValue());
            }
            mplew.writeInt(mp.speed);
            mplew.writeInt(mp.x1);
            mplew.writeInt(mp.x2);
            mplew.writeInt(mp.y1);
            mplew.writeInt(mp.y2);
            mplew.writeInt(mp.x1);
            mplew.writeInt(mp.y1);
            mplew.writeShort(mp.r);
        }

        return mplew.getPacket();
    }

    public static byte[] sendPyramidKills(int amount) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.PYRAMID_KILL_COUNT);
		mplew.writeInt(amount);

        return mplew.getPacket();
    }

    public static byte[] sendPVPMaps() {
        final MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.PVP_INFO);
		mplew.write(3); //max amount of players
        for (int i = 0; i < 20; i++) {
            mplew.writeInt(10); //how many peoples in each map
        }
        mplew.writeZeroBytes(124);
        mplew.writeShort(150); ////PVP 1.5 EVENT!
        mplew.write(0);

        return mplew.getPacket();
    }

    public static byte[] gainForce(int oid, int count, int color) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.GAIN_FORCE);
		mplew.write(1); // 0 = remote user?
        mplew.writeInt(oid);
        byte newcheck = 0;
        mplew.writeInt(newcheck); //unk
        if (newcheck > 0) {
            mplew.writeInt(0); //unk
            mplew.writeInt(0); //unk
        }
        mplew.write(0);
        mplew.writeInt(4); // size, for each below
        mplew.writeInt(count); //count
        mplew.writeInt(color); //color, 1-10 for demon, 1-2 for phantom
        mplew.writeInt(0); //unk
        mplew.writeInt(0); //unk
        return mplew.getPacket();
    }

    public static byte[] getAndroidTalkStyle(int npc, String talk, int... args) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.NPC_TALK);
		mplew.write(4);
        mplew.writeInt(npc);
        mplew.writeShort(10);
        mplew.writeMapleAsciiString(talk);
        mplew.write(args.length);

        for (int i = 0; i < args.length; i++) {
            mplew.writeInt(args[i]);
        }
        return mplew.getPacket();
    }
    
    public static byte[] achievementRatio(int amount) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.ACHIEVEMENT_RATIO);
		mplew.writeInt(amount);

        return mplew.getPacket();
    }

    public static byte[] getQuickMoveInfo(boolean show, List<QuickMoveNPC> qm) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.QUICK_MOVE);
		mplew.write(qm.size() <= 0 ? 0 : show ? qm.size() : 0);
        if (show && qm.size() > 0) {
            for (QuickMoveNPC qmn : qm) {
                mplew.writeInt(0);
                mplew.writeInt(qmn.getId());
                mplew.writeInt(qmn.getType());
                mplew.writeInt(qmn.getLevel());
                mplew.writeMapleAsciiString(qmn.getDescription());
                mplew.writeLong(PacketHelper.getTime(-2));
                mplew.writeLong(PacketHelper.getTime(-1));
            }
        }

        return mplew.getPacket();
    }
    
    public static byte[] differentIP(int minutesLeft) {
    	MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.DIFFERENT_IP);
		mplew.write(1); // Boolean
    	mplew.writeInt(minutesLeft);
    	
    	return mplew.getPacket();
    }

    /**
     * Note: To find MAX_BUFFSTAT, in an IDA go to 
     * CField::OnPacket -> CUserPool::OnUserEnterField -> 
     * CUserRemote::Init -> SecondaryStat::DecodeForRemote.
     * Find the first CInPacket::DecodeBuffer and the 
     * (field length) = MAX_BUFFSTAT * 4
     * @param chr
     * @return
     */
    public static byte[] spawnPlayerMapobject(MapleCharacter chr) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.SPAWN_PLAYER);
		mplew.writeInt(chr.getId());
        mplew.write(chr.getLevel());
        mplew.writeMapleAsciiString(chr.getName());
        MapleQuestStatus ultExplorer = chr.getQuestNoAdd(MapleQuest.getInstance(111111));
        if ((ultExplorer != null) && (ultExplorer.getCustomData() != null)) {
            mplew.writeMapleAsciiString(ultExplorer.getCustomData());
        } else {
            mplew.writeMapleAsciiString("");
        }
        if (chr.getGuildId() <= 0) {
            mplew.writeZeroBytes(8);
        } else {
            MapleGuild gs = World.Guild.getGuild(chr.getGuildId());
            if (gs != null) {
                mplew.writeMapleAsciiString(gs.getName());
                mplew.writeShort(gs.getLogoBG());
                mplew.write(gs.getLogoBGColor());
                mplew.writeShort(gs.getLogo());
                mplew.write(gs.getLogoColor());
            } else {
                mplew.writeZeroBytes(8);
            }
        }
        mplew.write(chr.getGender());
        mplew.writeInt(chr.getFame());
        mplew.writeInt(chr.getClient().getFarm().getLevel());
        mplew.writeInt(0); // nNameTagMark 
        
        final List<Pair<Integer, Integer>> buffvalue = new ArrayList<>();
        final List<Pair<Integer, Integer>> buffvaluenew = new ArrayList<>();
        int[] mask = new int[GameConstants.MAX_BUFFSTAT];
        
        mask[7] |= 0x4000000;
        mask[7] |= 0x2000000;
        
        mask[10] |= 0x80;
        mask[10] |= 0x40;
        
        mask[12] |= 0x80000000;
        mask[12] |= 0x8000;
        mask[12] |= 0x2000;
        mask[12] |= 0x100;
        
        mask[14] |= 0x2000;
        mask[14] |= 0x1000;
        mask[14] |= 0xF00;
        mask[14] |= 0x80;
        
        if ((chr.getBuffedValue(MapleBuffStat.DARKSIGHT) != null) || (chr.isHidden())) {
            mask[MapleBuffStat.DARKSIGHT.getPosition(true)] |= MapleBuffStat.DARKSIGHT.getValue();
        }
        if (chr.getBuffedValue(MapleBuffStat.SOULARROW) != null) {
            mask[MapleBuffStat.SOULARROW.getPosition(true)] |= MapleBuffStat.SOULARROW.getValue();
        }
        if (chr.getBuffedValue(MapleBuffStat.DAMAGE_ABSORBED) != null) {
            mask[MapleBuffStat.DAMAGE_ABSORBED.getPosition(true)] |= MapleBuffStat.DAMAGE_ABSORBED.getValue();
            buffvaluenew.add(new Pair(Integer.valueOf(1000), Integer.valueOf(2)));
            buffvaluenew.add(new Pair(Integer.valueOf(chr.getTrueBuffSource(MapleBuffStat.DAMAGE_ABSORBED)), Integer.valueOf(4)));
            buffvaluenew.add(new Pair(Integer.valueOf(9), Integer.valueOf(0)));
        }
        if (chr.getBuffedValue(MapleBuffStat.TEMPEST_BLADES) != null) {
            mask[MapleBuffStat.TEMPEST_BLADES.getPosition(true)] |= MapleBuffStat.TEMPEST_BLADES.getValue();
            buffvaluenew.add(new Pair(Integer.valueOf(chr.getTotalSkillLevel(chr.getTrueBuffSource(MapleBuffStat.TEMPEST_BLADES))), Integer.valueOf(2)));
            buffvaluenew.add(new Pair(Integer.valueOf(chr.getTrueBuffSource(MapleBuffStat.TEMPEST_BLADES)), Integer.valueOf(4)));
            buffvaluenew.add(new Pair(Integer.valueOf(5), Integer.valueOf(0)));
            buffvaluenew.add(new Pair(Integer.valueOf(chr.getTrueBuffSource(MapleBuffStat.TEMPEST_BLADES) == 61101002 ? 1 : 2), Integer.valueOf(4)));
            buffvaluenew.add(new Pair(Integer.valueOf(chr.getTrueBuffSource(MapleBuffStat.TEMPEST_BLADES) == 61101002 ? 3 : 5), Integer.valueOf(4)));
            buffvaluenew.add(new Pair(Integer.valueOf(chr.getBuffedValue(MapleBuffStat.TEMPEST_BLADES).intValue()), Integer.valueOf(4)));
            buffvaluenew.add(new Pair(Integer.valueOf(chr.getTrueBuffSource(MapleBuffStat.TEMPEST_BLADES) == 61101002 ? 3 : 5), Integer.valueOf(4)));
            if (chr.getTrueBuffSource(MapleBuffStat.TEMPEST_BLADES) != 61101002) {
                buffvaluenew.add(new Pair(Integer.valueOf(8), Integer.valueOf(0)));
            }
        }
        if ((chr.getBuffedValue(MapleBuffStat.COMBO) != null) && (chr.getBuffedValue(MapleBuffStat.TEMPEST_BLADES) == null)) {
            mask[MapleBuffStat.COMBO.getPosition(true)] |= MapleBuffStat.COMBO.getValue();
            buffvalue.add(new Pair(Integer.valueOf(chr.getBuffedValue(MapleBuffStat.COMBO).intValue()), Integer.valueOf(1)));
        }
        if (chr.getBuffedValue(MapleBuffStat.WK_CHARGE) != null) {
            mask[MapleBuffStat.WK_CHARGE.getPosition(true)] |= MapleBuffStat.WK_CHARGE.getValue();
            buffvalue.add(new Pair(Integer.valueOf(chr.getBuffedValue(MapleBuffStat.WK_CHARGE).intValue()), Integer.valueOf(2)));
            buffvalue.add(new Pair(Integer.valueOf(chr.getBuffSource(MapleBuffStat.WK_CHARGE)), Integer.valueOf(3)));
        }
        if ((chr.getBuffedValue(MapleBuffStat.SHADOWPARTNER) != null) && (chr.getBuffedValue(MapleBuffStat.TEMPEST_BLADES) == null)) {
            mask[MapleBuffStat.SHADOWPARTNER.getPosition(true)] |= MapleBuffStat.SHADOWPARTNER.getValue();
            buffvalue.add(new Pair(Integer.valueOf(chr.getBuffedValue(MapleBuffStat.SHADOWPARTNER).intValue()), Integer.valueOf(2)));
            buffvalue.add(new Pair(Integer.valueOf(chr.getBuffSource(MapleBuffStat.SHADOWPARTNER)), Integer.valueOf(3)));
        }
        //if ((chr.getBuffedValue(MapleBuffStat.MORPH) != null) && (chr.getBuffedValue(MapleBuffStat.TEMPEST_BLADES) == null)) {//TODO
        //    mask[MapleBuffStat.MORPH.getPosition(true)] |= MapleBuffStat.MORPH.getValue();
        //    buffvalue.add(new Pair(Integer.valueOf(chr.getStatForBuff(MapleBuffStat.MORPH).getMorph(chr)), Integer.valueOf(2)));
        //    buffvalue.add(new Pair(Integer.valueOf(chr.getBuffSource(MapleBuffStat.MORPH)), Integer.valueOf(3)));
        //}
        if (chr.getBuffedValue(MapleBuffStat.BERSERK_FURY) != null) {//works
            mask[MapleBuffStat.BERSERK_FURY.getPosition(true)] |= MapleBuffStat.BERSERK_FURY.getValue();
        }
        if (chr.getBuffedValue(MapleBuffStat.DIVINE_BODY) != null) {
            mask[MapleBuffStat.DIVINE_BODY.getPosition(true)] |= MapleBuffStat.DIVINE_BODY.getValue();
        }
        if (chr.getBuffedValue(MapleBuffStat.WIND_WALK) != null) {//TODO better
            mask[MapleBuffStat.WIND_WALK.getPosition(true)] |= MapleBuffStat.WIND_WALK.getValue();
            buffvalue.add(new Pair(Integer.valueOf(chr.getBuffedValue(MapleBuffStat.WIND_WALK).intValue()), Integer.valueOf(2)));
            buffvalue.add(new Pair(Integer.valueOf(chr.getTrueBuffSource(MapleBuffStat.WIND_WALK)), Integer.valueOf(3)));
        }
        if (chr.getBuffedValue(MapleBuffStat.PYRAMID_PQ) != null) {//TODO
            mask[MapleBuffStat.PYRAMID_PQ.getPosition(true)] |= MapleBuffStat.PYRAMID_PQ.getValue();
            buffvalue.add(new Pair(Integer.valueOf(chr.getBuffedValue(MapleBuffStat.PYRAMID_PQ).intValue()), Integer.valueOf(2)));
            buffvalue.add(new Pair(Integer.valueOf(chr.getTrueBuffSource(MapleBuffStat.PYRAMID_PQ)), Integer.valueOf(3)));
        }
        if (chr.getBuffedValue(MapleBuffStat.SOARING) != null) {//TODO
            mask[MapleBuffStat.SOARING.getPosition(true)] |= MapleBuffStat.SOARING.getValue();
            buffvalue.add(new Pair(Integer.valueOf(chr.getBuffedValue(MapleBuffStat.SOARING).intValue()), Integer.valueOf(1)));
        }
//        if (chr.getBuffedValue(MapleBuffStat.OWL_SPIRIT) != null) {//TODO
//            mask[MapleBuffStat.OWL_SPIRIT.getPosition(true)] |= MapleBuffStat.OWL_SPIRIT.getValue();
//            buffvalue.add(new Pair(Integer.valueOf(chr.getBuffedValue(MapleBuffStat.OWL_SPIRIT).intValue()), Integer.valueOf(2)));
//            buffvalue.add(new Pair(Integer.valueOf(chr.getTrueBuffSource(MapleBuffStat.OWL_SPIRIT)), Integer.valueOf(3)));
//        }
        if (chr.getBuffedValue(MapleBuffStat.FINAL_CUT) != null) {
            mask[MapleBuffStat.FINAL_CUT.getPosition(true)] |= MapleBuffStat.FINAL_CUT.getValue();
            buffvalue.add(new Pair(Integer.valueOf(chr.getBuffedValue(MapleBuffStat.FINAL_CUT).intValue()), Integer.valueOf(2)));
            buffvalue.add(new Pair(Integer.valueOf(chr.getTrueBuffSource(MapleBuffStat.FINAL_CUT)), Integer.valueOf(3)));
        }

        if (chr.getBuffedValue(MapleBuffStat.TORNADO) != null) {
            mask[MapleBuffStat.TORNADO.getPosition(true)] |= MapleBuffStat.TORNADO.getValue();
            buffvalue.add(new Pair(Integer.valueOf(chr.getBuffedValue(MapleBuffStat.TORNADO).intValue()), Integer.valueOf(2)));
            buffvalue.add(new Pair(Integer.valueOf(chr.getTrueBuffSource(MapleBuffStat.TORNADO)), Integer.valueOf(3)));
        }
        if (chr.getBuffedValue(MapleBuffStat.INFILTRATE) != null) {
            mask[MapleBuffStat.INFILTRATE.getPosition(true)] |= MapleBuffStat.INFILTRATE.getValue();
        }
        if (chr.getBuffedValue(MapleBuffStat.MECH_CHANGE) != null) {
            mask[MapleBuffStat.MECH_CHANGE.getPosition(true)] |= MapleBuffStat.MECH_CHANGE.getValue();
            buffvalue.add(new Pair(Integer.valueOf(chr.getBuffedValue(MapleBuffStat.MECH_CHANGE).intValue()), Integer.valueOf(2)));
            buffvalue.add(new Pair(Integer.valueOf(chr.getTrueBuffSource(MapleBuffStat.MECH_CHANGE)), Integer.valueOf(3)));
        }
        if (chr.getBuffedValue(MapleBuffStat.DARK_AURA) != null) {
            mask[MapleBuffStat.DARK_AURA.getPosition(true)] |= MapleBuffStat.DARK_AURA.getValue();
            buffvalue.add(new Pair(Integer.valueOf(chr.getBuffedValue(MapleBuffStat.DARK_AURA).intValue()), Integer.valueOf(2)));
            buffvalue.add(new Pair(Integer.valueOf(chr.getTrueBuffSource(MapleBuffStat.DARK_AURA)), Integer.valueOf(3)));
        }
        if (chr.getBuffedValue(MapleBuffStat.BLUE_AURA) != null) {
            mask[MapleBuffStat.BLUE_AURA.getPosition(true)] |= MapleBuffStat.BLUE_AURA.getValue();
            buffvalue.add(new Pair(Integer.valueOf(chr.getBuffedValue(MapleBuffStat.BLUE_AURA).intValue()), Integer.valueOf(2)));
            buffvalue.add(new Pair(Integer.valueOf(chr.getTrueBuffSource(MapleBuffStat.BLUE_AURA)), Integer.valueOf(3)));
        }
        if (chr.getBuffedValue(MapleBuffStat.YELLOW_AURA) != null) {
            mask[MapleBuffStat.YELLOW_AURA.getPosition(true)] |= MapleBuffStat.YELLOW_AURA.getValue();
            buffvalue.add(new Pair(Integer.valueOf(chr.getBuffedValue(MapleBuffStat.YELLOW_AURA).intValue()), Integer.valueOf(2)));
            buffvalue.add(new Pair(Integer.valueOf(chr.getTrueBuffSource(MapleBuffStat.YELLOW_AURA)), Integer.valueOf(3)));
        }
        if ((chr.getBuffedValue(MapleBuffStat.WATER_SHIELD) != null) && (chr.getBuffedValue(MapleBuffStat.TEMPEST_BLADES) == null)) {
            mask[MapleBuffStat.WATER_SHIELD.getPosition(true)] |= MapleBuffStat.WATER_SHIELD.getValue();
            buffvaluenew.add(new Pair(Integer.valueOf(chr.getTotalSkillLevel(chr.getTrueBuffSource(MapleBuffStat.WATER_SHIELD))), Integer.valueOf(2)));
            buffvaluenew.add(new Pair(Integer.valueOf(chr.getTrueBuffSource(MapleBuffStat.WATER_SHIELD)), Integer.valueOf(4)));
            buffvaluenew.add(new Pair(Integer.valueOf(9), Integer.valueOf(0)));
        }
        if (chr.getBuffedValue(MapleBuffStat.GIANT_POTION) != null) {
            mask[MapleBuffStat.GIANT_POTION.getPosition(true)] |= MapleBuffStat.GIANT_POTION.getValue();
            buffvalue.add(new Pair(Integer.valueOf(chr.getBuffedValue(MapleBuffStat.GIANT_POTION).intValue()), Integer.valueOf(2)));
            buffvalue.add(new Pair(Integer.valueOf(chr.getTrueBuffSource(MapleBuffStat.GIANT_POTION)), Integer.valueOf(3)));
        }

        for (int i = 0; i < mask.length; i++) {
            mplew.writeInt(mask[i]);
        }
        for (Pair i : buffvalue) {
            if (((Integer) i.right).intValue() == 3) {
                mplew.writeInt(((Integer) i.left).intValue());
            } else if (((Integer) i.right).intValue() == 2) {
                mplew.writeShort(((Integer) i.left).shortValue());
            } else if (((Integer) i.right).intValue() == 1) {
                mplew.write(((Integer) i.left).byteValue());
            }
        }
        mplew.writeInt(-1);
        if (buffvaluenew.isEmpty()) {
            mplew.writeZeroBytes(10);
        } else {
            mplew.write(0);
            for (Pair i : buffvaluenew) {
                if (((Integer) i.right).intValue() == 4) {
                    mplew.writeInt(((Integer) i.left).intValue());
                } else if (((Integer) i.right).intValue() == 2) {
                    mplew.writeShort(((Integer) i.left).shortValue());
                } else if (((Integer) i.right).intValue() == 1) {
                    mplew.write(((Integer) i.left).byteValue());
                } else if (((Integer) i.right).intValue() == 0) {
                    mplew.writeZeroBytes(((Integer) i.left).intValue());
                }
            }
        }
        mplew.writeZeroBytes(64); // v171 

        int CHAR_MAGIC_SPAWN = Randomizer.nextInt();
        mplew.write(1);
        mplew.writeInt(CHAR_MAGIC_SPAWN);
        mplew.writeZeroBytes(8); //v143 10->8
        mplew.write(1);
        mplew.writeInt(CHAR_MAGIC_SPAWN);
        mplew.writeZeroBytes(10);
        mplew.write(1);
        mplew.writeInt(CHAR_MAGIC_SPAWN);
        mplew.writeShort(0);
        int buffSrc = chr.getBuffSource(MapleBuffStat.MONSTER_RIDING);
        if (buffSrc > 0) {
            Item c_mount = chr.getInventory(MapleInventoryType.EQUIPPED).getItem((short) -118);
            Item mount = chr.getInventory(MapleInventoryType.EQUIPPED).getItem((short) -18);
            if ((GameConstants.getMountItem(buffSrc, chr) == 0) && (c_mount != null) && (chr.getInventory(MapleInventoryType.EQUIPPED).getItem((short) -119) != null)) {
                mplew.writeInt(c_mount.getItemId());
            } else if ((GameConstants.getMountItem(buffSrc, chr) == 0) && (mount != null) && (chr.getInventory(MapleInventoryType.EQUIPPED).getItem((short) -19) != null)) {
                mplew.writeInt(mount.getItemId());
            } else {
                mplew.writeInt(GameConstants.getMountItem(buffSrc, chr));
            }
            mplew.writeInt(buffSrc);
        } else {
            mplew.writeLong(0L);
        }
        mplew.write(1);
        mplew.writeInt(CHAR_MAGIC_SPAWN);
        mplew.writeLong(0L);
        mplew.write(1);
        mplew.writeInt(CHAR_MAGIC_SPAWN);
        mplew.write(0);// For zero?
        mplew.write(GameConstants.isZero(chr.getJob()) || GameConstants.isEvan(chr.getJob()) ? 0 : 1); //Shows the dragon in inventory it seems
        mplew.writeZeroBytes(13);// There must be something in here... But what?...
        mplew.write(1);
        mplew.writeInt(CHAR_MAGIC_SPAWN);
        mplew.writeZeroBytes(16);
        mplew.write(1);
        mplew.writeInt(CHAR_MAGIC_SPAWN);
        mplew.writeShort(0);

        mplew.writeShort(chr.getJob());
        mplew.writeShort(chr.getSubcategory());
        mplew.writeInt(0); // nTotalCHUC
        PacketHelper.addCharLook(mplew, chr, true, false);
        if (GameConstants.isZero(chr.getJob())) {
            PacketHelper.addCharLook(mplew, chr, true, false);
        }

        mplew.writeInt(0); // dwDriverID
        mplew.writeInt(0); // dwPassengerID

        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.writeInt(0);
        
        mplew.writeInt(Math.min(250, chr.getInventory(MapleInventoryType.CASH).countById(5110000))); //Valentine Effect
        mplew.writeInt(chr.getItemEffect()); // nActiveEffectItemID
        mplew.writeInt(0); // nMonkeyEffectItemID

        
        MapleQuestStatus stat = chr.getQuestNoAdd(MapleQuest.getInstance(124000));
        mplew.writeInt(stat != null && stat.getCustomData() != null ? Integer.parseInt(stat.getCustomData()) : 0); //title //head title? chr.getHeadTitle()
        mplew.writeInt(chr.getDamageSkin()); // nDamageSkin
        mplew.writeInt(0); // ptPos.x
        mplew.writeInt(0); // nDemonWingID
        mplew.writeInt(0); // nKaiserWingID
        mplew.writeInt(0); // nKaiserTailID
        mplew.writeInt(0); // nCompleteSetID
        mplew.writeShort(-1); // nFieldSeatID
        mplew.writeInt(GameConstants.getInventoryType(chr.getChair()) == MapleInventoryType.SETUP ? chr.getChair() : 0); // nPortableChairID
        mplew.writeInt(0);
        mplew.writeInt(0);
        
        mplew.writePos(chr.getTruePosition());
        mplew.write(chr.getStance());
        mplew.writeShort(chr.getFH());
        mplew.writeZeroBytes(3);

        mplew.write(1);
        mplew.write(0);

        mplew.writeInt(chr.getMount().getLevel());
        mplew.writeInt(chr.getMount().getExp());
        mplew.writeInt(chr.getMount().getFatigue());

        PacketHelper.addAnnounceBox(mplew, chr);
        mplew.write((chr.getChalkboard() != null) && (chr.getChalkboard().length() > 0) ? 1 : 0);
        
       /* if (GameConstants.isKaiser(chr.getJob())) { //doesn't do shit?
            mplew.writeShort(0);
            mplew.write(0);
            mplew.writeInt(1);
            mplew.writeShort(0);
        }*/
        
        if ((chr.getChalkboard() != null) && (chr.getChalkboard().length() > 0)) {
            mplew.writeMapleAsciiString(chr.getChalkboard());
        }

        Triple rings = chr.getRings(false);
        addRingInfo(mplew, (List) rings.getLeft());
        addRingInfo(mplew, (List) rings.getMid());
        addMRingInfo(mplew, (List) rings.getRight(), chr);

        mplew.write(chr.getStat().Berserk ? 1 : 0); //mask
        mplew.writeInt(0);

        if (GameConstants.isKaiser(chr.getJob())) {
            String x = chr.getOneInfo(12860, "extern");
            mplew.writeInt(x == null ? 0 : Integer.parseInt(x));
            x = chr.getOneInfo(12860, "inner");
            mplew.writeInt(x == null ? 0 : Integer.parseInt(x));
            x = chr.getOneInfo(12860, "primium");
            mplew.write(x == null ? 0 : Integer.parseInt(x));
        }

        mplew.write(0); //new v142->v143
        mplew.writeInt(0); //new v142->v143

        PacketHelper.addFarmInfo(mplew, chr.getClient(), 0);
        for (int i = 0; i < 5; i++) {
            mplew.write(-1);
        }

        mplew.writeInt(0);
        mplew.write(1);
        mplew.writeInt(0);
        mplew.writeInt(0); //v145
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.write(1);
        mplew.writeZeroBytes(26);
        
        return mplew.getPacket();
    }

    public static byte[] removePlayerFromMap(int charid) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.REMOVE_PLAYER_FROM_MAP);
		mplew.writeInt(charid);

        return mplew.getPacket();
    }

    public static byte[] getChatText(int cidfrom, String text, boolean whiteBG, int show) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.CHATTEXT);
		mplew.writeInt(cidfrom);
        mplew.write(whiteBG ? 1 : 0);
        mplew.writeMapleAsciiString(text);
        mplew.write(show);
        mplew.write(0);
        mplew.write(-1);

        return mplew.getPacket();
    }

    public static byte[] getScrollEffect(int chr, Equip.ScrollResult scrollSuccess, boolean legendarySpirit, int item, int scroll) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.SHOW_SCROLL_EFFECT);
		mplew.writeInt(chr);
        mplew.write(scrollSuccess == Equip.ScrollResult.SUCCESS ? 1 : scrollSuccess == Equip.ScrollResult.CURSE ? 2 : 0);
        mplew.write(legendarySpirit ? 1 : 0);
        mplew.writeInt(scroll); // scroll
        mplew.writeInt(item); // item
        mplew.writeInt(0);
        mplew.write(0);
        mplew.write(0);


        return mplew.getPacket();
    }

    public static byte[] showMagnifyingEffect(int chr, short pos) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.SHOW_MAGNIFYING_EFFECT);
		mplew.writeInt(chr);
        mplew.writeShort(pos);
        mplew.write(0);//new 143 is in ida?

        return mplew.getPacket();
    }

    public static byte[] showPotentialReset(int chr, boolean success, int itemid) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.SHOW_POTENTIAL_RESET);
		mplew.writeInt(chr);
        mplew.write(success ? 1 : 0);
        mplew.writeInt(itemid);

        /*
         if (!succes) {
         if (itemid / 100 == 20495 || itemid == 5062301) {//lol the itemid doesn't even exists yet.
         'Failed to expand Potential slots.'
         } else {
         'Resetting Potential has failed due to insufficient space in the Use item.'
         }
         } else {
         if (itemid / 100 == 20495 || itemid == 5062301) {//lol the itemid doesn't even exists yet.
         'Successfully expanded Potential slots.'
         } else {
         if (itemid != 2710000) {
         'Potential has been reset.\r\nYou've obtained: %s.' (%s is item name)
         }
         'Potential has been reset.'
         }
         }
         */
        return mplew.getPacket();
    }

    public static byte[] showNebuliteEffect(int chr, boolean success) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.SHOW_NEBULITE_EFFECT);
		mplew.writeInt(chr);
        mplew.write(success ? 1 : 0);
        mplew.writeMapleAsciiString(success ? "Successfully mounted Nebulite." : "Failed to mount Nebulite.");

        return mplew.getPacket();
    }

    public static byte[] useNebuliteFusion(int cid, int itemId, boolean success) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.SHOW_FUSION_EFFECT);
		mplew.writeInt(cid);
        mplew.write(success ? 1 : 0);
        mplew.writeInt(itemId);

        return mplew.getPacket();
    }

    public static byte[] pvpAttack(int cid, int playerLevel, int skill, int skillLevel, int speed, int mastery, int projectile, int attackCount, int chargeTime, int stance, int direction, int range, int linkSkill, int linkSkillLevel, boolean movementSkill, boolean pushTarget, boolean pullTarget, List<AttackPair> attack) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.PVP_ATTACK);
		mplew.writeInt(cid);
        mplew.write(playerLevel);
        mplew.writeInt(skill);
        mplew.write(skillLevel);
        mplew.writeInt(linkSkill != skill ? linkSkill : 0);
        mplew.write(linkSkillLevel != skillLevel ? linkSkillLevel : 0);
        mplew.write(direction);
        mplew.write(movementSkill ? 1 : 0);
        mplew.write(pushTarget ? 1 : 0);
        mplew.write(pullTarget ? 1 : 0);
        mplew.write(0);
        mplew.writeShort(stance);
        mplew.write(speed);
        mplew.write(mastery);
        mplew.writeInt(projectile);
        mplew.writeInt(chargeTime);
        mplew.writeInt(range);
        mplew.write(attack.size());
        mplew.write(0);
        mplew.writeInt(0);
        mplew.write(attackCount);
        mplew.write(0);
        for (AttackPair p : attack) {
            mplew.writeInt(p.objectid);
            mplew.writeInt(0);
            mplew.writePos(p.point);
            mplew.write(0);
            mplew.writeInt(0);
            for (Pair atk : p.attack) {
                mplew.writeInt(((Integer) atk.left).intValue());
                mplew.writeInt(0);
                mplew.write(((Boolean) atk.right).booleanValue() ? 1 : 0);
                mplew.writeShort(0);
            }
        }

        return mplew.getPacket();
    }

    public static byte[] getPVPMist(int cid, int mistSkill, int mistLevel, int damage) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.PVP_MIST);
		mplew.writeInt(cid);
        mplew.writeInt(mistSkill);
        mplew.write(mistLevel);
        mplew.writeInt(damage);
        mplew.write(8);
        mplew.writeInt(1000);

        return mplew.getPacket();
    }

    public static byte[] pvpCool(int cid, List<Integer> attack) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.PVP_COOL);
		mplew.writeInt(cid);
        mplew.write(attack.size());
        for (Iterator i$ = attack.iterator(); i$.hasNext();) {
            int b = ((Integer) i$.next()).intValue();
            mplew.writeInt(b);
        }

        return mplew.getPacket();
    }

    public static byte[] teslaTriangle(int cid, int sum1, int sum2, int sum3) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.TESLA_TRIANGLE);
		mplew.writeInt(cid);
        mplew.writeInt(sum1);
        mplew.writeInt(sum2);
        mplew.writeInt(sum3);

         mplew.writeZeroBytes(69);//test
        
        return mplew.getPacket();
    }

    public static byte[] followEffect(int initiator, int replier, Point toMap) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.FOLLOW_EFFECT);
		mplew.writeInt(initiator);
        mplew.writeInt(replier);
        mplew.writeLong(0);
        if (replier == 0) {
            mplew.write(toMap == null ? 0 : 1);
            if (toMap != null) {
                mplew.writeInt(toMap.x);
                mplew.writeInt(toMap.y);
            }
        }

        return mplew.getPacket();
    }

    public static byte[] showPQReward(int cid) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.SHOW_PQ_REWARD);
		mplew.writeInt(cid);
        for (int i = 0; i < 6; i++) {
            mplew.write(0);
        }

        return mplew.getPacket();
    }

    public static byte[] craftMake(int cid, int something, int time) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.CRAFT_EFFECT);
		mplew.writeInt(cid);
        mplew.writeInt(something);
        mplew.writeInt(time);

        return mplew.getPacket();
    }

    public static byte[] craftFinished(int cid, int craftID, int ranking, int itemId, int quantity, int exp) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.CRAFT_COMPLETE);
		mplew.writeInt(cid);
        mplew.writeInt(craftID);
        mplew.writeInt(ranking);
        mplew.writeInt(itemId);
        mplew.writeInt(quantity);
        mplew.writeInt(exp);

        return mplew.getPacket();
    }

    public static byte[] harvestResult(int cid, boolean success) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.HARVESTED);
		mplew.writeInt(cid);
        mplew.write(success ? 1 : 0);

        return mplew.getPacket();
    }

    public static byte[] playerDamaged(int cid, int dmg) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.PLAYER_DAMAGED);
		mplew.writeInt(cid);
        mplew.writeInt(dmg);

        return mplew.getPacket();
    }

    public static byte[] showPyramidEffect(int chr) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.NETT_PYRAMID);
		mplew.writeInt(chr);
        mplew.write(1);
        mplew.writeInt(0);
        mplew.writeInt(0);

        return mplew.getPacket();
    }

    public static byte[] pamsSongEffect(int cid) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.PAMS_SONG);
		mplew.writeInt(cid);
        return mplew.getPacket();
    }

    public static byte[] spawnHaku_change0(int cid) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.HAKU_CHANGE_0);
		mplew.writeInt(cid);

        return mplew.getPacket();
    }

    public static byte[] spawnHaku_change1(MapleHaku d) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.HAKU_CHANGE_1);
		mplew.writeInt(d.getOwner());
        mplew.writePos(d.getPosition());
        mplew.write(d.getStance());
        mplew.writeShort(0);
        mplew.write(0);
        mplew.writeInt(0);

        return mplew.getPacket();
    }

    public static byte[] spawnHaku_bianshen(int cid, int oid, boolean change) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.HAKU_CHANGE);
		mplew.writeInt(cid);
        mplew.writeInt(oid);
        mplew.write(change ? 2 : 1);

        return mplew.getPacket();
    }

    public static byte[] hakuUnk(int cid, int oid, boolean change) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.HAKU_CHANGE);
		mplew.writeInt(cid);
        mplew.writeInt(oid);
        mplew.write(0);
        mplew.write(0);
        mplew.writeMapleAsciiString("lol");

        return mplew.getPacket();
    }

    public static byte[] spawnHaku(MapleHaku d) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.SPAWN_HAKU);
		mplew.writeInt(d.getOwner());
        mplew.writeInt(d.getObjectId());
        mplew.writeInt(40020109);
        mplew.write(1);
        mplew.writePos(d.getPosition());
        mplew.write(0);
        mplew.writeShort(d.getStance());

        return mplew.getPacket();
    }

    public static byte[] moveHaku(int cid, int oid, Point pos, List<LifeMovementFragment> res) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.HAKU_MOVE);
		mplew.writeInt(cid);
        mplew.writeInt(oid);
        mplew.writeInt(0);
        mplew.writePos(pos);
        mplew.writeInt(0);
        PacketHelper.serializeMovementList(mplew, res);
        return mplew.getPacket();
    }

    public static byte[] spawnDragon(MapleDragon d) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.DRAGON_SPAWN);
		mplew.writeInt(d.getOwner());
        mplew.writeInt(d.getPosition().x);
        mplew.writeInt(d.getPosition().y);
        mplew.write(d.getStance());
        mplew.writeShort(0);
        mplew.writeShort(d.getJobId());

        return mplew.getPacket();
    }

    public static byte[] removeDragon(int chrid) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.DRAGON_REMOVE);
		mplew.writeInt(chrid);

        return mplew.getPacket();
    }

   public static byte[] moveDragon(MapleDragon d, Point startPos, List<LifeMovementFragment> moves) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.DRAGON_MOVE);
		mplew.writeInt(d.getOwner());
        mplew.writeInt(0);
        mplew.writePos(startPos);
        mplew.writeInt(0);
        PacketHelper.serializeMovementList(mplew, moves);

        return mplew.getPacket();
    }

    public static byte[] spawnAndroid(MapleCharacter cid, MapleAndroid android) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.ANDROID_SPAWN);
		mplew.writeInt(cid.getId());
        mplew.write(android.getItemId() == 1662006 ? 5 : android.getItemId() - 1661999);
        mplew.writePos(android.getPos());
        mplew.write(android.getStance());
        mplew.writeShort(0);
        mplew.writeShort(0);
        mplew.writeShort(android.getHair() - 30000);
        mplew.writeShort(android.getFace() - 20000);
        mplew.writeMapleAsciiString(android.getName());
        for (short i = -1200; i > -1207; i = (short) (i - 1)) {
            Item item = cid.getInventory(MapleInventoryType.EQUIPPED).getItem(i);
            mplew.writeInt(item != null ? item.getItemId() : 0);
        }

        return mplew.getPacket();
    }

    public static byte[] moveAndroid(int cid, Point pos, List<LifeMovementFragment> res) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.ANDROID_MOVE);
		mplew.writeInt(cid);
        mplew.writeInt(0);
        mplew.writePos(pos);
        mplew.writeInt(2147483647);
        PacketHelper.serializeMovementList(mplew, res);
        return mplew.getPacket();
    }

public static byte[] showAndroidEmotion(int cid, byte emotion) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.ANDROID_EMOTION);
        //mplew.writeInt(cid);
        mplew.writeInt(emotion);
        mplew.writeInt(0); // tDuration
      
        return mplew.getPacket();
    }

    public static byte[] updateAndroidLook(boolean itemOnly, MapleCharacter cid, MapleAndroid android) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.ANDROID_UPDATE);
		mplew.writeInt(cid.getId());
        mplew.write(itemOnly ? 1 : 0);
        if (itemOnly) {
            for (short i = -1200; i > -1207; i = (short) (i - 1)) {
                Item item = cid.getInventory(MapleInventoryType.EQUIPPED).getItem(i);
                mplew.writeInt(item != null ? item.getItemId() : 0);
            }
        } else {
            mplew.writeShort(0);
            mplew.writeShort(android.getHair() - 30000);
            mplew.writeShort(android.getFace() - 20000);
            mplew.writeMapleAsciiString(android.getName());
        }

        return mplew.getPacket();
    }

    public static byte[] deactivateAndroid(int cid) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.ANDROID_DEACTIVATED);
		mplew.writeInt(cid);

        return mplew.getPacket();
    }

    public static byte[] removeFamiliar(int cid) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.SPAWN_FAMILIAR);
		mplew.writeInt(cid);
        mplew.writeShort(0);
        mplew.write(0);

        return mplew.getPacket();
    }

    public static byte[] spawnFamiliar(MonsterFamiliar mf, boolean spawn, boolean respawn) {
        MaplePacketWriter mplew = new MaplePacketWriter(respawn ? SendPacketOpcode.RESPAWN_FAMILIAR : SendPacketOpcode.SPAWN_FAMILIAR);
		mplew.writeInt(mf.getCharacterId());
        mplew.write(spawn ? 1 : 0);
        mplew.write(respawn ? 1 : 0);
        mplew.write(0);
        if (spawn) {
            mplew.writeInt(mf.getFamiliar());
            mplew.writeInt(mf.getFatigue());
            mplew.writeInt(mf.getVitality() * 300); //max fatigue
            mplew.writeMapleAsciiString(mf.getName());
            mplew.writePos(mf.getTruePosition());
            mplew.write(mf.getStance());
            mplew.writeShort(mf.getFh());
        }

        return mplew.getPacket();
    }

    public static byte[] moveFamiliar(int cid, Point startPos, List<LifeMovementFragment> moves) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.MOVE_FAMILIAR);
		mplew.writeInt(cid);
        mplew.write(0);
        mplew.writePos(startPos);
        mplew.writeInt(0);
        PacketHelper.serializeMovementList(mplew, moves);

        return mplew.getPacket();
    }

    public static byte[] touchFamiliar(int cid, byte unk, int objectid, int type, int delay, int damage) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.TOUCH_FAMILIAR);
		mplew.writeInt(cid);
        mplew.write(0);
        mplew.write(unk);
        mplew.writeInt(objectid);
        mplew.writeInt(type);
        mplew.writeInt(delay);
        mplew.writeInt(damage);

        return mplew.getPacket();
    }

    public static byte[] familiarAttack(int cid, byte unk, List<Triple<Integer, Integer, List<Integer>>> attackPair) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.ATTACK_FAMILIAR);
		mplew.writeInt(cid);
        mplew.write(0);// familiar id?
        mplew.write(unk);
        mplew.write(attackPair.size());
        for (Triple<Integer, Integer, List<Integer>> s : attackPair) {
            mplew.writeInt(s.left);
            mplew.write(s.mid);
            mplew.write(s.right.size());
            for (int damage : s.right) {
                mplew.writeInt(damage);
            }
        }

        return mplew.getPacket();
    }

    public static byte[] renameFamiliar(MonsterFamiliar mf) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.RENAME_FAMILIAR);
		mplew.writeInt(mf.getCharacterId());
        mplew.write(0);
        mplew.writeInt(mf.getFamiliar());
        mplew.writeMapleAsciiString(mf.getName());

        return mplew.getPacket();
    }

    public static byte[] updateFamiliar(MonsterFamiliar mf) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.UPDATE_FAMILIAR);
		mplew.writeInt(mf.getCharacterId());
        mplew.writeInt(mf.getFamiliar());
        mplew.writeInt(mf.getFatigue());
        mplew.writeLong(PacketHelper.getTime(mf.getVitality() >= 3 ? System.currentTimeMillis() : -2L));

        return mplew.getPacket();
    }

    public static byte[] movePlayer(int cid, List<LifeMovementFragment> moves, Point startPos) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.MOVE_PLAYER);
		mplew.writeInt(cid);
        mplew.writeInt(0);
        mplew.writePos(startPos);
        mplew.writeShort(0);
        mplew.writeShort(0);
        PacketHelper.serializeMovementList(mplew, moves);

        return mplew.getPacket();
    }

    public static byte[] closeRangeAttack(int cid, int tbyte, int skill, int skillLevel, int display, byte speed, List<AttackPair> damage, boolean energy, int charLevel, byte mastery, byte unk, int charge) {
        return addAttackInfo(energy ? 4 : 0, cid, tbyte, skill, skillLevel, display, speed, damage, charLevel, mastery, unk, 0, null, 0);
    }

    public static byte[] rangedAttack(int cid, byte tbyte, int skill, int skillLevel, int display, byte speed, int itemid, List<AttackPair> damage, Point pos, int charLevel, byte mastery, byte unk) {
        return addAttackInfo(1, cid, tbyte, skill, skillLevel, display, speed, damage, charLevel, mastery, unk, itemid, pos, 0);
    }
    
    public static byte[] rangedAttack(int charid, AttackInfo attack, int itemid, short charLevel, byte mastery) {
		return addAttackInfo(1, charid, attack, itemid, charLevel, mastery);
	}

    public static byte[] addAttackInfo(int type, int charid, AttackInfo attack, int itemid, short charLevel, byte mastery) {
    	MaplePacketWriter mplew;
    	if (type == 0) {
    		mplew = new MaplePacketWriter(SendPacketOpcode.CLOSE_RANGE_ATTACK);
        } else if (type == 1 || type == 2) {
        	mplew = new MaplePacketWriter(SendPacketOpcode.RANGED_ATTACK);
        } else if (type == 3) {
        	mplew = new MaplePacketWriter(SendPacketOpcode.MAGIC_ATTACK);
        } else {
        	mplew = new MaplePacketWriter(SendPacketOpcode.ENERGY_ATTACK);
        }

    	final int skillLevel = attack.skillLevel, skillid = attack.skillid, ultLevel = 0;
    	final byte unk = attack.unk;

        mplew.writeInt(charid);
        mplew.write(0);
        mplew.write(attack.tbyte);
        mplew.write(charLevel);
        
        if (skillLevel > 0) {
        	mplew.write(skillLevel);
            mplew.writeInt(skillid);
        } else {
        	mplew.write(0);
        }

        if (GameConstants.isZero(skillid / 10000) && skillid != 100001283) {
            short zero1 = 0;
            short zero2 = 0;
            mplew.write(zero1 > 0 || zero2 > 0); //boolean
            if (zero1 > 0 || zero2 > 0) {
                mplew.writeShort(zero1);
                mplew.writeShort(zero2);
                //there is a full handler so better not write zero
            }
        }

        /*
        if (type == 1 || type == 2) { // if RANGED_ATTACK  (Got this from the IDA but it's wrong?)
            mplew.write(ultLevel);
            if (ultLevel > 0) {
                mplew.writeInt(3220010);
            }
        }
        */
        
        if (skillid == 4121013) {  // if the skill can be affect by a hyper stat
        	mplew.write(ultLevel); // Hyper Skill Boolean/Level
        	if (ultLevel > 0) {
        		mplew.writeInt(0); // Hyper Skill ID
        	}
        }

        if (skillid == 80001850) {
        	mplew.write(skillLevel);
        	if (skillLevel > 0) {
        		mplew.writeInt(skillid);
        	}
        }
        
        mplew.write(attack.flag); // some flag
        mplew.write(unk); // flag
        mplew.writeInt(0); // nOption3 or nBySummonedID
        
        if ((unk & 2) != 0) {
        	mplew.writeInt(skillid); // buckShotInfo.nSkillID
        	mplew.writeInt(skillLevel); // buckShotInfo.nSkillLV
        }
        
        if ((unk & 8) != 0) {
        	mplew.write(0); // nPassiveAddAttackCount
        }
        
        /*if (skillid == 40021185 || skillid == 42001006) {
            mplew.write(0); //boolean if true then int
        }*/

        
        mplew.writeShort(attack.display);
        mplew.write(attack.speed);
        mplew.write(mastery);
        mplew.writeInt(itemid > 0 ? itemid : 0); // Throwing Star ID
        
        for (AttackPair oned : attack.allDamage) {
            if (oned.attack != null) {
                mplew.writeInt(oned.objectid);
                mplew.write(oned.unknownByte != 0 ? oned.unknownByte : 7);
                mplew.write(oned.unknownBool1); // some boolean
                mplew.write(oned.unknownBool2); // some boolean
                mplew.writeShort(oned.unknownShort != 0 ? oned.unknownShort : 256); // ??
                if (skillid == 42111002) {
                    mplew.write(oned.attack.size());
                    for (Pair eachd : oned.attack) {
                        mplew.writeInt(((Integer) eachd.left).intValue());
                    }
                } else {
                    for (Pair eachd : oned.attack) {
                        mplew.write(((Boolean) eachd.right).booleanValue() ? 1 : 0); // Show critical if true
                        mplew.writeInt(((Integer) eachd.left).intValue());
                    }
                }
            }
        }
        if (skillid == 2321001 || skillid == 2221052 || skillid == 11121052) {
            mplew.writeInt(0);
        } else if (skillid == 65121052 || skillid == 101000202 || skillid == 101000102) {
            mplew.writeInt(0);
            mplew.writeInt(0);
        }
        if (skillid == 42100007) {
            mplew.writeShort(0);
            mplew.write(0);
        }
        /*if (type == 1 || type == 2) {
            mplew.writePos(pos);
        } else */
        if (type == 3 && attack.charge > 0) {
            mplew.writeInt(attack.charge);
        }
        if (skillid == 5321000
                || skillid == 5311001
                || skillid == 5321001
                || skillid == 5011002
                || skillid == 5311002
                || skillid == 5221013
                || skillid == 5221017
                || skillid == 3120019
                || skillid == 3121015
                || skillid == 4121017) {
            mplew.writePos(attack.position);
        }

        System.out.println(mplew.toString());
        return mplew.getPacket();
	}

	public static byte[] strafeAttack(int cid, byte tbyte, int skill, int skillLevel, int display, byte speed, int itemid, List<AttackPair> damage, Point pos, int charLevel, byte mastery, byte unk, int ultLevel) {
        return addAttackInfo(2, cid, tbyte, skill, skillLevel, display, speed, damage, charLevel, mastery, unk, itemid, pos, ultLevel);
    }

    public static byte[] magicAttack(int cid, int tbyte, int skill, int skillLevel, int display, byte speed, List<AttackPair> damage, int charge, int charLevel, byte unk) {
        return addAttackInfo(3, cid, tbyte, skill, skillLevel, display, speed, damage, charLevel, (byte) 0, unk, charge, null, 0);
    }

    public static byte[] addAttackInfo(int type, int cid, int tbyte, int skillid, int skillLevel, int display, byte speed, List<AttackPair> damage, int charLevel, byte mastery, byte unk, int charge, Point pos, int ultLevel) {
    	MaplePacketWriter mplew;
    	if (type == 0) {
    		mplew = new MaplePacketWriter(SendPacketOpcode.CLOSE_RANGE_ATTACK);
        } else if (type == 1 || type == 2) {
        	mplew = new MaplePacketWriter(SendPacketOpcode.RANGED_ATTACK);
        } else if (type == 3) {
        	mplew = new MaplePacketWriter(SendPacketOpcode.MAGIC_ATTACK);
        } else {
        	mplew = new MaplePacketWriter(SendPacketOpcode.ENERGY_ATTACK);
        }

        mplew.writeInt(cid);
        mplew.write(0);
        mplew.write(tbyte);
        mplew.write(charLevel);
        
        if (skillLevel > 0) {
        	mplew.write(skillLevel);
            mplew.writeInt(skillid);
        } else {
        	mplew.write(0);
        }

        if (GameConstants.isZero(skillid / 10000) && skillid != 100001283) {
            short zero1 = 0;
            short zero2 = 0;
            mplew.write(zero1 > 0 || zero2 > 0); //boolean
            if (zero1 > 0 || zero2 > 0) {
                mplew.writeShort(zero1);
                mplew.writeShort(zero2);
                //there is a full handler so better not write zero
            }
        }

        /*
        if (type == 1 || type == 2) { // if RANGED_ATTACK  (Got this from the IDA but it's wrong?)
            mplew.write(ultLevel);
            if (ultLevel > 0) {
                mplew.writeInt(3220010);
            }
        }
        */
        
        if (skillid == 4121013) {  // if the skill can be affect by a hyper stat
        	mplew.write(ultLevel); // Hyper Skill Boolean/Level
        	if (ultLevel > 0) {
        		mplew.writeInt(0); // Hyper Skill ID
        	}
        }

        if (skillid == 80001850) {
        	mplew.write(skillLevel);
        	if (skillLevel > 0) {
        		mplew.writeInt(skillid);
        	}
        }
        
        mplew.write(0); // some flag
        mplew.write(unk); // flag
        mplew.writeInt(0); // nOption3 or nBySummonedID
        
        if ((unk & 2) != 0) {
        	mplew.writeInt(skillid); // buckShotInfo.nSkillID
        	mplew.writeInt(skillLevel); // buckShotInfo.nSkillLV
        }
        
        if ((unk & 8) != 0) {
        	mplew.write(0); // nPassiveAddAttackCount
        }
        
        /*if (skillid == 40021185 || skillid == 42001006) {
            mplew.write(0); //boolean if true then int
        }*/

        
        mplew.writeShort(display);
        mplew.write(speed);
        mplew.write(mastery);
        mplew.writeInt(charge > 0 ? charge : 0); // Throwing Star ID
        
        for (AttackPair oned : damage) {
            if (oned.attack != null) {
                mplew.writeInt(oned.objectid);
                mplew.write(oned.unknownByte != 0 ? oned.unknownByte : 7);
                mplew.write(oned.unknownBool1); // some boolean
                mplew.write(oned.unknownBool2); // some boolean
                mplew.writeShort(oned.unknownShort != 0 ? oned.unknownShort : 256); // ??
                if (skillid == 42111002) {
                    mplew.write(oned.attack.size());
                    for (Pair eachd : oned.attack) {
                        mplew.writeInt(((Integer) eachd.left).intValue());
                    }
                } else {
                    for (Pair eachd : oned.attack) {
                        mplew.write(((Boolean) eachd.right).booleanValue() ? 1 : 0); // Show critical if true
                        mplew.writeInt(((Integer) eachd.left).intValue());
                    }
                }
            }
        }
        if (skillid == 2321001 || skillid == 2221052 || skillid == 11121052) {
            mplew.writeInt(0);
        } else if (skillid == 65121052 || skillid == 101000202 || skillid == 101000102) {
            mplew.writeInt(0);
            mplew.writeInt(0);
        }
        if (skillid == 42100007) {
            mplew.writeShort(0);
            mplew.write(0);
        }
        /*if (type == 1 || type == 2) {
            mplew.writePos(pos);
        } else */
        if (type == 3 && charge > 0) {
            mplew.writeInt(charge);
        }
        if (skillid == 5321000
                || skillid == 5311001
                || skillid == 5321001
                || skillid == 5011002
                || skillid == 5311002
                || skillid == 5221013
                || skillid == 5221017
                || skillid == 3120019
                || skillid == 3121015
                || skillid == 4121017) {
            mplew.writePos(pos);
        }

        System.out.println(mplew.toString());
        return mplew.getPacket();
    }

    public static byte[] skillEffect(MapleCharacter from, int skillId, byte level, short display, byte unk) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.SKILL_EFFECT);
		mplew.writeInt(from.getId());
        mplew.writeInt(skillId);
        mplew.write(level);
        mplew.writeShort(display);
        mplew.write(unk);
        if (skillId == 13111020) {
            mplew.writePos(from.getPosition()); // Position
        }         if (skillId == 27101202) {
            mplew.writePos(from.getPosition()); // Position
        }

        return mplew.getPacket();
    }

    public static byte[] skillCancel(MapleCharacter from, int skillId) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.CANCEL_SKILL_EFFECT);
		mplew.writeInt(from.getId());
        mplew.writeInt(skillId);

        return mplew.getPacket();
    }

    public static byte[] damagePlayer(int cid, int type, int damage, int monsteridfrom, byte direction, int skillid, int pDMG, boolean pPhysical, int pID, byte pType, Point pPos, byte offset, int offset_d, int fake) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.DAMAGE_PLAYER);
		mplew.writeInt(cid);
        mplew.write(type);
        mplew.writeInt(damage);
        mplew.write(0);
        if (type >= -1) {
            mplew.writeInt(monsteridfrom);
            mplew.write(direction);
            mplew.writeInt(skillid);
            mplew.writeInt(pDMG);
            mplew.write(0);
            if (pDMG > 0) {
                mplew.write(pPhysical ? 1 : 0);
                mplew.writeInt(pID);
                mplew.write(pType);
                mplew.writePos(pPos);
            }
            mplew.write(offset);
            if (offset == 1) {
                mplew.writeInt(offset_d);
            }
        }
        mplew.writeInt(damage);
        if ((damage <= 0) || (fake > 0)) {
            mplew.writeInt(fake);
        }

        return mplew.getPacket();
    }

    public static byte[] facialExpression(MapleCharacter from, int expression) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.FACIAL_EXPRESSION);
		mplew.writeInt(from.getId());
        mplew.writeInt(expression);
        mplew.writeInt(-1);
        mplew.write(0);

        return mplew.getPacket();
    }

    public static byte[] directionFacialExpression(int expression, int duration) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.DIRECTION_FACIAL_EXPRESSION);
		mplew.writeInt(expression);
            mplew.writeInt(duration);
            mplew.write(0); // bByItemOption


            /* Facial Expressions:
             * 0 - Normal 
             * 1 - F1
             * 2 - F2
             * 3 - F3
             * 4 - F4
             * 5 - F5
             * 6 - F6
             * 7 - F7
             * 8 - Vomit
             * 9 - Panic
             * 10 - Sweetness
             * 11 - Kiss
             * 12 - Wink
             * 13 - Ouch!
             * 14 - Goo goo eyes
             * 15 - Blaze
             * 16 - Star
             * 17 - Love
             * 18 - Ghost
             * 19 - Constant Sigh
             * 20 - Sleepy
             * 21 - Flaming hot
             * 22 - Bleh
             * 23 - No Face
             */
            return mplew.getPacket();
        } 

    
    public static byte[] itemEffect(int characterid, int itemid) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.SHOW_EFFECT);
		mplew.writeInt(characterid);
        mplew.writeInt(itemid);

        return mplew.getPacket();
    }

    public static byte[] showTitle(int characterid, int itemid) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.SHOW_TITLE);
		mplew.writeInt(characterid);
        mplew.writeInt(itemid);

        return mplew.getPacket();
    }

    public static byte[] showAngelicBuster(int characterid, int tempid) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.ANGELIC_CHANGE);
		mplew.writeInt(characterid);
        mplew.writeInt(tempid);

        return mplew.getPacket();
    }

    public static byte[] showChair(int characterid, int itemid) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.SHOW_CHAIR);
		mplew.writeInt(characterid);
        mplew.writeInt(itemid);
        mplew.writeInt(0);
        mplew.writeInt(0);
        
        return mplew.getPacket();
    }

    public static byte[] updateCharLook(MapleCharacter chr, boolean second) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.UPDATE_CHAR_LOOK);
		mplew.writeInt(chr.getId());
        mplew.write(1);
        PacketHelper.addCharLook(mplew, chr, false, second);
        Triple<List<MapleRing>, List<MapleRing>, List<MapleRing>> rings = chr.getRings(false);
        addRingInfo(mplew, rings.getLeft());
        addRingInfo(mplew, rings.getMid());
        addMRingInfo(mplew, rings.getRight(), chr);
        mplew.writeInt(0); // -> charid to follow (4)
        mplew.writeInt(0);
        
        return mplew.getPacket();
    }

    public static byte[] updatePartyMemberHP(int cid, int curhp, int maxhp) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.UPDATE_PARTYMEMBER_HP);
		mplew.writeInt(cid);
        mplew.writeInt(curhp);
        mplew.writeInt(maxhp);

        return mplew.getPacket();
    }

    public static byte[] loadGuildName(MapleCharacter chr) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.LOAD_GUILD_NAME);
		mplew.writeInt(chr.getId());
        
        if (chr.getGuildId() <= 0) {
            mplew.writeShort(0);
        } else {
            MapleGuild gs = World.Guild.getGuild(chr.getGuildId());
            if (gs != null) {
                mplew.writeMapleAsciiString(gs.getName());
            } else {
                mplew.writeShort(0);
            }
        }

        return mplew.getPacket();
    }

    public static byte[] loadGuildIcon(MapleCharacter chr) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.LOAD_GUILD_ICON);
		mplew.writeInt(chr.getId());
        
        if (chr.getGuildId() <= 0) {
            mplew.writeZeroBytes(6);
        } else {
            MapleGuild gs = World.Guild.getGuild(chr.getGuildId());
            if (gs != null) {
                mplew.writeShort(gs.getLogoBG());
                mplew.write(gs.getLogoBGColor());
                mplew.writeShort(gs.getLogo());
                mplew.write(gs.getLogoColor());
            } else {
                mplew.writeZeroBytes(6);
            }
        }

        return mplew.getPacket();
    }

    public static byte[] changeTeam(int cid, int type) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.LOAD_TEAM);
		mplew.writeInt(cid);
        mplew.write(type);

        return mplew.getPacket();
    }

    public static byte[] showHarvesting(int cid, int tool) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.SHOW_HARVEST);
		mplew.writeInt(cid);
        if (tool > 0) {
            mplew.write(1);
            mplew.write(0);
            mplew.writeShort(0);
            mplew.writeInt(tool);
            mplew.writeZeroBytes(30);
        } else {
            mplew.write(0);
            mplew.writeZeroBytes(33);
        }

        return mplew.getPacket();
    }

    public static byte[] getPVPHPBar(int cid, int hp, int maxHp) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.PVP_HP);
		mplew.writeInt(cid);
        mplew.writeInt(hp);
        mplew.writeInt(maxHp);

        return mplew.getPacket();
    }

    public static byte[] cancelChair(MapleCharacter chr) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.CANCEL_CHAIR);
		mplew.writeInt(chr.getId());
        mplew.write(0);

        return mplew.getPacket();
    }
    
    public static byte[] cancelChair(int id) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.CANCEL_CHAIR);
        if (id == -1) {
            mplew.write(0);
        } else {
            mplew.write(1);
            mplew.writeShort(id);
        }

        return mplew.getPacket();
    }

    public static byte[] instantMapWarp(byte portal) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.CURRENT_MAP_WARP);
		mplew.write(0);
        mplew.write(portal);
        if (portal <= 0) {
        	mplew.writeInt(0); // nIdx, map id? 
        } else {
        	mplew.writeInt(0); // dwCallerId, player id?
        	mplew.writeInt(0); // ptTarget, x,y point
        }

        return mplew.getPacket();
    }

    public static byte[] updateQuestInfo(MapleCharacter c, int questid, int npc, byte progress) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.UPDATE_QUEST_INFO);
		mplew.write(progress);
        mplew.writeInt(questid);
        mplew.writeInt(npc);
        mplew.writeInt(0);
        mplew.write(1);

        return mplew.getPacket();
    }

    public static byte[] updateQuestFinish(int questid, int npc, int nextquest) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.UPDATE_QUEST_INFO);
		mplew.write(11);
        mplew.writeInt(questid);
        mplew.writeInt(npc);  // uJobDemandLower
        mplew.writeInt(nextquest);
        mplew.write(0);

        return mplew.getPacket();
    }

    public static byte[] sendHint(String hint, int width, int height) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.PLAYER_HINT);
		mplew.writeMapleAsciiString(hint);
        mplew.writeShort(width < 1 ? Math.max(hint.length() * 10, 40) : width);
        mplew.writeShort(Math.max(height, 5));
        mplew.write(1);

        return mplew.getPacket();
    }

    public static byte[] updateCombo(int value) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.ARAN_COMBO);
		mplew.writeInt(value);

        return mplew.getPacket();
    }

    public static byte[] rechargeCombo(int value) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.ARAN_COMBO_RECHARGE);
		mplew.writeInt(value);

        return mplew.getPacket();
    }

    public static byte[] getFollowMessage(String msg) {
        return getGameMessage((short) 11, msg);
    }

    public static byte[] getGameMessage(short color, String message) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.GAME_MESSAGE);
		mplew.writeShort(color);
        mplew.writeMapleAsciiString(message);

        return mplew.getPacket();
    }

    public static byte[] getBuffZoneEffect(int itemId) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.BUFF_ZONE_EFFECT);
		mplew.writeInt(itemId);

        return mplew.getPacket();
    }

    public static byte[] getTimeBombAttack() {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.TIME_BOMB_ATTACK);
		mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.writeInt(10);
        mplew.writeInt(6);

        return mplew.getPacket();
    }

    public static byte[] moveFollow(Point otherStart, Point myStart, Point otherEnd, List<LifeMovementFragment> moves) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.FOLLOW_MOVE);
		mplew.writeInt(0);
        mplew.writePos(otherStart);
        mplew.writePos(myStart);
        PacketHelper.serializeMovementList(mplew, moves);
        mplew.write(17);
        for (int i = 0; i < 8; i++) {
            mplew.write(0);
        }
        mplew.write(0);
        mplew.writePos(otherEnd);
        mplew.writePos(otherStart);
        mplew.writeZeroBytes(100);

        return mplew.getPacket();
    }

    public static byte[] getFollowMsg(int opcode) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.FOLLOW_MSG);
		mplew.writeLong(opcode);

        return mplew.getPacket();
    }

    public static byte[] registerFamiliar(MonsterFamiliar mf) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.REGISTER_FAMILIAR);
		mplew.writeLong(mf.getId());
        mf.writeRegisterPacket(mplew, false);
        mplew.writeShort(mf.getVitality() >= 3 ? 1 : 0);

        return mplew.getPacket();
    }

    public static byte[] createUltimate(int amount) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.CREATE_ULTIMATE);
		mplew.writeInt(amount);

        return mplew.getPacket();
    }

    public static byte[] harvestMessage(int oid, int msg) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.HARVEST_MESSAGE);
		mplew.writeInt(oid);
        mplew.writeInt(msg);

        return mplew.getPacket();
    }

    public static byte[] openBag(int index, int itemId, boolean firstTime) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.OPEN_BAG);
		mplew.writeInt(index);
        mplew.writeInt(itemId);
        mplew.writeShort(firstTime ? 1 : 0);

        return mplew.getPacket();
    }

    public static byte[] dragonBlink(int portalId) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.DRAGON_BLINK);
		mplew.write(portalId);

        return mplew.getPacket();
    }

    public static byte[] getPVPIceGage(int score) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.PVP_ICEGAGE);
		mplew.writeInt(score);

        return mplew.getPacket();
    }

    public static byte[] skillCooldown(int sid, int time) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.COOLDOWN);
		mplew.writeInt(sid);
        mplew.writeInt(time);

        return mplew.getPacket();
    }

    /*
    public static byte[] dropItemFromMapObject(MapleMapItem drop, Point dropfrom, Point dropto, byte mod) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.DROP_ITEM_FROM_MAPOBJECT);
		mplew.write(mod);
        mplew.writeInt(drop.getObjectId());
        mplew.write(drop.getMeso() > 0 ? 1 : 0);
        mplew.writeInt(drop.getItemId());
        mplew.writeInt(drop.getOwner());
        mplew.write(drop.getDropType());
        mplew.writePos(dropto);
        mplew.writeInt(0);
        if (mod != 2) {
            mplew.writePos(dropfrom);
            mplew.writeShort(0);
//            mplew.write(0);//removed 143 or other0
        }
        mplew.write(0);
        if (drop.getMeso() == 0) {
            PacketHelper.addExpirationTime(mplew, drop.getItem().getExpiration());
        }
        mplew.writeShort(drop.isPlayerDrop() ? 0 : 1);
        mplew.writeZeroBytes(4);

        return mplew.getPacket();
    }
    */
    public static byte[] dropItemFromMapObject(MapleMapItem drop, Point dropfrom, Point dropto, byte mod) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.DROP_ITEM_FROM_MAPOBJECT);
		mplew.write(0); // eDropType
        mplew.write(mod);
        mplew.writeInt(drop.getObjectId());
        mplew.write(drop.getMeso() > 0 ? 1 : 0);
        mplew.writeInt(0); // bDropMotionType
        mplew.writeInt(0); // bDropSpeed
        mplew.writeInt(0); // bNoMove
        mplew.writeInt(drop.getItemId());
        mplew.writeInt(drop.getOwner());
        mplew.write(drop.getDropType()); // nOwnType
        mplew.writePos(dropto);
        mplew.writeInt(0); // dwSourceID
        if (mod != 2) {
            mplew.writePos(dropfrom);
            mplew.writeInt(0); // Delay Time
        }
        mplew.write(0); // bExplosiveDrop
        mplew.write(0); // ??
        if (drop.getMeso() == 0) {
            PacketHelper.addExpirationTime(mplew, drop.getItem().getExpiration());
        }
        mplew.writeShort(drop.isPlayerDrop() ? 0 : 1);
        mplew.writeLong(0);
        mplew.write(0); // potential state (1 | 2 | 3 | 4)
        
        return mplew.getPacket();
    }

    public static byte[] explodeDrop(int oid) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.REMOVE_ITEM_FROM_MAP);
		mplew.write(4);
        mplew.writeInt(oid);
        mplew.writeShort(655);

        return mplew.getPacket();
    }

    public static byte[] removeItemFromMap(int oid, int animation, int cid) {
        return removeItemFromMap(oid, animation, cid, 0);
    }

    public static byte[] removeItemFromMap(int oid, int animation, int cid, int slot) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.REMOVE_ITEM_FROM_MAP);
		mplew.write(animation);
        mplew.writeInt(oid);
        if (animation >= 2) {
            mplew.writeInt(cid);
            if (animation == 5) {
                mplew.writeInt(slot);
            }
        }
        return mplew.getPacket();
    }
    

    public static byte[] spawnClockMist(final MapleMist clock) {
        MaplePacketWriter outPacket = new MaplePacketWriter(SendPacketOpcode.SPAWN_MIST);
        outPacket.writeInt(clock.getObjectId());
        outPacket.write(1);
        outPacket.writeInt(clock.getMobOwner().getObjectId());
        outPacket.writeInt(clock.getMobSkill().getSkillId());
        outPacket.write(clock.getClockType());
        outPacket.writeShort(0x07);//clock.getSkillDelay());
        outPacket.writeInt(clock.getBox().x);
        outPacket.writeInt(clock.getBox().y);
        outPacket.writeInt(clock.getBox().x + clock.getBox().width);
        outPacket.writeInt(clock.getBox().y + clock.getBox().height);
        outPacket.writeInt(0);
        outPacket.writePos(clock.getMobOwner().getPosition());
        outPacket.writeInt(0);
        outPacket.writeInt(clock.getClockType() == 1 ? 15 : clock.getClockType() == 2 ? -15 : 0);
        outPacket.writeInt(0x78);
        //System.out.println(packet.toString());
        return outPacket.getPacket();
    }
    
        public static byte[] spawnObtacleAtomBomb(){
        MaplePacketWriter outPacket = new MaplePacketWriter(SendPacketOpcode.SPAWN_OBTACLE_ATOM);
        
        //Number of bomb objects to spawn.  You can also just send multiple packets instead of putting them all in one packet.
        outPacket.writeInt(500);
        
        //Unknown, this part is from IDA.
        byte unk = 0;
        outPacket.write(unk); //animation data or some shit
        if(unk == 1){
            outPacket.writeInt(300); //from Effect.img/BasicEff/ObtacleAtomCreate/%d
            outPacket.write(0); //rest idk
            outPacket.writeInt(0);
            outPacket.writeInt(0);
            outPacket.writeInt(0);
            outPacket.writeInt(0);
        }
        
            outPacket.write(1);
            outPacket.writeInt(1);
            outPacket.writeInt(1);
            outPacket.writeInt(900); //POSX
            outPacket.writeInt(-1347); //POSY
            outPacket.writeInt(25);
            outPacket.writeInt(3);
            outPacket.writeInt(0);
            outPacket.writeInt(25);
            outPacket.writeInt(-5);
            outPacket.writeInt(1000);
            outPacket.writeInt(800);
            outPacket.writeInt(80);
        return outPacket.getPacket();
    }  

    public static byte[] spawnMist(MapleMist mist) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.SPAWN_MIST);
		mplew.writeInt(mist.getObjectId());

        //mplew.write(mist.isMobMist() ? 0 : mist.isPoisonMist());
        mplew.write(0);
        mplew.writeInt(mist.getOwnerId());
        if (mist.getMobSkill() == null) {
            mplew.writeInt(mist.getSourceSkill().getId());
        } else {
            mplew.writeInt(mist.getMobSkill().getSkillId());
        }
        mplew.write(mist.getSkillLevel());
        mplew.writeShort(mist.getSkillDelay());
        mplew.writeRect(mist.getBox());
        mplew.writeInt(mist.isShelter() ? 1 : 0);
        mplew.writeInt(0);
        mplew.writePos(mist.getPosition());
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.writeShort(0);
        mplew.writeShort(mist.getSkillDelay());
        mplew.writeShort(0);

        return mplew.getPacket();
    }

    public static byte[] removeMist(int oid, boolean eruption) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.REMOVE_MIST);
		mplew.writeInt(oid);
        mplew.write(eruption ? 1 : 0);

        return mplew.getPacket();
    }
    
    
    public static byte[] removeMist(final int oid) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.REMOVE_MIST);
		mplew.writeInt(oid);
        mplew.write(0); //v181
        
        return mplew.getPacket();
    }

    public static byte[] spawnMysticDoor(int oid, int skillid, Point pos, boolean animation) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.SPAWN_MYSTIC_DOOR);
		mplew.write(animation ? 0 : 1);
        mplew.writeInt(oid);
        mplew.writeInt(skillid);
        mplew.writePos(pos);

        return mplew.getPacket();
    }

    public static byte[] removeMysticDoor(int oid, boolean animation) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.REMOVE_MYSTIC_DOOR);
		mplew.write(animation ? 0 : 1);
        mplew.writeInt(oid);

        return mplew.getPacket();
    }

    public static byte[] spawnKiteError() {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.SPAWN_KITE_ERROR);

        return mplew.getPacket();
    }

    public static byte[] spawnKite(int oid, int id, Point pos) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.SPAWN_KITE);
		mplew.writeInt(oid);
        mplew.writeInt(0);
        mplew.writeMapleAsciiString("");
        mplew.writeMapleAsciiString("");
        mplew.writePos(pos);

        return mplew.getPacket();
    }

    public static byte[] destroyKite(int oid, int id, boolean animation) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.DESTROY_KITE);
		mplew.write(animation ? 0 : 1);
        mplew.writeInt(oid);

        return mplew.getPacket();
    }

    public static byte[] spawnMechDoor(MechDoor md, boolean animated) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.SPAWN_MECH_DOOR);
		mplew.write(animated ? 0 : 1);
        mplew.writeInt(md.getOwnerId());
        mplew.writePos(md.getTruePosition());
        mplew.write(md.getId());
        mplew.writeInt(md.getPartyId());
        return mplew.getPacket();
    }

    public static byte[] removeMechDoor(MechDoor md, boolean animated) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.REMOVE_MECH_DOOR);
		mplew.write(animated ? 0 : 1);
        mplew.writeInt(md.getOwnerId());
        mplew.write(md.getId());

        return mplew.getPacket();
    }

    public static byte[] triggerReactor(MapleReactor reactor, int stance) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.REACTOR_HIT);
		mplew.writeInt(reactor.getObjectId());
        mplew.write(reactor.getState());
        mplew.writePos(reactor.getTruePosition());
        mplew.writeInt(stance);
        mplew.writeInt(0);
        
        return mplew.getPacket();
    }
    
    public static byte[] triggerReactor(MapleReactor reactor, int stance, MapleCharacter chr) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.REACTOR_HIT);
		mplew.writeInt(reactor.getObjectId());
        mplew.write(reactor.getState());
        mplew.writePos(reactor.getTruePosition());
        mplew.writeInt(stance);
        mplew.writeInt(chr.getId());
        
        return mplew.getPacket();
    }

    public static byte[] spawnReactor(MapleReactor reactor) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.REACTOR_SPAWN);
		mplew.writeInt(reactor.getObjectId());
        mplew.writeInt(reactor.getReactorId());
        mplew.write(reactor.getState());
        mplew.writePos(reactor.getTruePosition());
        mplew.write(reactor.getFacingDirection());
        mplew.writeMapleAsciiString(reactor.getName());

        return mplew.getPacket();
    }

    public static byte[] destroyReactor(MapleReactor reactor) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.REACTOR_DESTROY);
		mplew.writeInt(reactor.getObjectId());
        mplew.write(reactor.getState());
        mplew.writePos(reactor.getPosition());

        return mplew.getPacket();
    }

    public static byte[] makeExtractor(int cid, String cname, Point pos, int timeLeft, int itemId, int fee) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.SPAWN_EXTRACTOR);
		mplew.writeInt(cid);
        mplew.writeMapleAsciiString(cname);
        mplew.writeInt(pos.x);
        mplew.writeInt(pos.y);
        mplew.writeShort(timeLeft);
        mplew.writeInt(itemId);
        mplew.writeInt(fee);

        return mplew.getPacket();
    }

    public static byte[] removeExtractor(int cid) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.REMOVE_EXTRACTOR);
		mplew.writeInt(cid);
        mplew.writeInt(1);

        return mplew.getPacket();
    }

    public static byte[] rollSnowball(int type, MapleSnowball.MapleSnowballs ball1, MapleSnowball.MapleSnowballs ball2) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.ROLL_SNOWBALL);
		mplew.write(type);
        mplew.writeInt(ball1 == null ? 0 : ball1.getSnowmanHP() / 75);
        mplew.writeInt(ball2 == null ? 0 : ball2.getSnowmanHP() / 75);
        mplew.writeShort(ball1 == null ? 0 : ball1.getPosition());
        mplew.write(0);
        mplew.writeShort(ball2 == null ? 0 : ball2.getPosition());
        mplew.writeZeroBytes(11);

        return mplew.getPacket();
    }

    public static byte[] enterSnowBall() {
        return rollSnowball(0, null, null);
    }

    public static byte[] hitSnowBall(int team, int damage, int distance, int delay) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.HIT_SNOWBALL);
		mplew.write(team);
        mplew.writeShort(damage);
        mplew.write(distance);
        mplew.write(delay);

        return mplew.getPacket();
    }

    public static byte[] snowballMessage(int team, int message) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.SNOWBALL_MESSAGE);
		mplew.write(team);
        mplew.writeInt(message);

        return mplew.getPacket();
    }

    public static byte[] leftKnockBack() {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.LEFT_KNOCK_BACK);

        return mplew.getPacket();
    }

    public static byte[] hitCoconut(boolean spawn, int id, int type) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.HIT_COCONUT);
		mplew.writeInt(spawn ? 32768 : id);
        mplew.write(spawn ? 0 : type);

        return mplew.getPacket();
    }

    public static byte[] coconutScore(int[] coconutscore) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.COCONUT_SCORE);
		mplew.writeShort(coconutscore[0]);
        mplew.writeShort(coconutscore[1]);

        return mplew.getPacket();
    }

    public static byte[] updateAriantScore(List<MapleCharacter> players) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.ARIANT_SCORE_UPDATE);
		mplew.write(players.size());
        for (MapleCharacter i : players) {
            mplew.writeMapleAsciiString(i.getName());
            mplew.writeInt(0);
        }

        return mplew.getPacket();
    }

    public static byte[] sheepRanchInfo(byte wolf, byte sheep) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.SHEEP_RANCH_INFO);
		mplew.write(wolf);
        mplew.write(sheep);

        return mplew.getPacket();
    }

    public static byte[] sheepRanchClothes(int cid, byte clothes) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.SHEEP_RANCH_CLOTHES);
		mplew.writeInt(cid);
        mplew.write(clothes);

        return mplew.getPacket();
    }

    public static byte[] updateWitchTowerKeys(int keys) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.WITCH_TOWER);
		mplew.write(keys);

        return mplew.getPacket();
    }

    public static byte[] showChaosZakumShrine(boolean spawned, int time) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.CHAOS_ZAKUM_SHRINE);
		mplew.write(spawned ? 1 : 0);
        mplew.writeInt(time);

        return mplew.getPacket();
    }

    public static byte[] showChaosHorntailShrine(boolean spawned, int time) {
        return showHorntailShrine(spawned, time);
    }

    public static byte[] showHorntailShrine(boolean spawned, int time) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.HORNTAIL_SHRINE);
		mplew.write(spawned ? 1 : 0);
        mplew.writeInt(time);

        return mplew.getPacket();
    }

    public static byte[] getRPSMode(byte mode, int mesos, int selection, int answer) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.RPS_GAME);
		mplew.write(mode);
        switch (mode) {
            case 6:
                if (mesos == -1) {
                    break;
                }
                mplew.writeInt(mesos);
                break;
            case 8:
                mplew.writeInt(9000019);
                break;
            case 11:
                mplew.write(selection);
                mplew.write(answer);
        }

        return mplew.getPacket();
    }

    public static byte[] messengerInvite(String from, int messengerid) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.MESSENGER);
		mplew.write(3);
        mplew.writeMapleAsciiString(from);
        mplew.write(1);//channel?
        mplew.writeInt(messengerid);
        mplew.write(0);

        return mplew.getPacket();
    }

    public static byte[] addMessengerPlayer(String from, MapleCharacter chr, int position, int channel) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.MESSENGER);
		mplew.write(0);
        mplew.write(position);
        PacketHelper.addCharLook(mplew, chr, true, false);
        mplew.writeMapleAsciiString(from);
        mplew.write(channel);
        mplew.write(1); // v140
        mplew.writeInt(chr.getJob());

        return mplew.getPacket();
    }

    public static byte[] removeMessengerPlayer(int position) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.MESSENGER);
		mplew.write(2);
        mplew.write(position);

        return mplew.getPacket();
    }

    public static byte[] updateMessengerPlayer(String from, MapleCharacter chr, int position, int channel) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.MESSENGER);
		mplew.write(0); // v140.
        mplew.write(position);
        PacketHelper.addCharLook(mplew, chr, true, false);
        mplew.writeMapleAsciiString(from);
        mplew.write(channel);
        mplew.write(0); // v140.
        mplew.writeInt(chr.getJob()); // doubt it's the job, lol. v140.

        return mplew.getPacket();
    }

    public static byte[] joinMessenger(int position) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.MESSENGER);
		mplew.write(1);
        mplew.write(position);

        return mplew.getPacket();
    }

    public static byte[] messengerChat(String charname, String text) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.MESSENGER);
		mplew.write(6);
        mplew.writeMapleAsciiString(charname);
        mplew.writeMapleAsciiString(text);

        return mplew.getPacket();
    }

    public static byte[] messengerNote(String text, int mode, int mode2) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.MESSENGER);
		mplew.write(mode);
        mplew.writeMapleAsciiString(text);
        mplew.write(mode2);

        return mplew.getPacket();
    }

    public static byte[] messengerOpen(byte type, List<MapleCharacter> chars) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.MESSENGER_OPEN);
		mplew.write(type); //7 in messenger open ui 8 new ui
        if (chars.isEmpty()) {
            mplew.writeShort(0);
        }
        for (MapleCharacter chr : chars) {
            mplew.write(1);
            mplew.writeInt(chr.getId());
            mplew.writeInt(0); //likes
            mplew.writeLong(0); //some time
            mplew.writeMapleAsciiString(chr.getName());
            PacketHelper.addCharLook(mplew, chr, true, false);
        }

        return mplew.getPacket();
    }

    public static byte[] messengerCharInfo(MapleCharacter chr) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.MESSENGER);
		mplew.write(0x0B);
        mplew.writeMapleAsciiString(chr.getName());
        mplew.writeInt(chr.getJob());
        mplew.writeInt(chr.getFame());
        mplew.writeInt(0); //likes
        MapleGuild gs = World.Guild.getGuild(chr.getGuildId());
        mplew.writeMapleAsciiString(gs != null ? gs.getName() : "-");
        MapleGuildAlliance alliance = World.Alliance.getAlliance(gs.getAllianceId());
        mplew.writeMapleAsciiString(alliance != null ? alliance.getName() : "");
        mplew.write(2);

        return mplew.getPacket();
    }

    public static byte[] removeFromPackageList(boolean remove, int Package) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.PACKAGE_OPERATION);
		mplew.write(24);
        mplew.writeInt(Package);
        mplew.write(remove ? 3 : 4);

        return mplew.getPacket();
    }

    public static byte[] sendPackageMSG(byte operation, List<MaplePackageActions> packages) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.PACKAGE_OPERATION);
		mplew.write(operation);

        switch (operation) {
            case 9:
                mplew.write(1);
                break;
            case 10:
                mplew.write(0);
                mplew.write(packages.size());

                for (MaplePackageActions dp : packages) {
                    mplew.writeInt(dp.getPackageId());
                    mplew.writeAsciiString(dp.getSender(), 13);
                    mplew.writeInt(dp.getMesos());
                    mplew.writeLong(PacketHelper.getTime(dp.getSentTime()));
                    mplew.writeZeroBytes(205);

                    if (dp.getItem() != null) {
                        mplew.write(1);
                        PacketHelper.addItemInfo(mplew, dp.getItem());
                    } else {
                        mplew.write(0);
                    }
                }
                mplew.write(0);
        }

        return mplew.getPacket();
    }

    public static byte[] getKeymap(MapleKeyLayout layout) {
        MaplePacketWriter outPacket = new MaplePacketWriter(SendPacketOpcode.KEYMAP);
        layout.writeData(outPacket);

        return outPacket.getPacket();
    }

    public static byte[] petAutoHP(int itemId) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.PET_AUTO_HP);
		mplew.writeInt(itemId);

        return mplew.getPacket();
    }

    public static byte[] petAutoMP(int itemId) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.PET_AUTO_MP);
		mplew.writeInt(itemId);

        return mplew.getPacket();
    }

    public static byte[] petAutoCure(int itemId) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.PET_AUTO_CURE);
		mplew.writeInt(itemId);

        return mplew.getPacket();
    }

    public static byte[] petAutoBuff(int skillId) {
        MaplePacketWriter mplew = new MaplePacketWriter();

        //mplew.writeShort(SendPacketOpcode.PET_AUTO_BUFF);
		mplew.writeInt(skillId);

        return mplew.getPacket();
    }

    public static void addRingInfo(MaplePacketWriter mplew, List<MapleRing> rings) {
        mplew.write(rings.size());
        for (MapleRing ring : rings) {
            mplew.writeInt(1);
            mplew.writeLong(ring.getRingId());
            mplew.writeLong(ring.getPartnerRingId());
            mplew.writeInt(ring.getItemId());
        }
    }

    public static void addMRingInfo(MaplePacketWriter mplew, List<MapleRing> rings, MapleCharacter chr) {
        mplew.write(rings.size());
        for (MapleRing ring : rings) {
            mplew.writeInt(1);
            mplew.writeInt(chr.getId());
            mplew.writeInt(ring.getPartnerChrId());
            mplew.writeInt(ring.getItemId());
        }
    }

    public static byte[] getBuffBar(long millis) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.BUFF_BAR);
		mplew.writeLong(millis);

        return mplew.getPacket();
    }

    public static byte[] getBoosterFamiliar(int cid, int familiar, int id) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.BOOSTER_FAMILIAR);
		mplew.writeInt(cid);
        mplew.writeInt(familiar);
        mplew.writeLong(id);
        mplew.write(0);

        return mplew.getPacket();
    }

    static {
        DEFAULT_BUFFMASK |= MapleBuffStat.ENERGY_CHARGE.getValue();
        DEFAULT_BUFFMASK |= MapleBuffStat.DASH_SPEED.getValue();
        DEFAULT_BUFFMASK |= MapleBuffStat.DASH_JUMP.getValue();
        DEFAULT_BUFFMASK |= MapleBuffStat.MONSTER_RIDING.getValue();
        DEFAULT_BUFFMASK |= MapleBuffStat.SPEED_INFUSION.getValue();
        DEFAULT_BUFFMASK |= MapleBuffStat.HOMING_BEACON.getValue();
        DEFAULT_BUFFMASK |= MapleBuffStat.DEFAULT_BUFFSTAT.getValue();
    }

    public static byte[] viewSkills(MapleCharacter chr) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.TARGET_SKILL);
        List skillz = new ArrayList();
        for (Skill sk : chr.getSkills().keySet()) {
            if ((sk.canBeLearnedBy(chr.getJob())) && (GameConstants.canSteal(sk)) && (!skillz.contains(Integer.valueOf(sk.getId())))) {
                skillz.add(Integer.valueOf(sk.getId()));
            }
        }
        mplew.write(1);
        mplew.writeInt(chr.getId());
        mplew.writeInt(skillz.isEmpty() ? 2 : 4);
        mplew.writeInt(chr.getJob());
        mplew.writeInt(skillz.size());
        for (Iterator i$ = skillz.iterator(); i$.hasNext();) {
            int i = ((Integer) i$.next()).intValue();
            mplew.writeInt(i);
        }

        return mplew.getPacket();
    }

    public static class InteractionPacket {

        public static byte[] getTradeInvite(MapleCharacter c) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.PLAYER_INTERACTION);
            mplew.write(Interaction.INVITE_TRADE.action);
            mplew.write(4);//was 3
            mplew.writeMapleAsciiString(c.getName());
//            mplew.writeInt(c.getLevel());
            mplew.writeInt(c.getJob());
            return mplew.getPacket();
        }

        public static byte[] getTradeMesoSet(byte number, long meso) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.PLAYER_INTERACTION);
            mplew.write(Interaction.UPDATE_MESO.action);
            mplew.write(number);
            mplew.writeLong(meso);
            return mplew.getPacket();
        }
        
        public static byte[] gachaponMessage(Item item, String town, MapleCharacter player) {
        	final MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.SERVERMESSAGE);
        	mplew.write(0x0B);
        	mplew.writeMapleAsciiString(player.getName() + " : got a(n)");
        	mplew.writeInt(0); //random?
        	mplew.writeMapleAsciiString(town);
        	PacketHelper.addItemInfo(mplew, item);
        	return mplew.getPacket();	
        }

        public static byte[] getTradeItemAdd(byte number, Item item) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.PLAYER_INTERACTION);
            mplew.write(Interaction.SET_ITEMS.action);
            mplew.write(number);
            mplew.write(item.getPosition());
            PacketHelper.addItemInfo(mplew, item);

            return mplew.getPacket();
        }

        public static byte[] getTradeStart(MapleClient c, MapleTrade trade, byte number) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.PLAYER_INTERACTION);
//            mplew.write(PlayerInteractionHandler.Interaction.START_TRADE.action);
//            if (number != 0){//13 a0
////                mplew.write(HexTool.getByteArrayFromHexString("13 01 01 03 FE 53 00 00 40 08 00 00 00 E2 7B 00 00 01 E9 50 0F 00 03 62 98 0F 00 04 56 BF 0F 00 05 2A E7 0F 00 07 B7 5B 10 00 08 3D 83 10 00 09 D3 D1 10 00 0B 13 01 16 00 11 8C 1F 11 00 12 BF 05 1D 00 13 CB 2C 1D 00 31 40 6F 11 00 32 6B 46 11 00 35 32 5C 19 00 37 20 E2 11 00 FF 03 B6 98 0F 00 05 AE 0A 10 00 09 CC D0 10 00 FF FF 00 00 00 00 13 01 16 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 0B 00 4D 6F 6D 6F 6C 6F 76 65 73 4B 48 40 08"));
//                mplew.write(19);
//                mplew.write(1);
//                PacketHelper.addCharLook(mplew, trade.getPartner().getChr(), false);
//                mplew.writeMapleAsciiString(trade.getPartner().getChr().getName());
//                mplew.writeShort(trade.getPartner().getChr().getJob());
//            }else{
            mplew.write(20);
            mplew.write(4);
            mplew.write(2);
            mplew.write(number);

            if (number == 1) {
                mplew.write(0);
                PacketHelper.addCharLook(mplew, trade.getPartner().getChr(), false, false);
                mplew.writeMapleAsciiString(trade.getPartner().getChr().getName());
                mplew.writeShort(trade.getPartner().getChr().getJob());
            }
            mplew.write(number);
            PacketHelper.addCharLook(mplew, c.getCharacter(), false, false);
            mplew.writeMapleAsciiString(c.getCharacter().getName());
            mplew.writeShort(c.getCharacter().getJob());
            mplew.write(255);
//            }
            return mplew.getPacket();
        }

        public static byte[] getTradeConfirmation() {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.PLAYER_INTERACTION);
            mplew.write(Interaction.CONFIRM_TRADE.action);

            return mplew.getPacket();
        }

        public static byte[] TradeMessage(byte UserSlot, byte message) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.PLAYER_INTERACTION);
            mplew.write(Interaction.EXIT.action);
//            mplew.write(25);//new v141
            mplew.write(UserSlot);
            mplew.write(message);

            return mplew.getPacket();
        }

        public static byte[] getTradeCancel(byte UserSlot, int unsuccessful) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.PLAYER_INTERACTION);
            mplew.write(Interaction.EXIT.action);
            mplew.write(UserSlot);
            mplew.write(7);//was2

            return mplew.getPacket();
        }
    }

    public static class NPCPacket {

        public static byte[] spawnNPC(MapleNPC life, boolean show) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.SPAWN_NPC);
            mplew.writeInt(life.getObjectId());
            mplew.writeInt(life.getId());
            mplew.writeShort(life.getPosition().x);
            mplew.writeShort(life.getCy());
            mplew.write(life.getF() == 1 ? 0 : 1);
            mplew.writeShort(life.getFh());
            mplew.writeShort(life.getRx0());
            mplew.writeShort(life.getRx1());
            mplew.write(show ? 1 : 0);
            mplew.writeInt(0);//new 143
            mplew.write(0);
            mplew.writeInt(-1);
            mplew.writeZeroBytes(11);
            mplew.writeInt(0);

            return mplew.getPacket();
        }
        
        public static byte[] getMapSelection(final int npcid, final String sel) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.NPC_TALK);
            mplew.write(4);
            mplew.writeInt(npcid);
            mplew.writeShort(GameConstants.GMS ? 0x11 : 0x10);
            mplew.writeInt(npcid == 2083006 ? 1 : 0); //neo city
            mplew.writeInt(npcid == 9010022 ? 1 : 0); //dimensional
            mplew.writeMapleAsciiString(sel);
            
            return mplew.getPacket();
        }

        public static byte[] removeNPC(int objectid) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.REMOVE_NPC);
            mplew.writeInt(objectid);

            return mplew.getPacket();
        }

        public static byte[] removeNPCController(int objectid) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.SPAWN_NPC_REQUEST_CONTROLLER);
            mplew.write(0);
            mplew.writeInt(objectid);

            return mplew.getPacket();
        }

        public static byte[] spawnNPCRequestController(MapleNPC life, boolean MiniMap) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.SPAWN_NPC_REQUEST_CONTROLLER);
            mplew.write(1);
            mplew.writeInt(life.getObjectId());
            mplew.writeInt(life.getId());
            mplew.writeShort(life.getPosition().x);
            mplew.writeShort(life.getCy());
            mplew.write(life.getF() == 1 ? 0 : 1);
            mplew.writeShort(life.getFh());
            mplew.writeShort(life.getRx0());
            mplew.writeShort(life.getRx1());
            mplew.write(MiniMap ? 1 : 0);
            mplew.writeInt(0);//new 143
            mplew.write(0);
            mplew.writeInt(-1);
            mplew.writeZeroBytes(11);
            mplew.writeInt(0);
            
            return mplew.getPacket();
        }

        public static byte[] toggleNPCShow(int oid, boolean hide, boolean viewNameTag) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.NPC_TOGGLE_VISIBLE);
            mplew.writeInt(oid);
            mplew.write(hide ? 0 : 1);        // bView
            mplew.write(viewNameTag ? 0 : 1); // bViewNameTag
            
            return mplew.getPacket();
        }

        public static byte[] setNPCSpecialAction(int oid, String action, int duration, boolean localAct) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.NPC_SET_SPECIAL_ACTION);
            mplew.writeInt(oid);
            mplew.writeMapleAsciiString(action); // sName
            mplew.writeInt(duration);            // tDuration
            mplew.write(localAct ? 0 : 1);       // bLocalAct
            
            return mplew.getPacket();
        }

        public static byte[] setNPCForceMove(int oid, int forceX, int moveX, int speedRate) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.NPC_SET_FORCE_MOVE);
            mplew.writeInt(oid);
            mplew.writeInt(forceX);    // nForceX
            mplew.writeInt(moveX);     // nMoveX
            mplew.writeInt(speedRate); // nSpeedRate
            
            return mplew.getPacket();
        }

        public static byte[] setNPCScriptable() {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.NPC_SET_SCRIPT);

            List<Pair<Integer, String>> npcs = new LinkedList();
            npcs.add(new Pair<>(9070006, "Why...why has this happened to me? My knightly honor... My knightly pride..."));
            npcs.add(new Pair<>(9000021, "Are you enjoying the event?"));

            mplew.write(npcs.size());
            for (Pair<Integer, String> s : npcs) {
                mplew.writeInt(s.getLeft());
                mplew.writeMapleAsciiString(s.getRight());
                mplew.writeInt(0);
//                mplew.writeInt(Integer.MAX_VALUE);
                mplew.write(0);
            }
            return mplew.getPacket();
        }

        public static byte[] getNPCTalk(int npc, byte msgType, String talk, String endBytes, byte type) {
            return getNPCTalk(npc, msgType, talk, endBytes, type, npc);
        }

        public static byte[] getNPCTalk(int npc, byte msgType, String talk, String endBytes, byte type, int diffNPC) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.NPC_TALK);
            mplew.write(4);
            mplew.writeInt(npc);
            mplew.write(0);//v141, boolean if true another int
            mplew.write(msgType);
            mplew.writeShort(type); // mask
            if ((type & 0x4) != 0) {
                mplew.writeInt(diffNPC);
            }
            mplew.writeMapleAsciiString(talk);
            mplew.write(HexTool.getByteArrayFromHexString(endBytes));
            
            return mplew.getPacket();
        }

        public static byte[] getEnglishQuiz(int npc, byte type, int diffNPC, String talk, String endBytes) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.NPC_TALK);
            mplew.write(4);
            mplew.writeInt(npc);
            mplew.write(10); //not sure
            mplew.write(type);
            if ((type & 0x4) != 0) {
                mplew.writeInt(diffNPC);
            }
            mplew.writeMapleAsciiString(talk);
            mplew.write(HexTool.getByteArrayFromHexString(endBytes));

            return mplew.getPacket();
        }

        public static byte[] getAdviceTalk(String[] wzinfo) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.NPC_TALK);
            mplew.write(8);
            mplew.writeInt(0);
            mplew.write(1);
            mplew.write(1);
            mplew.write(wzinfo.length);
            for (String data : wzinfo) {
                mplew.writeMapleAsciiString(data);
            }
            return mplew.getPacket();
        }

        public static byte[] getSlideMenu(int npcid, int type, int lasticon, String sel) {
            //Types: 0 - map selection 1 - neo city map selection 2 - korean map selection 3 - tele rock map selection 4 - dojo buff selection
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.NPC_TALK);
            mplew.write(4); //slide menu
            mplew.writeInt(npcid);
            mplew.write(0);
            mplew.writeShort(0x11);//0x12
            mplew.writeInt(type); //menu type
            mplew.writeInt(type == 0 ? lasticon : 0); //last icon on menu
            mplew.writeMapleAsciiString(sel);

            return mplew.getPacket();
        }

        public static byte[] getNPCTalkStyle(int npc, String talk, int[] args, boolean second) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.NPC_TALK);
            mplew.write(4);
            mplew.writeInt(npc);
            mplew.write(0);
            mplew.writeShort(9);
            mplew.writeShort(second ? 1 : 0);//new143
            mplew.writeMapleAsciiString(talk);
            mplew.write(args.length);

            for (int i = 0; i < args.length; i++) {
                mplew.writeInt(args[i]);
            }
            return mplew.getPacket();
        }

        public static byte[] getNPCTalkNum(int npc, String talk, int def, int min, int max) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.NPC_TALK);
            mplew.write(4);
            mplew.writeInt(npc);
            mplew.write(0);//new 142
            mplew.writeShort(4);
            mplew.writeMapleAsciiString(talk);
            mplew.writeInt(def);
            mplew.writeInt(min);
            mplew.writeInt(max);
            mplew.writeInt(0);

            return mplew.getPacket();
        }

        public static byte[] getNPCTalkText(int npc, String talk) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.NPC_TALK);
            mplew.write(4);
            mplew.writeInt(npc);
            mplew.write(0);
            mplew.writeShort(3); //3 regular 6 quiz
           // mplew.write(0); //Removed in v144
            mplew.writeMapleAsciiString(talk);
            mplew.writeInt(0);
            mplew.writeInt(0);

            return mplew.getPacket();
        }

        public static byte[] getNPCTalkQuiz(int npc, String caption, String talk, int time) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.NPC_TALK);
            mplew.write(4);
            mplew.writeInt(npc);
            mplew.write(0);
            mplew.writeShort(6);
            mplew.write(0);
            mplew.writeMapleAsciiString(caption);
            mplew.writeMapleAsciiString(talk);
            mplew.writeShort(0);
            mplew.writeInt(0);
            mplew.writeInt(0xF); //no idea
            mplew.writeInt(time); //seconds

            return mplew.getPacket();
        }

        public static byte[] getSelfTalkText(String text) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.NPC_TALK);
            mplew.write(3);
            mplew.writeInt(0);
            mplew.writeInt(1);
            mplew.writeShort(0);
            mplew.write(17);
            mplew.write(0);
            mplew.writeMapleAsciiString(text);
            mplew.write(0);
            mplew.write(1);
            mplew.writeInt(0);
            return mplew.getPacket();
        }

        public static byte[] getNPCTutoEffect(String effect) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.NPC_TALK);
            mplew.write(3);
            mplew.writeInt(0);
            mplew.write(0);
            mplew.write(1);
            mplew.writeShort(257);
            mplew.writeMapleAsciiString(effect);
            return mplew.getPacket();
        }

        public static byte[] getCutSceneSkip() {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.NPC_TALK);
            mplew.write(3);
            mplew.writeInt(0);
            mplew.write(1);
            mplew.writeInt(0);
            mplew.write(2);
            mplew.write(5);
            mplew.writeInt(9010000); //Maple administrator
            mplew.writeMapleAsciiString("Would you like to skip the tutorial cutscenes?");
            return mplew.getPacket();
        }

        public static byte[] getDemonSelection() {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.NPC_TALK);
            mplew.write(3);
            mplew.writeInt(0);
            mplew.write(1);
            mplew.writeInt(2159311); //npc
            mplew.write(0x16);
            mplew.write(1);
            mplew.writeShort(1);
            mplew.writeZeroBytes(8);
            return mplew.getPacket();
        }

        public static byte[] getAngelicBusterAvatarSelect(int npc) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.NPC_TALK);
            mplew.write(4);
            mplew.writeInt(npc);
            mplew.write(0);
            mplew.writeShort(0x17);
            return mplew.getPacket();
        }

        public static byte[] getEvanTutorial(String data) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.NPC_TALK);
            mplew.write(8);
            mplew.writeInt(0);
            mplew.write(1);
            mplew.write(1);
            mplew.write(1);
            mplew.writeMapleAsciiString(data);

            return mplew.getPacket();
        }

        public static byte[] getNPCShop(int sid, MapleShop shop, MapleClient c) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.OPEN_NPC_SHOP);
            mplew.writeInt(0);
            mplew.write(0);
            mplew.writeInt(sid);
            //mplew.write(HexTool.getByteArrayFromHexString("00 00 00 00 00 04 00 F0 DD 13 00 32 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 40 E0 FD 3B 37 4F 01 00 80 05 BB 46 E6 17 02 00 00 00 00 00 00 00 00 00 00 00 00 00 00 01 00 01 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 75 96 8F 00 00 00 00 00 76 96 8F 00 00 00 00 00 77 96 8F 00 00 00 00 00 78 96 8F 00 00 00 00 00 04 05 14 00 32 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 40 E0 FD 3B 37 4F 01 00 80 05 BB 46 E6 17 02 00 00 00 00 00 00 00 00 00 00 00 00 00 00 01 00 01 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 75 96 8F 00 00 00 00 00 76 96 8F 00 00 00 00 00 77 96 8F 00 00 00 00 00 78 96 8F 00 00 00 00 00 15 2C 14 00 32 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 40 E0 FD 3B 37 4F 01 00 80 05 BB 46 E6 17 02 00 00 00 00 00 00 00 00 00 00 00 00 00 00 01 00 01 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 75 96 8F 00 00 00 00 00 76 96 8F 00 00 00 00 00 77 96 8F 00 00 00 00 00 78 96 8F 00 00 00 00 00 25 53 14 00 F4 01 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 40 E0 FD 3B 37 4F 01 00 80 05 BB 46 E6 17 02 00 00 00 00 00 00 00 00 00 00 00 00 00 00 01 00 01 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 75 96 8F 00 00 00 00 00 76 96 8F 00 00 00 00 00 77 96 8F 00 00 00 00 00 78 96 8F 00 00 00 00 00"));
            PacketHelper.addShopInfo(mplew, shop, c);

            return mplew.getPacket();
        }
        
        public static byte[] confirmShopTransaction(byte code, MapleShop shop, MapleClient c, int indexBought) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.CONFIRM_SHOP_TRANSACTION);
            mplew.write(code);
            if (code == 8) { //was 5
                mplew.writeInt(0);
                mplew.writeInt(shop.getNpcId());
                PacketHelper.addShopInfo(mplew, shop, c);
            } else {
                mplew.write(indexBought >= 0 ? 1 : 0);
                if (indexBought >= 0) {
                    mplew.writeInt(indexBought);
                    mplew.writeInt(0);
                    mplew.writeShort(0);
                } else {
                    mplew.writeInt(0);
                	mplew.write(0);
                }
            }

            return mplew.getPacket();
        }

        public static byte[] getStorage(int npcId, byte slots, Collection<Item> items, long meso) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.OPEN_STORAGE);
            mplew.write(22);
            mplew.writeInt(npcId);
            mplew.write(slots);
            mplew.writeShort(126);
            mplew.writeShort(0);
            mplew.writeInt(0);
            mplew.writeLong(meso);
            mplew.writeShort(0);
            mplew.write((byte) items.size());
            for (Item item : items) {
                PacketHelper.addItemInfo(mplew, item);
            }
            mplew.writeZeroBytes(2);//4

            return mplew.getPacket();
        }

        public static byte[] getStorageFull() {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.OPEN_STORAGE);
            mplew.write(17);

            return mplew.getPacket();
        }

        public static byte[] mesoStorage(byte slots, long meso) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.OPEN_STORAGE);
            mplew.write(19);
            mplew.write(slots);
            mplew.writeShort(2);
            mplew.writeShort(0);
            mplew.writeInt(0);
            mplew.writeLong(meso);

            return mplew.getPacket();
        }

        public static byte[] arrangeStorage(byte slots, Collection<Item> items, boolean changed) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.OPEN_STORAGE);
            mplew.write(15);
            mplew.write(slots);
            mplew.write(124);
            mplew.writeZeroBytes(10);
            mplew.write(items.size());
            for (Item item : items) {
                PacketHelper.addItemInfo(mplew, item);
            }
            mplew.write(0);
            return mplew.getPacket();
        }

        public static byte[] storeStorage(byte slots, MapleInventoryType type, Collection<Item> items) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.OPEN_STORAGE);
            mplew.write(13);
            mplew.write(slots);
            mplew.writeShort(type.getBitfieldEncoding());
            mplew.writeShort(0);
            mplew.writeInt(0);
            mplew.write(items.size());
            for (Item item : items) {
                PacketHelper.addItemInfo(mplew, item);
            }
            return mplew.getPacket();
        }

        public static byte[] takeOutStorage(byte slots, MapleInventoryType type, Collection<Item> items) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.OPEN_STORAGE);
            mplew.write(9);
            mplew.write(slots);
            mplew.writeShort(type.getBitfieldEncoding());
            mplew.writeShort(0);
            mplew.writeInt(0);
            mplew.write(items.size());
            for (Item item : items) {
                PacketHelper.addItemInfo(mplew, item);
            }
            return mplew.getPacket();
        }
    }

    public static class SummonPacket {

        public static byte[] spawnSummon(MapleSummon summon, boolean animated) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.SPAWN_SUMMON);
            mplew.writeInt(summon.getOwnerId());
            mplew.writeInt(summon.getObjectId());
            mplew.writeInt(summon.getSkill());
            mplew.write(summon.getOwnerLevel() - 1);
            mplew.write(summon.getSkillLevel());
            mplew.writePos(summon.getPosition());
            mplew.write((summon.getSkill() == 32111006) || (summon.getSkill() == 33101005) ? 5 : 4);// Summon Reaper Buff - Call of the Wild
            if ((summon.getSkill() == 35121003) && (summon.getOwner().getMap() != null)) {//Giant Robot SG-88
                mplew.writeShort(summon.getOwner().getMap().getFootholds().findBelow(summon.getPosition()).getId());
            } else {
                mplew.writeShort(summon.getOwner().getMap().getFootholds().findBelow(summon.getPosition()).getId());
            }
            mplew.write(summon.getMovementType().getValue());
            mplew.write(summon.getSummonType());
            mplew.write(animated ? 1 : 0);
            mplew.writeInt(0); // dwMobID
            mplew.write(0); // bFlyMob
            mplew.write(1); // bBeforeFirstAttack
            mplew.writeInt(0); // nLookID
            mplew.writeInt(0); // nBulletID
            MapleCharacter chr = summon.getOwner();
            mplew.write((summon.getSkill() == 4341006) && (chr != null) ? 1 : 0); // Mirrored Target
            if ((summon.getSkill() == 4341006) && (chr != null)) { // Mirrored Target
                PacketHelper.addCharLook(mplew, chr, true, false);
            }
            if (summon.getSkill() == 35111002) {// Rock 'n Shock
                mplew.write(0);
            }
            if (summon.getSkill() == 42111003) {
                mplew.writeZeroBytes(8);
            }
            if (summon.getSkill() == 3121013) {
                chr.dropMessage(6,"no");
            }
            
            mplew.write(0); // bJaguarActive
            mplew.writeInt(0); // tSummonTerm

            return mplew.getPacket();
        }

        public static byte[] removeSummon(int ownerId, int objId) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.REMOVE_SUMMON);
            mplew.writeInt(ownerId);
            mplew.writeInt(objId);
            mplew.write(10);

            return mplew.getPacket();
        }

        public static byte[] removeSummon(MapleSummon summon, boolean animated) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.REMOVE_SUMMON);
            mplew.writeInt(summon.getOwnerId());
            mplew.writeInt(summon.getObjectId());
            if (animated) {
                switch (summon.getSkill()) {
                    case 35121003:
                        mplew.write(10);
                        break;
                    case 33101008:
                    case 35111001:
                    case 35111002:
                    case 35111005:
                    case 35111009:
                    case 35111010:
                    case 35111011:
                    case 35121009:
                    case 35121010:
                    case 35121011:
                        mplew.write(5);
                        break;
                    default:
                        mplew.write(4);
                        break;
                }
            } else {
                mplew.write(1);
            }

            return mplew.getPacket();
        }

        public static byte[] moveSummon(int cid, int oid, Point startPos, List<LifeMovementFragment> moves) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.MOVE_SUMMON);
            mplew.writeInt(cid);
            mplew.writeInt(oid);
            mplew.writeInt(0);
            mplew.writePos(startPos);
            mplew.writeInt(0);
            PacketHelper.serializeMovementList(mplew, moves);

            return mplew.getPacket();
        }

        public static byte[] summonAttack(int cid, int summonSkillId, byte animation, List<Pair<Integer, Integer>> allDamage, int level, boolean darkFlare) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.SUMMON_ATTACK);
            mplew.writeInt(cid);
            mplew.writeInt(summonSkillId);
            mplew.write(level - 1);
            mplew.write(animation);
            mplew.write(allDamage.size());
            for (Pair attackEntry : allDamage) {
                mplew.writeInt(((Integer) attackEntry.left).intValue());
                mplew.write(7);
                mplew.writeInt(((Integer) attackEntry.right).intValue());
            }
            mplew.write(darkFlare ? 1 : 0);

            return mplew.getPacket();
        }

        public static byte[] pvpSummonAttack(int cid, int playerLevel, int oid, int animation, Point pos, List<AttackPair> attack) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.SUMMON_PVP_ATTACK);
            mplew.writeInt(cid);
            mplew.writeInt(oid);
            mplew.write(playerLevel);
            mplew.write(animation);
            mplew.writePos(pos);
            mplew.writeInt(0);
            mplew.write(attack.size());
            for (AttackPair p : attack) {
                mplew.writeInt(p.objectid);
                mplew.writePos(p.point);
                mplew.write(p.attack.size());
                mplew.write(0);
                for (Pair atk : p.attack) {
                    mplew.writeInt(((Integer) atk.left).intValue());
                }
            }

            return mplew.getPacket();
        }

        public static byte[] summonSkill(int cid, int summonSkillId, int newStance) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.SUMMON_SKILL);
            mplew.writeInt(cid);
            mplew.writeInt(summonSkillId);
            mplew.write(newStance);

            return mplew.getPacket();
        }

        public static byte[] damageSummon(int cid, int summonSkillId, int damage, int unkByte, int monsterIdFrom) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.DAMAGE_SUMMON);
            mplew.writeInt(cid);
            mplew.writeInt(summonSkillId);
            mplew.write(unkByte);
            mplew.writeInt(damage);
            mplew.writeInt(monsterIdFrom);
            mplew.write(0);

            return mplew.getPacket();
        }
    }

    public static class UIPacket {

        public static byte[] getDirectionStatus(boolean enable) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.DIRECTION_STATUS);
            mplew.write(enable ? 1 : 0);

            return mplew.getPacket();
        }
        
        public static byte[] openUI(int type) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.OPEN_UI);
            mplew.write(type);

            return mplew.getPacket();
        }

        public static byte[] sendRepairWindow(int npc) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.OPEN_UI_OPTION);
            mplew.writeInt(33);
            mplew.writeInt(npc);
            mplew.writeInt(0);//new143

            return mplew.getPacket();
        }

        public static byte[] sendJewelCraftWindow(int npc) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.OPEN_UI_OPTION);
            mplew.writeInt(104);
            mplew.writeInt(npc);
            mplew.writeInt(0);//new143

            return mplew.getPacket();
        }

        public static byte[] startAzwan(int npc) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.OPEN_UI_OPTION);
            mplew.writeInt(70);
            mplew.writeInt(npc);
            mplew.writeInt(0);//new143
            return mplew.getPacket();
        }

        public static byte[] openUIOption(int type, int npc) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.OPEN_UI_OPTION);
            mplew.writeInt(type);
            mplew.writeInt(npc);
            return mplew.getPacket();
        }

        public static byte[] sendDojoResult(int points) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.OPEN_UI_OPTION);
            mplew.writeInt(0x48);
            mplew.writeInt(points);

            return mplew.getPacket();
        }

        public static byte[] sendAzwanResult() {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.OPEN_UI_OPTION);
            mplew.writeInt(0x45);
            mplew.writeInt(0);

            return mplew.getPacket();
        }

        public static byte[] DublStart(boolean dark) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.SHOW_SPECIAL_EFFECT);
            mplew.write(0x28);
            mplew.write(dark ? 1 : 0);

            return mplew.getPacket();
        }

        public static byte[] DublStartAutoMove() {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.MOVE_SCREEN);
            mplew.write(3);
            mplew.writeInt(2);

            return mplew.getPacket();
        }

        public static byte[] IntroLock(boolean enable) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.INTRO_LOCK);
            mplew.write(enable ? 1 : 0);
            mplew.writeInt(0);

            return mplew.getPacket();
        }

        public static byte[] IntroEnableUI(int enable) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.INTRO_ENABLE_UI);
            mplew.write(enable > 0 ? 1 : 0);
            if (enable > 0) {
                mplew.writeShort(enable);
                mplew.write(0);
            } else {
            	//mplew.write(enable < 0 ? 1 : 0);
            }
            
            System.out.println(mplew.toString());
            return mplew.getPacket();
        }

        public static byte[] IntroDisableUI(boolean enable) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.INTRO_DISABLE_UI);
            mplew.write(enable ? 1 : 0);

            return mplew.getPacket();
        }

        public static byte[] summonHelper(boolean summon) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.SUMMON_HINT);
            mplew.write(summon ? 1 : 0);

            return mplew.getPacket();
        }

        public static byte[] summonMessage(int type) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.SUMMON_HINT_MSG);
            mplew.write(1);
            mplew.writeInt(type);
            mplew.writeInt(7000);

            return mplew.getPacket();
        }

        public static byte[] summonMessage(String message) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.SUMMON_HINT_MSG);
            mplew.write(0);
            mplew.writeMapleAsciiString(message);
            mplew.writeInt(200);
            mplew.writeShort(0);
            mplew.writeInt(10000);

            return mplew.getPacket();
        }

        public static byte[] getDirectionInfo(int type, int value, int x) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.DIRECTION_INFO);
             if (x > 0) {
                mplew.write(x);
            }
            mplew.write((byte) type);
            mplew.writeInt(value);

                        
            return mplew.getPacket();
        }

        public static byte[] getDirectionInfo(int type, int value) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.DIRECTION_INFO);
            mplew.write((byte) type);
            mplew.writeInt(value);
                        
            return mplew.getPacket();
        }
        
        /**
         * Delays map events for a {@code delay} of milliseconds.
         * @param delay
         * @return
         * @see CInGameDirectionEvent::OnInGameDirectionEvent
         */
        public static byte[] delayDirectionInfo(int delay) {
        	MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.DIRECTION_INFO);
        	mplew.write(1);
            mplew.writeInt(delay);
                        
            return mplew.getPacket();
        }
        
        /**
         * Forces the character to move in a certain direction during map events.
         * @param input
         * @return
         */
        public static byte[] forceMoveCharacter(int input) {
        	MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.DIRECTION_INFO);
        	mplew.write(3);
            mplew.writeInt(input);
                        
            return mplew.getPacket();
        }
        
        public static byte[] getDirectionInfo(String data, int value, int x, int y, int a, int b) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.DIRECTION_INFO);
            mplew.write(2);
            mplew.writeMapleAsciiString(data);
            mplew.writeInt(value);
            mplew.writeInt(x);
            mplew.writeInt(y);
            mplew.write(a);
            if (a > 0) {
                mplew.writeInt(0);
            }
            mplew.write(b);
            if (b > 1) {
                mplew.writeInt(0);
            }
            
            return mplew.getPacket();
        }

        public static byte[] getDirectionEffect(String data, int value, int x, int y) {
            return getDirectionEffect(data, value, x, y, 0);
        }

        public static byte[] getDirectionEffect(String data, int value, int x, int y, int npc) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.DIRECTION_INFO);
            mplew.write(2);
            mplew.writeMapleAsciiString(data);
            mplew.writeInt(value);
            mplew.writeInt(x);
            mplew.writeInt(y);
            mplew.write(1);
            mplew.writeInt(0);
            mplew.write(1);
            mplew.writeInt(npc); // dwNpcID
            mplew.write(1); // bNotOrigin
            mplew.write(0);

            return mplew.getPacket();
        }

        public static byte[] getDirectionInfoNew(byte x, int value, int a, int b) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.DIRECTION_INFO);
            mplew.write(5);
            mplew.write(x);
            mplew.writeInt(value);
            if (x == 0) {
                mplew.writeInt(a);
                mplew.writeInt(b);
            }


            return mplew.getPacket();
        }
        
        public static byte[] getDirectionInfoNew2(byte x, int value) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.DIRECTION_INFO);
            mplew.write(5);
            mplew.write(x);
            mplew.writeInt(value);


            return mplew.getPacket();
        }

        public static byte[] getDirectionEffect1(String data, int value, int x, int y, int npc) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.DIRECTION_INFO);
            mplew.write(2);
            mplew.writeAsciiString(data);
            mplew.writeInt(value);
            mplew.writeInt(x);
            mplew.writeInt(y);
            mplew.write(1);
            mplew.writeInt(npc);
            mplew.write(0);

            // Added for BeastTamer
            return mplew.getPacket();
        }

        public static byte[] getDirectionInfoNew(byte x, int value) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.DIRECTION_INFO);
            mplew.write(5);
            mplew.write(x);
            mplew.writeInt(value);
            if (x == 0) {
                mplew.writeInt(value);
                mplew.writeInt(value);
            }

            return mplew.getPacket();
        }

        public static byte[] moveScreen(int x) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.MOVE_SCREEN_X);
            mplew.writeInt(x);
            mplew.writeInt(0);
            mplew.writeInt(0);

            return mplew.getPacket();
        }

        public static byte[] screenDown() {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.MOVE_SCREEN_DOWN);

            return mplew.getPacket();
        }

        public static byte[] resetScreen() {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.RESET_SCREEN);

            return mplew.getPacket();
        }

        public static byte[] reissueMedal(int itemId, int type) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.REISSUE_MEDAL);
            mplew.write(type);
            mplew.writeInt(itemId);

            return mplew.getPacket();
        }

        public static byte[] playMovie(String data, boolean show) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.PLAY_MOVIE);
            mplew.writeMapleAsciiString(data);
            mplew.write(show ? 1 : 0);

            return mplew.getPacket();
        }

        public static byte[] setRedLeafStatus(int joejoe, int hermoninny, int littledragon, int ika) {
            //packet made to set status
            //should remove it and make a handler for it, it's a recv opcode
            /*
             * inPacket:
             * E2 9F 72 00
             * 5D 0A 73 01
             * E2 9F 72 00
             * 04 00 00 00
             * 00 00 00 00
             * 75 96 8F 00
             * 55 01 00 00
             * 76 96 8F 00
             * 00 00 00 00
             * 77 96 8F 00
             * 00 00 00 00
             * 78 96 8F 00
             * 00 00 00 00
             */
            MaplePacketWriter mplew = new MaplePacketWriter();

            //mplew.writeShort();
            mplew.writeInt(7512034); //no idea
            mplew.writeInt(24316509); //no idea
            mplew.writeInt(7512034); //no idea
            mplew.writeInt(4); //no idea
            mplew.writeInt(0); //no idea
            mplew.writeInt(9410165); //joe joe
            mplew.writeInt(joejoe); //amount points added
            mplew.writeInt(9410166); //hermoninny
            mplew.writeInt(hermoninny); //amount points added
            mplew.writeInt(9410167); //little dragon
            mplew.writeInt(littledragon); //amount points added
            mplew.writeInt(9410168); //ika
            mplew.writeInt(ika); //amount points added

            return mplew.getPacket();
        }

        public static byte[] sendRedLeaf(int points, boolean viewonly) {
            /*
             * inPacket:
             * 73 00 00 00
             * 0A 00 00 00
             * 01
             */
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.OPEN_UI_OPTION);
            mplew.writeInt(0x73);
            mplew.writeInt(points);
            mplew.write(viewonly ? 1 : 0); //if view only, then complete button is disabled

            return mplew.getPacket();
        }
    }

    public static class EffectPacket {

        public static byte[] showForeignEffect(int effect) {
            return showForeignEffect(-1, effect);
        }

        public static byte[] showForeignEffect(int charid, int effect) {
        	MaplePacketWriter mplew;
            if (charid == -1) {
            	mplew = new MaplePacketWriter(SendPacketOpcode.SHOW_SPECIAL_EFFECT);
            } else {
            	mplew = new MaplePacketWriter(SendPacketOpcode.SHOW_FOREIGN_EFFECT);
                mplew.writeInt(charid);
            }
            mplew.write(effect);

            System.out.println("showForeignEffect");
            return mplew.getPacket();
        }

        public static byte[] showItemLevelupEffect() {
            return showForeignEffect(18);
        }

        public static byte[] showForeignItemLevelupEffect(int cid) {
            return showForeignEffect(cid, 18);
        }

        public static byte[] showOwnDiceEffect(int skillid, int effectid, int effectid2, int level) {
            return showDiceEffect(-1, skillid, effectid, effectid2, level);
        }

        public static byte[] showDiceEffect(int charid, int skillid, int effectid, int effectid2, int level) {
        	MaplePacketWriter mplew;
            if (charid == -1) {
            	mplew = new MaplePacketWriter(SendPacketOpcode.SHOW_SPECIAL_EFFECT);
            } else {
            	mplew = new MaplePacketWriter(SendPacketOpcode.SHOW_FOREIGN_EFFECT);
                mplew.writeInt(charid);
            }
            mplew.write(3);
            mplew.writeInt(effectid);
            mplew.writeInt(effectid2);
            mplew.writeInt(skillid);
            mplew.write(level);
            mplew.write(0);
            mplew.writeZeroBytes(100);

            return mplew.getPacket();
        }

        public static byte[] useCharm(byte charmsleft, byte daysleft, boolean safetyCharm) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.SHOW_SPECIAL_EFFECT);
            mplew.write(8);
            mplew.write(safetyCharm ? 1 : 0);
            mplew.write(charmsleft);
            mplew.write(daysleft);
            if (!safetyCharm) {
                mplew.writeInt(0);
            }

            return mplew.getPacket();
        }

        public static byte[] Mulung_DojoUp2() {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.SHOW_SPECIAL_EFFECT);
            mplew.write(10);

            return mplew.getPacket();
        }

        public static byte[] showOwnHpHealed(int amount) {
            return showHpHealed(-1, amount);
        }

        /**
         * Sends a packet that shows the amount of HP you healed.
         * Usually sends after you use a skill such as Recovery.
         * 
         * @param charid This is the character's ID.
         * @param amount This is the amount of HP to display.
         * @return
         */
        public static byte[] showHpHealed(int charid, int amount) {
        	MaplePacketWriter mplew;
            if (charid == -1) {
            	mplew = new MaplePacketWriter(SendPacketOpcode.SHOW_SPECIAL_EFFECT);
            } else {
            	mplew = new MaplePacketWriter(SendPacketOpcode.SHOW_FOREIGN_EFFECT);
                mplew.writeInt(charid);
            }
            mplew.write(15); // This value changes between patches.
            mplew.write(amount);

            return mplew.getPacket();
        }

        public static byte[] showRewardItemAnimation(int itemId, String effect) {
            return showRewardItemAnimation(itemId, effect, -1);
        }

        public static byte[] showRewardItemAnimation(int itemId, String effect, int from_playerid) {
        	MaplePacketWriter mplew;
            if (from_playerid == -1) {
            	mplew = new MaplePacketWriter(SendPacketOpcode.SHOW_SPECIAL_EFFECT);
            } else {
            	mplew = new MaplePacketWriter(SendPacketOpcode.SHOW_FOREIGN_EFFECT);
                mplew.writeInt(from_playerid);
            }
            mplew.write(17);
            mplew.writeInt(itemId);
            mplew.write((effect != null) && (effect.length() > 0) ? 1 : 0);
            if ((effect != null) && (effect.length() > 0)) {
                mplew.writeMapleAsciiString(effect);
            }

            return mplew.getPacket();
        }

        public static byte[] showCashItemEffect(int itemId) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.SHOW_SPECIAL_EFFECT);
            mplew.write(23);
            mplew.writeInt(itemId);

            return mplew.getPacket();
        }

        public static byte[] ItemMaker_Success() {
            return ItemMaker_Success_3rdParty(-1);
        }

        public static byte[] ItemMaker_Success_3rdParty(int from_playerid) {
        	MaplePacketWriter mplew;
            if (from_playerid == -1) {
            	mplew = new MaplePacketWriter(SendPacketOpcode.SHOW_SPECIAL_EFFECT);
            } else {
            	mplew = new MaplePacketWriter(SendPacketOpcode.SHOW_FOREIGN_EFFECT);
                mplew.writeInt(from_playerid);
            }
            mplew.write(19);
            mplew.writeInt(0);

            return mplew.getPacket();
        }

        public static byte[] useWheel(byte charmsleft) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.SHOW_SPECIAL_EFFECT);
            mplew.write(24);
            mplew.write(charmsleft);

            return mplew.getPacket();
        }

        public static byte[] showOwnBuffEffect(int skillid, int effectid, int playerLevel, int skillLevel) {
            return showBuffEffect(-1, skillid, effectid, playerLevel, skillLevel, (byte) 3);
        }

        public static byte[] showOwnBuffEffect(int skillid, int effectid, int playerLevel, int skillLevel, byte direction) {
            return showBuffEffect(-1, skillid, effectid, playerLevel, skillLevel, direction);
        }

        public static byte[] showBuffEffect(int charid, int skillid, int effectid, int playerLevel, int skillLevel) {
            return showBuffEffect(charid, skillid, effectid, playerLevel, skillLevel, (byte) 3);
        }

        public static byte[] showBuffEffect(int charid, int skillid, int effectid, int playerLevel, int skillLevel, byte direction) {
        	MaplePacketWriter mplew;
            if (charid == -1) {
            	mplew = new MaplePacketWriter(SendPacketOpcode.SHOW_SPECIAL_EFFECT);
            } else {
            	mplew = new MaplePacketWriter(SendPacketOpcode.SHOW_FOREIGN_EFFECT);
                mplew.writeInt(charid);
            }
            mplew.write(effectid);
            mplew.writeInt(skillid);
            if(effectid == 1) {
            	mplew.write(playerLevel);
            }
            /*
            if ((effectid == 2) && (skillid == 31111003)) {
                mplew.writeInt(0);
            }
            */
            mplew.write(skillLevel);
            
            /*
            if ((direction != 3) || (skillid == 1320006) || (skillid == 30001062) || (skillid == 30001061)) {
                mplew.write(direction);
            }

            if (skillid == 30001062) {
                mplew.writeInt(0);
            }
            */
            mplew.writeZeroBytes(10); // Not correct, just added so wouldn't dc.

            System.out.println("ShowBuffEffect");
            return mplew.getPacket();
        }

        /**
         * Sends a packet to display a WZ effect.
         * Ex: Maple Island Beginner Job Animation
         * 
         * @param data String directory to the WZ effect.
         * @return
         */
        public static byte[] ShowWZEffect(String data) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.SHOW_SPECIAL_EFFECT);
            mplew.writeLong(24); // Last updated: v172.1
            mplew.writeShort(0);
            mplew.writeMapleAsciiString(data);

            return mplew.getPacket();
        }

        public static byte[] showOwnCraftingEffect(String effect, byte direction, int time, int mode) {
            return showCraftingEffect(-1, effect, direction, time, mode);
        }

        public static byte[] showCraftingEffect(int charid, String effect, byte direction, int time, int mode) {
            MaplePacketWriter mplew;
            if (charid == -1) {
            	mplew = new MaplePacketWriter(SendPacketOpcode.SHOW_SPECIAL_EFFECT);
            } else {
            	mplew = new MaplePacketWriter(SendPacketOpcode.SHOW_FOREIGN_EFFECT);
                mplew.writeInt(charid);
            }
            mplew.write(34); //v140
            mplew.writeMapleAsciiString(effect);
            mplew.write(direction);
            mplew.writeInt(time);
            mplew.writeInt(mode);
            if (mode == 2) {
                mplew.writeInt(0);
            }

            return mplew.getPacket();
        }

        public static byte[] TutInstructionalBalloon(String data) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.SHOW_SPECIAL_EFFECT);
            mplew.write(25);//was 26 in v140
            mplew.writeMapleAsciiString(data);
            mplew.writeInt(1);

            return mplew.getPacket();
        }

        public static byte[] showOwnPetLevelUp(byte index) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.SHOW_SPECIAL_EFFECT);
            mplew.write(6);
            mplew.write(0);
            mplew.write(index);

            return mplew.getPacket();
        }

        public static byte[] showOwnChampionEffect() {
            return showChampionEffect(-1);
        }

        public static byte[] showChampionEffect(int from_playerid) {
        	MaplePacketWriter mplew;
            if (from_playerid == -1) {
            	mplew = new MaplePacketWriter(SendPacketOpcode.SHOW_SPECIAL_EFFECT);
            } else {
            	mplew = new MaplePacketWriter(SendPacketOpcode.SHOW_FOREIGN_EFFECT);
                mplew.writeInt(from_playerid);
            }
            mplew.write(34);
            mplew.writeInt(30000);

            return mplew.getPacket();
        }

        public static byte[] updateDeathCount(int deathCount) {
            MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.DEATH_COUNT);
            mplew.writeInt(deathCount);

            return mplew.getPacket();
        }
        
    }

    public static byte[] showWeirdEffect(String effect, int itemId) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.SHOW_SPECIAL_EFFECT);
		mplew.write(0x20);
        mplew.writeMapleAsciiString(effect);
        mplew.write(1);
        mplew.writeInt(0);//weird high number is it will keep showing it lol
        mplew.writeInt(2);
        mplew.writeInt(itemId);
        return mplew.getPacket();
    }

    public static byte[] showWeirdEffect(int chrId, String effect, int itemId) {
    	MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.SHOW_FOREIGN_EFFECT);
		mplew.writeInt(chrId);
        mplew.write(0x20);
        mplew.writeMapleAsciiString(effect);
        mplew.write(1);
        mplew.writeInt(0);//weird high number is it will keep showing it lol
        mplew.writeInt(2);//this makes it read the itemId
        mplew.writeInt(itemId);
        return mplew.getPacket();
    }

    public static byte[] enchantResult(int result) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.STRENGTHEN_UI);
		mplew.writeInt(result);//0=fail/1=sucess/2=idk/3=shows stats
        return mplew.getPacket();
    }

    public static byte[] sendSealedBox(short slot, int itemId, List<Integer> items) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.SEALED_BOX);
		mplew.writeShort(slot);
        mplew.writeInt(itemId);
        mplew.writeInt(items.size());
        for (int item : items) {
            mplew.writeInt(item);
        }

        return mplew.getPacket();
    }
    
    public static byte[] sendBoxDebug(short opcode, int itemId, List<Integer> items) {
        System.out.println("sendBoxDebug\r\n" + opcode + "\r\n" + itemId + "\r\n");
        MaplePacketWriter mplew = new MaplePacketWriter();

        mplew.writeShort(opcode);
        mplew.writeShort(1);
        mplew.writeInt(itemId);
        mplew.writeInt(items.size());
        for (int item : items) {
            mplew.writeInt(item);
        }
        System.out.println("sendBoxDebug end");
        return mplew.getPacket();
    }
    
    public static byte[] getCassandrasCollection() {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.CASSANDRAS_COLLECTION);
		mplew.write(6);

        return mplew.getPacket();
    }

    public static byte[] unsealBox(int reward) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.SHOW_SPECIAL_EFFECT);
		mplew.write(0x31);
        mplew.write(1);
        mplew.writeInt(reward);
        mplew.writeInt(1);

        return mplew.getPacket();
    }
    
    /**
     * Shows a Revive UI that sends the player to the nearest town after they have died.
     * @return <code>01 00 00 00 00 00 00 00 00</code> packet
     */
    public static byte[] showReviveUI() {
    	MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.REVIVE_UI);
		mplew.write(1); // 1 to show; 0 to not show
    	mplew.writeLong(0);
    	
    	return mplew.getPacket();
    }

}