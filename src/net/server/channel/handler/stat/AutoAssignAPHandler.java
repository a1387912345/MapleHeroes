package net.server.channel.handler.stat;

import java.util.EnumMap;
import java.util.Map;

import client.MapleClient;
import client.MapleStat;
import client.character.MapleCharacter;
import client.character.PlayerStats;
import constants.GameConstants;
import net.MaplePacketHandler;
import net.RecvPacketOpcode;
import net.netty.MaplePacketReader;
import net.packet.CWvsContext;

public class AutoAssignAPHandler extends MaplePacketHandler {

	public AutoAssignAPHandler(RecvPacketOpcode recv) {
		super(recv);
	}

	@Override
	public void handlePacket(final MaplePacketReader mpr, final MapleClient c, final MapleCharacter chr) {
		chr.updateTick(mpr.readInt());
        mpr.skip(4);
        if (mpr.available() < 16L) {
            return;
        }
       // final int count = inPacket.readInt();
        int PrimaryStat = GameConstants.GMS ? (int) mpr.readLong() : mpr.readInt();
        int amount = mpr.readInt();
        int SecondaryStat = GameConstants.GMS ? (int) mpr.readLong() : mpr.readInt();
        int amount2 = mpr.readInt();
        if ((amount < 0) || (amount2 < 0)) {
            return;
        }

        PlayerStats playerst = chr.getStat();

        Map statupdate = new EnumMap(MapleStat.class);
        c.sendPacket(CWvsContext.updatePlayerStats(statupdate, true, chr));

    /*    if (chr.getRemainingAp() == amount + amount2) {
            switch (PrimaryStat) {*/ //testing fix from RZ
           if (chr.getRemainingAp() >= amount + amount2) {
            switch (PrimaryStat) {
             case 0x40: // Str   case 64:
                    if (playerst.getStr() + amount > 999) {
                        return;
                    }
                    playerst.setStr((short) (playerst.getStr() + amount), chr);
                    statupdate.put(MapleStat.STR, Long.valueOf(playerst.getStr()));
                    break;
              case 0x80: // Dex  case 128:
                    if (playerst.getDex() + amount > 999) {
                        return;
                    }
                    playerst.setDex((short) (playerst.getDex() + amount), chr);
                    statupdate.put(MapleStat.DEX, Long.valueOf(playerst.getDex()));
                    break;
            case 0x100: // Int    case 256:
                    if (playerst.getInt() + amount > 999) {
                        return;
                    }
                    playerst.setInt((short) (playerst.getInt() + amount), chr);
                    statupdate.put(MapleStat.INT, Long.valueOf(playerst.getInt()));
                    break;
            case 0x200: // LUK    case 512:
                    if (playerst.getLuk() + amount > 999) {
                        return;
                    }
                    playerst.setLuk((short) (playerst.getLuk() + amount), chr);
                    statupdate.put(MapleStat.LUK, Long.valueOf(playerst.getLuk()));
                    break;
             case 0x800: // HP       case 0x800: //Max Hp
                       System.out.println("Reading hp case."); 
                   if (playerst.getMaxHp() + (amount * 30) > 500000) {
                            return;
                        }
                        System.out.println("HP Didn't get added Sorry nigger."); 
                        playerst.setMaxHp((short) (playerst.getMaxHp() + amount * 30), chr);
                        statupdate.put(MapleStat.MAXHP, Long.valueOf(playerst.getMaxHp()));
                        break;
                default:
                    c.sendPacket(CWvsContext.enableActions());
                    return;
            }
            switch (SecondaryStat) {
                case 64:
                    if (playerst.getStr() + amount2 > 999) {
                        return;
                    }
                    playerst.setStr((short) (playerst.getStr() + amount2), chr);
                    statupdate.put(MapleStat.STR, Long.valueOf(playerst.getStr()));
                    break;
                case 128:
                    if (playerst.getDex() + amount2 > 999) {
                        return;
                    }
                    playerst.setDex((short) (playerst.getDex() + amount2), chr);
                    statupdate.put(MapleStat.DEX, Long.valueOf(playerst.getDex()));
                    break;
                case 256:
                    if (playerst.getInt() + amount2 > 999) {
                        return;
                    }
                    playerst.setInt((short) (playerst.getInt() + amount2), chr);
                    statupdate.put(MapleStat.INT, Long.valueOf(playerst.getInt()));
                    break;
                case 512:
                    if (playerst.getLuk() + amount2 > 999) {
                        return;
                    }
                    playerst.setLuk((short) (playerst.getLuk() + amount2), chr);
                    statupdate.put(MapleStat.LUK, Long.valueOf(playerst.getLuk()));
                    break;
               case 1024: //Max Hp
                   if (playerst.getMaxHp() + (amount2 * 30) > 500000) {
                            return;
                   }
                     System.out.println("HP Didn't get added Sorry nigger.");       
                        playerst.setMaxHp((short) (playerst.getMaxHp() + amount2 * 30), chr);
                        statupdate.put(MapleStat.MAXHP, Long.valueOf(playerst.getMaxHp()));
                        break;
                default:
                    c.sendPacket(CWvsContext.enableActions());
                    return;
            }
            chr.setRemainingAp((short) (chr.getRemainingAp() - (amount + amount2)));
            statupdate.put(MapleStat.AVAILABLEAP, (long) chr.getRemainingAp());
            c.sendPacket(CWvsContext.updatePlayerStats(statupdate, true, chr));
        }
	}

}
