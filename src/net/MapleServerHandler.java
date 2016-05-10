/*
 This file is part of the OdinMS Maple Story Server
 Copyright (C) 2008 ~ 2012 Patrick Huy <patrick.huy@frz.cc> 
 Matthias Butz <matze@odinms.de>
 Jan Christian Meyer <vimes@odinms.de>

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU Affero General Public License version 3
 as published by the Free Software Foundation. You may not use, modify
 or distribute this program under any other version of the
 GNU Affero General Public License.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU Affero General Public License for more details.

 You should have received a copy of the GNU Affero General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net;

import client.MapleCharacter;
import client.MapleClient;
import client.inventory.Item;
import client.inventory.MaplePet;
import client.inventory.PetDataFactory;
import constants.ServerConfig;
import constants.ServerConstants;
import net.cashshop.handler.*;
import net.channel.handler.*;
import net.login.LoginServer;
import net.login.handler.*;
import net.mina.MaplePacketDecoder;
import net.server.channel.handlers.monster.MobHelper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;
import org.apache.mina.common.IdleStatus;
import org.apache.mina.common.IoHandlerAdapter;
import org.apache.mina.common.IoSession;
import scripting.NPCScriptManager;
import server.CashItemFactory;
import server.CashItemInfo;
import server.Randomizer;
import server.commands.CommandProcessor;
import server.shark.SharkPacket;
import tools.FilePrinter;
import tools.FileoutputUtil;
import tools.HexTool;
import tools.MapleAESOFB;
import tools.Pair;
import tools.data.ByteArrayByteStream;
import tools.data.LittleEndianAccessor;
import tools.packet.CField;
import tools.packet.LoginPacket;
import tools.packet.CSPacket;

public class MapleServerHandler extends IoHandlerAdapter implements MapleServerHandlerMBean {

	private PacketProcessor processor;
	private int world = -1, channel = -1;
    public static boolean Log_Packets = false;
    private static int numDC = 0;
    private static long lastDC = System.currentTimeMillis();
    private final List<String> BlockedIP = new ArrayList<>();
    private final Map<String, Pair<Long, Byte>> tracker = new ConcurrentHashMap<>();
    //Screw locking. Doesn't matter.
    //private static final ReentrantReadWriteLock IPLoggingLock = new ReentrantReadWriteLock();
    private static final String nl = System.getProperty("line.separator");
    private static final HashMap<String, FileWriter> logIPMap = new HashMap<>();
    private static String client_username;
    private static String client_password;
    //Note to Zero: Use an enumset. Don't iterate through an array.
    private static final EnumSet<RecvPacketOpcode> blocked = EnumSet.noneOf(RecvPacketOpcode.class), sBlocked = EnumSet.noneOf(RecvPacketOpcode.class);

    public MapleServerHandler() {
        this.processor = PacketProcessor.getProcessor(-1, -1);
    }
    
    public MapleServerHandler(int world, int channel) {
    	this.processor = PacketProcessor.getProcessor(world, channel);
    	this.world = world;
    	this.channel = channel;
    }
    
    
    public static void reloadLoggedIPs() {
        //IPLoggingLock.writeLock().lock();
        //try {
        for (FileWriter fw : logIPMap.values()) {
            if (fw != null) {
                try {
                    fw.write("=== Closing Log ===");
                    fw.write(nl);
                    fw.flush(); //Just in case.
                    fw.close();
                } catch (IOException ex) {
                    System.out.println("Error closing Packet Log.");
                    System.out.println(ex);
                }
            }
        }
        logIPMap.clear();
        try {
            File logips = new File("LogIPs.txt");
            if (logips.exists()) {
                Scanner sc = new Scanner(logips);
                while (sc.hasNextLine()) {
                    String line = sc.nextLine().trim();
                    if (line.length() > 0) {
                        addIP(line);
                    }
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Could not reload packet logged IPs.");
            System.out.println(e);
        }
        //} finally {
        //    IPLoggingLock.writeLock().unlock();
        //}
    }
    //Return the Filewriter if the IP is logged. Null otherwise.

    private static FileWriter isLoggedIP(IoSession sess) {
        String a = sess.getRemoteAddress().toString();
        String realIP = a.substring(a.indexOf('/') + 1, a.indexOf(':'));
        return logIPMap.get(realIP);
    }

    public static void addIP(String theIP) {
        try {
            FileWriter fw = new FileWriter(new File("PacketLog_" + theIP + ".txt"), true);
            fw.write("=== Creating Log ===");
            fw.write(nl);
            fw.flush();
            logIPMap.put(theIP, fw);
        } catch (IOException e) {
            FileoutputUtil.outputFileError(FileoutputUtil.PacketEx_Log, e);
        }

    }
// <editor-fold defaultstate="collapsed" desc="Packet Log Implementation">
    private static final int Log_Size = 10000, Packet_Log_Size = 25;
    private static final ArrayList<LoggedPacket> Packet_Log = new ArrayList<>(Log_Size);
    private static final ReentrantReadWriteLock Packet_Log_Lock = new ReentrantReadWriteLock();
    private static final String Packet_Log_Output = "Packet/PacketLog";
    private static int Packet_Log_Index = 0;

    public static void log(String packet, String op, MapleClient c, IoSession io) {
        try {
            Packet_Log_Lock.writeLock().lock();
            LoggedPacket logged = null;
            if (Packet_Log.size() == Log_Size) {
                logged = Packet_Log.remove(0);
            }
            //This way, we don't create new LoggedPacket objects, we reuse them =]
            if (logged == null) {
                logged = new LoggedPacket(packet, op, io.getRemoteAddress().toString(),
                        c == null ? -1 : c.getAccID(), FileoutputUtil.CurrentReadable_Time(),
                        c == null || c.getAccountName() == null ? "[Null]" : c.getAccountName(),
                        c == null || c.getPlayer() == null || c.getPlayer().getName() == null ? "[Null]" : c.getPlayer().getName(),
                        c == null || c.getPlayer() == null || c.getPlayer().getMap() == null ? "[Null]" : String.valueOf(c.getPlayer().getMapId()),
                        c == null || NPCScriptManager.getInstance().getCM(c) == null ? "[Null]" : String.valueOf(NPCScriptManager.getInstance().getCM(c).getNpc()));
            } else {
                logged.setInfo(packet, op, io.getRemoteAddress().toString(),
                        c == null ? -1 : c.getAccID(), FileoutputUtil.CurrentReadable_Time(),
                        c == null || c.getAccountName() == null ? "[Null]" : c.getAccountName(),
                        c == null || c.getPlayer() == null || c.getPlayer().getName() == null ? "[Null]" : c.getPlayer().getName(),
                        c == null || c.getPlayer() == null || c.getPlayer().getMap() == null ? "[Null]" : String.valueOf(c.getPlayer().getMapId()),
                        c == null || NPCScriptManager.getInstance().getCM(c) == null ? "[Null]" : String.valueOf(NPCScriptManager.getInstance().getCM(c).getNpc()));
            }
            Packet_Log.add(logged);
        } finally {
            Packet_Log_Lock.writeLock().unlock();
        }
    }

    private static class LoggedPacket {

        private static final String nl = System.getProperty("line.separator");
        private String ip, accName, accId, chrName, packet, mapId, npcId, op, time;
        private long timestamp;

        public LoggedPacket(String p, String op, String ip, int id, String time, String accName, String chrName, String mapId, String npcId) {
            setInfo(p, op, ip, id, time, accName, chrName, mapId, npcId);
        }

        public final void setInfo(String p, String op, String ip, int id, String time, String accName, String chrName, String mapId, String npcId) {
            this.ip = ip;
            this.op = op;
            this.time = time;
            this.packet = p;
            this.accName = accName;
            this.chrName = chrName;
            this.mapId = mapId;
            this.npcId = npcId;
            timestamp = System.currentTimeMillis();
            this.accId = String.valueOf(id);
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("[IP: ").append(ip).append("] [").append(accId).append('|').append(accName).append('|').append(chrName).append("] [").append(npcId).append('|').append(mapId).append("] [Time: ").append(timestamp).append("] [").append(time).append(']');
            sb.append(nl);
            sb.append("[Op: ").append(op).append("] [").append(packet).append(']');
            return sb.toString();
        }
    }

    public static void registerMBean() {
        MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
        try {
            MapleServerHandler mbean = new MapleServerHandler();
            //The log is a static object, so we can just use this hacky method.
            mBeanServer.registerMBean(mbean, new ObjectName("handling:type=MapleServerHandler"));
        } catch (MalformedObjectNameException | InstanceAlreadyExistsException | MBeanRegistrationException | NotCompliantMBeanException e) {
            System.out.println("Error registering PacketLog MBean");
        }
    }

    @Override
    public void writeLog() {
        writeLog(false);
    }

    public void writeLog(boolean crash) {
        Packet_Log_Lock.readLock().lock();
        try {
            try (FileWriter fw = new FileWriter(new File(Packet_Log_Output + Packet_Log_Index + (crash ? "_DC.txt" : ".txt")), true)) {
                String newline = System.getProperty("line.separator");
                for (LoggedPacket loggedPacket : Packet_Log) {
                    fw.write(loggedPacket.toString());
                    fw.write(newline);
                }
                final String logString = "Log has been written at " + lastDC + " [" + FileoutputUtil.CurrentReadable_Time() + "] - " + numDC + " have disconnected, within " + (System.currentTimeMillis() - lastDC) + " milliseconds. (" + System.currentTimeMillis() + ")";
                System.out.println(logString);
                fw.write(logString);
                fw.write(newline);
                fw.flush();
            }
            Packet_Log.clear();
            Packet_Log_Index++;
            if (Packet_Log_Index > Packet_Log_Size) {
                Packet_Log_Index = 0;
                Log_Packets = false;
            }
        } catch (IOException ex) {
            System.out.println("Error writing log to file.");
        } finally {
            Packet_Log_Lock.readLock().unlock();
        }

    }

    public static void initiate() {
        reloadLoggedIPs();
        RecvPacketOpcode[] block = new RecvPacketOpcode[]{/*RecvPacketOpcode.NPC_ACTION,*/ RecvPacketOpcode.MOVE_PLAYER, RecvPacketOpcode.PONG, RecvPacketOpcode.MOVE_PET, RecvPacketOpcode.MOVE_SUMMON, RecvPacketOpcode.MOVE_DRAGON, RecvPacketOpcode.MOVE_LIFE, RecvPacketOpcode.MOVE_ANDROID, RecvPacketOpcode.HEAL_OVER_TIME, RecvPacketOpcode.STRANGE_DATA, RecvPacketOpcode.AUTO_AGGRO, RecvPacketOpcode.CANCEL_DEBUFF, RecvPacketOpcode.MOVE_FAMILIAR};
        RecvPacketOpcode[] serverBlock = new RecvPacketOpcode[]{RecvPacketOpcode.CHANGE_KEYMAP, RecvPacketOpcode.ITEM_PICKUP, RecvPacketOpcode.PET_LOOT, RecvPacketOpcode.TAKE_DAMAGE, RecvPacketOpcode.FACE_EXPRESSION, RecvPacketOpcode.USE_ITEM, RecvPacketOpcode.CLOSE_RANGE_ATTACK, RecvPacketOpcode.MAGIC_ATTACK, RecvPacketOpcode.RANGED_ATTACK, /*RecvPacketOpcode.ARAN_COMBO, */RecvPacketOpcode.SPECIAL_MOVE, RecvPacketOpcode.GENERAL_CHAT, RecvPacketOpcode.MONSTER_BOMB, RecvPacketOpcode.PASSIVE_ENERGY, RecvPacketOpcode.PET_AUTO_POT, RecvPacketOpcode.USE_CASH_ITEM, RecvPacketOpcode.PARTYCHAT, RecvPacketOpcode.CANCEL_BUFF, RecvPacketOpcode.SKILL_EFFECT, RecvPacketOpcode.CHAR_INFO_REQUEST, RecvPacketOpcode.ALLIANCE_OPERATION, RecvPacketOpcode.AUTO_ASSIGN_AP, RecvPacketOpcode.DISTRIBUTE_AP, RecvPacketOpcode.USE_MAGNIFY_GLASS, RecvPacketOpcode.SPAWN_PET, RecvPacketOpcode.SUMMON_ATTACK, RecvPacketOpcode.ITEM_MOVE, RecvPacketOpcode.PARTY_SEARCH_STOP};
        blocked.addAll(Arrays.asList(block));
        sBlocked.addAll(Arrays.asList(serverBlock));
        if (Log_Packets) {
            for (int i = 1; i <= Packet_Log_Size; i++) {
                if (!(new File(Packet_Log_Output + i + ".txt")).exists() && !(new File(Packet_Log_Output + i + "_DC.txt")).exists()) {
                    Packet_Log_Index = i;
                    break;
                }
            }
            if (Packet_Log_Index <= 0) { //25+ files, do not log
                Log_Packets = false;
            }
        }

        registerMBean();
    }

    @Override
    public void exceptionCaught(final IoSession session, final Throwable cause) throws Exception {
        /*
         * MapleClient client = (MapleClient)
         * session.getAttribute(MapleClient.CLIENT_KEY);
         * log.error(MapleClient.getLogMessage(client, cause.getMessage()),
         * cause);
         */
        // cause.printStackTrace();
    	if (cause instanceof IOException || cause instanceof ClassCastException) {
            return;
        }
    	MapleClient c = (MapleClient) session.getAttribute(MapleClient.CLIENT_KEY);
        if (c != null && c.getPlayer() != null) {
            FilePrinter.printError(FilePrinter.EXCEPTION_CAUGHT, cause, "Exception caught by: " + c.getPlayer());
        }
    }

    @Override
    public void sessionOpened(final IoSession session) throws Exception {
        // Start of IP checking
        final String address = session.getRemoteAddress().toString().split(":")[0];

        if (BlockedIP.contains(address)) {
            session.close();
            return;
        }
        final Pair<Long, Byte> track = tracker.get(address);

        byte count;
        if (track == null) {
            count = 1;
        } else {
            count = track.right;

            final long difference = System.currentTimeMillis() - track.left;
            if (difference < 2000) { // Less than 2 sec
                count++;
            } else if (difference > 20000) { // Over 20 sec
                count = 1;
            }
            if (count >= 10) {
                BlockedIP.add(address);
                tracker.remove(address); // Cleanup
                session.close();
                return;
            }
        }
        tracker.put(address, new Pair<>(System.currentTimeMillis(), count));
        // End of IP checking.
        String IP = address.substring(address.indexOf('/') + 1, address.length());
        if (LoginServer.isShutdown()) {
            session.close();
            return;
        }
        LoginServer.removeIPAuth(IP);
        // IV used to decrypt packets from client.
        final byte ivRecv[] = new byte[]{(byte) Randomizer.nextInt(255), (byte) Randomizer.nextInt(255), (byte) Randomizer.nextInt(255), (byte) Randomizer.nextInt(255)};
        // IV used to encrypt packets for client.
        final byte ivSend[] = new byte[]{(byte) Randomizer.nextInt(255), (byte) Randomizer.nextInt(255), (byte) Randomizer.nextInt(255), (byte) Randomizer.nextInt(255)};

        final MapleClient client = new MapleClient(
                new MapleAESOFB(ServerConfig.USE_FIXED_IV ? ServerConfig.Static_LocalIV : ivSend, (short) (0xFFFF - ServerConstants.MAPLE_VERSION)), // Sent Cypher
                new MapleAESOFB(ServerConfig.USE_FIXED_IV ? ServerConfig.Static_RemoteIV : ivRecv, ServerConstants.MAPLE_VERSION), // Recv Cypher
                session);
        client.setChannel(-1);

        MaplePacketDecoder.DecoderState decoderState = new MaplePacketDecoder.DecoderState();
        session.setAttribute(MaplePacketDecoder.DECODER_STATE_KEY, decoderState);


        session.write(LoginPacket.getHello(ivSend, ivRecv));
        //System.out.println("GETHELLO SENT TO " + address);
        session.setAttribute(MapleClient.CLIENT_KEY, client);
        session.setIdleTime(IdleStatus.READER_IDLE, 60);
        session.setIdleTime(IdleStatus.WRITER_IDLE, 60);

        if (LoginServer.isAdminOnly()) {
            StringBuilder sb = new StringBuilder();
            sb.append("IoSession opened ").append(address);
            System.out.println(sb.toString());
        }

        FileWriter fw = isLoggedIP(session);
        if (fw != null) {
            client.setMonitored(true);
        }
    }

    @Override
    public void sessionClosed(final IoSession session) throws Exception {
        final MapleClient client = (MapleClient) session.getAttribute(MapleClient.CLIENT_KEY);

        if (client != null) {
            byte state = MapleClient.CHANGE_CHANNEL;
            if (Log_Packets && !LoginServer.isShutdown() && client.getPlayer() != null) {
                state = client.getLoginState();
            }
            if (state != MapleClient.CHANGE_CHANNEL) {
                log("Data: " + numDC, "CLOSED", client, session);
                if (System.currentTimeMillis() - lastDC < 60000) { //within the minute
                    numDC++;
                    if (numDC > 100) { //100+ people have dc'd in minute in channelserver
                        System.out.println("Writing log...");
                        writeLog();
                        numDC = 0;
                        lastDC = System.currentTimeMillis(); //intentionally place here
                    }
                } else {
                    numDC = 0;
                    lastDC = System.currentTimeMillis(); //intentionally place here
                }
            }
            try {
                FileWriter fw = isLoggedIP(session);
                if (fw != null) {
                    fw.write("=== Session Closed ===");
                    fw.write(nl);
                    fw.flush();
                }

                client.disconnect(true, true);
            } finally {
                session.close();
                session.removeAttribute(MapleClient.CLIENT_KEY);
            }
        }
        super.sessionClosed(session);
    }

    @Override
    public void sessionIdle(final IoSession session, final IdleStatus status) throws Exception {
        final MapleClient client = (MapleClient) session.getAttribute(MapleClient.CLIENT_KEY);

        /*
         * if (client != null && client.getPlayer() != null) {
         * System.out.println("Player "+ client.getPlayer().getName() +" went
         * idle"); }
         */
        if (client != null) {
            //client.sendPing();
        }
        super.sessionIdle(session, status);
    }

    /*
    @Override
    public void messageReceived(final IoSession session, final Object message) {
        if (message == null || session == null) {
            return;
        }
        final LittleEndianAccessor slea = new LittleEndianAccessor(new ByteArrayByteStream((byte[]) message));
        if (slea.available() < 2) {
            return;
        }
        final MapleClient c = (MapleClient) session.getAttribute(MapleClient.CLIENT_KEY);
        if (c == null || !c.isReceiving()) {
            return;
        }
        if (ServerConstants.LOG_SHARK) {
            final SharkPacket sp = new SharkPacket((byte[]) message, true);
            c.sl.log(sp);
        }
        final short header_num = slea.readShort();
        if (ServerConfig.logPackets && !isSpamHeader(RecvPacketOpcode.valueOf(RecvPacketOpcode.nameOf(header_num)))) {
            final StringBuilder sb = new StringBuilder("Received data :\n");
            sb.append(HexTool.toString((byte[]) message)).append("\n").append(HexTool.toStringFromAscii((byte[]) message));
            System.out.println(sb.toString());
        }
        for (final RecvPacketOpcode recv : RecvPacketOpcode.values()) {
            if (recv.getValue() == header_num) {
                if (recv.NeedsChecking()) {
                    if (!c.isLoggedIn()) {
                        return;
                    }
                }
                try {
                    if (c.getPlayer() != null && c.isMonitored() && !blocked.contains(recv)) {
                        try (FileWriter fw = new FileWriter(new File("MonitorLogs/" + c.getPlayer().getName() + "_log.txt"), true)) {
                            fw.write(String.valueOf(recv) + " (" + Integer.toHexString(header_num) + ") Handled: \r\n" + slea.toString() + "\r\n");
                            fw.flush();
                        }
                    }
                    //no login packets
                    if (Log_Packets && !blocked.contains(recv) && !sBlocked.contains(recv) && c.getPlayer() != null) {
                        log(slea.toString(), recv.toString(), c, session);
                    }
                    handlePacket(recv, slea, c);
                    FileWriter fw = isLoggedIP(session);
                    if (fw != null && !blocked.contains(recv)) {
                        if (recv == RecvPacketOpcode.PLAYER_LOGGEDIN && c != null) {
                            fw.write(">> [AccountName: "
                                    + (c.getAccountName() == null ? "null" : c.getAccountName()) + "] | [IGN: "
                                    + (c.getPlayer() == null || c.getPlayer().getName() == null ? "null" : c.getPlayer().getName()) + "] | [Time: "
                                    + FileoutputUtil.CurrentReadable_Time() + "]");
                            fw.write(nl);
                        }
                        fw.write("[" + recv.toString() + "]" + slea.toString(true));
                        fw.write(nl);
                        fw.flush();
                    }
                } catch (NegativeArraySizeException | ArrayIndexOutOfBoundsException e) {
                    if (ServerConstants.Use_Localhost) {
                        FileoutputUtil.outputFileError(FileoutputUtil.PacketEx_Log, e);
                        FileoutputUtil.log(FileoutputUtil.PacketEx_Log, "Packet: " + header_num + "\n" + slea.toString(true));
                    }
                } catch (Exception e) {
                    FileoutputUtil.outputFileError(FileoutputUtil.PacketEx_Log, e);
                    FileoutputUtil.log(FileoutputUtil.PacketEx_Log, "Packet: " + header_num + "\n" + slea.toString(true));
                }

                return;
            }
        }
        final StringBuilder sb = new StringBuilder("Received data : (Unhandled)\n");
        sb.append(HexTool.toString((byte[]) message)).append("\n").append(HexTool.toStringFromAscii((byte[]) message));
        System.out.println(sb.toString());
    }
    */
    
    
    @Override
    public void messageReceived(IoSession session, Object message) {
        final LittleEndianAccessor lea = new LittleEndianAccessor(new ByteArrayByteStream((byte[]) message));
        short packetId = lea.readShort();
        MapleClient client = (MapleClient) session.getAttribute(MapleClient.CLIENT_KEY);
        
        if (ServerConstants.LOG_SHARK) {
            final SharkPacket sp = new SharkPacket((byte[]) message, true);
            client.sl.log(sp);
        }
        
        final MaplePacketHandler packetHandler = processor.getHandler(packetId);
        if (packetHandler != null && packetHandler.validateState(client)) {
        	if (ServerConfig.logPackets && !isSpamHeader(RecvPacketOpcode.valueOf(RecvPacketOpcode.getOpcodeName(packetId)))) {
        		String tab = "";
        		String recv = packetHandler.getRecvOpcode().name();
        		for (int i = 4; i > Integer.valueOf(recv.length() / 8); i--) {
                    tab += "\t";
                }
        		System.out.println("[Recv]\t" + recv + tab + "|     " + HexTool.getOpcodeToString(packetId) + "\t|\t" + lea.toString());
                System.out.println(HexTool.toStringFromAscii((byte[]) message)); 
        	}
            try {
                packetHandler.handlePacket(lea, client, client.getPlayer());
            } catch (final Throwable t) {
            	System.err.println("[Error] An error occured when trying to handle the packet " + packetId);
                FilePrinter.printError(FilePrinter.PACKET_HANDLER + packetHandler.getClass().getName() + ".txt", t, "Error for " + (client.getPlayer() == null ? "" : "player ; " + client.getPlayer() + " on map ; " + client.getPlayer().getMapId() + " - ") + "account ; " + client.getAccountName() + "\r\n" + lea.toString());
                //client.announce(MaplePacketCreator.enableActions());//bugs sometimes
            }
        } else if(ServerConfig.logPackets && !isSpamHeader(RecvPacketOpcode.valueOf(RecvPacketOpcode.getOpcodeName(packetId)))){
        	String tab = "";
    		String recv = "UNKNOWN";
    		for (int i = 4; i > Integer.valueOf(recv.length() / 8); i--) {
                tab += "\t";
            }

        	System.out.println("[Recv]\t" + recv + tab + "|     " + HexTool.getOpcodeToString(packetId) + "\t|\t" + lea.toString());
            System.out.println(HexTool.toStringFromAscii((byte[]) message)); 
        }
    }
    

    public static void handlePacket(final RecvPacketOpcode header, final LittleEndianAccessor slea, final MapleClient c) throws Exception {
        if (ServerConfig.logPackets && !isSpamHeader(header)) {
            String tab = "";
            for (int i = 4; i > Integer.valueOf(header.name().length() / 8); i--) {
                tab += "\t";
            }
            System.out.println("[Recv]\t" + header.name() + tab + "|\t" + header.getValue() + "\t|\t" + HexTool.getOpcodeToString(header.getValue()));
            FileoutputUtil.log("PacketLog.txt", "\r\n\r\n[Recv]\t" + header.name() + tab + "|\t" + header.getValue() + "\t|\t" + HexTool.getOpcodeToString(header.getValue()) + "\r\n\r\n");
        }
        switch (header) {
        /*    case LOGIN_REDIRECTOR:
                System.out.println("Redirector login received");
                if (!ServerConstants.Redirector) {
                    System.out.println("Redirector login packet recieved, but server is not set to redirector. Please change it in ServerConstants!");
                } else {
                    CharLoginHandler.redirectorLogin(slea, c);
                }*/
        	case LOGIN_REDIRECTOR:
                    client_username = slea.readMapleAsciiString();
                    client_password = slea.readMapleAsciiString();
                    c.loginData(client_username);
                  //  client_password = slea.readMapleAsciiString();
                    System.out.println(client_username);
                    c.getSession().write(LoginPacket.getAuthSuccessRequest(c));
                    break; //<-- Unsecure Hawt login
            /*
            case CLIENT_START:
            	
                if (c.getSessionIPAddress().contains("8.31.99.141") || c.getSessionIPAddress().contains("127.0.0.1")) {
                    c.loginData("admin");
                    c.getSession().write(LoginPacket.getAuthSuccessRequest(c));
                }  if (c.getSessionIPAddress().contains("192.168.2.27")) {
                    c.loginData("admin2");
                    c.getSession().write(LoginPacket.getAuthSuccessRequest(c));
                }
                
                break; 
                */
            case CLIENT_FAILED:
                //c.getSession().write(LoginPacket.getCustomEncryption());
                break;
            case VIEW_SERVERLIST:
                if (slea.readByte() == 0) {
                    //CharLoginHandler.ServerListRequest(c);
                }
                break;
            case CHAR_SELECT_NO_PIC:
                CharLoginHandler.Character_WithoutSecondPassword(slea, c, false, false);
                break;
            case VIEW_REGISTER_PIC:
                CharLoginHandler.Character_WithoutSecondPassword(slea, c, true, true);
                break;
            case CHAR_SELECT:
                //CharLoginHandler.Character_WithoutSecondPassword(slea, c, true, false);
            	//CharLoginHandler.Character_WithSecondPassword(slea, c, false);
            	CharLoginHandler.Character_WithSecondPassword(slea, c, true);
                break;
            case VIEW_SELECT_PIC:
                break;
            case AUTH_SECOND_PASSWORD:
                CharLoginHandler.Character_WithSecondPassword(slea, c, false);
                break;
            case CLIENT_ERROR:
                //System.out.println("Client error: " + slea.toString());
                if (slea.available() < 8) {
                    System.out.println(slea.toString());
                    return;
                }
                short type = slea.readShort();
                String type_str = "Unknown?!";
                if (type == 0x01) {
                    type_str = "SendBackupPacket";
                } else if (type == 0x02) {
                    type_str = "Crash Report";
                } else if (type == 0x03) {
                    type_str = "Exception";
                }
                int errortype = slea.readInt(); // example error 38
                //if (errortype == 0) { // i don't wanna log error code 0 stuffs, (usually some bounceback to login)
                //    return;
                //}
                short data_length = slea.readShort();
                slea.skip(4); // ?B3 86 01 00 00 00 FF 00 00 00 00 00 9E 05 C8 FF 02 00 CD 05 C9 FF 7D 00 00 00 3F 00 00 00 00 00 02 77 01 00 25 06 C9 FF 7D 00 00 00 40 00 00 00 00 00 02 C1 02
                short opcodeheader = slea.readShort();
//                System.out.println("[CLIENT ERROR] Error Code " + errortype + " Type " + type_str + "\r\n\t[Data] Length " + data_length + " [" + SendPacketOpcode.getOpcodeName(opcodeheader) + " | " + opcodeheader + "]\r\n" + HexTool.toString(slea.read((int) slea.available())));
                FileoutputUtil.log("ErrorCodes.txt", "Error Type: " + errortype
                        + "\r\n" + "Data Length: " + data_length + "\r\n"
                        + "Error for player ; " + c.getPlayer().getName()
                        + " - account ; " + c.getAccountName() + "\r\n"
                        + SendPacketOpcode.getOpcodeName(opcodeheader)
                        + " Opcode: " + opcodeheader + "\r\n"
                        + HexTool.toString(slea.read((int) slea.available()))
                        + "\r\n MapID: " + c.getPlayer().getMapId() + "\r\n\r\n");
                break;
            case ENABLE_SPECIAL_CREATION:
                c.getSession().write(LoginPacket.enableSpecialCreation(c.getAccID(), true));
                break;
            case RSA_KEY: // Fix this somehow
                //c.getSession().write(LoginPacket.getLoginAUTH());
                // c.getSession().write(LoginPacket.StrangeDATA());
                break;
            // END OF LOGIN SERVER;

            case PROFESSION_INFO:
                ItemMakerHandler.ProfessionInfo(slea, c);
                break;
            case CRAFT_DONE:
                ItemMakerHandler.CraftComplete(slea, c, c.getPlayer());
                break;
            case CRAFT_MAKE:
                ItemMakerHandler.CraftMake(slea, c, c.getPlayer());
                break;
            case CRAFT_EFFECT:
                ItemMakerHandler.CraftEffect(slea, c, c.getPlayer());
                break;
            case START_HARVEST:
                ItemMakerHandler.StartHarvest(slea, c, c.getPlayer());
                break;
            case STOP_HARVEST:
                ItemMakerHandler.StopHarvest(slea, c, c.getPlayer());
                break;
            case MAKE_EXTRACTOR:
                ItemMakerHandler.MakeExtractor(slea, c, c.getPlayer());
                break;
            case USE_TITLE:
                PlayerHandler.UseTitle(slea.readInt(), c, c.getPlayer());
                break;
            case ANGELIC_CHANGE:
                PlayerHandler.AngelicChange(slea, c, c.getPlayer());
                break;
            case DRESSUP_TIME:
                PlayerHandler.DressUpTime(slea, c, c.getPlayer());
                break;


            case SKILL_EFFECT:
                PlayerHandler.SkillEffect(slea, c.getPlayer());
                break;
            case QUICK_SLOT:
                PlayerHandler.QuickSlot(slea, c.getPlayer());
                break;

            case UPDATE_ENV:
                // We handle this in MapleMap
                break;


            case LIE_DETECTOR:
            case LIE_DETECTOR_SKILL:
                //PlayersHandler.LieDetector(slea, c, c.getPlayer(), header == RecvPacketOpcode.LIE_DETECTOR);
                break;
            case LIE_DETECTOR_RESPONSE:
                //PlayersHandler.LieDetectorResponse(slea, c);
                break;
            case ARAN_COMBO:
                PlayerHandler.AranCombo(c, c.getPlayer(), 1);
               // CField.updateCombo(+1);
                break;
            case REMOVE_ARAN_COMBO:
                PlayerHandler.RemoveAranCombo(c.getPlayer());
                break;
            case TRANSFORM_PLAYER:
                PlayersHandler.TransformPlayer(slea, c, c.getPlayer());
                break;
            case NOTE_ACTION:
                PlayersHandler.Note(slea, c.getPlayer());
                break;
            case MOVE_BAG:
                InventoryHandler.MoveBag(slea, c);
                break;
            case SWITCH_BAG:
                InventoryHandler.SwitchBag(slea, c);
                break;
            case ITEM_MAKER:
                ItemMakerHandler.ItemMaker(slea, c);
                break;
            case HOLLY:
                PlayersHandler.HOLLY(c, slea);
                break;
            case USE_ALIEN_SOCKET_RESPONSE:
                slea.skip(4); // all 0
                c.getSession().write(CSPacket.useAlienSocket(false));
                break;
            case GOLDEN_HAMMER:
                InventoryHandler.UseGoldenHammer(slea, c);
                break;
            case VICIOUS_HAMMER:
                slea.skip(4); // 3F 00 00 00
                slea.skip(4); // all 0
                c.getSession().write(CSPacket.ViciousHammer(false, 0));
                break;

            case REWARD_ITEM:
                InventoryHandler.UseRewardItem(slea, c, c.getPlayer());
                break;
            case SOLOMON_EXP:
                InventoryHandler.UseExpItem(slea, c, c.getPlayer());
                break;

            case TOT_GUIDE:
                break;

            case ADMIN_COMMAND:
                PlayerHandler.AdminCommand(slea, c, c.getPlayer());
                break;
            case ADMIN_LOG:
                CommandProcessor.logCommandToDB(c.getPlayer(), slea.readMapleAsciiString(), "adminlog");
                break;
            case QUICK_MOVE:
                NPCHandler.OpenQuickMove(slea, c);
                break;
            case BBS_OPERATION:
                BBSHandler.BBSOperation(slea, c);
                break;

            case CYGNUS_SUMMON:
                UserInterfaceHandler.CygnusSummon_NPCRequest(c);
                break;
            case SHIP_OBJECT:
                UserInterfaceHandler.ShipObjectRequest(slea.readInt(), c);
                break;
            case BUY_CS_ITEM:
                CashShopOperation.BuyCashItem(slea, c, c.getPlayer());
                break;
            case COUPON_CODE:
                slea.skip(2);
                String code = slea.readMapleAsciiString();
                CashShopOperation.CouponCode(code, c);
//                CashShopOperation.doCSPackets(c);
                break;
            case CASH_CATEGORY:
                CashShopOperation.SwitchCategory(slea, c);
                break;
            case TWIN_DRAGON_EGG:
                System.out.println("TWIN_DRAGON_EGG: " + slea.toString());
                final CashItemInfo item = CashItemFactory.getInstance().getItem(10003055);
                Item itemz = c.getPlayer().getCashInventory().toItem(item);
                //Aristocat c.getSession().write(CSPacket.sendTwinDragonEgg(true, true, 38, itemz, 1));
                break;
            case XMAS_SURPRISE:
                System.out.println("XMAS_SURPRISE: " + slea.toString());
                break;
            case CS_SURPRISE:
                System.out.println("XMAS_SURPRISE: " + slea.toString());
                break;
            case CS_UPDATE:
                CashShopOperation.CSUpdate(c);
                break;
            case USE_POT:
                ItemMakerHandler.UsePot(slea, c);
                break;
            case CLEAR_POT:
                ItemMakerHandler.ClearPot(slea, c);
                break;
            case FEED_POT:
                ItemMakerHandler.FeedPot(slea, c);
                break;
            case CURE_POT:
                ItemMakerHandler.CurePot(slea, c);
                break;
            case REWARD_POT:
                ItemMakerHandler.RewardPot(slea, c);
                break;
            case MONSTER_CARNIVAL:
                MonsterCarnivalHandler.MonsterCarnival(slea, c);
                break;
            case PACKAGE_OPERATION:
                PackageHandler.handleAction(slea, c);
                break;
            case USE_HIRED_MERCHANT:
                HiredMerchantHandler.UseHiredMerchant(c, true);
                break;
            case MERCH_ITEM_STORE:
                HiredMerchantHandler.MerchantItemStore(slea, c);
                break;

            //case MAPLETV:
            //    break;
            case LEFT_KNOCK_BACK:
                PlayerHandler.leftKnockBack(slea, c);
                break;
            case SNOWBALL:
                PlayerHandler.snowBall(slea, c);
                break;
            case COCONUT:
                PlayersHandler.hitCoconut(slea, c);
                break;
            case START_EVOLUTION:
                PlayersHandler.startEvo(slea, c.getPlayer(), c);
                break;
            case ZERO_TAG:
                MapleCharacter.ZeroTag(slea, c);
            case REPAIR:
                NPCHandler.repair(slea, c);
                break;
            case REPAIR_ALL:
                NPCHandler.repairAll(c);
                break;
            case BUY_SILENT_CRUSADE:
                PlayersHandler.buySilentCrusade(slea, c);
                break;
            //case GAME_POLL:
            //    UserInterfaceHandler.InGame_Poll(slea, c);
            //    break;
            case OWL:
                InventoryHandler.Owl(slea, c);
                break;
            case OWL_WARP:
                InventoryHandler.OwlWarp(slea, c);
                break;
            case USE_OWL_MINERVA:
                InventoryHandler.OwlMinerva(slea, c);
                break;
            case RPS_GAME:
                NPCHandler.RPSGame(slea, c);
                break;
            case UPDATE_QUEST:
                NPCHandler.UpdateQuest(slea, c);
                break;
            case USE_ITEM_QUEST:
                NPCHandler.UseItemQuest(slea, c);
                break;
            case FOLLOW_REQUEST:
                System.out.println("Follow_request_Bij");
                PlayersHandler.FollowRequest(slea, c);
                break;
            case AUTO_FOLLOW_REPLY:
            case FOLLOW_REPLY:
                System.out.println("Follow_Reply_Bij");
                PlayersHandler.FollowReply(slea, c);
                break;
            case RING_ACTION:
                PlayersHandler.RingAction(slea, c);
                break;
            case REQUEST_FAMILY:
                FamilyHandler.RequestFamily(slea, c);
                break;
            case OPEN_FAMILY:
                FamilyHandler.OpenFamily(slea, c);
                break;
            case FAMILY_OPERATION:
                FamilyHandler.FamilyOperation(slea, c);
                break;
            case DELETE_JUNIOR:
                FamilyHandler.DeleteJunior(slea, c);
                break;
            case DELETE_SENIOR:
                FamilyHandler.DeleteSenior(slea, c);
                break;
            case USE_FAMILY:
                FamilyHandler.UseFamily(slea, c);
                break;
            case FAMILY_PRECEPT:
                FamilyHandler.FamilyPrecept(slea, c);
                break;
            case FAMILY_SUMMON:
                FamilyHandler.FamilySummon(slea, c);
                break;
            case ACCEPT_FAMILY:
                FamilyHandler.AcceptFamily(slea, c);
                break;
            case SOLOMON:
                PlayersHandler.Solomon(slea, c);
                break;
            case GACH_EXP:
                PlayersHandler.GachExp(slea, c);
                break;
            case PARTY_SEARCH_START:
                PartyHandler.MemberSearch(slea, c);
                break;
            case PARTY_SEARCH_STOP:
                PartyHandler.PartySearch(slea, c);
                break;
            case EXPEDITION_LISTING:
                PartyHandler.PartyListing(slea, c);
                break;
            case EXPEDITION_OPERATION:
                PartyHandler.Expedition(slea, c);
                break;
            case USE_TELE_ROCK:
                InventoryHandler.TeleRock(slea, c);
                break;
            case AZWAN_REVIVE:
                PlayersHandler.reviveAzwan(slea, c);
                break;
            case INNER_CIRCULATOR:
                InventoryHandler.useInnerCirculator(slea, c);
                break;
            case PAM_SONG:
                InventoryHandler.PamSong(slea, c);
                break;
            case REPORT:
                PlayersHandler.Report(slea, c);
                break;
            //working
            case CANCEL_OUT_SWIPE:
                slea.readInt();
                break;
            //working
            case VIEW_SKILLS:
                PlayersHandler.viewSkills(slea, c);
                break;
            //working
            case SKILL_SWIPE:
                PlayersHandler.StealSkill(slea, c);
                break;
            case CHOOSE_SKILL:
                PlayersHandler.ChooseSkill(slea, c);
                break;
            case MAGIC_WHEEL:
                System.out.println("[MAGIC_WHEEL] [" + slea.toString() + "]");
                PlayersHandler.magicWheel(slea, c);
                break;
            case CASSANDRAS_COLLECTION:
                PlayersHandler.CassandrasCollection(slea, c);
                break;
            case REWARD:
                PlayersHandler.onReward(slea, c);
                break;
            case BLACK_FRIDAY:
                PlayersHandler.blackFriday(slea, c);
            case UPDATE_RED_LEAF:
                PlayersHandler.updateRedLeafHigh(slea, c);
                break;
            case DF_COMBO:
                PlayerHandler.absorbingDF(slea, c);
                break;
            case MESSENGER_RANKING:
                PlayerHandler.MessengerRanking(slea, c, c.getPlayer());
                break;
            case OS_INFORMATION:
                System.out.println(c.getSessionIPAddress());
                break;
//            case BUFF_RESPONSE://wat does it do?
//                break;
            case BUTTON_PRESSED:
                break;
            default:
                System.out.println("[UNHANDLED] Recv [" + header.toString() + "] found");
                break;
        }
    }

    public static boolean isSpamHeader(RecvPacketOpcode header) {
        switch (header) {
            case AUTH_REQUEST:
            case MOVE_LIFE:
            case MOVE_PLAYER:
            case SPECIAL_MOVE:
            case MOVE_ANDROID:
            case MOVE_DRAGON:
            case MOVE_SUMMON:
            case MOVE_FAMILIAR:
            case MOVE_PET:
            case QUEST_ACTION:
            case HEAL_OVER_TIME:
            case CHANGE_KEYMAP:
            case USE_INNER_PORTAL:
            case MOVE_HAKU:
            //case TAKE_DAMAGE:
            case FRIENDLY_DAMAGE:
           // case CLOSE_RANGE_ATTACK: //todo code zero
          //  case RANGED_ATTACK: //todo code zero
            case ARAN_COMBO:
            case SPECIAL_STAT:
            case DISTRIBUTE_HYPER:
            case RESET_HYPER: 
            case NPC_ACTION:
            case ANGELIC_CHANGE: 
          //  case QUEST_ACTION
//            case DRESSUP_TIME:
            case BUTTON_PRESSED: 
                return true;
        }
        return false;
    }
    
}