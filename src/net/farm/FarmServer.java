/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.farm;

import constants.ServerConfig;
import net.Acceptor;
import net.channel.PlayerStorage;

import java.net.InetSocketAddress;

/**
 *
 * @author Itzik
 */
public class FarmServer {

    private static String ip;
    private static final int PORT = 8611;
    private static Acceptor acceptor;
    private static PlayerStorage players;
    private static boolean finishedShutdown = false;

    public static void run() {
        ip = ServerConfig.interface_ + ":" + PORT;

        players = new PlayerStorage(-30);
        try {
        	acceptor = new Acceptor(new InetSocketAddress(PORT));
			acceptor.run();
            System.out.println("Farm Server is listening on port  " + PORT + ".");
        } catch (Exception e) {
            System.err.println("Binding to port " + PORT + " failed");
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
        System.out.println("Saving all connected clients (Farm)...");
        players.disconnectAll();
        System.out.println("Shutting down Farm...");

        finishedShutdown = true;
    }

    public static boolean isShutdown() {
        return finishedShutdown;
    }
}
