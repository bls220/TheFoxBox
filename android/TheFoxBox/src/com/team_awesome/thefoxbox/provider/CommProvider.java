package com.team_awesome.thefoxbox.provider;

import java.io.IOException;
import java.util.List;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

public class CommProvider extends ContentProvider {
	private static final String AUTH = "com.team_awesome.thefoxbox.provider";
	private static final int VOTE = 1,
	                         SONGLIST = 2,
	                         SEARCH = 3;
	
	
	private static final UriMatcher URIMATCH = new UriMatcher(UriMatcher.NO_MATCH);
	static {
		URIMATCH.addURI(AUTH, "vote/#/#", VOTE);
		URIMATCH.addURI(AUTH, "songlist", SONGLIST);
		URIMATCH.addURI(AUTH, "search/*", SEARCH);
	}
	
	private static final Uri BASE_AUTH = Uri.fromParts("", "", AUTH);
	
	public static Uri makeSearchUri(String term) {
		return BASE_AUTH.buildUpon().appendPath("search").appendPath(term).build();
	}
	
	public static Uri makeVoteUri(int songid, int amt) {
		return BASE_AUTH.buildUpon().
				appendPath("vote").
				appendPath(Integer.toString(songid)).
				appendPath(Integer.toString(amt)).build();
	}
	
	public static Uri getQueueUri() {
		return BASE_AUTH.buildUpon().appendPath("songlist").build();
	}
	
	@Override
	public int delete(Uri arg0, String arg1, String[] arg2) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getType(Uri uri) {
		return null;
	}

	private CommunicatorFactory fact;
	
	@Override
	public boolean onCreate() {
		fact = new CommunicatorFactory("192.168.1.1", 5853);
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		try {
			Communicator comm = fact.get();
			switch (URIMATCH.match(uri)) {
				case SONGLIST:
					return comm.getSongList();
				case SEARCH:
					return comm.search(uri.getPathSegments().get(1));
				case VOTE: {
					List<String> parts = uri.getPathSegments();
					// Both guaranteed to not fail because we already matched them as numbers
					int songid = Integer.parseInt(parts.get(1));
					int amt = Integer.parseInt(parts.get(2));
					return comm.vote(songid, amt);
				}
			}
		}catch(IOException ex) {
			ex.printStackTrace();
		}
		return null;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		throw new UnsupportedOperationException();
	}
}
