/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.farm.handler;

import client.MapleCharacter;
import client.MapleClient;
import constants.WorldConstants.WorldOption;
import net.channel.ChannelServer;
import net.farm.FarmServer;
import net.login.LoginServer;
import net.netty.MaplePacketReader;
import net.packet.CField;
import net.packet.FarmPacket;
import net.world.CharacterTransfer;
import net.world.World;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.TimeZone;
import server.farm.MapleFarm;
import tools.Pair;
import tools.Triple;

/**
 *
 * @author Itzik
 */
public class FarmOperation {

    public static void EnterFarm(final CharacterTransfer transfer, final MapleClient c) {
        if (transfer == null) {
            c.getSocketChannel().close();
            return;
        }
        MapleCharacter chr = MapleCharacter.ReconstructChr(transfer, c, false);

        c.setCharacter(chr);
        c.setAccID(chr.getAccountID());

        if (!c.CheckIPAddress()) { // Remote hack
            c.getSocketChannel().close();
            return;
        }

        final int state = c.getLoginState();
        boolean allowLogin = false;
        if (state == MapleClient.LOGIN_SERVER_TRANSITION || state == MapleClient.CHANGE_CHANNEL) {
            if (!World.isCharacterListConnected(c.loadCharacterNames(c.getWorld()))) {
                allowLogin = true;
            }
        }
        if (!allowLogin) {
            c.setCharacter(null);
            c.getSocketChannel().close();
            return;
        }
        c.updateLoginState(MapleClient.LOGIN_LOGGEDIN, c.getSessionIPAddress());
        FarmServer.getPlayerStorage().registerPlayer(chr);
        c.sendPacket(FarmPacket.updateMonster(new LinkedList()));
        c.sendPacket(FarmPacket.enterFarm(c));
        c.sendPacket(FarmPacket.farmQuestData(new LinkedList(), new LinkedList()));
        c.sendPacket(FarmPacket.updateMonsterInfo(new LinkedList()));
        c.sendPacket(FarmPacket.updateAesthetic(c.getFarm().getAestheticPoints()));
        c.sendPacket(FarmPacket.spawnFarmMonster1());
        c.sendPacket(FarmPacket.farmPacket1());
        c.sendPacket(FarmPacket.updateFarmFriends(new LinkedList()));
        c.sendPacket(FarmPacket.updateFarmInfo(c));
        //c.sendPacket(CField.getPacketFromHexString("19 72 1E 02 00 00 00 00 00 00 00 00 00 00 00 00 0B 00 43 72 65 61 74 69 6E 67 2E 2E 2E 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 02 00 00 00 00 00 00 00 00 01 00 00 00 00 0B 00 43 72 65 61 74 69 6E 67 2E 2E 2E 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 02 00 00 00 00 00 00 00 00 01 00 00 00 00 00 00 00 00 FF FF FF FF 00"));
        c.sendPacket(FarmPacket.updateQuestInfo(21002, (byte) 1, ""));
        SimpleDateFormat sdfGMT = new SimpleDateFormat("yyyy-MM-dd-HH-mm");
        sdfGMT.setTimeZone(TimeZone.getTimeZone("Canada/Pacific"));
        String timeStr = sdfGMT.format(Calendar.getInstance().getTime()).replaceAll("-", "");
        c.sendPacket(FarmPacket.updateQuestInfo(21001, (byte) 1, timeStr));
        c.sendPacket(FarmPacket.updateQuestInfo(21003, (byte) 1, "30"));
        c.sendPacket(FarmPacket.updateUserFarmInfo(chr, false));
        List<Pair<MapleFarm, Integer>> ranking = new LinkedList();
        ranking.add(new Pair<>(MapleFarm.getDefault(1, c, "Pyrous"), 999999));
        ranking.add(new Pair<>(MapleFarm.getDefault(1, c, "Sango"), 1));
        ranking.add(new Pair<>(MapleFarm.getDefault(1, c, "Hemmi"), -1));
        c.sendPacket(FarmPacket.sendFarmRanking(chr, ranking));
        c.sendPacket(FarmPacket.updateAvatar(new Pair<>(WorldOption.Scania, chr), null, false));
        if (c.getFarm().getName().equals("Creating...")) { //todo put it on farm update handler
            c.sendPacket(FarmPacket.updateQuestInfo(1111, (byte) 0, "A1/"));
            c.sendPacket(FarmPacket.updateQuestInfo(2001, (byte) 0, "A1/"));
        }
    }

}
