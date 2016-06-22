package net.server.loginauth;

import java.net.InetSocketAddress;

public class LoginAuthServer {
	
	private static final int PORT = 47611;
	private static final LoginAuthServer instance = new LoginAuthServer();
	
	private static LoginAuthAcceptor acceptor;
	
	private LoginAuthServer() {
		
	}
	
	public static LoginAuthServer getInstance() {
		return instance;
	}
	
	public void run() {
		try {
			acceptor = new LoginAuthAcceptor(new InetSocketAddress(PORT));
			acceptor.run();
            System.out.println("Login Authentication Server is listening on port " + PORT + ".");
		} catch (Exception e) {
            System.err.println("Could not bind to port " + PORT + ": " + e);
		}
	}

}
