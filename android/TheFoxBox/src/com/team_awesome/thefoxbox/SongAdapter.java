/**
 * 
 */
package com.team_awesome.thefoxbox;

import com.team_awesome.thefoxbox.SongItem.EVote;
import com.team_awesome.thefoxbox.provider.SongListCursor;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
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
public class SongAdapter extends BaseAdapter implements OnItemLongClickListener {
	protected final Context context;
	static final int layoutResourceId = R.layout.list_item_song;

	public SongAdapter(Context context) {
		this.context = context;
	}

	private Cursor cur;
	
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
			holder.voteUp = (RadioButton) row.findViewById(R.id.radioUp);
			holder.voteDown = (RadioButton) row.findViewById(R.id.radioDown);

			row.setTag(holder);
		} else {
			holder = (SongHolder) row.getTag();
		}
		
		holder.voteGroup.setOnCheckedChangeListener(null); //Don't report changes during setup

		
		final SongItem song = (SongItem) getItem(position);
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
		
		row.findViewById(R.id.btnSubmit).setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				CommThread comm = new CommThread();
				comm.submit(song.getID());
				comm.start();
				Toast.makeText(context, String.format("%s from %s was added to the playlist.", song.getTitle(), song.getArtist()), Toast.LENGTH_SHORT).show();
			}
			
		});
		

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
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
		SongItem song = (SongItem) parent.getItemAtPosition(position);
		//Submit item
		CommThread comm = new CommThread();
		comm.submit(song.getID());
		comm.start();
		Toast.makeText(context, String.format("%s from %s was added to the playlist.", song.getTitle(), song.getArtist()), Toast.LENGTH_SHORT).show();
		return true;
	}

	@Override
	public int getCount() {
		if (cur == null) {
			return 0;
		}
		
		return cur.getCount();
	}

	@Override
	public Object getItem(int position) {
		cur.move(position);
		return new SongItem(cur.getString(SongListCursor.ALBUM_COL),
				cur.getString(SongListCursor.ARTIST_COL), 
				cur.getString(SongListCursor.TITLE_COL), 
				cur.getInt(SongListCursor.ID_COL),
				EVote.NONE);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	public void setCursor(Cursor c){
		if (cur != null){
			cur.close();
		}
		
		cur = c;
		notifyDataSetChanged();
	}
}
