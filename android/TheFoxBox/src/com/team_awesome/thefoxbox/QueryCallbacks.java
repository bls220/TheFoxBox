package com.team_awesome.thefoxbox;

public interface QueryCallbacks {
	public void loginCallback(String authToken);
	public void queueCallback(FoxData data);
	public void searchCallback(String[] results);
}
