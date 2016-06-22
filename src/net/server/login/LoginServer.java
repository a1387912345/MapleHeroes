/*
 This file is part of the OdinMS Maple Story Server
 Copyright (C) 2008 ~ 2010 Patrick Huy <patrick.huy@frz.cc> 
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
package net.server.login;

import constants.ServerConfig;
import net.Acceptor;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import tools.Triple;

public class LoginServer {

    public static final int PORT = 8484;
    private Acceptor acceptor;
    private Map<Integer, Integer> load = new HashMap<>();
    private String serverName, eventMessage;
    private byte flag;
    private int maxCharacters, userLimit, usersOn = 0;
    private boolean finishedShutdown = true, adminOnly = false;
    private final HashMap<Integer, Triple<String, String, Integer>> loginAuth = new HashMap<>();
    private final HashSet<String> loginIPAuth = new HashSet<>();
    
    private static final LoginServer instance = new LoginServer();
    
    private LoginServer() {
    	
    }
    
    public static LoginServer getInstance() {
		return instance;
	}

    public void putLoginAuth(int chrid, String ip, String tempIP, int channel) {
        Triple<String, String, Integer> put = loginAuth.put(chrid, new Triple<>(ip, tempIP, channel));
        loginIPAuth.add(ip);
    }

    public Triple<String, String, Integer> getLoginAuth(int chrid) {
        return loginAuth.remove(chrid);
    }

    public boolean containsIPAuth(String ip) {
        return loginIPAuth.contains(ip);
    }
    
    public void removeIPAuth(String ip) {
        loginIPAuth.remove(ip);
    }

    public void addIPAuth(String ip) {
        loginIPAuth.add(ip);
    }

    public final void addChannel(final int channel) {
        load.put(channel, 0);
    }

    public final void removeChannel(final int channel) {
        load.remove(channel);
    }

    public final void run() {
        System.out.print("Loading Login Server...");
        userLimit = ServerConfig.userLimit;
        serverName = ServerConfig.serverName;
        eventMessage = ServerConfig.eventMessage;
        flag = ServerConfig.flag;
        adminOnly = ServerConfig.adminOnly;
        maxCharacters = ServerConfig.maxCharacters;
        
        try {
        	acceptor = new Acceptor(new InetSocketAddress(PORT));
			acceptor.run();
            System.out.println(" Complete!");
            System.out.println("Login Server is listening on port " + PORT + ".");
        } catch (Exception e) {
            System.out.println(" Failed!");
            System.err.println("Could not bind to port " + PORT + ": " + e);
        }
    }

    public final void shutdown() {
        if (finishedShutdown) {
            return;
        }
        System.out.println("Shutting down login...");
        acceptor.stop();
        finishedShutdown = true; //nothing. lol
    }

    public final String getServerName() {
        return serverName;
    }

    public final String getTrueServerName() {
        return serverName.substring(0, serverName.length() - 2);
    }

    public String getEventMessage() {
        return eventMessage;
    }

    public int getMaxCharacters() {
        return maxCharacters;
    }

    public Map<Integer, Integer> getLoad() {
        return load;
    }

    public void setLoad(final Map<Integer, Integer> load_, final int usersOn_) {
        load = load_;
        usersOn = usersOn_;
    }

    public String getEventMessage(int world) { //TODO: Finish this
        switch (world) {
            case 0:
                return null;
        }
        return null;
    }

    public final void setFlag(final byte newflag) {
        flag = newflag;
    }

    public final int getUserLimit() {
        return userLimit;
    }

    public final int getUsersOn() {
        return usersOn;
    }

    public final void setUserLimit(final int newLimit) {
        userLimit = newLimit;
    }

    /*
    public final int getNumberOfSessions() {
        return acceptor.getManagedSessions(InetSocketadd).size();
    }
    */

    public final boolean isAdminOnly() {
        return adminOnly;
    }

    public final boolean isShutdown() {
        return finishedShutdown;
    }

    public final void setOn() {
        finishedShutdown = false;
    }
}
