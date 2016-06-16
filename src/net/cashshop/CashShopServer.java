package net.cashshop;

import constants.ServerConfig;
import net.Acceptor;
import net.channel.PlayerStorage;

import java.net.InetSocketAddress;

public class CashShopServer {

    private static String ip;
    private static InetSocketAddress InetSocketadd;
    private final static int PORT = 8610;
    private static Acceptor acceptor;
    private static PlayerStorage players;
    private static boolean finishedShutdown = false;

    public static void run() {
        System.out.print("Loading Cash Shop...");
        ip = ServerConfig.interface_ + ":" + PORT;

        players = new PlayerStorage(-10);

        try {
        	acceptor = new Acceptor(new InetSocketAddress(PORT));
			acceptor.run();
            System.out.println(" Complete!");
            System.out.println("Cash Shop Server is listening on port " + PORT + ".");
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
        System.out.println("Saving all connected clients (CS)...");
        players.disconnectAll();
        System.out.println("Shutting down CS...");
        //acceptor.unbindAll();
        finishedShutdown = true;
    }

    public static boolean isShutdown() {
        return finishedShutdown;
    }
}
