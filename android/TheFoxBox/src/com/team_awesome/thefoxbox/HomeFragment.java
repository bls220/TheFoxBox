/**
 *
 */
package com.team_awesome.thefoxbox;

import com.team_awesome.thefoxbox.data.SongItem;

import android.widget.TextView;

import android.support.v4.app.Fragment;
import android.view.View;
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
	
	public void setNowPlaying(SongItem s) {
		View v = getView();
		((TextView) v.findViewById(R.id.npTitle)).setText(s.mTitle);
		((TextView) v.findViewById(R.id.npArtist)).setText(s.mArtist);
	}
}
