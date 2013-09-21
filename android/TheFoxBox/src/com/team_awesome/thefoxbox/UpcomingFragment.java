/**
 * 
 */
package com.team_awesome.thefoxbox;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * @author bsmith
 *
 */
public class UpcomingFragment extends Fragment {

	/**
	 * 
	 */
	public UpcomingFragment() {	}
	
	 @Override
     public View onCreateView(LayoutInflater inflater, ViewGroup container,
             Bundle savedInstanceState) {
		 View rootView = inflater.inflate(R.layout.fragment_upcoming, container, false);
         return rootView;
	 }

}
