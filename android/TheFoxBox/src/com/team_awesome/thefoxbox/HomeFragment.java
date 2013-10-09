/**
 *
 */
package com.team_awesome.thefoxbox;

import com.team_awesome.thefoxbox.data.SongItem;

import android.widget.TextView;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
/**
 * @author bsmith
 *
 */
public class HomeFragment extends Fragment {
	/**
	 *
	 */
	public HomeFragment() {
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_nowplaying, container, false);
		
		return rootView;
	}
	
	public void setNowPlaying(SongItem s) {
		View v = getView();
		((TextView) v.findViewById(R.id.npTitle)).setText(s.mTitle);
		((TextView) v.findViewById(R.id.npArtist)).setText(s.mArtist);
	}
}
