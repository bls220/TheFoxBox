/**
 * 
 */
package com.team_awesome.thefoxbox;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * @author bsmith
 *
 */
public class SongAdapter extends ArrayAdapter<SongItem>{

    Context context; 
    static final int layoutResourceId = R.layout.list_item_song;    
    ArrayList<SongItem> data = null;
    
    public SongAdapter(Context context) {
    	this(context,new SongItem[0]);
    }
    
    public SongAdapter(Context context, SongItem[] data) {
        super(context, layoutResourceId, data);
        this.context = context;
        
        this.data = new ArrayList<SongItem>();
        for( SongItem song : data){
        	this.data.add(song);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        SongHolder holder = null;
        
        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            
            holder = new SongHolder();
            holder.imgArt = (ImageView)row.findViewById(R.id.imageViewAlbumArt);
            holder.txtTitle = (TextView)row.findViewById(R.id.textViewSongTitle);
            holder.txtArtist = (TextView)row.findViewById(R.id.textViewSongArtist);
            
            row.setTag(holder);
        }
        else
        {
            holder = (SongHolder)row.getTag();
        }
        
        SongItem song = data.get(position);
        holder.txtTitle.setText(song.getTitle());
        holder.txtArtist.setText(song.getArtist());
        //TODO: holder.imgArt.setImageURI(uri);
        
        return row;
    }
    
    public void clear(){
    	data.clear();
    	notifyDataSetChanged();
    }
    
    static class SongHolder
    {
        ImageView imgArt;
        TextView txtTitle;
        TextView txtArtist;
        //TextView txtAlbum;
    }
}
