package net.server.cashshop.handler;

import client.MapleClient;
import client.MapleQuestStatus;
import client.character.MapleCharacter;
import client.character.MapleCharacterUtil;
import client.inventory.Item;
import client.inventory.MapleInventoryIdentifier;
import client.inventory.MapleInventoryType;
import client.inventory.MapleRing;
import constants.GameConstants;
import database.DatabaseConnection;
import net.netty.MaplePacketReader;
import net.packet.CField;
import net.packet.CSPacket;
import net.packet.CWvsContext;
import net.server.cashshop.CashShopServer;
import net.server.channel.ChannelServer;
import net.server.login.LoginServer;
import net.world.CharacterTransfer;
import net.world.World;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import server.CashItem;
import server.CashItemFactory;
import server.CashItemInfo;
import server.MapleInventoryManipulator;
import server.MapleItemInformationProvider;
import server.quest.MapleQuest;
import tools.FileoutputUtil;
import tools.Triple;

public class CashShopOperation {

    public static void LeaveCS(final MaplePacketReader inPacket, final MapleClient c, final MapleCharacter chr) {
        CashShopServer.getPlayerStorage().deregisterPlayer(chr);
        c.updateLoginState(MapleClient.LOGIN_SERVER_TRANSITION, c.getSessionIPAddress());

        try {

            World.ChannelChange_Data(new CharacterTransfer(chr), chr.getID(), c.getChannel());
            c.sendPacket(CField.getChannelChange(c, Integer.parseInt(ChannelServer.getInstance(c.getChannel()).getIP().split(":")[1])));
        } finally {
            final String s = c.getSessionIPAddress();
            LoginServer.getInstance().addIPAuth(s.substring(s.indexOf('/') + 1, s.length()));
            chr.saveToDB(false, true);
            c.setCharacter(null);
            c.setReceiving(false);
            c.close();
        }
    }

    public static void EnterCS(final CharacterTransfer transfer, final MapleClient c) {
        if (transfer == null) {
            c.close();
            return;
        }
        MapleCharacter chr = MapleCharacter.ReconstructChr(transfer, c, false);

        c.setCharacter(chr);
        c.setAccountID(chr.getAccountID());

        if (!c.checkIPAddress()) { // Remote hack
            c.close();
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
            c.close();
            return;
        }
        c.updateLoginState(MapleClient.LOGIN_LOGGEDIN, c.getSessionIPAddress());
        CashShopServer.getPlayerStorage().registerPlayer(chr);
        c.sendPacket(CSPacket.warpCS(c));
        //c.sendPacket(CSPacket.Use_CS(false));
        c.sendPacket(CSPacket.loadCategories());
        c.sendPacket(CSPacket.CS_Picture_Item());
        c.sendPacket(CSPacket.CS_Top_Items());
        c.sendPacket(CSPacket.CS_Special_Item());
        c.sendPacket(CSPacket.CS_Featured_Item());
        c.sendPacket(CSPacket.showNXMapleTokens(c.getCharacter()));
        doCSPackets(c);
        //loadCashShop(c);
//        doCSPackets(c);
    }

    public static void loadCashShop(MapleClient c) {
        c.sendPacket(CSPacket.loadCategories());
        String head = "E2 02";
        c.sendPacket(CField.getPacketFromHexString(head + " 04 01 19 00 09 3D 00 10 30 3D 00 15 54 10 00 47 00 68 74 74 70 3A 2F 2F 6E 78 63 61 63 68 65 2E 6E 65 78 6F 6E 2E 6E 65 74 2F 6D 61 70 6C 65 73 74 6F 72 79 2F 73 68 6F 70 2F 67 61 6D 65 2F 41 6C 6C 53 74 61 72 73 48 61 69 72 5F 32 30 31 33 30 34 32 34 2E 6A 70 67 E7 F1 FA 02 6D 95 4E 00 01 00 00 00 04 00 00 00 00 00 00 00 1E 00 00 00 B8 0B 00 00 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 B8 0B 00 00 00 00 00 00 01 00 00 00 1E 00 00 00 01 01 01 00 01 02 00 00 00 26 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 09 3D 00 10 30 3D 00 15 54 10 00 47 00 68 74 74 70 3A 2F 2F 6E 78 63 61 63 68 65 2E 6E 65 78 6F 6E 2E 6E 65 74 2F 6D 61 70 6C 65 73 74 6F 72 79 2F 73 68 6F 70 2F 67 61 6D 65 2F 41 6C 6C 53 74 61 72 73 48 61 69 72 5F 32 30 31 33 30 34 32 34 2E 6A 70 67 E8 F1 FA 02 6D 95 4E 00 01 00 00 00 04 00 00 00 00 00 00 00 1E 00 00 00 30 75 00 00 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 30 75 00 00 00 00 00 00 0B 00 00 00 1E 00 00 00 01 01 01 00 01 02 00 00 00 0C 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 09 3D 00 20 57 3D 00 A0 E1 0F 00 44 00 68 74 74 70 3A 2F 2F 6E 78 63 61 63 68 65 2E 6E 65 78 6F 6E 2E 6E 65 74 2F 6D 61 70 6C 65 73 74 6F 72 79 2F 73 68 6F 70 2F 67 61 6D 65 2F 43 75 62 65 50 63 6B 67 73 5F 32 30 31 33 30 34 32 34 2E 6A 70 67 89 2C 9A 00 30 E6 8A 00 01 00 00 00 03 00 00 00 01 00 00 00 21 00 00 00 F0 55 00 00 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 00 00 2C F1 B2 E7 CD 01 00 80 05 BB 46 E6 17 02 1C 3E 00 00 00 00 00 00 01 00 00 00 1E 00 00 00 01 01 01 00 01 02 00 00 00 8E 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 02 00 00 00 7C E1 F5 05 72 3D 4D 00 01 00 00 00 38 4A 00 00 B0 36 00 00 00 00 00 00 0A 00 00 00 1E 00 00 00 02 00 00 00 E9 2C 9A 00 80 3A 54 00 00 00 00 00 B8 0B 00 00 6C 07 00 00 00 00 00 00 01 00 00 00 1E 00 00 00 02 00 00 00 00 09 3D 00 20 57 3D 00 A0 E1 0F 00 44 00 68 74 74 70 3A 2F 2F 6E 78 63 61 63 68 65 2E 6E 65 78 6F 6E 2E 6E 65 74 2F 6D 61 70 6C 65 73 74 6F 72 79 2F 73 68 6F 70 2F 67 61 6D 65 2F 43 75 62 65 50 63 6B 67 73 5F 32 30 31 33 30 34 32 34 2E 6A 70 67 8C 2C 9A 00 32 E6 8A 00 01 00 00 00 03 00 00 00 01 00 00 00 21 00 00 00 48 71 00 00 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 00 00 2C F1 B2 E7 CD 01 00 80 05 BB 46 E6 17 02 BC 4D 00 00 00 00 00 00 01 00 00 00 1E 00 00 00 01 01 01 00 01 02 00 00 00 78 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 02 00 00 00 EA 2C 9A 00 75 3D 4D 00 01 00 00 00 A8 61 00 00 68 42 00 00 00 00 00 00 0A 00 00 00 1E 00 00 00 02 00 00 00 EB 2C 9A 00 81 3A 54 00 01 00 00 00 A0 0F 00 00 54 0B 00 00 00 00 00 00 01 00 00 00 1E 00 00 00 02 00 00 00 00 09 3D 00 20 57 3D 00 A0 E1 0F 00 44 00 68 74 74 70 3A 2F 2F 6E 78 63 61 63 68 65 2E 6E 65 78 6F 6E 2E 6E 65 74 2F 6D 61 70 6C 65 73 74 6F 72 79 2F 73 68 6F 70 2F 67 61 6D 65 2F 43 75 62 65 50 63 6B 67 73 5F 32 30 31 33 30 34 32 34 2E 6A 70 67 D4 2C 9A 00 2F E6 8A 00 01 00 00 00 03 00 00 00 01 00 00 00 25 00 00 00 58 0F 02 00 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 00 00 2C F1 B2 E7 CD 01 00 80 05 BB 46 E6 17 02 A0 8C 00 00 00 00 00 00 01 00 00 00 07 00 00 00 01 01 01 00 01 02 00 00 00 A9 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 02 00 00 00 A1 A3 98 00 70 3D 4D 00 00 00 00 00 A0 8C 00 00 A0 8C 00 00 00 00 00 00 23 00 00 00 07 00 00 00 02 00 00 00 7E E1 F5 05 F8 50 4D 00 01 00 00 00 B8 82 01 00 00 00 00 00 00 00 00 00 0A 00 00 00 07 00 00 00 02 00 00 00 00 09 3D 00 20 57 3D 00 A0 E1 0F 00 44 00 68 74 74 70 3A 2F 2F 6E 78 63 61 63 68 65 2E 6E 65 78 6F 6E 2E 6E 65 74 2F 6D 61 70 6C 65 73 74 6F 72 79 2F 73 68 6F 70 2F 67 61 6D 65 2F 43 75 62 65 50 63 6B 67 73 5F 32 30 31 33 30 34 32 34 2E 6A 70 67 9E E1 F5 05 2D E6 8A 00 01 00 00 00 00 00 00 00 01 00 00 00 00 00 00 00 F4 1A 00 00 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 F4 1A 00 00 00 00 00 00 01 00 00 00 5A 00 00 00 01 01 01 00 01 02 00 00 00 2A 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 02 00 00 00 44 E1 F5 05 D0 46 4D 00 01 00 00 00 F4 1A 00 00 F4 1A 00 00 00 00 00 00 01 00 00 00 5A 00 00 00 02 00 00 00 A9 E1 F5 05 9E 62 54 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 01 00 00 00 5A 00 00 00 02 00 00 00 00 09 3D 00 30 7E 3D 00 B4 7C 10 00 46 00 68 74 74 70 3A 2F 2F 6E 78 63 61 63 68 65 2E 6E 65 78 6F 6E 2E 6E 65 74 2F 6D 61 70 6C 65 73 74 6F 72 79 2F 73 68 6F 70 2F 67 61 6D 65 2F 70 65 74 70 61 63 6B 61 67 65 73 5F 32 30 31 33 30 34 31 37 2E 6A 70 67 81 E2 F5 05 91 E7 8A 00 01 00 00 00 03 00 00 00 01 00 00 00 00 00 00 00 88 5E 00 00 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 00 40 50 3D 41 30 CE 01 00 80 05 BB 46 E6 17 02 50 46 00 00 00 00 00 00 01 00 00 00 5A 00 00 00 01 01 01 00 01 02 00 00 00 11 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 05 00 00 00 35 E2 F5 05 39 4C 4C 00 01 00 00 00 00 19 00 00 70 17 00 00 00 00 00 00 01 00 00 00 5A 00 00 00 02 00 00 00 37 E2 F5 05 3A 4C 4C 00 01 00 00 00 00 19 00 00 70 17 00 00 00 00 00 00 01 00 00 00 5A 00 00 00 02 00 00 00 39 E2 F5 05 3B 4C 4C 00 01 00 00 00 00 19 00 00 70 17 00 00 00 00 00 00 01 00 00 00 5A 00 00 00 02 00 00 00 7D E2 F5 05 AB AE 4F 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 05 00 00 00 5A 00 00 00 02 00 00 00 65 94 96 03 20 50 53 00 01 00 00 00 C4 09 00 00 00 00 00 00 00 00 00 00 01 00 00 00 5A 00 00 00 02 00 00 00 00 09 3D 00 30 7E 3D 00 B4 7C 10 00 46 00 68 74 74 70 3A 2F 2F 6E 78 63 61 63 68 65 2E 6E 65 78 6F 6E 2E 6E 65 74 2F 6D 61 70 6C 65 73 74 6F 72 79 2F 73 68 6F 70 2F 67 61 6D 65 2F 70 65 74 70 61 63 6B 61 67 65 73 5F 32 30 31 33 30 34 31 37 2E 6A 70 67 82 E2 F5 05 92 E7 8A 00 01 00 00 00 03 00 00 00 01 00 00 00 00 00 00 00 20 4E 00 00 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 00 40 50 3D 41 30 CE 01 00 80 05 BB 46 E6 17 02 BC 34 00 00 00 00 00 00 01 00 00 00 5A 00 00 00 01 01 01 00 01 02 00 00 00 11 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 05 00 00 00 4A E1 F5 05 33 4C 4C 00 01 00 00 00 88 13 00 00 94 11 00 00 00 00 00 00 01 00 00 00 5A 00 00 00 02 00 00 00 4D E1 F5 05 34 4C 4C 00 01 00 00 00 88 13 00 00 94 11 00 00 00 00 00 00 01 00 00 00 5A 00 00 00 02 00 00 00 50 E1 F5 05 35 4C 4C 00 01 00 00 00 88 13 00 00 94 11 00 00 00 00 00 00 01 00 00 00 5A 00 00 00 02 00 00 00 7D E2 F5 05 AB AE 4F 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 05 00 00 00 5A 00 00 00 02 00 00 00 65 94 96 03 20 50 53 00 01 00 00 00 C4 09 00 00 00 00 00 00 00 00 00 00 01 00 00 00 5A 00 00 00 02 00 00 00 00 09 3D 00 40 A5 3D 00 EC 7B 10 00 4A 00 68 74 74 70 3A 2F 2F 6E 78 63 61 63 68 65 2E 6E 65 78 6F 6E 2E 6E 65 74 2F 6D 61 70 6C 65 73 74 6F 72 79 2F 73 68 6F 70 2F 67 61 6D 65 2F 32 30 6F 66 66 5F 70 65 74 65 71 75 69 70 73 5F 32 30 31 33 30 34 31 37 2E 6A 70 67 A0 0D 95 03 20 A6 1B 00 01 00 00 00 03 00 00 00 00 00 00 00 09 00 00 00 60 09 00 00 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 00 80 F5 80 FE 3A CE 01 00 00 BE D2 FE 45 CE 01 80 07 00 00 00 00 00 00 01 00 00 00 5A 00 00 00 01 01 01 00 01 02 00 00 00 0A 02 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 09 3D 00 40 A5 3D 00 EC 7B 10 00 4A 00 68 74 74 70 3A 2F 2F 6E 78 63 61 63 68 65 2E 6E 65 78 6F 6E 2E 6E 65 74 2F 6D 61 70 6C 65 73 74 6F 72 79 2F 73 68 6F 70 2F 67 61 6D 65 2F 32 30 6F 66 66 5F 70 65 74 65 71 75 69 70 73 5F 32 30 31 33 30 34 31 37 2E 6A 70 67 A4 0D 95 03 21 A6 1B 00 01 00 00 00 03 00 00 00 00 00 00 00 08 00 00 00 60 09 00 00 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 00 80 F5 80 FE 3A CE 01 00 00 BE D2 FE 45 CE 01 80 07 00 00 00 00 00 00 01 00 00 00 5A 00 00 00 01 01 01 00 01 02 00 00 00 88 01 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 09 3D 00 40 A5 3D 00 EC 7B 10 00 4A 00 68 74 74 70 3A 2F 2F 6E 78 63 61 63 68 65 2E 6E 65 78 6F 6E 2E 6E 65 74 2F 6D 61 70 6C 65 73 74 6F 72 79 2F 73 68 6F 70 2F 67 61 6D 65 2F 32 30 6F 66 66 5F 70 65 74 65 71 75 69 70 73 5F 32 30 31 33 30 34 31 37 2E 6A 70 67 C4 0D 95 03 22 A6 1B 00 01 00 00 00 03 00 00 00 00 00 00 00 07 00 00 00 08 07 00 00 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 00 80 F5 80 FE 3A CE 01 00 00 BE D2 FE 45 CE 01 A0 05 00 00 00 00 00 00 01 00 00 00 5A 00 00 00 01 01 01 00 01 02 00 00 00 9A 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 09 3D 00 40 A5 3D 00 EC 7B 10 00 4A 00 68 74 74 70 3A 2F 2F 6E 78 63 61 63 68 65 2E 6E 65 78 6F 6E 2E 6E 65 74 2F 6D 61 70 6C 65 73 74 6F 72 79 2F 73 68 6F 70 2F 67 61 6D 65 2F 32 30 6F 66 66 5F 70 65 74 65 71 75 69 70 73 5F 32 30 31 33 30 34 31 37 2E 6A 70 67 C6 0D 95 03 23 A6 1B 00 01 00 00 00 03 00 00 00 00 00 00 00 06 00 00 00 08 07 00 00 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 00 80 F5 80 FE 3A CE 01 00 00 BE D2 FE 45 CE 01 A0 05 00 00 00 00 00 00 01 00 00 00 5A 00 00 00 01 01 01 00 01 02 00 00 00 7C 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 09 3D 00 40 A5 3D 00 EC 7B 10 00 4A 00 68 74 74 70 3A 2F 2F 6E 78 63 61 63 68 65 2E 6E 65 78 6F 6E 2E 6E 65 74 2F 6D 61 70 6C 65 73 74 6F 72 79 2F 73 68 6F 70 2F 67 61 6D 65 2F 32 30 6F 66 66 5F 70 65 74 65 71 75 69 70 73 5F 32 30 31 33 30 34 31 37 2E 6A 70 67 C8 0D 95 03 26 A6 1B 00 01 00 00 00 03 00 00 00 00 00 00 00 04 00 00 00 08 07 00 00 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 00 80 F5 80 FE 3A CE 01 00 00 BE D2 FE 45 CE 01 A0 05 00 00 00 00 00 00 01 00 00 00 5A 00 00 00 01 01 01 00 01 02 00 00 00 6B 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 09 3D 00 40 A5 3D 00 EC 7B 10 00 4A 00 68 74 74 70 3A 2F 2F 6E 78 63 61 63 68 65 2E 6E 65 78 6F 6E 2E 6E 65 74 2F 6D 61 70 6C 65 73 74 6F 72 79 2F 73 68 6F 70 2F 67 61 6D 65 2F 32 30 6F 66 66 5F 70 65 74 65 71 75 69 70 73 5F 32 30 31 33 30 34 31 37 2E 6A 70 67 C9 0D 95 03 24 A6 1B 00 01 00 00 00 03 00 00 00 00 00 00 00 05 00 00 00 08 07 00 00 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 00 80 F5 80 FE 3A CE 01 00 00 BE D2 FE 45 CE 01 A0 05 00 00 00 00 00 00 01 00 00 00 5A 00 00 00 01 01 01 00 01 02 00 00 00 C6 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 09 3D 00 40 A5 3D 00 EC 7B 10 00 4A 00 68 74 74 70 3A 2F 2F 6E 78 63 61 63 68 65 2E 6E 65 78 6F 6E 2E 6E 65 74 2F 6D 61 70 6C 65 73 74 6F 72 79 2F 73 68 6F 70 2F 67 61 6D 65 2F 32 30 6F 66 66 5F 70 65 74 65 71 75 69 70 73 5F 32 30 31 33 30 34 31 37 2E 6A 70 67 CA 0D 95 03 25 A6 1B 00 01 00 00 00 03 00 00 00 00 00 00 00 01 00 00 00 08 07 00 00 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 00 80 F5 80 FE 3A CE 01 00 00 BE D2 FE 45 CE 01 A0 05 00 00 00 00 00 00 01 00 00 00 5A 00 00 00 01 01 01 00 01 02 00 00 00 92 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 09 3D 00 40 A5 3D 00 EC 7B 10 00 4A 00 68 74 74 70 3A 2F 2F 6E 78 63 61 63 68 65 2E 6E 65 78 6F 6E 2E 6E 65 74 2F 6D 61 70 6C 65 73 74 6F 72 79 2F 73 68 6F 70 2F 67 61 6D 65 2F 32 30 6F 66 66 5F 70 65 74 65 71 75 69 70 73 5F 32 30 31 33 30 34 31 37 2E 6A 70 67 E0 0D 95 03 27 A6 1B 00 01 00 00 00 03 00 00 00 00 00 00 00 05 00 00 00 B8 0B 00 00 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 00 80 F5 80 FE 3A CE 01 00 00 BE D2 FE 45 CE 01 60 09 00 00 00 00 00 00 01 00 00 00 5A 00 00 00 01 01 01 00 01 02 00 00 00 22 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 09 3D 00 40 A5 3D 00 EC 7B 10 00 4A 00 68 74 74 70 3A 2F 2F 6E 78 63 61 63 68 65 2E 6E 65 78 6F 6E 2E 6E 65 74 2F 6D 61 70 6C 65 73 74 6F 72 79 2F 73 68 6F 70 2F 67 61 6D 65 2F 32 30 6F 66 66 5F 70 65 74 65 71 75 69 70 73 5F 32 30 31 33 30 34 31 37 2E 6A 70 67 3D 0E 95 03 28 A6 1B 00 01 00 00 00 03 00 00 00 00 00 00 00 14 00 00 00 AC 0D 00 00 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 00 80 F5 80 FE 3A CE 01 00 00 BE D2 FE 45 CE 01 F0 0A 00 00 00 00 00 00 01 00 00 00 5A 00 00 00 01 01 01 00 01 02 00 00 00 52 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 09 3D 00 40 A5 3D 00 EC 7B 10 00 4A 00 68 74 74 70 3A 2F 2F 6E 78 63 61 63 68 65 2E 6E 65 78 6F 6E 2E 6E 65 74 2F 6D 61 70 6C 65 73 74 6F 72 79 2F 73 68 6F 70 2F 67 61 6D 65 2F 32 30 6F 66 66 5F 70 65 74 65 71 75 69 70 73 5F 32 30 31 33 30 34 31 37 2E 6A 70 67 3F 0E 95 03 29 A6 1B 00 01 00 00 00 03 00 00 00 00 00 00 00 18 00 00 00 08 07 00 00 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 00 80 F5 80 FE 3A CE 01 00 00 BE D2 FE 45 CE 01 A0 05 00 00 00 00 00 00 01 00 00 00 5A 00 00 00 01 01 01 00 01 02 00 00 00 31 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 09 3D 00 40 A5 3D 00 EC 7B 10 00 4A 00 68 74 74 70 3A 2F 2F 6E 78 63 61 63 68 65 2E 6E 65 78 6F 6E 2E 6E 65 74 2F 6D 61 70 6C 65 73 74 6F 72 79 2F 73 68 6F 70 2F 67 61 6D 65 2F 32 30 6F 66 66 5F 70 65 74 65 71 75 69 70 73 5F 32 30 31 33 30 34 31 37 2E 6A 70 67 80 E1 F5 05 2A A6 1B 00 01 00 00 00 03 00 00 00 00 00 00 00 00 00 00 00 08 07 00 00 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 00 80 F5 80 FE 3A CE 01 00 00 BE D2 FE 45 CE 01 A0 05 00 00 00 00 00 00 01 00 00 00 5A 00 00 00 01 01 01 00 01 02 00 00 00 52 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 09 3D 00 50 CC 3D 00 48 DF 0F 00 45 00 68 74 74 70 3A 2F 2F 6E 78 63 61 63 68 65 2E 6E 65 78 6F 6E 2E 6E 65 74 2F 6D 61 70 6C 65 73 74 6F 72 79 2F 73 68 6F 70 2F 67 61 6D 65 2F 43 61 72 76 65 64 53 6C 6F 74 5F 32 30 31 33 30 34 30 33 2E 6A 70 67 71 E2 F5 05 F8 62 54 00 01 00 00 00 02 00 00 00 00 00 00 00 00 00 00 00 D8 0E 00 00 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 D8 0E 00 00 00 00 00 00 01 00 00 00 5A 00 00 00 01 01 01 00 01 02 00 00 00 5B 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 09 3D 00 50 CC 3D 00 48 DF 0F 00 45 00 68 74 74 70 3A 2F 2F 6E 78 63 61 63 68 65 2E 6E 65 78 6F 6E 2E 6E 65 74 2F 6D 61 70 6C 65 73 74 6F 72 79 2F 73 68 6F 70 2F 67 61 6D 65 2F 43 61 72 76 65 64 53 6C 6F 74 5F 32 30 31 33 30 34 30 33 2E 6A 70 67 72 E2 F5 05 64 3F 4D 00 01 00 00 00 02 00 00 00 00 00 00 00 00 00 00 00 60 09 00 00 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 60 09 00 00 00 00 00 00 01 00 00 00 5A 00 00 00 01 01 01 00 01 02 00 00 00 2E 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 09 3D 00 50 CC 3D 00 48 DF 0F 00 45 00 68 74 74 70 3A 2F 2F 6E 78 63 61 63 68 65 2E 6E 65 78 6F 6E 2E 6E 65 74 2F 6D 61 70 6C 65 73 74 6F 72 79 2F 73 68 6F 70 2F 67 61 6D 65 2F 43 61 72 76 65 64 53 6C 6F 74 5F 32 30 31 33 30 34 30 33 2E 6A 70 67 73 E2 F5 05 64 3F 4D 00 01 00 00 00 02 00 00 00 00 00 00 00 00 00 00 00 C0 5D 00 00 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 C0 5D 00 00 00 00 00 00 0B 00 00 00 5A 00 00 00 01 01 01 00 01 02 00 00 00 1F 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 09 3D 00 50 CC 3D 00 48 DF 0F 00 45 00 68 74 74 70 3A 2F 2F 6E 78 63 61 63 68 65 2E 6E 65 78 6F 6E 2E 6E 65 74 2F 6D 61 70 6C 65 73 74 6F 72 79 2F 73 68 6F 70 2F 67 61 6D 65 2F 43 61 72 76 65 64 53 6C 6F 74 5F 32 30 31 33 30 34 30 33 2E 6A 70 67 7E E2 F5 05 E6 62 54 00 01 00 00 00 02 00 00 00 00 00 00 00 00 00 00 00 8C 0A 00 00 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 8C 0A 00 00 00 00 00 00 01 00 00 00 5A 00 00 00 01 01 01 00 01 02 00 00 00 1C 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 09 3D 00 50 CC 3D 00 A0 E1 0F 00 45 00 68 74 74 70 3A 2F 2F 6E 78 63 61 63 68 65 2E 6E 65 78 6F 6E 2E 6E 65 74 2F 6D 61 70 6C 65 73 74 6F 72 79 2F 73 68 6F 70 2F 67 61 6D 65 2F 43 61 72 76 65 64 53 6C 6F 74 5F 32 30 31 33 30 34 30 33 2E 6A 70 67 7F E2 F5 05 93 E7 8A 00 01 00 00 00 02 00 00 00 01 00 00 00 00 00 00 00 B8 3D 00 00 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 00 40 50 3D 41 30 CE 01 00 80 05 BB 46 E6 17 02 00 32 00 00 00 00 00 00 01 00 00 00 5A 00 00 00 01 01 01 00 01 02 00 00 00 18 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 03 00 00 00 74 E2 F5 05 64 3F 4D 00 00 00 00 00 E0 2E 00 00 AC 26 00 00 00 00 00 00 05 00 00 00 5A 00 00 00 02 00 00 00 06 2D 9A 00 9C 62 54 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 01 00 00 00 5A 00 00 00 02 00 00 00 71 E2 F5 05 F8 62 54 00 01 00 00 00 D8 0E 00 00 54 0B 00 00 00 00 00 00 01 00 00 00 5A 00 00 00 02 00 00 00 00 09 3D 00 50 CC 3D 00 A0 E1 0F 00 45 00 68 74 74 70 3A 2F 2F 6E 78 63 61 63 68 65 2E 6E 65 78 6F 6E 2E 6E 65 74 2F 6D 61 70 6C 65 73 74 6F 72 79 2F 73 68 6F 70 2F 67 61 6D 65 2F 43 61 72 76 65 64 53 6C 6F 74 5F 32 30 31 33 30 34 30 33 2E 6A 70 67 80 E2 F5 05 94 E7 8A 00 01 00 00 00 02 00 00 00 01 00 00 00 00 00 00 00 98 6C 00 00 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 00 40 50 3D 41 30 CE 01 00 80 05 BB 46 E6 17 02 28 55 00 00 00 00 00 00 01 00 00 00 5A 00 00 00 01 01 01 00 01 02 00 00 00 42 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 04 00 00 00 75 E2 F5 05 64 3F 4D 00 00 00 00 00 C0 5D 00 00 D4 49 00 00 00 00 00 00 0A 00 00 00 5A 00 00 00 02 00 00 00 06 2D 9A 00 9C 62 54 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 01 00 00 00 5A 00 00 00 02 00 00 00 09 2D 9A 00 9D 62 54 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 01 00 00 00 5A 00 00 00 02 00 00 00 71 E2 F5 05 F8 62 54 00 01 00 00 00 D8 0E 00 00 54 0B 00 00 00 00 00 00 01 00 00 00 5A 00 00 00 02 00 00 00"));
        c.sendPacket(CField.getPacketFromHexString(head + " 05 01 06 C0 C6 2D 00 D0 ED 2D 00 D4 B7 0F 00 00 00 D9 FE FD 02 A0 A6 4F 00 01 00 00 00 00 00 00 00 00 00 00 00 32 00 00 00 30 75 00 00 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 AE 0B 00 00 5A 00 00 00 23 00 00 00 5A 00 00 00 00 01 01 00 00 02 00 00 00 26 03 00 00 00 00 00 00 00 00 04 00 00 00 00 00 00 00 00 00 00 00 00 00 C0 C6 2D 00 D0 ED 2D 00 38 B8 0F 00 00 00 88 2C 9A 00 AC AE 4F 00 01 00 00 00 00 00 00 00 00 00 00 00 22 00 00 00 D0 84 00 00 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 3E 0D 00 00 5A 00 00 00 0B 00 00 00 1E 00 00 00 00 01 01 00 00 02 00 00 00 BC 02 00 00 00 00 00 00 00 00 04 00 00 00 00 00 00 00 00 00 00 00 00 00 C0 C6 2D 00 D0 ED 2D 00 20 07 10 00 00 00 13 FE FD 02 F0 DA 52 00 01 00 00 00 00 00 00 00 00 00 00 00 01 00 00 00 F4 1A 00 00 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 A8 02 00 00 5A 00 00 00 01 00 00 00 5A 00 00 00 00 01 01 00 00 02 00 00 00 2B 03 00 00 00 00 00 00 00 00 04 00 00 00 00 00 00 00 00 00 00 00 00 00 C0 C6 2D 00 D0 ED 2D 00 15 54 10 00 00 00 9C F1 FA 02 58 95 4E 00 01 00 00 00 00 00 00 00 00 00 00 00 32 00 00 00 E4 0C 00 00 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 40 01 00 00 5A 00 00 00 01 00 00 00 5A 00 00 00 00 01 01 00 00 02 00 00 00 18 07 00 00 00 00 00 00 00 00 04 00 00 00 00 00 00 00 00 00 00 00 00 00 C0 C6 2D 00 D0 ED 2D 00 16 54 10 00 00 00 C9 F1 FA 02 35 9D 4E 00 01 00 00 00 00 00 00 00 00 00 00 00 32 00 00 00 E4 0C 00 00 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 40 01 00 00 5A 00 00 00 01 00 00 00 5A 00 00 00 00 01 01 00 00 02 00 00 00 70 04 00 00 00 00 00 00 00 00 04 00 00 00 00 00 00 00 00 00 00 00 00 00 C0 C6 2D 00 D0 ED 2D 00 34 A2 10 00 00 00 42 77 FC 02 7A C0 4C 00 01 00 00 00 00 00 00 00 00 00 00 00 01 00 00 00 0C 17 00 00 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 44 02 00 00 5A 00 00 00 01 00 00 00 07 00 00 00 00 01 01 00 00 02 00 00 00 89 00 00 00 00 00 00 00 00 00 04 00 00 00 00 00 00 00 00 00 00 00 00 00"));
        c.sendPacket(CField.getPacketFromHexString(head + " 06 01 05 C0 C6 2D 00 E0 14 2E 00 15 54 10 00 00 00 9C F1 FA 02 58 95 4E 00 01 00 00 00 04 00 00 00 00 00 00 00 32 00 00 00 E4 0C 00 00 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 40 01 00 00 5A 00 00 00 01 00 00 00 5A 00 00 00 00 01 01 00 00 02 00 00 00 18 07 00 00 00 00 00 00 00 00 04 00 00 00 00 00 00 00 00 00 00 00 00 00 C0 C6 2D 00 E0 14 2E 00 16 54 10 00 00 00 C9 F1 FA 02 35 9D 4E 00 01 00 00 00 04 00 00 00 00 00 00 00 32 00 00 00 E4 0C 00 00 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 40 01 00 00 5A 00 00 00 01 00 00 00 5A 00 00 00 00 01 01 00 00 02 00 00 00 70 04 00 00 00 00 00 00 00 00 04 00 00 00 00 00 00 00 00 00 00 00 00 00 C0 C6 2D 00 E0 14 2E 00 C4 90 0F 00 00 00 BF C3 C9 01 84 E7 4C 00 01 00 00 00 00 00 00 00 00 00 00 00 0F 00 00 00 AC 26 00 00 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 AC 26 00 00 00 00 00 00 01 00 00 00 1E 00 00 00 01 01 01 00 01 02 00 00 00 22 03 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 C0 C6 2D 00 E0 14 2E 00 38 B8 0F 00 00 00 87 2C 9A 00 AC AE 4F 00 01 00 00 00 00 00 00 00 00 00 00 00 22 00 00 00 48 0D 00 00 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 4A 01 00 00 5A 00 00 00 01 00 00 00 1E 00 00 00 00 01 01 00 00 02 00 00 00 10 08 00 00 00 00 00 00 00 00 04 00 00 00 00 00 00 00 00 00 00 00 00 00 C0 C6 2D 00 E0 14 2E 00 D4 B7 0F 00 00 00 D9 FE FD 02 A0 A6 4F 00 01 00 00 00 00 00 00 00 00 00 00 00 32 00 00 00 30 75 00 00 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 AE 0B 00 00 5A 00 00 00 23 00 00 00 5A 00 00 00 00 01 01 00 00 02 00 00 00 26 03 00 00 00 00 00 00 00 00 04 00 00 00 00 00 00 00 00 00 00 00 00 00"));
        c.sendPacket(CField.getPacketFromHexString(head + " 09 01 01 C0 C6 2D 00 00 63 2E 00 B0 08 10 00 00 00 18 E3 F5 05 A8 69 52 00 01 00 00 00 05 00 00 00 00 00 00 00 00 00 00 00 01 00 00 00 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 01 00 00 00 00 00 00 00 01 00 00 00 1E 00 00 00 01 01 01 01 01 02 00 00 00 65 01 00 00 32 00 00 00 0A 00 31 4D 53 35 34 30 31 30 30 30 00 00 00 00 00 00 00 00 00 00 00 00 00 00"));
        c.sendPacket(CField.getPacketFromHexString(head + " 08 01 05 C0 C6 2D 00 F0 3B 2E 00 C4 90 0F 00 00 00 BF C3 C9 01 84 E7 4C 00 01 00 00 00 04 00 00 00 00 00 00 00 0F 00 00 00 AC 26 00 00 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 AC 26 00 00 00 00 00 00 01 00 00 00 1E 00 00 00 01 01 01 00 01 02 00 00 00 22 03 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 C0 C6 2D 00 F0 3B 2E 00 74 E0 0F 00 00 00 7C FE FD 02 81 3A 54 00 01 00 00 00 04 00 00 00 00 00 00 00 02 00 00 00 A0 0F 00 00 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 86 01 00 00 5A 00 00 00 01 00 00 00 5A 00 00 00 00 01 01 00 00 02 00 00 00 B9 01 00 00 00 00 00 00 00 00 04 00 00 00 00 00 00 00 00 00 00 00 00 00 C0 C6 2D 00 F0 3B 2E 00 E8 07 10 00 00 00 40 FE FD 02 70 13 54 00 01 00 00 00 04 00 00 00 00 00 00 00 01 00 00 00 F4 01 00 00 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 28 00 00 00 5A 00 00 00 01 00 00 00 5A 00 00 00 00 01 01 00 00 02 00 00 00 8C 00 00 00 00 00 00 00 00 00 04 00 00 00 00 00 00 00 00 00 00 00 00 00 C0 C6 2D 00 F0 3B 2E 00 10 E0 0F 00 00 00 3D FE FD 02 D0 FD 54 00 01 00 00 00 04 00 00 00 00 00 00 00 04 00 00 00 24 13 00 00 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 E0 01 00 00 5A 00 00 00 01 00 00 00 5A 00 00 00 00 01 01 00 00 02 00 00 00 78 00 00 00 00 00 00 00 00 00 04 00 00 00 00 00 00 00 00 00 00 00 00 00 C0 C6 2D 00 F0 3B 2E 00 74 E0 0F 00 00 00 35 FE FD 02 80 3A 54 00 01 00 00 00 04 00 00 00 00 00 00 00 03 00 00 00 B8 0B 00 00 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 00 80 22 D6 94 EF C4 01 00 80 05 BB 46 E6 17 02 22 01 00 00 5A 00 00 00 01 00 00 00 5A 00 00 00 00 01 01 00 00 02 00 00 00 F3 01 00 00 00 00 00 00 00 00 04 00 00 00 00 00 00 00 00 00 00 00 00 00"));
    }

    public static void CSUpdate(final MapleClient c) {

        doCSPackets(c);
    }

    private static boolean CouponCodeAttempt(final MapleClient c) {
        c.couponAttempt++;
        return c.couponAttempt > 5;
    }

    public static void CouponCode(final String code, final MapleClient c) {
        if (code.length() <= 0) {
            return;
        }
        Triple<Boolean, Integer, Integer> info = null;
        try {
            info = MapleCharacterUtil.getNXCodeInfo(code);
        } catch (SQLException e) {
        }
        if (info != null && info.left) {
            if (!CouponCodeAttempt(c)) {
                int type = info.mid, item = info.right;
                try {
                    MapleCharacterUtil.setNXCodeUsed(c.getCharacter().getName(), code);
                } catch (SQLException e) {
                }
                /*
                 * Explanation of type!
                 * Basically, this makes coupon codes do
                 * different things!
                 *
                 * Type 1: A-Cash,
                 * Type 2: Maple Points
                 * Type 3: Item.. use SN
                 * Type 4: Mesos
                 */
                Map<Integer, Item> itemz = new HashMap<>();
                int maplePoints = 0, mesos = 0, iItem = 0;
                switch (type) {
                    case 1:
                    case 2: //Confirmed MaplePoints
                        c.getCharacter().modifyCSPoints(type, item, false);
                        maplePoints = item;
                        break;
                    case 3:
                        c.getCharacter().gainMeso(item, false);
                        mesos = item;
                    case 4:
                        System.out.println("4");
                        short quantity = 1;
                   //     item  iItem;
                        quantity = quantity;
                        MapleInventoryManipulator.addById(c, item, (short) quantity, null);
                        break;                       
                       case 5:
                        System.out.println("4");
                        MapleInventoryManipulator.addById(c, item, (short) 100, null);
                        c.sendPacket(CSPacket.showCouponRedeemedItem(itemz, mesos, maplePoints, c));
                        break;
                }
                c.sendPacket(CSPacket.showCouponRedeemedItem(itemz, mesos, maplePoints, c));
                doCSPackets(c);
            }
        }
    }

    public static void BuyCashItem(final MaplePacketReader inPacket, final MapleClient c, final MapleCharacter chr) {
        final int action = inPacket.readByte();
        System.out.println("action " + action);
        if (action == 0) {
            inPacket.skip(2);
            CouponCode(inPacket.readMapleAsciiString(), c);
        } else if (action == 2) {
            inPacket.skip(1);
            int type = inPacket.readInt();//type
            int sn = inPacket.readInt();
//            final CashItem item = CashItemFactory.getInstance().getMenuItem(sn);
            final CashItem item = CashItemFactory.getInstance().getAllItem(sn);
            final int toCharge = inPacket.readInt();//price
            if (item == null) {
                c.sendPacket(CSPacket.sendCSFail(0));
            }
            chr.modifyCSPoints(type, -toCharge, true);
            Item itemz = chr.getCashInventory().toItem(item);
            if (itemz != null) {
                chr.getCashInventory().addToInventory(itemz);
                c.sendPacket(CSPacket.showBoughtCSItem(itemz, item.getSN(), c.getAccountID()));
            } else {
                c.sendPacket(CSPacket.sendCSFail(0));
            }
        } else if (action == 101) {//TODO BETTER idk what it is
//            System.out.println("action 101");//might be farm mesos? RITE NOW IS FREEH
            inPacket.skip(1);
            int type = inPacket.readInt();//type
            int sn = inPacket.readInt();
            final CashItem item = CashItemFactory.getInstance().getAllItem(sn);
            if (item == null) {
                c.sendPacket(CSPacket.sendCSFail(0));
            }
//            chr.modifyCSPoints(type, -toCharge, true);
            Item itemz = chr.getCashInventory().toItem(item);
            if (itemz != null) {
                chr.getCashInventory().addToInventory(itemz);
                c.sendPacket(CSPacket.showBoughtCSItem(itemz, item.getSN(), c.getAccountID()));
            } else {
                c.sendPacket(CSPacket.sendCSFail(0));
            }
        } else if (action == 3) {
            inPacket.skip(1);
            final int toCharge = inPacket.readInt();
            final CashItemInfo item = CashItemFactory.getInstance().getItem(inPacket.readInt());

            if (item != null && chr.getCSPoints(toCharge) >= item.getPrice()) {
                if (!item.genderEquals(c.getCharacter().getGender())/* && c.getPlayer().getAndroid() == null*/) {
                    c.sendPacket(CSPacket.sendCSFail(0xA7));
                    doCSPackets(c);
                    return;
                } else if (item.getId() == 5211046 || item.getId() == 5211047 || item.getId() == 5211048 || item.getId() == 5050100 || item.getId() == 5051001) {
                    c.sendPacket(CWvsContext.broadcastMsg(1, "You cannot purchase this item through cash shop."));
                    c.sendPacket(CWvsContext.enableActions());
                    doCSPackets(c);
                    return;
                } else if (c.getCharacter().getCashInventory().getItemsSize() >= 100) {
                    c.sendPacket(CSPacket.sendCSFail(0xB2));
                    doCSPackets(c);
                    return;
                }
                for (int id : GameConstants.cashBlock) {
                    if (item.getId() == id) {
                        c.sendPacket(CWvsContext.broadcastMsg(1, "You cannot purchase this item through cash shop."));
                        c.sendPacket(CWvsContext.enableActions());
                        doCSPackets(c);
                        return;
                    }
                }
                chr.modifyCSPoints(toCharge, -item.getPrice(), false);
                Item itemz = chr.getCashInventory().toItem(item);
                if (itemz != null && itemz.getUniqueId() > 0 && itemz.getItemId() == item.getId() && itemz.getQuantity() == item.getCount()) {
                    chr.getCashInventory().addToInventory(itemz);
                    //c.sendPacket(CSPacket.confirmToCSInventory(itemz, c.getAccID(), item.getSN()));
                    c.sendPacket(CSPacket.showBoughtCSItem(itemz, item.getSN(), c.getAccountID()));

                } else {
                    c.sendPacket(CSPacket.sendCSFail(0));
                }
            } else {
                c.sendPacket(CSPacket.sendCSFail(0));
            }
        } else if (action == 4 /*|| action == 34*/) { //gift, package
            inPacket.readMapleAsciiString(); // pic
            final CashItemInfo item = CashItemFactory.getInstance().getItem(inPacket.readInt());
            if (action == 4) {
                inPacket.skip(1);
            }
            String partnerName = inPacket.readMapleAsciiString();
            String msg = inPacket.readMapleAsciiString();
            if (item == null || c.getCharacter().getCSPoints(1) < item.getPrice() || msg.length() > 73 || msg.length() < 1) { //dont want packet editors gifting random stuff =P
                c.sendPacket(CSPacket.sendCSFail(0));
                doCSPackets(c);
                return;
            }
            Triple<Integer, Integer, Integer> info = MapleCharacterUtil.getInfoByName(partnerName, c.getCharacter().getWorld());
            if (info == null || info.getLeft().intValue() <= 0 || info.getLeft().intValue() == c.getCharacter().getID() || info.getMid().intValue() == c.getAccountID()) {
                c.sendPacket(CSPacket.sendCSFail(0xA2)); //9E v75
                doCSPackets(c);
                return;
            } else if (!item.genderEquals(info.getRight().intValue())) {
                c.sendPacket(CSPacket.sendCSFail(0xA3));
                doCSPackets(c);
                return;
            } else {
                //get the packets for that
                c.getCharacter().getCashInventory().gift(info.getLeft().intValue(), c.getCharacter().getName(), msg, item.getSN(), MapleInventoryIdentifier.getInstance());
                c.getCharacter().modifyCSPoints(1, -item.getPrice(), false);
                c.sendPacket(CSPacket.sendGift(item.getPrice(), item.getId(), item.getCount(), partnerName, action == 34));
            }
        } else if (action == 5) { //was Wishlist - Now inc inv slot
            inPacket.skip(1);
            final int toCharge = inPacket.readInt();
            final boolean coupon = inPacket.readByte() > 0;
            if (coupon) {
                final MapleInventoryType type = getInventoryType(inPacket.readInt());
                if (chr.getCSPoints(toCharge) >= 6000 && chr.getInventory(type).getSlotLimit() < 89) {
                    chr.modifyCSPoints(toCharge, -6000, false);
                    chr.getInventory(type).addSlot((byte) 8);
                    chr.dropMessage(1, "Slots has been increased to " + chr.getInventory(type).getSlotLimit());
                    c.sendPacket(CField.getCharInfo(chr));
                } else {
                    c.sendPacket(CSPacket.sendCSFail(0xA4));
                }
            } else {
                final MapleInventoryType type = MapleInventoryType.getByType(inPacket.readByte());
                if (chr.getCSPoints(toCharge) >= 4000 && chr.getInventory(type).getSlotLimit() < 93) {
                    chr.modifyCSPoints(toCharge, -4000, false);
                    chr.getInventory(type).addSlot((byte) 4);
                    chr.dropMessage(1, "Slots has been increased to " + chr.getInventory(type).getSlotLimit());
                    c.sendPacket(CField.getCharInfo(chr));
                } else {
                    c.sendPacket(CSPacket.sendCSFail(0xA4));
                }
            }
//            chr.clearWishlist();
//            if (inPacket.available() < 40) {
//                c.sendPacket(CSPacket.sendCSFail(0));
//                doCSPackets(c);
//                return;
//            }
//            int[] wishlist = new int[10];
//            for (int i = 0; i < 10; i++) {
//                wishlist[i] = inPacket.readInt();
//            }
//            chr.setWishlist(wishlist);
//            c.sendPacket(CSPacket.sendWishList(chr, true));

        } else if (action == 6) { // Increase inv
            inPacket.skip(1);
            final int toCharge = inPacket.readInt();
            final boolean coupon = inPacket.readByte() > 0;
            if (coupon) {
                final MapleInventoryType type = getInventoryType(inPacket.readInt());
                if (chr.getCSPoints(toCharge) >= 6000 && chr.getInventory(type).getSlotLimit() < 89) {
                    chr.modifyCSPoints(toCharge, -6000, false);
                    chr.getInventory(type).addSlot((byte) 8);
                    chr.dropMessage(1, "Slots has been increased to " + chr.getInventory(type).getSlotLimit());
                    c.sendPacket(CField.getCharInfo(chr));
                } else {
                    c.sendPacket(CSPacket.sendCSFail(0xA4));
                }
            } else {
                final MapleInventoryType type = MapleInventoryType.getByType(inPacket.readByte());
                if (chr.getCSPoints(toCharge) >= 4000 && chr.getInventory(type).getSlotLimit() < 93) {
                    chr.modifyCSPoints(toCharge, -4000, false);
                    chr.getInventory(type).addSlot((byte) 4);
                    chr.dropMessage(1, "Slots has been increased to " + chr.getInventory(type).getSlotLimit());
                    c.sendPacket(CField.getCharInfo(chr));
                } else {
                    c.sendPacket(CSPacket.sendCSFail(0xA4));
                }
            }

        } else if (action == 7) { // Increase slot space
            inPacket.skip(1);
            final int toCharge = inPacket.readInt();
            final int coupon = inPacket.readByte() > 0 ? 2 : 1;
            if (chr.getCSPoints(toCharge) >= 4000 * coupon && chr.getStorage().getSlots() < (49 - (4 * coupon))) {
                chr.modifyCSPoints(toCharge, -4000 * coupon, false);
                chr.getStorage().increaseSlots((byte) (4 * coupon));
                chr.getStorage().saveToDB();
                chr.dropMessage(1, "Storage slots increased to: " + chr.getStorage().getSlots());
                c.sendPacket(CField.getCharInfo(chr));
            } else {
                c.sendPacket(CSPacket.sendCSFail(0xA4));
            }
        } else if (action == 8) { //...10 = pendant slot expansion
            inPacket.skip(1);
            final int toCharge = inPacket.readInt();
            CashItemInfo item = CashItemFactory.getInstance().getItem(inPacket.readInt());
            int slots = c.getCharacterSlots();
            if (item == null || c.getCharacter().getCSPoints(toCharge) < item.getPrice() || slots > 15 || item.getId() != 5430000) {
                c.sendPacket(CSPacket.sendCSFail(0));
                doCSPackets(c);
                return;
            }
            if (c.gainCharacterSlot()) {
                c.getCharacter().modifyCSPoints(toCharge, -item.getPrice(), false);
                chr.dropMessage(1, "Character slots increased to: " + (slots + 1));
            } else {
                c.sendPacket(CSPacket.sendCSFail(0));
            }
        } else if (action == 10) { //...10 = pendant slot expansion
            //Data: 00 01 00 00 00 DC FE FD 02
            inPacket.readByte(); //Action is short?
            inPacket.readInt(); //always 1 - No Idea
            final int sn = inPacket.readInt();
            CashItemInfo item = CashItemFactory.getInstance().getItem(sn);
            if (item == null || c.getCharacter().getCSPoints(1) < item.getPrice() || item.getId() / 10000 != 555) {
                c.sendPacket(CSPacket.sendCSFail(0));
                doCSPackets(c);
                return;
            }
            MapleQuestStatus marr = c.getCharacter().getQuestNoAdd(MapleQuest.getInstance(GameConstants.PENDANT_SLOT));
            if (marr != null && marr.getCustomData() != null && Long.parseLong(marr.getCustomData()) >= System.currentTimeMillis()) {
                c.sendPacket(CSPacket.sendCSFail(0));
            } else {
                c.getCharacter().getQuestNAdd(MapleQuest.getInstance(GameConstants.PENDANT_SLOT)).setCustomData(String.valueOf(System.currentTimeMillis() + ((long) item.getPeriod() * 24 * 60 * 60000)));
                c.getCharacter().modifyCSPoints(1, -item.getPrice(), false);
                chr.dropMessage(1, "Additional pendant slot gained.");
            }
        } else if (action == 14) { //get item from csinventory
            //uniqueid, 00 01 01 00, type->position(short)
            Item item = c.getCharacter().getCashInventory().findByCashId((int) inPacket.readLong());
//            Item item = MapleItemInformationProvider.getInstance().getEquipById(item);
            if (item != null && item.getQuantity() > 0 && MapleInventoryManipulator.checkSpace(c, item.getItemId(), item.getQuantity(), item.getOwner())) {
                Item item_ = item.copy();
                short pos = MapleInventoryManipulator.addbyItem(c, item_, true);
                if (pos >= 0) {
                    if (item_.getPet() != null) {
                        item_.getPet().setInventoryPosition(pos);
                        c.getCharacter().addPet(item_.getPet());
                    }
                    c.getCharacter().getCashInventory().removeFromInventory(item);
                    c.sendPacket(CSPacket.confirmFromCSInventory(item_, pos));
                } else {
                    c.sendPacket(CSPacket.sendCSFail(0xB1));
                }
            } else {
                c.sendPacket(CSPacket.sendCSFail(0xB1));
            }
        } else if (action == 15) { //put item in cash inventory  
//            System.out.println("action 15");
            int uniqueid = (int) inPacket.readLong();
            MapleInventoryType type = MapleInventoryType.getByType(inPacket.readByte());
            Item item = c.getCharacter().getInventory(type).findByUniqueId(uniqueid);
            if (item != null && item.getQuantity() > 0 && item.getUniqueId() > 0 && c.getCharacter().getCashInventory().getItemsSize() < 100) {
                Item item_ = item.copy();
                MapleInventoryManipulator.removeFromSlot(c, type, item.getPosition(), item.getQuantity(), false);
                if (item_.getPet() != null) {
                    c.getCharacter().removePetCS(item_.getPet());
                }
                item_.setPosition((byte) 0);
                c.getCharacter().getCashInventory().addToInventory(item_);
                c.sendPacket(CSPacket.showBoughtCSItem(item, item.getUniqueId(), c.getAccountID()));
                //warning: this d/cs
//                c.sendPacket(CSPacket.confirmToCSInventory(item, c.getAccID(), itemz.getSN()));
            } else {
                c.sendPacket(CSPacket.sendCSFail(0xB1));
            }
        } else if ((action == 33) || (action == 39)) {
            //c.sendPacket(MTSCSPacket.sendCSFail(0));
            inPacket.readMapleAsciiString(); // as13
            final int toCharge = inPacket.readInt();
            final CashItemInfo item = CashItemFactory.getInstance().getItem(inPacket.readInt());
            final String partnerName = inPacket.readMapleAsciiString();
            final String msg = inPacket.readMapleAsciiString();
            if (item == null || !GameConstants.isEffectRing(item.getId()) || c.getCharacter().getCSPoints(toCharge) < item.getPrice() || msg.length() > 73 || msg.length() < 1) {
                c.sendPacket(CSPacket.sendCSFail(0));
                doCSPackets(c);
                return;
            } else if (!item.genderEquals(c.getCharacter().getGender())) {
                c.sendPacket(CSPacket.sendCSFail(0xA6));
                doCSPackets(c);
                return;
            } else if (c.getCharacter().getCashInventory().getItemsSize() >= 100) {
                c.sendPacket(CSPacket.sendCSFail(0xB1));
                doCSPackets(c);
                return;

            }
            Triple<Integer, Integer, Integer> info = MapleCharacterUtil.getInfoByName(partnerName, c.getCharacter().getWorld());
            if (info == null || info.getLeft().intValue() <= 0 || info.getLeft().intValue() == c.getCharacter().getID()) {
                c.sendPacket(CSPacket.sendCSFail(0xB4)); //9E v75
                doCSPackets(c);
                return;
            } else if (info.getMid().intValue() == c.getAccountID()) {
                c.sendPacket(CSPacket.sendCSFail(0xA3)); //9D v75
                doCSPackets(c);
                return;
            } else {
                if (info.getRight().intValue() == c.getCharacter().getGender() && action == 30) {
                    c.sendPacket(CSPacket.sendCSFail(0xA1)); //9B v75
                    doCSPackets(c);
                    return;
                }

                int err = MapleRing.createRing(item.getId(), c.getCharacter(), partnerName, msg, info.getLeft().intValue(), item.getSN());

                if (err != 1) {
                    c.sendPacket(CSPacket.sendCSFail(0)); //9E v75
                    doCSPackets(c);
                    return;
                }
                c.getCharacter().modifyCSPoints(toCharge, -item.getPrice(), false);
            }
        } else if (action == 34) {//was 33 - packages
            inPacket.skip(1);
            int unk = inPacket.readInt();//is1
            final CashItemInfo item = CashItemFactory.getInstance().getItem(inPacket.readInt());
            final int toCharge = inPacket.readInt();
            List<Integer> ccc = null;
            if (item != null) {
                ccc = CashItemFactory.getInstance().getPackageItems(item.getId());
            }
            if (item == null || ccc == null || c.getCharacter().getCSPoints(toCharge) < item.getPrice()) {
                c.sendPacket(CSPacket.sendCSFail(0));
                doCSPackets(c);
                return;
            } else if (!item.genderEquals(c.getCharacter().getGender())) {
                c.sendPacket(CSPacket.sendCSFail(0xA6));
                doCSPackets(c);
                return;
            } else if (c.getCharacter().getCashInventory().getItemsSize() >= (100 - ccc.size())) {
                c.sendPacket(CSPacket.sendCSFail(0xB1));
                doCSPackets(c);
                return;
            }

            Map<Integer, Item> ccz = new HashMap<>();
            for (int i : ccc) {
                final CashItemInfo cii = CashItemFactory.getInstance().getSimpleItem(i);
                if (cii == null) {
                    continue;
                }
                Item itemz = c.getCharacter().getCashInventory().toItem(cii);
                if (itemz == null || itemz.getUniqueId() <= 0) {
                    continue;
                }
                for (int iz : GameConstants.cashBlock) {
                    if (itemz.getItemId() == iz) {
                    }
                }
                ccz.put(i, itemz);
                c.getCharacter().getCashInventory().addToInventory(itemz);
            }
            chr.modifyCSPoints(toCharge, -item.getPrice(), false);
            c.sendPacket(CSPacket.showBoughtCSPackage(ccz, c.getAccountID()));

        } else if (action == 35 || action == 99) { //99 buy with mesos
            final CashItemInfo item = CashItemFactory.getInstance().getItem(inPacket.readInt());
            if (item == null || !MapleItemInformationProvider.getInstance().isQuestItem(item.getId())) {
                c.sendPacket(CSPacket.sendCSFail(0));
                doCSPackets(c);
                return;
            } else if (c.getCharacter().getMeso() < item.getPrice()) {
                c.sendPacket(CSPacket.sendCSFail(0xB8));
                doCSPackets(c);
                return;
            } else if (c.getCharacter().getInventory(GameConstants.getInventoryType(item.getId())).getNextFreeSlot() < 0) {
                c.sendPacket(CSPacket.sendCSFail(0xB1));
                doCSPackets(c);
                return;
            }
            byte pos = MapleInventoryManipulator.addId(c, item.getId(), (short) item.getCount(), null, "Cash shop: quest item" + " on " + FileoutputUtil.CurrentReadable_Date());
            if (pos < 0) {
                c.sendPacket(CSPacket.sendCSFail(0xB1));
                doCSPackets(c);
                return;
            }
            chr.gainMeso(-item.getPrice(), false);
            c.sendPacket(CSPacket.showBoughtCSQuestItem(item.getPrice(), (short) item.getCount(), pos, item.getId()));
        } else if (action == 48) {
            c.sendPacket(CSPacket.updatePurchaseRecord());
        }  else if (action == 58) {
            System.out.println("action 58");
        } else if (action == 91) { // Open random box.
            final int uniqueid = (int) inPacket.readLong();

            c.sendPacket(CSPacket.sendRandomBox(uniqueid, new Item(1302000, (short) 1, (short) 1, (short) 0, 10), (short) 0));
            //} else if (action == 99) { //buy with mesos
            //    int sn = inPacket.readInt();
            //    int price = inPacket.readInt();
        } else {
            System.out.println("New Action: " + action + " Remaining: " + inPacket.toString());
            c.sendPacket(CSPacket.sendCSFail(0));
        }
        doCSPackets(c);
    }

    public static void SwitchCategory(final MaplePacketReader inPacket, final MapleClient c) {
        int Scategory = inPacket.readByte();
//        System.out.println("Scategory " + Scategory);
        if (Scategory == 103) {
            inPacket.skip(1);
            int itemSn = inPacket.readInt();
            try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement("INSERT INTO `wishlist` VALUES (?, ?)")) {
                ps.setInt(1, c.getCharacter().getID());
                ps.setInt(2, itemSn);
                ps.executeUpdate();
                ps.close();
            } catch (SQLException ex) {
                System.out.println("error");
            }
            c.sendPacket(CSPacket.addFavorite(false, itemSn));
        } else if (Scategory == 105) {
            int item = inPacket.readInt();
            try {
                Connection con = DatabaseConnection.getConnection();
                try (PreparedStatement ps = con.prepareStatement("UPDATE cashshop_items SET likes = likes+" + 1 + " WHERE sn = ?")) {
                    ps.setInt(1, item);
                    ps.executeUpdate();
                }
            } catch (SQLException ex) {
            }
            c.sendPacket(CSPacket.Like(item));
        } else if (Scategory == 109) {
            c.sendPacket(CSPacket.Favorite(c.getCharacter()));
        } else if (Scategory == 112) {//click on special item TODO
            //int C8 - C9 - CA
        } else if (Scategory == 113) {//buy from cart inventory TODO
            //byte buy = 1 or gift = 0
            //byte amount
            //for each SN
        } else {
            int category = inPacket.readInt();
            if (category == 4000000) {
                c.sendPacket(CSPacket.CS_Top_Items());
                c.sendPacket(CSPacket.CS_Picture_Item());
            } else if (category == 1060100) {
//                c.sendPacket(CSPacket.showNXChar(category));
                c.sendPacket(CSPacket.changeCategory(category));
            } else {
//                System.err.println(category);
                c.sendPacket(CSPacket.changeCategory(category));
            }
        }
    }

    private static MapleInventoryType getInventoryType(final int id) {
        switch (id) {
            case 50200093:
                return MapleInventoryType.EQUIP;
            case 50200094:
                return MapleInventoryType.USE;
            case 50200197:
                return MapleInventoryType.SETUP;
            case 50200095:
                return MapleInventoryType.ETC;
            default:
                return MapleInventoryType.UNDEFINED;
        }
    }

    public static void doCSPackets(MapleClient c) {
        c.sendPacket(CSPacket.getCSInventory(c));
        c.sendPacket(CSPacket.doCSMagic());
        c.sendPacket(CSPacket.getCSGifts(c));
        c.sendPacket(CWvsContext.BuddylistPacket.updateBuddylist(c.getCharacter().getBuddylist().getBuddies()));
        c.sendPacket(CSPacket.showNXMapleTokens(c.getCharacter()));
//        c.sendPacket(CSPacket.sendWishList(c.getPlayer(), false));
        c.sendPacket(CSPacket.showNXMapleTokens(c.getCharacter()));
        c.sendPacket(CSPacket.enableCSUse());
         c.getCharacter().getCashInventory().checkExpire(c);
    }
}
