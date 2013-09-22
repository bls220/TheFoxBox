/**
 * 
 */
package com.team_awesome.thefoxbox;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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

		adapterNowPlaying = new SongAdapter(getActivity());
		((ListView) rootView.findViewById(R.id.listViewNowPlaying))
				.setAdapter(adapterNowPlaying);

		return rootView;
	}

	@Override
	public void loginCallback(String authToken) {
	}

	@Override
	public void queueCallback(SongItem[] data) {
		final SongItem[] data2 = data.clone();
		Activity act = getActivity();
		if (act == null)
			return;
		act.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				// Update now playing
				adapterNowPlaying.clear();
				 if (data2.length > 0)
				 adapterNowPlaying.add(data2[0]);
			}
		});

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
