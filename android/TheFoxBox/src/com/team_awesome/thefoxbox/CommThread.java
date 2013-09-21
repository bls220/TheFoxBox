/**
 * 
 */
package com.team_awesome.thefoxbox;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

/**
 * @author bsmith
 * 
 */
public class CommThread extends Thread {

	private static final String REQUEST_FIELD = "Request";

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
		SEARCH, VOTE, LOGIN, SUBMIT, MOODCHANGE
	};

	public void login(QueryCallbacks callback, String username)
			throws JSONException {
		JSONObject params = new JSONObject();
		mJSONOut = new JSONObject();
		mJSONOut.put(REQUEST_FIELD, "login");
		params.put("Name", username.toLowerCase().trim());
		mJSONOut.put("Params", params);
		mCallback = callback;
	}

	@Override
	public void run() {
		try {
			mSocket = getSocket();
			if (mSocket == null || !mSocket.isConnected() || mJSONOut == null) {
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
			// get response
			BufferedInputStream in = new BufferedInputStream(
					mSocket.getInputStream());
			len = in.read() | (in.read() << 8) | (in.read() << 16)
					| (in.read() << 24);
			byte[] buf = new byte[len];
			int cur = 0;
			do {
				cur += in.read(buf, cur, len-cur);
			}while(cur < len);

			mJSONOut = new JSONObject(new String(buf));
			// Handle response
			handleResponse();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private Socket getSocket() throws IOException {
		Socket ret = new Socket(InetAddress.getByName("67.194.68.48"), 5853);
		return ret;
	}

	private void handleResponse() {
		try {
			String type = mJSONOut.getString(REQUEST_FIELD);
			if (type.equals(EMSG_TYPE.LOGIN.toString().toLowerCase())) {
				mCallback.loginCallback(mJSONOut.getString("AuthToken"));
			} else if (type.equals(EMSG_TYPE.SUBMIT.toString().toLowerCase())) {
				mCallback.submitCallback(mJSONOut.getString(REQUEST_FIELD));
			}
			// TODO: others
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

}
