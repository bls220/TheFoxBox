/**
 *
 */
package com.team_awesome.thefoxbox;

import android.widget.TextView;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

/**
 * @author bsmith
 *
 */
public class HomeFragment extends Fragment {
	private Button btnLogin;
	private ProgressBar loginBar;
	private EditText txtUser;

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

		/*
		 * adapterNowPlaying = new SongAdapter(getActivity());
		   ListView nowList = ((ListView) rootView.findViewById(R.id.listViewNowPlaying));
		   nowList.setAdapter(adapterNowPlaying);
		   nowList.setOnItemLongClickListener((OnItemLongClickListener) adapterNowPlaying);
        */

		btnLogin = (Button) rootView.findViewById(R.id.btnLogin);
		loginBar = (ProgressBar) rootView.findViewById(R.id.loginBar);
		txtUser = (EditText) rootView.findViewById(R.id.txtUser);

		btnLogin.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.w(MainActivity.TAG, "Testing shiz");
				btnLogin.setVisibility(View.GONE);
				txtUser.setVisibility(View.GONE);
				loginBar.setVisibility(View.VISIBLE);
				
				// TODO: Init the LoaderHelper with this info.
				loginBar.setVisibility(View.GONE);
			}
		});

		return rootView;
	}
	
	public void setNowPlaying(SongItem s) {
		View v = getView();
		((TextView) v.findViewById(R.id.npTitle)).setText(s.getTitle());
		((TextView) v.findViewById(R.id.npArtist)).setText(s.getArtist());
	}
}
