/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package custom;

import client.MapleClient;
import client.character.MapleCharacter;
import client.inventory.Equip;
import client.inventory.Item;
import client.inventory.MapleInventoryType;
import constants.GameConstants;
import net.netty.MaplePacketReader;
import net.packet.CWvsContext;
import net.packet.LoginPacket;
import server.MapleItemInformationProvider;

/**
 *
 * @author Itzik
 */
public class CustomPacketHandler {

    public static void handle(MaplePacketReader inPacket, MapleClient c) {
        if (inPacket.available() < 2) {
            c.sendPacket(CWvsContext.broadcastMsg(1, "Exception: Length is too short. Excepted: 4 Bytes.\r\nUse action 0x00 for help.\r\nPacket Structure:\r\n[(Short)Packet Header]\r\n[(Short)Action]"));
            dispose(c);
            return;
        }
        int action = inPacket.readShort();
        String charname;
        byte gm;
        MapleCharacter victim;
        switch (action) {
            case 0x00:
                StringBuilder sb = new StringBuilder();
                sb.append("Actions List:");
                sb.append("\r\n0x00: View Actions");
                sb.append("\r\n0x01: Unban");
                sb.append("\r\n0x02: Ban");
                sb.append("\r\n0x04: GM Powers");
                sb.append("\r\n0x08: GM Person");
                sb.append("\r\n0x10: Drop Item");
                sb.append("\r\n0x20: Level Up");
                c.sendPacket(CWvsContext.broadcastMsg(1, sb.toString()));
                dispose(c);
                break;
            case 0x01:
                if (inPacket.available() < 2) {
                    c.sendPacket(CWvsContext.broadcastMsg(1, "Exception: Length is too short. Excepted: 6 Bytes.\r\nUse action 0x00 for help.\r\nPacket Structure:\r\n[(Short)Packet Header]\r\n[(Short)Action]\r\n[(Short)String Length]\r\n[(String)ASCII String]"));
                    dispose(c);
                    return;
                }
                charname = inPacket.readMapleAsciiString();
                MapleClient.unban(charname);
                MapleClient.unbanIPMacs(charname);
                break;
            case 0x02:
                if (inPacket.available() < 2) {
                    c.sendPacket(CWvsContext.broadcastMsg(1, "Exception: Length is too short. Excepted: 6 Bytes.\r\nUse action 0x00 for help.\r\nPacket Structure:\r\n[(Short)Packet Header]\r\n[(Short)Action]\r\n[(Short)String Length]\r\n[(String)ASCII String]"));
                    dispose(c);
                    return;
                }
                charname = inPacket.readMapleAsciiString();
                MapleCharacter.ban(charname, "an unknown reason", false, 101, true);
                break;
            case 0x04:
                if (c.getCharacter() == null || inPacket.available() < 1) {
                    c.sendPacket(CWvsContext.broadcastMsg(1, "Exception: Length is too short. Excepted: 5 Bytes.\r\nUse action 0x00 for help.\r\nPacket Structure:\r\n[(Short)Packet Header]\r\n[(Short)Action]\r\n[(Byte)GM Level]"));
                    dispose(c);
                    return;
                }
                gm = inPacket.readByte();
                c.getCharacter().setGmLevel(gm);
                break;
            case 0x08:
                if (c.getCharacter() == null || inPacket.available() < 3) {
                    c.sendPacket(CWvsContext.broadcastMsg(1, "Exception: Length is too short. Excepted: 7 Bytes.\r\nUse action 0x00 for help.\r\nPacket Structure:\r\n[(Short)Packet Header]\r\n[(Short)Action]\r\n[(Short)String Length]\r\n[(String)ASCII String]\r\n[(Byte)GM Level]"));
                    dispose(c);
                    return;
                }
                charname = inPacket.readMapleAsciiString();
                gm = inPacket.readByte();
                victim = c.getChannelServer().getPlayerStorage().getCharacterByName(charname);
                if (victim == null) {
                    c.sendPacket(CWvsContext.broadcastMsg(1, "Exception: Character not found."));
                    dispose(c);
                    return;
                }
                victim.setGmLevel(gm);
                break;
            case 0x10:
                if (c.getCharacter() == null || inPacket.available() < 6) {
                    c.sendPacket(CWvsContext.broadcastMsg(1, "Exception: Length is too short. Excepted: 10 Bytes.\r\nUse action 0x00 for help.\r\nPacket Structure:\r\n[(Short)Packet Header]\r\n[(Short)Action]\r\n[(Int)Item ID]\r\n[(Short)Quantity]"));
                    dispose(c);
                    return;
                }
                int itemId = inPacket.readInt();
                short quantity = inPacket.readShort();
                MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
                if (!ii.itemExists(itemId)) {
                    c.getCharacter().dropMessage(5, itemId + " does not exist");
                } else {
                    Item toDrop;
                    if (GameConstants.getInventoryType(itemId) == MapleInventoryType.EQUIP) {

                        toDrop = ii.randomizeStats((Equip) ii.getEquipById(itemId));
                    } else {
                        toDrop = new client.inventory.Item(itemId, (byte) 0, (short) quantity, (byte) 0);
                    }
                    if (!c.getCharacter().isAdmin()) {
                        toDrop.setGMLog(c.getCharacter().getName() + " used !drop");
                        toDrop.setOwner(c.getCharacter().getName());
                    }
                    c.getCharacter().getMap().spawnItemDrop(c.getCharacter(), c.getCharacter(), toDrop, c.getCharacter().getPosition(), true, true);
                }
                break;
            case 0x20:
                if (c.getCharacter() == null || inPacket.available() < 2) {
                    c.sendPacket(CWvsContext.broadcastMsg(1, "Exception: Length is too short. Excepted: 6 Bytes.\r\nUse action 0x00 for help.\r\nPacket Structure:\r\n[(Short)Packet Header]\r\n[(Short)Action]\r\n(Short)Level"));
                    dispose(c);
                    return;
                }
                short toLevel = inPacket.readShort();
                if (toLevel > 255 || toLevel < 1) {
                    c.sendPacket(CWvsContext.broadcastMsg(1, "Exception: Short out of range.\r\nRange: Minimum: 1 Maximum: 255"));
                    dispose(c);
                    return;
                }
                if (c.getCharacter().getLevel() >= toLevel) {
                    c.getCharacter().setLevel(toLevel);
                } else {
                    while (c.getCharacter().getLevel() < toLevel) {
                        c.getCharacter().levelUp();
                    }
                }
                break;
            default:
                c.sendPacket(CWvsContext.broadcastMsg(1, "Invalid Action.\r\nUse action 0x00 for help."));
                dispose(c);
                return;
        }
        dispose(c);
    }

    public static void dispose(MapleClient c) {
        if (c.getCharacter() == null) {
            c.sendPacket(LoginPacket.getLoginFailed(1)); //Login Screen Dispose
        } else {
            c.sendPacket(CWvsContext.enableActions()); //In Game Dispose
        }
    }
}
