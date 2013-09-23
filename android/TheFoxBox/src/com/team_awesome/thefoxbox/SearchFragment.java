/**
 * 
 */
package com.team_awesome.thefoxbox;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
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
public class SearchFragment extends Activity implements QueryCallbacks {

	private ListView mSearchList;
	private SongAdapter mSearchAdapter;

	/**
	 * 
	 */
	public SearchFragment() {
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_search);

		// Get List views
		mSearchList = (ListView) findViewById(R.id.listViewSearch);
		
		mSearchAdapter = new SongAdapter(this);
		mSearchList.setAdapter(mSearchAdapter);
		mSearchList.setOnItemLongClickListener(mSearchAdapter);
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		setIntent(intent);
		// Get the intent, verify the action and get the query
		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			String query = intent.getStringExtra(SearchManager.QUERY);
			CommThread comms = new CommThread();
			comms.search(this, query);
			comms.start();
		}

	}
	
	public void doSearch(final SongItem[] songs){
		runOnUiThread(new Runnable(){
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

	@Override
	public void loginCallback(String authToken) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void queueCallback(SongItem[] data) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void searchCallback(SongItem[] results) {
		// TODO Auto-generated method stub
		
	}
}

