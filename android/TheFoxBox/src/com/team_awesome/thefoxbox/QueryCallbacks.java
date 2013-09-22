package com.team_awesome.thefoxbox;

public interface QueryCallbacks {
	public void loginCallback(String authToken);
	public void queueCallback(SongItem[] data);
	public void searchCallback(SongItem[] results);
}
