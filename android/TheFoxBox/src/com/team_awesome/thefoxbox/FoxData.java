package com.team_awesome.thefoxbox;


public class FoxData {
	
	private SongItem[] mSongs;
	private static String mAuthToken = "";
	
	/**
	 * @param mRequest
	 * @param mAuthToken
	 * @param mData
	 */
	public FoxData(String mAuthToken, String data ) {
		super();
		this.setAuthToken(mAuthToken);
	}

	public String getAuthToken() {
		return mAuthToken;
	}

	public void setAuthToken(String mAuthToken) {
		this.mAuthToken = mAuthToken;
	}

}
