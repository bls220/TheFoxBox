package com.team_awesome.thefoxbox;

import java.util.Arrays;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.widget.SearchView;
import android.widget.Toast;

import com.team_awesome.thefoxbox.provider.LoaderHelper;

public class MainActivity extends FragmentActivity implements LoaderHelper.Callback<SongItem[]> {

	static final String TAG = "TheFoxBox";

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
	 * will keep every loaded fragment in memory. If this becomes too memory
	 * intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;
	

	private HomeFragment mHomeFrag;
	private UpcomingFragment mUpcomingFrag;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);
		
		schedUpdate(0);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);

		// Get the SearchView and set the searchable configuration
		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		SearchView searchView = (SearchView) menu.findItem(R.id.action_search)
				.getActionView();
		// Assumes current activity is the searchable activity
		searchView.setSearchableInfo(searchManager
				.getSearchableInfo(getComponentName()));

		return true;
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page.
			Fragment fragment = null;
			switch (position) {
			case 0: // Now Playing
				mHomeFrag = new HomeFragment();
				fragment = mHomeFrag;
				break;
			case 1: // Upcoming
				mUpcomingFrag = new UpcomingFragment();
				fragment = mUpcomingFrag;
				break;
			default:
				Log.e(TAG, "Fragment created out of bounds.");
				throw new IndexOutOfBoundsException("Page Viewer doesn't hold "
						+ (position + 1) + " fragments.");
			}
			return fragment;
		}

		@Override
		public int getCount() {
			// Show 2 total pages.
			return 2;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return getString(R.string.title_section_home).toUpperCase(l);
			case 1:
				return getString(R.string.title_section_queue).toUpperCase(l);
			}
			return null;
		}
	}

	private void schedUpdate(int delay) {
		if (delay == 0) {
			LoaderHelper.getSongList(this);
		} else {
			new Timer().schedule(new TimerTask() {
				@Override
				public void run() {
					LoaderHelper.getSongList(MainActivity.this);
				}
			}, delay);
		}
	}
	
	@Override
	public void done(SongItem[] ret) {
		if (ret.length == 0) {
			Log.w(TAG, "Weird result: Length of songlist is 0");
		} else {
			if (mHomeFrag != null) {
				mHomeFrag.setNowPlaying(ret[0]);
			}
			if (mUpcomingFrag != null && ret.length > 1) {
				ret = Arrays.copyOfRange(ret, 1, ret.length);
				mUpcomingFrag.setUpcoming(ret);
			}
		}
		
		// Update again in 5 seconds
		schedUpdate(5000);
	}

	@Override
	public void err(Exception ex) {
		Toast.makeText(this, "Could not load song queue: " + ex, Toast.LENGTH_LONG).show();
	}
}
