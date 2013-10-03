/**
 * 
 */
package com.team_awesome.thefoxbox;

import java.util.HashMap;

import com.team_awesome.thefoxbox.data.EVote;
import com.team_awesome.thefoxbox.data.SongItem;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

/**
 * @author bsmith
 * 
 */
public class SongAdapter extends BaseAdapter implements OnCheckedChangeListener {
	interface ActionCallback {
		void vote(SongItem song, EVote vote);
	}
	
	protected final LayoutInflater flate;
	protected final ActionCallback callback;
	protected final boolean voteable;
	static final int layoutResourceId = R.layout.list_item_song;

	/**
	 * 
	 * @param context
	 * @param callback
	 * @param voteable If true the list of songs will have voting buttons. When these buttons are
	 *        clicked the {@link ActionCallback#vote(SongItem, EVote)} method is called. Else there will be no buttons nor
	 *        callback.
	 */
	public SongAdapter(Activity context, ActionCallback callback, boolean voteable) {
		this.flate = context.getLayoutInflater();
		this.callback = callback;
		this.voteable = voteable;
		
		if (voteable && callback == null) {
			throw new RuntimeException("If you listen for votes you must provide a callback!");
		}
	}

	private SongItem[] cur;
	// TODO: Make sure to clear this when a mood changes
	private final HashMap<SongItem, Boolean> checkedMap = new HashMap<SongItem, Boolean>();
	
	public void clearCheckedCache() {
		checkedMap.clear();
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		SongHolder holder;

		if (row == null) {
			row = flate.inflate(layoutResourceId, parent, false);

			holder = new SongHolder();
			holder.imgArt = (ImageView) row
					.findViewById(R.id.imageViewAlbumArt);
			holder.txtTitle = (TextView) row
					.findViewById(R.id.textViewSongTitle);
			holder.txtArtist = (TextView) row
					.findViewById(R.id.textViewSongArtist);
			holder.voteGroup = (RadioGroup) row
					.findViewById(R.id.songVoteGroup);
			holder.voteUp = (RadioButton) row.findViewById(R.id.radioUp);
			holder.voteDown = (RadioButton) row.findViewById(R.id.radioDown);

			row.setTag(holder);
		} else {
			holder = (SongHolder) row.getTag();
		}
		
		holder.voteGroup.setOnCheckedChangeListener(null); //Don't report changes during setup

		SongItem song = getItem(position);
		holder.txtTitle.setText(song.mTitle);
		holder.txtArtist.setText(song.mArtist);
		// TODO: holder.imgArt.setImageURI(uri);
		
		if (voteable) {
			// Set the tag so that the handler can figure out which one the user clicked on
			holder.voteGroup.setTag(song);
			
			Boolean ch = checkedMap.get(song);
			if (ch == null) {
				holder.voteGroup.clearCheck();
			} else if (ch) {
				holder.voteUp.setChecked(true);
			} else {
				holder.voteDown.setChecked(true);
			}
	
			// Set click listeners
			holder.voteGroup.setOnCheckedChangeListener(this);
			holder.voteGroup.setVisibility(View.VISIBLE);
		} else {
			holder.voteGroup.setVisibility(View.GONE);
		}
		
		/*
		row.findViewById(R.id.btnSubmit).setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				CommThread comm = new CommThread();
				comm.submit(song.getID());
				comm.start();
				Toast.makeText(context, String.format("%s from %s was added to the playlist.", song.getTitle(), song.getArtist()), Toast.LENGTH_SHORT).show();
			}
			
		});*/
		

		return row;
	}

	static class SongHolder {
		ImageView imgArt;
		TextView txtTitle;
		TextView txtArtist;
		RadioGroup voteGroup;
		RadioButton voteUp;
		RadioButton voteDown;
		// TextView txtAlbum;
	}

	
	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		if (voteable) {
			SongItem it = (SongItem)group.getTag();
			boolean vote = checkedId == R.id.radioUp;
			callback.vote(it, vote ?  EVote.UP : EVote.DOWN);
			checkedMap.put(it, vote);
		}
	}

	@Override
	public int getCount() {
		return cur == null ? 0 : cur.length;
	}

	@Override
	public SongItem getItem(int position) {
		return cur[position];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	public void setData(SongItem[] c){
		cur = c;
		notifyDataSetChanged();
	}
}
