/**
 * 
 */
package com.team_awesome.thefoxbox;

import com.team_awesome.thefoxbox.data.EVote;
import com.team_awesome.thefoxbox.data.SongItem;
import com.team_awesome.thefoxbox.provider.LoaderHelper;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemLongClickListener;

/**
 * @author bsmith
 * 
 */
public class SearchFragment extends Activity implements SongAdapter.ActionCallback, OnItemLongClickListener {

	private ListView mSearchList;
	private SongAdapter mSearchAdapter;

	/**
	 * 
	 */
	public SearchFragment() {}

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_search);
		
		// Get List views
		mSearchList = (ListView) findViewById(R.id.listViewSearch);
		
		mSearchAdapter = new SongAdapter(this, this, false);
		mSearchList.setAdapter(mSearchAdapter);
		mSearchList.setOnItemLongClickListener(this);
		
		// Get the intent, verify the action and get the query
		if (Intent.ACTION_SEARCH.equals(getIntent().getAction())) {
			LoaderHelper.search(songlistCallback, getIntent().getStringExtra(SearchManager.QUERY));
		}
	}
	
	@Override
	public void vote(SongItem song, EVote vote) {
		throw new RuntimeException("This should not have been called!");
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
		LoaderHelper.submit(enqueueCallback, mSearchAdapter.getItem(position).mID);
		
		return true;
	}
	
	LoaderHelper.Callback<String> enqueueCallback = new LoaderHelper.Callback<String>() {
		@Override
		public void done(String ret) {
			Toast.makeText(SearchFragment.this, ret, Toast.LENGTH_SHORT).show();
		}

		@Override
		public void err(Exception ex) {
			Toast.makeText(SearchFragment.this, "Could not enqueue song: " + ex, Toast.LENGTH_LONG).show();
		}
	};
	
	LoaderHelper.Callback<SongItem[]> songlistCallback = new LoaderHelper.Callback<SongItem[]>() {
		@Override
		public void done(SongItem[] ret) {
			mSearchAdapter.setData(ret);
		}

		@Override
		public void err(Exception ex) {
			Toast.makeText(SearchFragment.this, "Could not load songs: " + ex, Toast.LENGTH_LONG).show();
		}
	};
}

