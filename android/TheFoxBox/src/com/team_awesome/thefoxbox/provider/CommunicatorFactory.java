package com.team_awesome.thefoxbox.provider;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

import com.team_awesome.thefoxbox.provider.Communicator.AuthToken;

/**
 * I've always hated factories used for factories sake, but this instance it actually makes sense
 * to do it this way.
 * 
 * @author Kevin
 *
 */
public class CommunicatorFactory {
	private final String addr;
	private final int port;
	/**
	 * Creates a new factory for Communicators. Does not log in or anything; authentication is done lazily
	 * on first call.
	 * 
	 * TODO: Require username(and password) at this point. 
	 * 
	 * @param addr
	 * @param port
	 */
	public CommunicatorFactory(String addr, int port) {
		this.addr = addr;
		this.port = port;
	}
	
	/**
	 * Lazily set. Should only ever be accessed by {@link #getAuth(Socket)}
	 */
	private AuthToken auth;
	
	/**
	 * Gets the Auth token for new communicators. Note that if logging in has already occurred,
	 * the socket parameter is not used. Deals with logging in if needed.
	 * 
	 * Blocks so that logging in doesn't happen twice.
	 * 
	 * @param sock
	 * @return The AuthToken
	 * @throws IOException 
	 */
	private synchronized AuthToken getAuth(Socket sock) throws IOException {
		if (auth == null) {
			auth = ServerAPI.getAuthToken(Communicator.sendReadJSON(sock, ServerAPI.login("dummy")));
		}
		
		return auth;
	}
	
	/**
	 * Gets a communicator to talk to the server.
	 * 
	 * Allows us to reuse connections if needed.
	 * 
	 * @return
	 * @throws IOException 
	 */
	public Communicator get() throws IOException {
		// TODO: Reuse connections. For now just make a new Socket every time.
		Socket sock = new Socket(InetAddress.getByName(addr), port);
		AuthToken auth = getAuth(sock);
		
		return new Communicator(sock, auth);
	}
}
