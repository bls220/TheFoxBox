package com.team_awesome.thefoxbox.provider;

/**
 * Doing it this way makes it easier to add stuff to the token later if
 * needed. I admit right now it looks like overkill.
 * 
 * @author Kevin
 * 
 */
final class AuthToken {
	private final String token;

	public AuthToken(String auth) {
		token = auth;
	}

	public String toString() {
		return token;
	}
}