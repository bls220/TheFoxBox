/**
 * 
 */
package com.team_awesome.thefoxbox;

import com.team_awesome.thefoxbox.SongItem.EVote;

import android.app.Activity;
import android.content.Context;
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
	
	protected final Context context;
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
	public SongAdapter(Context context, ActionCallback callback, boolean voteable) {
		this.context = context;
		this.callback = callback;
		this.voteable = voteable;
		
		if (voteable && callback == null) {
			throw new RuntimeException("If you listen for votes you must provide a callback!");
		}
	}

	private SongItem[] cur;
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		SongHolder holder;

		if (row == null) {
			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			row = inflater.inflate(layoutResourceId, parent, false);

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
		if (voteable) {
			// Set the tag so that the handler can figure out which one the user clicked on
			holder.voteGroup.setTag(song);
			
			holder.txtTitle.setText(song.getTitle());
			holder.txtArtist.setText(song.getArtist());
			// TODO: holder.imgArt.setImageURI(uri);
			switch(song.getVote()){
			case DOWN:
				holder.voteDown.setChecked(true);
				break;
			case NONE:
				holder.voteGroup.clearCheck();
				break;
			case UP:
				holder.voteUp.setChecked(true);
				break;
			default:
				break;
			
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
			callback.vote(((SongItem)group.getTag()), checkedId == R.id.radioUp ?  EVote.UP : EVote.DOWN);
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
