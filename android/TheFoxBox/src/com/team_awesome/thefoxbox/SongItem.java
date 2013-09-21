package com.team_awesome.thefoxbox;

public class SongItem {

	private String mAlbum;
	private String mTitle;
	private String mArtist;
	private int mID;
	private EVote mVote;

	public static enum EVote {
		DOWN(-1), NONE(0), UP(1);
		private final int id;

		EVote(int id) {
			this.id = id;
		}
		
		EVote(){
			this.id = 0;
		}

		public int value() {
			return id;
		}
	};

	public SongItem() {
		this("No Album","No Title","No Artist",-1,EVote.NONE);
	}

	/**
	 * @param mAlbum
	 * @param mTitle
	 * @param mArtist
	 */
	public SongItem(String mAlbum, String mTitle, String mArtist,int mID, EVote mVote) {
		super();
		this.mAlbum = mAlbum;
		this.mTitle = mTitle;
		this.mArtist = mArtist;
		this.setID(mID);
		this.mVote = mVote;
	}
	
	/**
	 * @return the mAlbum
	 */
	public String getAlbum() {
		return mAlbum;
	}

	/**
	 * @param mAlbum the mAlbum to set
	 */
	public void setAlbum(String mAlbum) {
		this.mAlbum = mAlbum;
	}

	/**
	 * @return the mTitle
	 */
	public String getTitle() {
		return mTitle;
	}

	/**
	 * @param mTitle the mTitle to set
	 */
	public void setTitle(String mTitle) {
		this.mTitle = mTitle;
	}

	/**
	 * @return the mArtist
	 */
	public String getArtist() {
		return mArtist;
	}

	/**
	 * @param mArtist the mArtist to set
	 */
	public void setArtist(String mArtist) {
		this.mArtist = mArtist;
	}

	/**
	 * @return the mID
	 */
	public int getID() {
		return mID;
	}

	/**
	 * @param mID the mID to set
	 */
	public void setID(int mID) {
		this.mID = mID;
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
