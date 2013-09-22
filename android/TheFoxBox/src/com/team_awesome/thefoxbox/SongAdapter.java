/**
 * 
 */
package com.team_awesome.thefoxbox;

import com.team_awesome.thefoxbox.SongItem.EVote;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author bsmith
 * 
 */
public class SongAdapter extends ArrayAdapter<SongItem> implements OnItemLongClickListener {

	Context context;
	static final int layoutResourceId = R.layout.list_item_song;

	public SongAdapter(Context context) {
		super(context, layoutResourceId);
		this.context = context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		SongHolder holder = null;

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

			row.setTag(holder);
		} else {
			holder = (SongHolder) row.getTag();
		}

		final SongItem song = getItem(position);
		holder.txtTitle.setText(song.getTitle());
		holder.txtArtist.setText(song.getArtist());
		// TODO: holder.imgArt.setImageURI(uri);

		// Set click listeners
		holder.voteGroup
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(RadioGroup group, int checkedId) {
						int val = 0;
						if( checkedId == R.id.radioUp ){
							val += EVote.UP.value();
						}else{
							val += EVote.DOWN.value();
						}
						CommThread comm = new CommThread();
						comm.vote(song.getID(), val);
						comm.start();
					}
				});
		

		return row;
	}

	static class SongHolder {
		ImageView imgArt;
		TextView txtTitle;
		TextView txtArtist;
		RadioGroup voteGroup;
		// TextView txtAlbum;
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
		SongItem song = (SongItem) parent.getItemAtPosition(position);
		//Submit item
		CommThread comm = new CommThread();
		comm.submit(song.getID());
		comm.start();
		Toast.makeText(getContext(), String.format("%s from %s was added to the playlist.", song.getTitle(), song.getArtist()), Toast.LENGTH_SHORT).show();
		return true;
	}
}
