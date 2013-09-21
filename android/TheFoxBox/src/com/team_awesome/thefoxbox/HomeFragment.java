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
import android.widget.ListView;

/**
 * @author bsmith
 * 
 */
public class HomeFragment extends Fragment implements QueryCallbacks {

	private SongAdapter adapterNowPlaying;

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

		adapterNowPlaying = new SongAdapter(getActivity(),
				new SongItem[] { new SongItem() });
		((ListView) rootView.findViewById(R.id.listViewNowPlaying))
				.setAdapter(adapterNowPlaying);

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
	}

	@Override
	public void queueCallback(SongItem[] data) {
		// Update now playing
		adapterNowPlaying.clear();
		if (data.length > 0)
			adapterNowPlaying.add(data[0]);
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
