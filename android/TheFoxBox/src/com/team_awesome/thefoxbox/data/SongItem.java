package com.team_awesome.thefoxbox.data;

public class SongItem {
	public final String mAlbum;
	public final String mTitle;
	public final String mArtist;
	public final int mID;
	private EVote mVote;

	public SongItem() {
		this("No Album","No Title","No Artist",-1,EVote.NONE);
	}

	/**
	 * @param mAlbum
	 * @param mTitle
	 * @param mArtist
	 */
	public SongItem(String album, String title, String artist, int id, EVote vote) {
		this.mAlbum = album;
		this.mTitle = title;
		this.mArtist = artist;
		this.mID = id;
		this.mVote = vote;
	}

	/**
	 * @return the mVote
	 */
	public EVote getVote() {
		return mVote;
	}

	/**
	 * @param mVote the mVote to set
	 */
	public void setVote(EVote mVote) {
		this.mVote = mVote;
	}

}
