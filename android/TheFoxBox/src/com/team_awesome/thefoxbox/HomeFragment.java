/**
 * 
 */
package com.team_awesome.thefoxbox;

import org.json.JSONException;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * @author bsmith
 * 
 */
public class HomeFragment extends Fragment implements QueryCallbacks {
	
	private static String mAuthCode = "";

	/**
	 * 
	 */
	public HomeFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_nowplaying,
				container, false);

		Log.w(MainActivity.TAG, "Testing shiz");
		CommThread thread = new CommThread();
		try {
			thread.login(this, "Ben");
			thread.start();
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return rootView;
	}

	@Override
	public void loginCallback(String authToken) {
		Log.d(MainActivity.TAG, "AuthCode: " + authToken);
		mAuthCode = authToken;
	}

	@Override
	public void queueCallback(SongItem[] data) {
		// TODO Update now playing
	}

	@Override
	public void searchCallback(SongItem[] results) {
		// DO Nothing here
	}

	@Override
	public void submitCallback(String error) {
		// Do Nothing here
	}
}
