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
	private static enum EMSG_TYPE {
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
	
	private static final String REQUEST_FIELD = "Request";
	private static final String AUTHCODE_FIELD = "AuthToken";
	private static final String PARAMS_FIELD = "Params";
	
	public static JSONObject songlist(AuthToken toke) {
		JSONObject ret = new JSONObject();
		try {
			ret.put(AUTHCODE_FIELD, toke);
			ret.put(REQUEST_FIELD, EMSG_TYPE.SONGLIST);
			ret.put(PARAMS_FIELD, new JSONObject());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return ret;
	}
	
	public static JSONObject login(String username) {
		JSONObject ret = new JSONObject();
		try {
			ret.put(REQUEST_FIELD, EMSG_TYPE.LOGIN);
			
			JSONObject JSONParams = new JSONObject();
			JSONParams.put("Name", username);
			
			ret.put(PARAMS_FIELD, JSONParams);

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return ret;
	}
	
	public static AuthToken getAuthToken(JSONObject ret) throws IOException {
		try {
			return new AuthToken(ret.getString(AUTHCODE_FIELD));
		} catch(JSONException ex) {
			throw new IOException(ex);
		}
	}
	
	public static JSONObject vote(AuthToken toke, int songid, int amt) {
		JSONObject ret = new JSONObject();
		try {
			ret.put(AUTHCODE_FIELD, toke);
			ret.put(REQUEST_FIELD, EMSG_TYPE.VOTE);
			
			JSONObject JSONParams = new JSONObject();
			JSONParams.put("Id", Integer.toString(songid));
			JSONParams.put("Amt", Integer.toString(amt));
			ret.put(PARAMS_FIELD, JSONParams);

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return ret;
	}

	public static JSONArray getSongList(JSONObject obj) throws IOException {
		try {
			return obj.getJSONArray("Songs");
		} catch (JSONException ex) {
			throw new IOException(ex);
		}
	}

	public static JSONObject search(AuthToken toke, String term) {
		JSONObject ret = new JSONObject();
		try {
			ret.put(AUTHCODE_FIELD, toke);
			ret.put(REQUEST_FIELD, EMSG_TYPE.VOTE);
			
			JSONObject JSONParams = new JSONObject();
			JSONParams.put("Term", term);
			ret.put(PARAMS_FIELD, JSONParams);

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return ret;
	}
	
}
