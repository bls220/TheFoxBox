package com.team_awesome.thefoxbox.provider;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.team_awesome.thefoxbox.data.SongItem;

/**
 * All of the methods here are blocking. In general most methods will fail after a few retries by throwing a
 * {@link SocketTimeoutException};
 * @author Kevin
 *
 */
public class Communicator {
	private final Socket sock;
	private final AuthToken auth;

	Communicator(Socket sock, AuthToken auth) throws IOException {
		this.sock = sock;
		this.auth = auth;

		sock.setSoTimeout(5000);
	}

	private static SongItem[] parseSongList(JSONObject obj) throws IOException {
		try {
			JSONArray arr = obj.getJSONArray("Songs");
			int len = arr.length();
			SongItem[] ret = new SongItem[len];
			for (int i = 0; i < len; i++) {
				JSONObject s = arr.getJSONObject(i);

				ret[i] = new SongItem(s.getString("Album"),
						s.getString("Title"), s.getString("Artist"),
						s.getInt("Id"));
			}
			return ret;
		} catch (JSONException ex) {
			throw new IOException(ex);
		}
	}

	SongItem[] getSongList() throws IOException {
		return parseSongList(sendReadJSON(sock, wrap(auth, MsgType.SONGLIST)));
	}

	SongItem[] search(String term) throws IOException {
		return parseSongList(sendReadJSON(sock, wrap(auth, MsgType.SEARCH, "Term", term)));
	}

	void vote(int songid, int amt) throws IOException {
		sendJSON(sock, wrap(auth, MsgType.VOTE, "Id", songid, "Amt", amt));
	}

	String submit(int songid) throws IOException {
		try {
			JSONObject obj = sendReadJSON(sock, wrap(auth, MsgType.SUBMIT, "Id", songid));
			String ret = obj.getString("Ret");
			return ret.length() == 0 ? null : ret;
		} catch (JSONException ex) {
			throw new IOException(ex);
		}
	}

	boolean ping() throws IOException {
		try {
			JSONObject ret = sendReadJSON(sock, wrap(auth, MsgType.PING));
			String v = ret.getString("Request");
			return v.equals("pong");
		} catch (JSONException ex) {
			throw new IOException(ex);
		} catch (SocketTimeoutException ex) {
			Log.w("Communicator", "Ping timed out: " + ex);
			return false;
		}
	}

	static AuthToken login(Socket sock, LoginInfo logfo) throws IOException {
		String username = logfo.username;
		return parseAuthToken(Communicator.sendReadJSON(sock,
				wrap(null, MsgType.LOGIN, "Name", username)));
	}

	/**
	 * Keeps track of the different names of commands to send to the server.
	 * 
	 */
	enum MsgType {
		SEARCH("search"), VOTE("vote"), LOGIN("login"), SUBMIT("submit"), MOODCHANGE(
				"moodchange"), SONGLIST("songlist"), PING("ping");
		private String val;

		MsgType(String value) {
			val = value;
		}

		public String toString() {
			return val;
		}
	}

	private static final String REQUEST_FIELD = "Request";
	private static final String AUTHCODE_FIELD = "AuthToken";
	private static final String PARAMS_FIELD = "Params";

	static AuthToken parseAuthToken(JSONObject ret) throws IOException {
		try {
			return new AuthToken(ret.getString(AUTHCODE_FIELD));
		} catch (JSONException ex) {
			throw new IOException(ex);
		}
	}

	private static JSONObject wrap(AuthToken toke, MsgType type,
			Object... params) {
		int plen = params.length;
		if (plen % 2 != 0) {
			throw new RuntimeException("PARAMS ARE OF THe WRONG LENGTH, SILLY!");
		}

		JSONObject ret = new JSONObject();
		try {
			if (toke != null) {
				ret.put(AUTHCODE_FIELD, toke);
			}
			ret.put(REQUEST_FIELD, type);

			JSONObject jp = new JSONObject();
			for (int i = 0; i < plen; i += 2) {
				jp.put((String) params[i], params[i + 1].toString());
			}
			ret.put(PARAMS_FIELD, jp);
		} catch (JSONException ex) {
			ex.printStackTrace();
		}

		return ret;
	}


	/**
	 * Calls {@link #sendReadJSON(Socket, JSONObject, int)} with a retry value of 5.
	 */
	static JSONObject sendReadJSON(Socket dest, JSONObject json)
			throws IOException { 
		return sendReadJSON(dest, json, 5);
	}
	/**
	 * Send JSON to the destination and wait for a response. Also deals with
	 * retries.
	 * 
	 * This is implemented as a static method taking a sock so that it can be
	 * shared by the CommunicatorFactory's logging in method.
	 * 
	 * @param dest
	 * @param json
	 * @throws IOException
	 * @throws JSONException
	 */
	static JSONObject sendReadJSON(Socket dest, JSONObject json, int numRetries)
			throws IOException {
		sendJSON(dest, json);

		InputStream in = dest.getInputStream();
		
		int len;
		while (true) {
			try {
				len = in.read() | (in.read() << 8) | (in.read() << 16)
						| (in.read() << 24);
				
				// Got the data!
				break;
			} catch (SocketTimeoutException ex) {
				numRetries--;
				if (numRetries <= 0) {
					// Out of retries;
					throw ex;
				}
			}
		}
		
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
