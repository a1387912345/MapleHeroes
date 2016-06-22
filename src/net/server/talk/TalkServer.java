package net.server.talk;

import constants.ServerConfig;
import net.Acceptor;
import net.server.channel.PlayerStorage;

import java.net.InetSocketAddress;

public class TalkServer {
	
	private static String ip;
    private static InetSocketAddress InetSocketadd;
    private final static int PORT = 8785;
    private static Acceptor acceptor;
    private static PlayerStorage players;
    private static boolean finishedShutdown = false;
    
    public static void run() {
        System.out.print("Loading Talk Server...");
        ip = ServerConfig.interface_ + ":" + PORT;

        players = new PlayerStorage(-10);

        try {
        	acceptor = new Acceptor(new InetSocketAddress(PORT));
			acceptor.run();
            System.out.println(" Complete!");
            System.out.println("Talk Server is listening on port " + PORT + ".");
        } catch (Exception e) {
            System.out.println(" Failed!");
            System.err.println("Could not bind to port " + PORT + ".");
            throw new RuntimeException("Binding failed.", e);
        }
    }

    public static String getIP() {
        return ip;
    }

    public static PlayerStorage getPlayerStorage() {
        return players;
    }

    public static void shutdown() {
        if (finishedShutdown) {
            return;
        }
        System.out.println("Saving all connected clients (Talk)...");
        players.disconnectAll();
        System.out.println("Shutting down Talk Server...");
        //acceptor.unbindAll();
        finishedShutdown = true;
    }

    public static boolean isShutdown() {
        return finishedShutdown;
    }
    
}
