package com.team_awesome.thefoxbox.data;

public final class SongItem {
	public final String mAlbum;
	public final String mTitle;
	public final String mArtist;
	public final int mID;

	/**
	 * @param mAlbum
	 * @param mTitle
	 * @param mArtist
	 */
	public SongItem(String album, String title, String artist, int id) {
		this.mAlbum = album;
		this.mTitle = title;
		this.mArtist = artist;
		this.mID = id;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((mAlbum == null) ? 0 : mAlbum.hashCode());
		result = prime * result + ((mArtist == null) ? 0 : mArtist.hashCode());
		result = prime * result + mID;
		result = prime * result + ((mTitle == null) ? 0 : mTitle.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SongItem other = (SongItem) obj;
		if (mAlbum == null) {
			if (other.mAlbum != null)
				return false;
		} else if (!mAlbum.equals(other.mAlbum))
			return false;
		if (mArtist == null) {
			if (other.mArtist != null)
				return false;
		} else if (!mArtist.equals(other.mArtist))
			return false;
		if (mID != other.mID)
			return false;
		if (mTitle == null) {
			if (other.mTitle != null)
				return false;
		} else if (!mTitle.equals(other.mTitle))
			return false;
		return true;
	}
}
