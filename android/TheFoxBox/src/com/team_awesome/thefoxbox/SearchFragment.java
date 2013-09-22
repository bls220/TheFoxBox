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
public class SearchFragment extends Fragment {

	private ListView mSearchList;
	private SongAdapter mSearchAdapter;

	/**
	 * 
	 */
	public SearchFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_search, container,
				false);

		// Get List views
		mSearchList = (ListView) rootView.findViewById(R.id.listViewSearch);
		
		mSearchAdapter = new SongAdapter(getActivity());
		mSearchList.setAdapter(mSearchAdapter);
		mSearchList.setOnItemLongClickListener(mSearchAdapter);

		return rootView;
	}
	
	public void doSearch(final SongItem[] songs){
		getActivity().runOnUiThread(new Runnable(){
			@Override
			public void run() {
				mSearchAdapter.clear();
				for( SongItem song: songs){
					mSearchAdapter.add(song);
				}
				mSearchAdapter.notifyDataSetChanged();
			}
			
		});
	}
}

