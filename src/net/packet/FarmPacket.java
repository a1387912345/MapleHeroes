/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.packet;

import client.MapleCharacter;
import client.MapleClient;
import constants.WorldConstants;
import constants.WorldConstants.WorldOption;
import net.SendPacketOpcode;
import net.netty.MaplePacketWriter;

import java.util.LinkedList;
import java.util.List;
import server.farm.MapleFarm;
import tools.Pair;

/**
 *
 * @author Itzik
 */
public class FarmPacket {

    public static byte[] enterFarm(MapleClient c) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.FARM_OPEN);
        PacketHelper.addCharacterInfo(mplew, c.getCharacter());
        MapleFarm f = c.getFarm();
        long time = System.currentTimeMillis();
        /* Farm House positions:
         * 000 001 002 003 004
         * 025 026 027 028 029
         * 050 051 052 053 054
         * 075 076 077 078 079
         * 100 101 102 103 104 //104 is base position
         */
        List<Integer> house = new LinkedList();
        int houseBase = 104;
        int houseId = 4150001; //15x15 house need to code better houses
        for (int i = 0; i < 5; i++) { //5x5
            for (int j = 0; j < 5; j++) { //5x5
                house.add(houseBase - j - i); //104 base position
            }
        }
        for (int i = 0; i < 25 * 25; i++) { //2D building at every position
            boolean housePosition = house.contains(i);
            mplew.writeInt(housePosition ? houseId : 0); //building that the position contains
            mplew.writeInt(i == houseBase ? houseId : 0); //building that the position bases
            mplew.writeZeroBytes(5);
            mplew.writeLong(PacketHelper.getTime(time));
        }
        mplew.writeInt(14);
        mplew.writeInt(14);
        mplew.writeInt(0);
        mplew.writeLong(PacketHelper.getTime(time + 180000));

        return mplew.getPacket();
    }

    public static byte[] farmQuestData(List<Pair<Integer, String>> canStart, List<Pair<Integer, String>> completed) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.FARM_QUEST_DATA);
		mplew.writeInt(canStart.size());
        for (Pair<Integer, String> i : canStart) {
            mplew.writeInt(i.getLeft());
            mplew.writeMapleAsciiString(i.getRight());
        }
        mplew.writeInt(completed.size());
        for (Pair<Integer, String> i : completed) {
            mplew.writeInt(i.getLeft());
            mplew.writeMapleAsciiString(i.getRight());
        }

        return mplew.getPacket();
    }

    public static byte[] alertQuest(int questId, int status) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.QUEST_ALERT);
		mplew.writeInt(questId);
        mplew.write((byte) status);

        return mplew.getPacket();
    }

    public static byte[] updateMonsterInfo(List<Pair<Integer, Integer>> monsters) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.FARM_MONSTER_INFO);
		mplew.writeInt(monsters.size());
        for (Pair<Integer, Integer> i : monsters) {
            mplew.writeInt(i.getLeft());
            mplew.writeInt(i.getRight());
        }

        return mplew.getPacket();
    }

    public static byte[] updateAesthetic(int quantity) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.AESTHETIC_POINT);
		mplew.writeInt(quantity);

        return mplew.getPacket();
    }

    public static byte[] spawnFarmMonster1() {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.SPAWN_FARM_MONSTER1);
		mplew.writeInt(0);
        mplew.write(1);
        mplew.writeInt(0); //if 1 then same as spawnmonster2 but last byte is 1

        return mplew.getPacket();
    }

    public static byte[] farmPacket1() {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.FARM_PACKET1);
		mplew.writeZeroBytes(4);

        return mplew.getPacket();
    }

    public static byte[] farmPacket4() {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.FARM_PACKET4);
		mplew.writeZeroBytes(4);

        return mplew.getPacket();
    }

    public static byte[] updateQuestInfo(int id, int mode, String data) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.FARM_QUEST_INFO);
		mplew.writeInt(id);
        mplew.write((byte) mode);
        mplew.writeMapleAsciiString(data);

        return mplew.getPacket();
    }

    public static byte[] farmMessage(String msg) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.FARM_MESSAGE);
		mplew.writeMapleAsciiString(msg);

        return mplew.getPacket();
    }

    public static byte[] updateItemQuantity(int id, int quantity) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.FARM_ITEM_GAIN);
		mplew.writeInt(id);
        mplew.writeInt(quantity);

        return mplew.getPacket();
    }

    public static byte[] itemPurchased(int id) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.FARM_ITEM_PURCHASED);
		mplew.writeInt(id);
        mplew.write(1);

        return mplew.getPacket();
    }

    public static byte[] showExpGain(int quantity, int mode) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.FARM_EXP);
		mplew.writeInt(quantity);
        mplew.writeInt(mode);

        return mplew.getPacket();
    }

    public static byte[] updateWaru(int quantity) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.UPDATE_WARU);
		mplew.writeInt(quantity);

        return mplew.getPacket();
    }

    public static byte[] showWaruHarvest(int slot, int quantity) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.HARVEST_WARU);
		mplew.write(0);
        mplew.writeInt(slot);
        mplew.writeInt(quantity);

        return mplew.getPacket();
    }

    public static byte[] spawnFarmMonster(MapleClient c, int id) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.SPAWN_FARM_MONSTER2);
		mplew.writeInt(0);
        mplew.write(1);
        mplew.writeInt(1);
        mplew.writeInt(1);
        mplew.writeInt(c.getFarm().getId());
        mplew.writeInt(1);
        mplew.writeInt(id);
        mplew.writeMapleAsciiString(""); //monster.getName()
        mplew.writeInt(1); //level?
        mplew.writeInt(0);
        mplew.writeInt(15);
        mplew.writeInt(3); //monster.getNurturesLeft()
        mplew.writeInt(20); //monster.getPlaysLeft()
        mplew.writeInt(0);
        long time = System.currentTimeMillis(); //should be server time
        mplew.writeLong(PacketHelper.getTime(time));
        mplew.writeLong(PacketHelper.getTime(time + 25920000000000L));
        mplew.writeLong(PacketHelper.getTime(time + 25920000000000L));
        for (int i = 0; i < 4; i++) {
            mplew.writeLong(PacketHelper.getTime(time));
        }
        mplew.writeInt(-1);
        mplew.writeInt(-1);
        mplew.writeZeroBytes(12);
        mplew.write(0);

        return mplew.getPacket();
    }

    public static byte[] updateMonster(List<Pair<Integer, Long>> monsters) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.UPDATE_MONSTER);
		mplew.write(monsters.size());
        for (Pair<Integer, Long> monster : monsters) {
            mplew.writeInt(monster.getLeft()); //mob id as regular monster
            mplew.writeLong(PacketHelper.getTime(monster.getRight())); //expire
        }

        return mplew.getPacket();
    }

    public static byte[] updateMonsterQuantity(int itemId, int monsterId) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.FARM_MONSTER_GAIN);
		mplew.write(0);
        mplew.writeInt(itemId);
        mplew.write(1);
        mplew.writeInt(monsterId);
        mplew.writeInt(1); //quantity?

        return mplew.getPacket();
    }

    public static byte[] renameMonster(int index, String name) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.RENAME_MONSTER);
		mplew.writeInt(0);
        mplew.writeInt(index);
        mplew.writeMapleAsciiString(name);

        return mplew.getPacket();
    }

    public static byte[] updateFarmFriends(List<MapleFarm> friends) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.FARM_FRIENDS);
		mplew.writeInt(friends.size());
        for (MapleFarm f : friends) {
            mplew.writeInt(f.getId());
            mplew.writeMapleAsciiString(f.getName());
            mplew.writeZeroBytes(5);
        }
        mplew.writeInt(0); //blocked?
        mplew.writeInt(0); //follower

        return mplew.getPacket();
    }

    public static byte[] updateFarmInfo(MapleClient c) {
        return updateFarmInfo(c, false);
    }

    public static byte[] updateFarmInfo(MapleClient c, boolean newname) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.FARM_INFO);
		mplew.writeInt(c.getFarm().getId()); //Farm ID
        mplew.writeInt(0);
        mplew.writeLong(0); //decodeMoney ._.

        //first real farm info
        PacketHelper.addFarmInfo(mplew, c, 2);
        mplew.write(0);

        //then fake farm info
        if (newname) {
            mplew.writeMapleAsciiString("Creating...");
            mplew.writeInt(0);
            mplew.writeInt(0);
            mplew.writeInt(0);
            mplew.writeInt(0);
            mplew.writeInt(0);

            mplew.write(2);
            mplew.writeInt(0);
            mplew.writeInt(0);
            mplew.writeInt(1);
        } else { //or real info again incase name wasn't chosen this time
            PacketHelper.addFarmInfo(mplew, c, 2);
        }
        mplew.write(0);

        mplew.writeInt(0);
        mplew.writeInt(-1);
        mplew.write(0);

        System.out.println(mplew.toString());
        return mplew.getPacket();
    }

    public static byte[] updateUserFarmInfo(MapleCharacter chr, boolean update) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.FARM_USER_INFO);
		mplew.write(update);
        if (update) {
            mplew.writeInt(chr.getWorld());
            mplew.writeMapleAsciiString(WorldConstants.getNameById(chr.getWorld()));
            mplew.writeInt(chr.getId()); //Not sure if character id or farm id
            mplew.writeMapleAsciiString(chr.getName());
        }

        return mplew.getPacket();
    }

    public static byte[] sendFarmRanking(MapleCharacter chr, List<Pair<MapleFarm, Integer>> rankings) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.FARM_RANKING);
		mplew.writeInt(0); //Visitors
        mplew.writeInt(0); //Playtime
        mplew.writeInt(0); //Combinations
        mplew.writeInt(rankings.size());
        int i = 0;
        for (Pair<MapleFarm, Integer> best : rankings) {
            mplew.writeInt(i); //Type; 0 = visitors 1 = playtime 2 = combinations
            mplew.writeInt(best.getLeft().getId());
            mplew.writeMapleAsciiString(best.getLeft().getName());
            mplew.writeInt(best.getRight()); //Value of type
            if (i < 2) {
                i++;
            }
        }
        mplew.write(0); //Boolean; enable or disable entry reward button

        return mplew.getPacket();
    }

    public static byte[] updateAvatar(Pair<WorldOption, MapleCharacter> from, Pair<WorldOption, MapleCharacter> to, boolean change) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.FARM_AVATAR);
		mplew.write(change);
        mplew.writeInt(from.getLeft().getWorld());
        mplew.writeMapleAsciiString(WorldConstants.getNameById(from.getLeft().getWorld()));
        mplew.writeInt(from.getRight().getId());
        mplew.writeMapleAsciiString(from.getRight().getName());
        if (change) {
            mplew.writeInt(to.getLeft().getWorld());
            mplew.writeMapleAsciiString(WorldConstants.getNameById(to.getLeft().getWorld()));
            mplew.writeInt(to.getRight().getId());
            mplew.writeMapleAsciiString(to.getRight().getName());
        }

        return mplew.getPacket();
    }
}
