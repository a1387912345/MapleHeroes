package net.packet;

import client.MapleCharacter;
import client.MapleStat;
import client.inventory.Item;
import client.inventory.MaplePet;
import constants.GameConstants;
import net.SendPacketOpcode;
import net.netty.MaplePacketWriter;

import java.awt.Point;
import java.util.List;
import server.movement.LifeMovementFragment;

public class PetPacket {

    public static final byte[] updatePet(MaplePet pet, Item item, boolean active) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.INVENTORY_OPERATION);
		mplew.write(0);
        mplew.write(2);
        mplew.write(0);//new141
        mplew.write(3);
        mplew.write(5);
        mplew.writeShort(pet.getInventoryPosition());
        mplew.write(0);
        mplew.write(5);
        mplew.writeShort(pet.getInventoryPosition());
        mplew.write(3);
        mplew.writeInt(pet.getPetItemId());
        mplew.write(1);
        mplew.writeLong(pet.getUniqueId());
        PacketHelper.addPetItemInfo(mplew, item, pet, active);
        return mplew.getPacket();
    }

    public static final byte[] showPet(MapleCharacter chr, MaplePet pet, boolean remove, boolean hunger) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.SPAWN_PET);
		mplew.writeInt(chr.getId());
        mplew.write(chr.getPetIndex(pet));
        if (remove) {
            mplew.write(0);
            mplew.write(hunger ? 1 : 0);
        } else {
            mplew.write(1);
            mplew.write(1);//was 0
            mplew.writeInt(pet.getPetItemId());
            mplew.writeMapleAsciiString(pet.getName());
            mplew.writeLong(pet.getUniqueId());
            mplew.writeShort(pet.getPos().x);
            mplew.writeShort(pet.getPos().y - 20);
            mplew.write(pet.getStance());
//            mplew.writeShort(1);//new 141
            mplew.writeShort(pet.getFh());
            mplew.writeInt(-1);
            mplew.writeInt(100);//new 141
        }

        return mplew.getPacket();
    }

    public static final byte[] removePet(int cid, int index) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.SPAWN_PET);
		mplew.writeInt(cid);
        mplew.write(index);
        mplew.writeShort(0);

        return mplew.getPacket();
    }

    public static byte[] movePet(int cid, int pid, byte slot, Point pos, List<LifeMovementFragment> moves) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.MOVE_PET);
		mplew.writeInt(cid);
        mplew.write(slot);
        mplew.writeInt(0);//new 141
        mplew.writePos(pos);
        mplew.writeInt(pid);
        PacketHelper.serializeMovementList(mplew, moves);

        return mplew.getPacket();
    }

    public static byte[] petChat(int cid, int un, String text, byte slot) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.PET_CHAT);
		mplew.writeInt(cid);
        mplew.write(slot);
        mplew.write(un);
        mplew.write(0);
        mplew.writeMapleAsciiString(text);
        mplew.write(0);

        return mplew.getPacket();
    }
    
    public static byte[] petColor(int cid,byte slot, int color) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.PET_COLOR);
		mplew.writeInt(cid);
        mplew.write(slot);
        mplew.writeInt(color);

        return mplew.getPacket();
    }

    public static final byte[] commandResponse(int cid, byte command, byte slot, boolean success, boolean food) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.PET_COMMAND);
		mplew.writeInt(cid);
        mplew.write(slot);
        mplew.write(command == 1 ? 1 : 0);
        mplew.write(command);
        mplew.write(success ? 1 : command == 1 ? 0 : 0);
        mplew.writeInt(0);
        mplew.write(0);//new142
        mplew.write(0);//new142

        return mplew.getPacket();
    }

    public static final byte[] showPetLevelUp(MapleCharacter chr, byte index) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.SHOW_FOREIGN_EFFECT);
		mplew.writeInt(chr.getId());
        mplew.write(6);
        mplew.write(0);
        mplew.writeInt(index);

        return mplew.getPacket();
    }
    
    public static final byte[] petSize(int cid, byte slot,short size) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.PET_SIZE);
		mplew.writeInt(cid);
        mplew.write(slot);
        mplew.writeShort(size);

        return mplew.getPacket();
    }

    public static final byte[] showPetUpdate(MapleCharacter chr, int uniqueId, byte index) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.PET_EXCEPTION_LIST);
		mplew.writeInt(chr.getId());
        mplew.write(index);
        mplew.writeLong(uniqueId);
        mplew.write(0);
        mplew.writeInt(0);
        //mplew.writeZeroBytes(50);

        return mplew.getPacket();
    }

    public static byte[] petStatUpdate(MapleCharacter chr) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.UPDATE_STATS);
		mplew.write(0);
        mplew.writeLong(MapleStat.PET.getValue());

        byte count = 0;
        for (MaplePet pet : chr.getPets()) {
            if (pet.getSummoned()) {
                mplew.writeLong(pet.getUniqueId());
                count = (byte) (count + 1);
            }
        }
        while (count < 3) {
            mplew.writeZeroBytes(8);
            count = (byte) (count + 1);
        }
        mplew.write(0);
        mplew.writeShort(0);
        mplew.writeZeroBytes(100);

        return mplew.getPacket();
    }
}
