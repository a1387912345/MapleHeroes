package server;

import client.SkillFactory;
import client.inventory.MapleInventoryIdentifier;
import constants.GameConstants;
import constants.ServerConfig;
import constants.ServerConstants;
import constants.WorldConstants;
import constants.WorldConstants.TespiaWorldOption;
import constants.WorldConstants.WorldOption;
import custom.CustomPlayerRankings;
import database.DatabaseConnection;
import net.PacketProcessor;
import net.server.cashshop.CashShopServer;
import net.server.channel.ChannelServer;
import net.server.channel.MapleDojoRanking;
import net.server.channel.MapleGuildRanking;
import net.server.farm.FarmServer;
import net.server.login.LoginInformationProvider;
import net.server.login.LoginServer;
import net.server.loginauth.LoginAuthServer;
import net.server.talk.TalkServer;
import net.world.World;
import net.world.family.MapleFamily;
import net.world.guild.MapleGuild;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;
import server.Timer.BuffTimer;
import server.Timer.CloneTimer;
import server.Timer.EtcTimer;
import server.Timer.EventTimer;
import server.Timer.MapTimer;
import server.Timer.PingTimer;
import server.Timer.WorldTimer;
import server.events.MapleOxQuizFactory;
import server.life.MapleLifeFactory;
import server.life.MapleMonsterInformationProvider;
import server.life.PlayerNPC;
import server.maps.MapleMapFactory;
import server.quest.MapleQuest;

public class Start implements Runnable {

    public static long startTime = System.currentTimeMillis();
    private static final Start instance = new Start();
    public static AtomicInteger CompletedLoadingThreads = new AtomicInteger(0);
    
    private Start() {
    	
    }

    @Override
    public void run() {
//        System.setProperty("wzpath", "wz"); // test only..?
        /*System.out.println("Pick a SQL Setting.");
         Scanner input = new Scanner(System.in);
         for (SQLInfo sqli : LoadingOption.SQLInfo.values()) {
         System.out.print(sqli);
         }

         int inputNum;
         inputNum = input.nextInt();
         SQLInfo use = SQLInfo.getById(inputNum);
         ServerConstants.SQL_PORT = use.getPort();
         ServerConstants.SQL_USER = use.getUser();
         ServerConstants.SQL_PASSWORD = use.getPass();
         ServerConstants.SQL_DATABASE = use.getDb();

         System.out.println("Pick an interface IP.");
         int i = 0; 
         for (String s : LoadingOption.ipSetting) {
         System.out.println(++i + ". " + s);
         }
         inputNum = input.nextInt();
         ServerConfig.interface_ = LoadingOption.ipSetting[inputNum - 1];
        
         boolean inputBool;
         System.out.println("Admin Mode? (true/false)");
         inputBool = input.nextBoolean();
         ServerConfig.adminOnly = inputBool;
        
         System.out.println("Want the Server to Log Packets? (true/false)");
         inputBool = input.nextBoolean();
         ServerConfig.logPackets = inputBool;*/
        
        Properties p = new Properties();
        try {
            p.load(new FileInputStream("config.ini"));
        } catch (IOException ex) {
            System.out.println("Failed to load config.ini");
            System.exit(0);
        }
        
        ServerConfig.interface_ = p.getProperty("ip");
        ServerConfig.logPackets = Boolean.parseBoolean(p.getProperty("logOps"));
        ServerConfig.adminOnly = Boolean.parseBoolean(p.getProperty("adminOnly"));
        ServerConfig.USE_FIXED_IV = Boolean.parseBoolean(p.getProperty("antiSniff"));
        ServerConstants.SQL_PORT = p.getProperty("sql_port");
        ServerConstants.SQL_USER = p.getProperty("sql_user");
        ServerConstants.SQL_PASSWORD = p.getProperty("sql_password");
        ServerConstants.SQL_DATABASE = p.getProperty("sql_db");
        System.setProperty("wzpath", p.getProperty("wzpath"));

        if (ServerConfig.adminOnly || ServerConstants.Use_Localhost) {
            System.out.println("Admin Only mode is active.");
        }
        try {
            try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement("UPDATE accounts SET loggedin = 0")) {
                ps.executeUpdate();
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Runtime Exception - Could not connect to MySql Server.");
        }

        World.init();
        System.out.println("Host: " + ServerConfig.interface_ + ":" + LoginServer.PORT);
        System.out.println("In-game Version: " + ServerConstants.CLIENT_VERSION + "." + ServerConstants.CLIENT_SUBVERSION);
        System.out.println("Source Revision: " + ServerConstants.SOURCE_REVISION);

        int servers = 0;
        if (ServerConstants.TESPIA) {
            for (TespiaWorldOption server : TespiaWorldOption.values()) {
                if (server.show()) {
                    servers++;
                }
            }
        } else {
            for (WorldOption server : WorldOption.values()) {
                if (server.show()) {
                    servers++;
                }
            }
        }
        System.out.println("Worlds: Total: " + (ServerConstants.TESPIA ? TespiaWorldOption.values().length : WorldOption.values().length) + " Visible: " + servers + (WorldConstants.gmserver > -1 ? " GM Server: " + WorldConstants.getNameById(WorldConstants.gmserver) : ""));
        System.out.println("Running Threads");
        WorldTimer.getInstance().start();
        EtcTimer.getInstance().start();
        MapTimer.getInstance().start();
        CloneTimer.getInstance().start();
        //System.out.print(/*"\u25CF"*/".");
        EventTimer.getInstance().start();
        BuffTimer.getInstance().start();
        PingTimer.getInstance().start();
        GameConstants.LoadEXP();
        //System.out.print(/*"\u25CF"*/".");
        MapleDojoRanking.getInstance().load();
        MapleGuildRanking.getInstance().load();
        MapleGuild.loadAll();
        MapleFamily.loadAll();
        //System.out.print(/*"\u25CF"*/".");
        MapleLifeFactory.loadQuestCounts();
        MapleQuest.initQuests();
        
        // Load Resources
        System.out.println("Loading resources...");
		MapleItemInformationProvider.getInstance().runItems();
        MapleItemInformationProvider.getInstance().runEtc();
        MapleMonsterInformationProvider.getInstance().load();
        //System.out.print(/*"\u25CF"*/".");
        SkillFactory.load();
        LoginInformationProvider.getInstance();
        RandomRewards.load();
        //System.out.print(/*"\u25CF"*/".");
        MapleOxQuizFactory.getInstance();
        MapleCarnivalFactory.getInstance();
        CharacterCardFactory.getInstance().initialize();
  //      MobSkillFactory.getInstance();
        //System.out.print(/*"\u25CF"*/".");
        SpeedRunner.loadSpeedRuns();
        MapleInventoryIdentifier.getInstance();
        MapleMapFactory.loadCustomLife();
        //System.out.print(/*"\u25CF"*/".");
        Connection con = DatabaseConnection.getConnection();
        PreparedStatement ps;
        try {
            ps = con.prepareStatement("DELETE FROM `moonlightachievements` where achievementid > 0;");
            ps.executeUpdate();
            ps.close();
        } catch (SQLException ex) {
        }
        //System.out.println(" Complete!");
        CashItemFactory.getInstance().initialize();
        PacketProcessor.getInstance().initialize();
        
        // Load Servers
        LoginServer.getInstance().run();
        LoginAuthServer.getInstance().run();
        ChannelServer.startChannels();
        CashShopServer.run();
        FarmServer.run();
        //TalkServer.run();
        
        Runtime.getRuntime().addShutdownHook(new Thread(new Shutdown()));
        World.registerRespawn();
        ShutdownServer.registerMBean();
        PlayerNPC.loadAll();
        MapleMonsterInformationProvider.getInstance().addExtra();
        LoginServer.getInstance().setOn();
        RankingWorker.run();
        //System.out.println("Event Script List: " + ServerConfig.getEventList());
        if (ServerConfig.logPackets) {
            System.out.println("Packet logging mode had been set to enabled.");
        }
        if (ServerConfig.USE_FIXED_IV) {
            System.out.println("[Anti-Sniff] Server is using Fixed IVs!");
        }
        CustomPlayerRankings.getInstance().load();
        
        double now = System.currentTimeMillis() - startTime;
        double seconds = now / 1000;
        System.out.println("Total loading time: " + seconds + " seconds.");
    }

    public static class Shutdown implements Runnable {

        @Override
        public void run() {
            ShutdownServer.getInstance().run();
            ShutdownServer.getInstance().run();
        }
    }

    public static void main(final String args[]) throws InterruptedException, IOException {
        instance.run();
    }
}
