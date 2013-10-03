/**
 * 
 */
package com.team_awesome.thefoxbox;

import com.team_awesome.thefoxbox.SongItem.EVote;
import com.team_awesome.thefoxbox.provider.LoaderHelper;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.Toast;

/**
 * @author bsmith
 * 
 */
public class UpcomingFragment extends Fragment implements SongAdapter.ActionCallback, OnItemLongClickListener, LoaderHelper.Callback<String> {
	private SongAdapter queue, suggestions;

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
		ListView lq = (ListView) rootView.findViewById(R.id.listViewQueue);
		queue = new SongAdapter(getActivity(), this, true);
		lq.setAdapter(queue);
		ListView sq = (ListView) rootView.findViewById(R.id.listViewSuggestion);
		suggestions = new SongAdapter(getActivity(), this, false);
		sq.setOnItemLongClickListener(this);
		
		return rootView;
	}

	public void setUpcoming(SongItem[] data) {
		queue.setData(data);
	}

	@Override
	public void vote(SongItem song, EVote vote) {
		LoaderHelper.vote(song.getID(), vote.value());
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
		SongItem song = suggestions.getItem(position);
		LoaderHelper.submit(this, song.getID());
		
		return true;
	}

	@Override
	public void done(String ret) {
		if (ret == null) {
			ret = "Song submitted!";
		}
		Toast.makeText(getActivity(), ret, Toast.LENGTH_LONG).show();
	}

	@Override
	public void err(Exception ex) {
		Toast.makeText(getActivity(), "Error submitting song: " + ex, Toast.LENGTH_LONG).show();
	}
}









