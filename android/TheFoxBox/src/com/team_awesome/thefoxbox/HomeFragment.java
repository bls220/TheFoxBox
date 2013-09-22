/**
 *
 */
package com.team_awesome.thefoxbox;

import android.widget.TextView;
import org.json.JSONException;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;

/**
 * @author bsmith
 *
 */
public class HomeFragment extends Fragment implements QueryCallbacks {

	private SongAdapter adapterNowPlaying;

	private Button btnLogin;
	private ProgressBar loginBar;
	private EditText txtUser;

	public static boolean loggedIn = false;

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
				CommThread thread = new CommThread();
				btnLogin.setVisibility(View.GONE);
				txtUser.setVisibility(View.GONE);
				loginBar.setVisibility(View.VISIBLE);

				try {
					thread.login(HomeFragment.this, txtUser.getText()
							.toString());
					thread.start();
				} catch (JSONException e) {
					e.printStackTrace();
				}

			}
		});

		return rootView;
	}

	@Override
	public void loginCallback(String authToken) {
		Log.d(MainActivity.TAG, "AuthCode: " + authToken);
		loggedIn = true;
		getActivity().runOnUiThread(new Runnable(){
			@Override
			public void run() {
				btnLogin.setVisibility(View.GONE);
				txtUser.setVisibility(View.GONE);
				loginBar.setVisibility(View.GONE);
			}
		});
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
				// adapterNowPlaying.clear();
				 if (data2.length > 0)
				 // adapterNowPlaying.add(data2[0]);
				 {
				     TextView tv = ((TextView) getView().findViewById(R.id.npTitle));
				     tv.setText(data2[0].getTitle());
				     tv = ((TextView) getView().findViewById(R.id.npArtist));
				     tv.setText(data2[0].getArtist());
				 }
			}
		});

	}

	@Override
	public void searchCallback(SongItem[] results) {
		// DO Nothing here
	}
}
