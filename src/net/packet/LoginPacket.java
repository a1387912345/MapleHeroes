package net.packet;

import client.MapleCharacter;
import client.MapleClient;
import client.PartTimeJob;
import constants.GameConstants;
import constants.JobConstants;
import constants.JobConstants.LoginJob;
import constants.ServerConfig;
import constants.ServerConstants;
import constants.WorldConstants.WorldOption;
import net.SendPacketOpcode;
import net.login.LoginServer;
import net.netty.MaplePacketWriter;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import server.Randomizer;
import tools.HexTool;
import tools.Triple;

public class LoginPacket {

    public static byte[] getHello(byte[] sendIv, byte[] recvIv) {
        MaplePacketWriter mplew = new MaplePacketWriter();
        
        mplew.writeShort(0x0F);                                        // Packet Size
        mplew.writeShort(ServerConstants.CLIENT_VERSION);              // MapleStory Version
        mplew.writeMapleAsciiString(ServerConstants.CLIENT_SUBVERSION);// MapleStory Patch Location/Subversion
        if (ServerConfig.USE_FIXED_IV) {
        	mplew.write(ServerConfig.Static_RemoteIV);
        	mplew.write(ServerConfig.Static_LocalIV);
        } else {
            mplew.write(recvIv);                                        // Local Initializing Vector 
            mplew.write(sendIv);                                        // Remote Initializing Vector
        }
        mplew.write(8);                                                 // MapleStory Locale 8 = GMS
        mplew.write(0);                                                 // Unknown

        return mplew.getPacket();
    }

    public static final byte[] getPing() {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.PING);
        return mplew.getPacket();
    }

    public static byte[] getAuthSuccessRequest(MapleClient client) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.LOGIN_STATUS);
		mplew.writeZeroBytes(6);
        mplew.writeMapleAsciiString(client.getAccountName());
        mplew.writeInt(client.getAccID());
        //mplew.writeShort(2);
        mplew.write(0);
        mplew.writeInt(0);
        mplew.writeZeroBytes(6);
        mplew.writeMapleAsciiString(client.getAccountName());
        mplew.writeZeroBytes(11);
        mplew.writeZeroBytes(6);
        mplew.write(1);
        mplew.writeInt(0);
        mplew.write(JobConstants.enableJobs ? 1 : 0); //toggle
        mplew.write(JobConstants.jobOrder); //Job Order (orders are located in wz)
        for (LoginJob j : LoginJob.values()) {
            mplew.write(j.getFlag());
            mplew.writeShort(1);
        }
    	mplew.write(1);
        mplew.writeInt(-1);
        mplew.writeShort(1);
        //mplew.writeLong(client.getSocketChannel().getCreationTime());
        mplew.writeLong(0);
        
        return mplew.getPacket();
    }

    public static final byte[] getLoginFailed(int reason) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.LOGIN_STATUS);
		mplew.write(reason);
        mplew.write(0);
        mplew.writeInt(0);

        return mplew.getPacket();
    }
    /*
     * location: UI.wz/Login.img/Notice/text
     * reasons:
     * useful:
     * 32 - server under maintenance check site for updates
     * 35 - your computer is running thirdy part programs close them and play again
     * 36 - due to high population char creation has been disabled
     * 43 - revision needed your ip is temporary blocked
     * 75-78 are cool for auto register
     
     */

    public static byte[] getPermBan(byte reason) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.LOGIN_STATUS);
		mplew.write(2);
        mplew.write(0);
        mplew.writeInt(0);
        mplew.writeShort(reason);
        mplew.write(HexTool.getByteArrayFromHexString("01 01 01 01 00"));

        return mplew.getPacket();
    }

    public static byte[] getTempBan(long timestampTill, byte reason) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.LOGIN_STATUS);
		mplew.write(2);
        mplew.write(0);
        mplew.writeInt(0);
        mplew.write(reason);
        mplew.writeLong(timestampTill);

        return mplew.getPacket();
    }
    
    public static byte[] sendPicResponse(int response) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.PIC_RESPONSE);
		mplew.write(response);
        
        System.out.println(mplew.toString());
        return mplew.getPacket();
    }

    public static final byte[] getSecondAuthSuccess(MapleClient client) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.LOGIN_SECOND);
		mplew.write(0);
        mplew.writeInt(client.getAccID());
        mplew.writeZeroBytes(5);
        mplew.writeMapleAsciiString(client.getAccountName());
        mplew.writeLong(2L);
        mplew.writeZeroBytes(3);
        mplew.writeInt(Randomizer.nextInt());
        mplew.writeInt(Randomizer.nextInt());
        mplew.writeInt(28);
        mplew.writeInt(Randomizer.nextInt());
        mplew.writeInt(Randomizer.nextInt());
        mplew.write(1);

        return mplew.getPacket();
    }

    public static final byte[] deleteCharResponse(int cid, int state) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.DELETE_CHAR_RESPONSE);
		mplew.writeInt(cid);
        mplew.write(state);

        return mplew.getPacket();
    }

    public static byte[] secondPwError(byte mode) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.SECONDPW_ERROR);
		mplew.write(mode);

        return mplew.getPacket();
    }
    
    public static byte[] getIntegrityResponse(int request) {
    	MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.CLIENT_AUTH);
    	//mplew.write(request);
    	mplew.write(0);
    	
    	return mplew.getPacket();
    }
    
    public static byte[] getClientResponse() {
    	MaplePacketWriter mplew = new MaplePacketWriter();
    	
    	mplew.write(1);
    	
    	return mplew.getPacket();
    }

    /**
     * Sends an authentication response every 15 seconds which allows the client to keep a connection to the server.
     * @param response
     * @return
     */
    public static byte[] sendAuthResponse(int response) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.AUTH_RESPONSE);
		mplew.writeInt(response);
        
        return mplew.getPacket();
    }

    public static byte[] enableRecommended(int world) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.ENABLE_RECOMMENDED);
		mplew.writeInt(world);
        
        return mplew.getPacket();
    }

    public static byte[] sendRecommended(int world, String message) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.SEND_RECOMMENDED);
		mplew.write(message != null ? 1 : 0);
        if (message != null) {
            mplew.writeInt(world);
            mplew.writeMapleAsciiString(message);
        }
        
        return mplew.getPacket();
    }

    public static byte[] ResetScreen() {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.RESET_SCREEN);
		mplew.write(HexTool.getByteArrayFromHexString("02 08 00 32 30 31 32 30 38 30 38 00 08 00 32 30 31 32 30 38 31 35 00"));

        return mplew.getPacket();
    }

    public static byte[] getServerList(int serverId, Map<Integer, Integer> channelLoad) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.SERVERLIST);
		mplew.write(serverId);
        String worldName = LoginServer.getTrueServerName();
        mplew.writeMapleAsciiString(worldName);
        mplew.write(WorldOption.getById(serverId).getFlag());
        mplew.writeMapleAsciiString(LoginServer.getEventMessage());
        mplew.writeShort(100);
        mplew.writeShort(100);
        mplew.write(0);
        int lastChannel = 1;
        Set<Integer> channels = channelLoad.keySet();
        for (int i = 30; i > 0; i--) {
            if (channels.contains(Integer.valueOf(i))) {
                lastChannel = i;
                break;
            }
        }
        mplew.write(lastChannel);

        for (int i = 1; i <= lastChannel; i++) {
            int load;

            if (channels.contains(i)) {
            	load = channelLoad.get(i);      
            } else {
                load = 1;
            }
            mplew.writeMapleAsciiString(worldName + "-" + i);
            mplew.writeInt(load);
            mplew.write(serverId);
            mplew.writeShort(i - 1);
        }
        mplew.writeShort(0);
        mplew.writeInt(0);
        mplew.write(0);

        return mplew.getPacket();
    }

    public static byte[] getEndOfServerList() {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.SERVERLIST);
		mplew.write(-1);
        mplew.write(4);
        mplew.writeMapleAsciiString("http://maplestory.nexon.com/maplestory/news/2015/login_banner.html");
        mplew.writeMapleAsciiString("http://maplestory2.nexon.com/event/2015/OnLineFestival?");
        mplew.writeInt(5000);
        mplew.writeInt(415);
        mplew.writeInt(80);
        mplew.writeInt(192);
        mplew.writeInt(452);
        mplew.writeMapleAsciiString("http://s.nx.com/s2/Game/Maplestory/Maple2013/image/banner/ingame_bn/MS2_Festival_1.jpg");
        mplew.writeMapleAsciiString("http://maplestory2.nexon.com/live/20150627/OnlineFestival?st=maple1&bn=cla_ser&ev=live&dt=20150627");
        mplew.writeInt(5000);
        mplew.writeInt(370);
        mplew.writeInt(70);
        mplew.writeLong(0);
        mplew.writeMapleAsciiString("http://s.nx.com/s2/Game/Maplestory/Maple2013/image/banner/ingame_bn/MS2_Festival_2.jpg");
        mplew.writeMapleAsciiString("http://maplestory2.nexon.com/event/2015/StarterPack?st=maple1&bn=cla_ser&ev=starter&dt=20150623");
        mplew.writeInt(5000);
        mplew.writeInt(370);
        mplew.writeInt(70);
        mplew.writeLong(0);
        mplew.writeMapleAsciiString("http://s.nx.com/s2/Game/Maplestory/Maple2013/image/banner/ingame_bn/ingame_150701_01.jpg");
        mplew.writeMapleAsciiString("http://closers.nexon.com/news/events/view.aspx?n4articlesn=117&st=maple&bn=login&ev=20150624");
        mplew.writeInt(5000);
        mplew.writeInt(370);
        mplew.writeInt(70);
        mplew.writeLong(0);
        mplew.writeShort(0);
        
        return mplew.getPacket();
    }

    public static final byte[] getLoginWelcome() {
        List flags = new LinkedList();

        return CField.spawnFlags(flags);
    }

    public static byte[] getServerStatus(int status) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.SERVERSTATUS);
		mplew.writeShort(status);

        return mplew.getPacket();
    }

    public static byte[] changeBackground(Triple<String, Integer, Boolean>[] backgrounds) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.CHANGE_BACKGROUND);
        //mplew.write(HexTool.getByteArrayFromHexString("04 02 00 61 33 00 02 00 61 32 00 02 00 61 31 00 02 00 61 30 01"));
        mplew.write(backgrounds.length); //number of bgs
        for (Triple<String, Integer, Boolean> background : backgrounds) {
            mplew.writeMapleAsciiString(background.getLeft());
            mplew.write(background.getRight() ? Randomizer.nextInt(2) : background.getMid());
        }
        
        /* Map.wz/Obj/login.img/WorldSelect/background/background number
         Backgrounds ids sometime have more than one background anumation */
        /* Background are like layers, backgrounds in the packets are
         removed, so the background which was hiden by the last one
         is shown.
         */
        
        return mplew.getPacket();
    }

    public static byte[] getChannelSelected() {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.CHANNEL_SELECTED);
		mplew.writeZeroBytes(3);

        return mplew.getPacket();
    }

    public static byte[] getCharList(String secondpw, List<MapleCharacter> chars, int charslots) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.CHARLIST);

        /* v146
        mplew.writeShort(SendPacketOpcode.CHARLIST);
		mplew.write(0);
        mplew.write(chars.size());
        for (MapleCharacter chr : chars) {
            addCharEntry(mplew, chr, (!chr.isGM()) && (chr.getLevel() >= 30), false);
        }
        if (constants.ServerConfig.DISABLE_PIC) {
            mplew.write(2);
        } else {
            mplew.write((secondpw != null) && (secondpw.length() <= 0) ? 2 : (secondpw != null) && (secondpw.length() > 0) ? 1 : 0);
        }
        mplew.write(0);
        mplew.writeInt(charslots);
        mplew.writeInt(0);
        mplew.writeInt(-1);
        mplew.writeReversedLong(PacketHelper.getTime(System.currentTimeMillis()));
        mplew.writeZeroBytes(5);
        */

		mplew.write(0);
        mplew.writeMapleAsciiString("normal");
        mplew.writeInt(4);
        mplew.write(0);
        mplew.writeInt(0);
        mplew.writeReversedLong(PacketHelper.getTime(System.currentTimeMillis()));
        mplew.write(0);
        mplew.writeInt(chars.size());
        for (MapleCharacter chr: chars) {
        	mplew.writeInt(chr.getId());
        }
        mplew.write(chars.size());
        for (MapleCharacter chr : chars) {
            addCharEntry(mplew, chr, (!chr.isGM()) && (chr.getLevel() >= 30), false);
        }
        if (constants.ServerConfig.DISABLE_PIC) {
            mplew.writeShort(2);
        } else {
            mplew.writeShort((secondpw != null) && (secondpw.length() <= 0) ? 2 : (secondpw != null) && (secondpw.length() > 0) ? 1 : 0);
        }
        mplew.writeInt(charslots);
        mplew.writeInt(0);
        mplew.writeInt(-1);
        mplew.writeLong(PacketHelper.getTime(System.currentTimeMillis()));
        mplew.writeZeroBytes(6);
        
        return mplew.getPacket();
    }

    public static byte[] charNameResponse(String charname, boolean nameUsed) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.CHAR_NAME_RESPONSE);
		mplew.writeMapleAsciiString(charname);
        mplew.write(nameUsed ? 1 : 0);

        return mplew.getPacket();
    }
    
    public static byte[] addNewCharEntry(MapleCharacter chr, boolean worked) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.ADD_NEW_CHAR_ENTRY);
		mplew.write(worked ? 0 : 1);
        addCharEntry(mplew, chr, false, false);
        
        return mplew.getPacket();
    }

    private static void addCharEntry(MaplePacketWriter mplew, MapleCharacter chr, boolean ranking, boolean viewAll) {
        PacketHelper.addCharStats(mplew, chr);
        PacketHelper.addCharLook(mplew, chr, true, false);
        if (GameConstants.isZero(chr.getJob())) {
            PacketHelper.addCharLook(mplew, chr, true, true);
        }
        if (!viewAll) {
            mplew.write(0);
        }
        mplew.write(ranking ? 1 : 0);
        if (ranking) {
            mplew.writeInt(chr.getRank());
            mplew.writeInt(chr.getRankMove());
            mplew.writeInt(chr.getJobRank());
            mplew.writeInt(chr.getJobRankMove());
        }
    }

    public static byte[] enableSpecialCreation(int accid, boolean enable) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.SPECIAL_CREATION);
		mplew.writeInt(accid);
        mplew.write(enable ? 0 : 1);
        mplew.write(0);

        return mplew.getPacket();
    }

    public static byte[] partTimeJob(int cid, short type, long time) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.PART_TIME);
		mplew.writeInt(cid);
        mplew.write(0);
        mplew.write(type);
        //1) 0A D2 CD 01 70 59 9F EA
        //2) 0B D2 CD 01 B0 6B 9C 18
        mplew.writeReversedLong(PacketHelper.getTime(time));
        mplew.writeInt(0);
        mplew.write(0);

        return mplew.getPacket();
    }

    public static byte[] updatePartTimeJob(PartTimeJob partTime) {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.PART_TIME);
		mplew.writeInt(partTime.getCharacterId());
        mplew.write(0);
        PacketHelper.addPartTimeJob(mplew, partTime);
        return mplew.getPacket();
    }

    public static byte[] sendLink() {
        MaplePacketWriter mplew = new MaplePacketWriter(SendPacketOpcode.SEND_LINK);
		mplew.write(1);
        mplew.write(CField.Nexon_IP);
        mplew.writeShort(0x2057);
        mplew.write(0);

        return mplew.getPacket();
    }

	public static final byte[] sendUnknown() {
		MaplePacketWriter mplew = new MaplePacketWriter(3);
	    mplew.writeShort(0x16);
	    mplew.write(0x07);
	       
	    return mplew.getPacket();
	}
}
