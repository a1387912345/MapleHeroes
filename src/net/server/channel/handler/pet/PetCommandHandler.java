package net.server.channel.handler.pet;

import client.MapleClient;
import client.character.MapleCharacter;
import client.inventory.MapleInventoryType;
import client.inventory.MaplePet;
import client.inventory.PetCommand;
import client.inventory.PetDataFactory;
import constants.GameConstants;
import net.MaplePacketHandler;
import net.RecvPacketOpcode;
import net.netty.MaplePacketReader;
import net.packet.PetPacket;
import net.packet.CField.EffectPacket;
import server.Randomizer;

public class PetCommandHandler extends MaplePacketHandler {

	public PetCommandHandler(RecvPacketOpcode recv) {
		super(recv);
	}

	@Override
	public void handlePacket(MaplePacketReader mpr, MapleClient c, MapleCharacter chr) {
		final MaplePet pet = chr.getPet(chr.getPetIndex((int) mpr.readLong()));
		mpr.readByte(); // Always 0?
		
		if(pet == null) {
			return;
		}
		
		final PetCommand petCommand = PetDataFactory.getPetCommand(pet.getPetItemId(), mpr.readByte());
		
		if(petCommand == null) {
            return;
        }
		
        byte petIndex = (byte) chr.getPetIndex(pet);
        boolean success = false;
        if(Randomizer.nextInt(99) <= petCommand.getProbability()) {
            success = true;
            if (pet.getCloseness() < 30000) {
                int newCloseness = pet.getCloseness() + (petCommand.getIncrease() * c.getChannelServer().getTraitRate());
                if(newCloseness > 30000) {
                    newCloseness = 30000;
                }
                pet.setCloseness(newCloseness);
                if(newCloseness >= GameConstants.getClosenessNeededForLevel(pet.getLevel() + 1)) {
                    pet.setLevel(pet.getLevel() + 1);
                    c.sendPacket(EffectPacket.showOwnPetLevelUp(petIndex));
                    chr.getMap().broadcastMessage(PetPacket.showPetLevelUp(chr, petIndex));
                }
                c.sendPacket(PetPacket.updatePet(pet, chr.getInventory(MapleInventoryType.CASH).getItem((byte) pet.getInventoryPosition()), true));
            }
        }
        chr.getMap().broadcastMessage(PetPacket.commandResponse(chr.getID(), (byte) petCommand.getSkillId(), petIndex, success, false));
	}

}
