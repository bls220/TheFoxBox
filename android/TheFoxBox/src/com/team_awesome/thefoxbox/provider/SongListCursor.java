package com.team_awesome.thefoxbox.provider;

import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONException;

import android.database.AbstractCursor;
import android.database.Cursor;

public class SongListCursor extends AbstractCursor {
	private final JSONArray data;
	
	private int cur;
	private static final String[] colsToString = new String[]{"Album", "Artist", "Title", "Id"};
	public static final int ALBUM_COL = 0;
	public static final int ARTIST_COL = 1;
	public static final int TITLE_COL = 2;
	public static final int ID_COL = 3;
	
	public SongListCursor(JSONArray songs) throws IOException {
		data = songs;
	}
	
	@Override
	public int getCount() {
		return data.length();
	}

	@Override
	public String getColumnName(int columnIndex) {
		return colsToString[columnIndex];
	}

	@Override
	public String[] getColumnNames() {
		return colsToString.clone();
	}

	@Override
	public int getColumnCount() {
		return colsToString.length;
	}

	@Override
	public String getString(int columnIndex) {
		try {
			return data.getJSONObject(cur).getString(colsToString[columnIndex]);
		} catch (JSONException ex) {
			throw new RuntimeException(ex);
		}
	}

	@Override
	public short getShort(int columnIndex) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int getInt(int columnIndex) {
		if (columnIndex != ID_COL) {
			throw new UnsupportedOperationException();
		}
		
		try {
			return data.getJSONObject(cur).getInt(colsToString[ID_COL]);
		} catch (JSONException ex) {
			throw new RuntimeException(ex);
		}
	}

	@Override
	public long getLong(int columnIndex) {
		throw new UnsupportedOperationException();
	}

	@Override
	public float getFloat(int columnIndex) {
		throw new UnsupportedOperationException();
	}

	@Override
	public double getDouble(int columnIndex) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int getType(int columnIndex) {
		if (columnIndex == ID_COL) {
			return Cursor.FIELD_TYPE_INTEGER;
		}
		return Cursor.FIELD_TYPE_STRING;
	}

	@Override
	public boolean isNull(int columnIndex) {
		return false;
	}

}
