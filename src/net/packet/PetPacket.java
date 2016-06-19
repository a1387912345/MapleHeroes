package net.packet;

import client.MapleStat;
import client.character.MapleCharacter;
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
        MaplePacketWriter mpw = new MaplePacketWriter(SendPacketOpcode.INVENTORY_OPERATION);
		mpw.write(0);
        mpw.write(2);
        mpw.write(0);//new141
        mpw.write(3);
        mpw.write(5);
        mpw.writeShort(pet.getInventoryPosition());
        mpw.write(0);
        mpw.write(5);
        mpw.writeShort(pet.getInventoryPosition());
        mpw.write(3);
        mpw.writeInt(pet.getPetItemId());
        mpw.write(1);
        mpw.writeLong(pet.getUniqueId());
        PacketHelper.addPetItemInfo(mpw, item, pet, active);
        return mpw.getPacket();
    }

    public static final byte[] showPet(MapleCharacter chr, MaplePet pet, boolean remove, boolean hunger) {
        MaplePacketWriter mpw = new MaplePacketWriter(SendPacketOpcode.SPAWN_PET);
		mpw.writeInt(chr.getID());
        mpw.write(chr.getPetIndex(pet));
        if (remove) {
            mpw.write(0);
            mpw.write(hunger ? 1 : 0);
        } else {
            mpw.write(1);
            mpw.write(1);//was 0
            mpw.writeInt(pet.getPetItemId());
            mpw.writeMapleAsciiString(pet.getName());
            mpw.writeLong(pet.getUniqueId());
            mpw.writeShort(pet.getPos().x);
            mpw.writeShort(pet.getPos().y - 20);
            mpw.write(pet.getStance());
//            mpw.writeShort(1);//new 141
            mpw.writeShort(pet.getFh());
            mpw.writeInt(-1);
            mpw.writeInt(100);//new 141
        }

        return mpw.getPacket();
    }

    public static final byte[] removePet(int cid, int index) {
        MaplePacketWriter mpw = new MaplePacketWriter(SendPacketOpcode.SPAWN_PET);
		mpw.writeInt(cid);
        mpw.write(index);
        mpw.writeShort(0);

        return mpw.getPacket();
    }

    public static byte[] movePet(int cid, int pid, byte slot, Point pos, List<LifeMovementFragment> moves) {
        MaplePacketWriter mpw = new MaplePacketWriter(SendPacketOpcode.MOVE_PET);
		mpw.writeInt(cid);
        mpw.write(slot);
        mpw.writeInt(0);//new 141
        mpw.writePos(pos);
        mpw.writeInt(pid);
        PacketHelper.serializeMovementList(mpw, moves);

        return mpw.getPacket();
    }

    public static byte[] petChat(int cid, int un, String text, byte slot) {
        MaplePacketWriter mpw = new MaplePacketWriter(SendPacketOpcode.PET_CHAT);
		mpw.writeInt(cid);
        mpw.write(slot);
        mpw.write(un);
        mpw.write(0);
        mpw.writeMapleAsciiString(text);
        mpw.write(0);

        return mpw.getPacket();
    }
    
    public static byte[] petColor(int cid,byte slot, int color) {
        MaplePacketWriter mpw = new MaplePacketWriter(SendPacketOpcode.PET_COLOR);
		mpw.writeInt(cid);
        mpw.write(slot);
        mpw.writeInt(color);

        return mpw.getPacket();
    }

    public static final byte[] commandResponse(int cid, byte command, byte slot, boolean success, boolean food) {
        MaplePacketWriter mpw = new MaplePacketWriter(SendPacketOpcode.PET_COMMAND);
		mpw.writeInt(cid);
        mpw.write(slot);
        mpw.write(command == 1 ? 1 : 0);
        mpw.write(command);
        mpw.write(success ? 1 : command == 1 ? 0 : 0);
        mpw.writeInt(0);
        mpw.write(0);//new142
        mpw.write(0);//new142

        return mpw.getPacket();
    }

    public static final byte[] showPetLevelUp(MapleCharacter chr, byte index) {
        MaplePacketWriter mpw = new MaplePacketWriter(SendPacketOpcode.SHOW_FOREIGN_EFFECT);
		mpw.writeInt(chr.getID());
        mpw.write(6);
        mpw.write(0);
        mpw.writeInt(index);

        return mpw.getPacket();
    }
    
    public static final byte[] petSize(int cid, byte slot,short size) {
        MaplePacketWriter mpw = new MaplePacketWriter(SendPacketOpcode.PET_SIZE);
		mpw.writeInt(cid);
        mpw.write(slot);
        mpw.writeShort(size);

        return mpw.getPacket();
    }

    public static final byte[] showPetUpdate(MapleCharacter chr, int uniqueId, byte index) {
        MaplePacketWriter mpw = new MaplePacketWriter(SendPacketOpcode.PET_EXCEPTION_LIST);
		mpw.writeInt(chr.getID());
        mpw.write(index);
        mpw.writeLong(uniqueId);
        mpw.write(0);
        mpw.writeInt(0);
        //mpw.writeZeroBytes(50);

        return mpw.getPacket();
    }

    public static byte[] petStatUpdate(MapleCharacter chr) {
        MaplePacketWriter mpw = new MaplePacketWriter(SendPacketOpcode.UPDATE_STATS);
		mpw.write(0);
        mpw.writeLong(MapleStat.PET.getValue());

        byte count = 0;
        for (MaplePet pet : chr.getPets()) {
            if (pet.getSummoned()) {
                mpw.writeLong(pet.getUniqueId());
                count = (byte) (count + 1);
            }
        }
        while (count < 3) {
            mpw.writeZeroBytes(8);
            count = (byte) (count + 1);
        }
        mpw.write(0);
        mpw.writeShort(0);
        mpw.writeZeroBytes(100);

        return mpw.getPacket();
    }
}
