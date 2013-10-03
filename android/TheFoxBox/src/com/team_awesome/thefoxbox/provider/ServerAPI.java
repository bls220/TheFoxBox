package com.team_awesome.thefoxbox.provider;

import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.team_awesome.thefoxbox.provider.Communicator.AuthToken;

/**
 * A class that encapsulates the API for communicating with the music server.
 * 
 * Each method takes in the specific params and puts out a JSONObject which should
 * be sent to the server
 * 
 * @author Kevin
 *
 */
class ServerAPI {
	private static final String REQUEST_FIELD = "Request";
	private static final String AUTHCODE_FIELD = "AuthToken";
	private static final String PARAMS_FIELD = "Params";
	
	public static JSONArray getSongList(JSONObject obj) throws IOException {
		try {
			return obj.getJSONArray("Songs");
		} catch (JSONException ex) {
			throw new IOException(ex);
		}
	}
	
	public static AuthToken getAuthToken(JSONObject ret) throws IOException {
		try {
			return new AuthToken(ret.getString(AUTHCODE_FIELD));
		} catch(JSONException ex) {
			throw new IOException(ex);
		}
	}
	
	public static JSONObject songlist(AuthToken toke) {
		return wrap(toke, MsgType.SONGLIST);
	}
	
	public static JSONObject login(String username) {
		return wrap(null, MsgType.LOGIN, "Name", username);
	}
	
	public static JSONObject vote(AuthToken toke, int songid, int amt) {
		return wrap(toke, MsgType.VOTE, "Id", songid, "Amt", amt);
	}

	public static JSONObject search(AuthToken toke, String term) {
		return wrap(toke, MsgType.SEARCH, "Term", term);
	}

	public static JSONObject submit(AuthToken toke, int songid) {
		return wrap(toke, MsgType.SUBMIT, "Id", songid);
	}
	
	private static JSONObject wrap(AuthToken toke, MsgType type, Object...params) {
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
				jp.put((String)params[i], params[i+1].toString());
			}
			ret.put(PARAMS_FIELD, jp);
		}catch (JSONException ex) {
			ex.printStackTrace();
		}
		
		return ret;
	}
}






