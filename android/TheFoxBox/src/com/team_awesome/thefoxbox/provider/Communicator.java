package com.team_awesome.thefoxbox.provider;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.team_awesome.thefoxbox.SongItem;
import com.team_awesome.thefoxbox.SongItem.EVote;

public class Communicator {
	private final Socket sock;
	private final AuthToken auth;

	Communicator(Socket sock, AuthToken auth) {
		this.sock = sock;
		this.auth = auth;
	}

	/**
	 * Doing it this way makes it easier to add stuff to the token later if
	 * needed. I admit right now it looks like overkill.
	 * 
	 * @author Kevin
	 * 
	 */
	final static class AuthToken {
		private final String token;

		public AuthToken(String auth) {
			token = auth;
		}

		public String toString() {
			return token;
		}
	}

	private SongItem[] parseSongList(JSONObject obj) throws IOException {
		try {
			JSONArray arr = obj.getJSONArray("Songs");
			int len = arr.length();
			SongItem[] ret = new SongItem[len];
			for (int i = 0; i < len; i++) {
				JSONObject s = arr.getJSONObject(i);

				ret[i] = new SongItem(s.getString("Album"),
						s.getString("Title"), s.getString("Artist"),
						s.getInt("Id"), EVote.NONE);
			}
			return ret;
		} catch (JSONException ex) {
			throw new IOException(ex);
		}
	}

	SongItem[] getSongList() throws IOException {
		return parseSongList(sendReadJSON(sock, ServerAPI.songlist(auth)));
	}

	SongItem[] search(String term) throws IOException {
		return parseSongList(sendReadJSON(sock, ServerAPI.search(auth, term)));
	}

	void vote(int songid, int amt) throws IOException {
		sendJSON(sock, ServerAPI.vote(auth, songid, amt));
	}
	
	String submit(int songid) throws IOException {
		try {
			JSONObject obj = sendReadJSON(sock, ServerAPI.submit(auth, songid));
			String ret = obj.getString("Ret");
			return ret.length() == 0 ? null : ret;
		} catch (JSONException ex) {
			throw new IOException(ex);
		}
	}
	
	

	// TODO: These should really be in ServerAPI. Or maybe combine the ServerAPI
	// with this class.
	// We don't need that much abstraction at this point.

	/**
	 * Send JSON to the destination and wait for a response. Also deals with
	 * retries.
	 * 
	 * TODO: Deal with retries.
	 * 
	 * This is implemented as a static method taking a sock so that it can be
	 * shared by the CommunicatorFactory's logging in method.
	 * 
	 * @param dest
	 * @param json
	 * @throws IOException
	 * @throws JSONException
	 */
	static JSONObject sendReadJSON(Socket dest, JSONObject json)
			throws IOException {
		sendJSON(dest, json);

		InputStream in = dest.getInputStream();
		int len = in.read() | (in.read() << 8) | (in.read() << 16)
				| (in.read() << 24);
		if (len < 0) {
			throw new IOException("Invalid message length.");
		}
		byte[] buf = new byte[len];
		int cur = 0;
		do {
			cur += in.read(buf, cur, len - cur);
		} while (cur < len);

		try {
			return new JSONObject(new String(buf));
		} catch (JSONException ex) {
			throw new IOException(ex);
		}
	}

	/**
	 * Send JSON to the destination.
	 * 
	 * 
	 * @param dest
	 * @param json
	 * @throws IOException
	 */
	static void sendJSON(Socket dest, JSONObject json) throws IOException {
		OutputStream out = dest.getOutputStream();
		byte[] son = json.toString().getBytes();

		int len = json.length();
		out.write(len);
		out.write(len >> 8);
		out.write(len >> 16);
		out.write(len >> 24);
		out.write(son);
		out.flush();
	}
}
