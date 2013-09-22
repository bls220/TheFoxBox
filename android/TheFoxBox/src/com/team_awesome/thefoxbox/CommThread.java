/**
 * 
 */
package com.team_awesome.thefoxbox;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.team_awesome.thefoxbox.SongItem.EVote;

import android.util.Log;
import android.widget.Toast;

/**
 * @author bsmith
 * 
 */
public class CommThread extends Thread {
	private static final String IPADDR = "192.168.1.108";// "67.194.107.169";
	private static final int PORT = 5853;

	public static String mAuthCode;

	private Socket mSocket;

	private JSONObject mJSONOut;
	private QueryCallbacks mCallback;

	/**
	 * Defines message types.
	 * 
	 * @see api definitions.txt
	 * 
	 */
	public static enum EMSG_TYPE {
		SEARCH("search"), VOTE("vote"), LOGIN("login"), SUBMIT("submit"), MOODCHANGE(
				"moodchange"), SONGLIST("songlist");
		private String val;

		EMSG_TYPE(String value) {
			val = value;
		}

		public String toString() {
			return val;
		}
	};

	public void login(QueryCallbacks callback, String username)
			throws JSONException {
		Map<String, String> map = new HashMap<String, String>();
		map.put("Name", username.trim().toLowerCase());
		mJSONOut = JSONPacker.pack("", EMSG_TYPE.LOGIN, map);
		mCallback = callback;
	}

	public void search(QueryCallbacks callback, String query) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("Term", query.trim().toLowerCase());
		mJSONOut = JSONPacker.pack(mAuthCode, EMSG_TYPE.SEARCH, map);
		mCallback = callback;
	}

	public void getQueue(QueryCallbacks callback) {
		Map<String, String> map = new HashMap<String, String>();
		mJSONOut = JSONPacker.pack(mAuthCode, EMSG_TYPE.SONGLIST, map);
		mCallback = callback;
	}

	public void vote(int id, int val) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("Id", ((Integer) id).toString());
		map.put("Amt", ((Integer) val).toString());
		mJSONOut = JSONPacker.pack(mAuthCode, EMSG_TYPE.VOTE, map);
		mCallback = null;
	}

	public void submit(int id) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("Id", ((Integer) id).toString());
		mJSONOut = JSONPacker.pack(mAuthCode, EMSG_TYPE.SUBMIT, map);
		mCallback = null;
	}

	@Override
	public void run() {
		boolean retry = true;
		while (retry == true) {
			try {
				mSocket = getSocket();
				if (mSocket == null || !mSocket.isConnected()
						|| mJSONOut == null) {
					return;
				}

				// Send message

				BufferedOutputStream out = new BufferedOutputStream(
						mSocket.getOutputStream());
				String json = mJSONOut.toString();
				Log.d(MainActivity.TAG, json);
				int len = json.length();
				for (int i = 0; i < 4; i++) {
					out.write(len);
					len >>= 8;
				}
				out.write(json.getBytes());
				out.flush();
				if (mCallback == null)
					return; // No response
				// get response
				BufferedInputStream in = new BufferedInputStream(
						mSocket.getInputStream());
				len = in.read() | (in.read() << 8) | (in.read() << 16)
						| (in.read() << 24);
				if (len < 0) {
					throw new IOException("Invalid message length.");
				}
				byte[] buf = new byte[len];
				int cur = 0;
				do {
					cur += in.read(buf, cur, len - cur);
				} while (cur < len);

				JSONObject jsonIn = new JSONObject(new String(buf));
				// Handle response
				handleResponse(jsonIn);
			} catch (IOException e) {
				// retry
				e.printStackTrace();
				break;
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			retry = false;
		}
	}

	private Socket getSocket() throws IOException {
		Socket ret = new Socket(InetAddress.getByName(IPADDR), PORT);
		return ret;
	}

	private SongItem[] getSongList(JSONObject in) throws JSONException {
		JSONArray array = in.getJSONArray("Songs");
		SongItem[] list = new SongItem[array.length()];
		for (int i = 0; i < list.length; i++) {
			JSONObject obj = (JSONObject) array.get(i);
			Log.d(MainActivity.TAG, obj.toString());

			SongItem newSong = new SongItem();
			newSong.setAlbum(obj.getString("Album"));
			newSong.setArtist(obj.getString("Artist"));
			newSong.setTitle(obj.getString("Title"));
			newSong.setID(obj.getInt("Id"));
			list[i] = newSong;
		}
		return list;
	}

	private void handleResponse(JSONObject jsonIn) {
		try {
			String type = jsonIn.getString(JSONPacker.REQUEST_FIELD);
			if (type.equals(EMSG_TYPE.LOGIN.toString())) {
				mAuthCode = jsonIn.getString(JSONPacker.AUTHCODE_FIELD);
				mCallback.loginCallback(mAuthCode);
			} else if (type.equals(EMSG_TYPE.SEARCH.toString())) {
				mCallback.searchCallback(getSongList(jsonIn));
			} else if (type.equals(EMSG_TYPE.SONGLIST.toString())) {
				mCallback.queueCallback(getSongList(jsonIn));
			}
			// TODO: others
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private static final class JSONPacker {
		public static final String REQUEST_FIELD = "Request";
		public static final String AUTHCODE_FIELD = "AuthToken";
		public static final String PARAMS_FIELD = "Params";

		public static final JSONObject pack(String authCode, EMSG_TYPE type,
				Map<String, String> params) {
			JSONObject ret = new JSONObject();
			try {
				ret.put(AUTHCODE_FIELD, authCode);
				ret.put(REQUEST_FIELD, type);
				JSONObject JSONParams = new JSONObject();
				for (Entry<String, String> entry : params.entrySet()) {
					JSONParams.put(entry.getKey(), entry.getValue());
				}
				ret.put(PARAMS_FIELD, JSONParams);

			} catch (JSONException e) {
				e.printStackTrace();
			}
			return ret;
		}
	}

}
