/**
 * 
 */
package com.team_awesome.thefoxbox;

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
    int layoutResourceId;    
    SongItem data[] = null;
    
    public SongAdapter(Context context, int layoutResourceId, SongItem[] data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
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
        
        SongItem song = data[position];
        holder.txtTitle.setText(song.getTitle());
        holder.txtArtist.setText(song.getArtist());
        //TODO: holder.imgArt.setImageURI(uri);
        
        return row;
    }
    
    static class SongHolder
    {
        ImageView imgArt;
        TextView txtTitle;
        TextView txtArtist;
        //TextView txtAlbum;
    }
}
