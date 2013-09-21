/**
 * 
 */
package com.team_awesome.thefoxbox;

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
public class UpcomingFragment extends Fragment implements QueryCallbacks{

	private ListView mQueueList;
	private ListView mSuggestionList;

	/**
	 * 
	 */
	public UpcomingFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_upcoming, container,
				false);

		// Get List views
		mQueueList = (ListView) rootView.findViewById(R.id.listViewQueue);
		mSuggestionList = (ListView) rootView
				.findViewById(R.id.listViewSuggestion);

		// Set song adapters
		SongItem[] songList = new SongItem[23];
		for (int i = 0; i < songList.length; i++) {
			songList[i] = new SongItem();
		}

		mQueueList.setAdapter(new SongAdapter(getActivity()));
		mSuggestionList.setAdapter(new SongAdapter(getActivity()));

		return rootView;
	}

	@Override
	public void loginCallback(String authToken) {
		// Do NOthing
	}

	@Override
	public void queueCallback(SongItem[] data) {
		// TODO Update queue list
		
	}

	@Override
	public void searchCallback(SongItem[] results) {
		// Do Nothing
	}

	@Override
	public void submitCallback(String error) {
		// TODO Check for errors
		
	}

}
