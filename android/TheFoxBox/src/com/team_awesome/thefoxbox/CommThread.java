/**
 * 
 */
package com.team_awesome.thefoxbox;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.SocketException;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author bsmith
 * 
 */
public class CommThread extends Thread {

	private static Socket mSocket;

	private JSONObject mJSONOut;
	private EMSG_TYPE mQueryType;
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

	/**
	 * @throws SocketException
	 * 
	 */
	public CommThread(Socket socket) throws SocketException {
		if (!socket.isConnected()) {
			throw new SocketException("Socket not connected.");
		}
		mSocket = socket;
	}

	public void login(QueryCallbacks callback, String username)
			throws JSONException {
		mJSONOut = new JSONObject();
		mJSONOut.accumulate("Request", "login");
		mJSONOut.accumulate("Name", username.toLowerCase().trim());
		mCallback = callback;
	}

	@Override
	public void run() {
		if (mSocket == null || !mSocket.isConnected() || mJSONOut == null ) {
			return;
		}
		
		//Send message
		try {
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(mSocket.getOutputStream()));
			String json = mJSONOut.toString();
			out.write(json.length());
			out.write(json);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//get response
		
		//parse message
		//call callbacks
	}

}
