package com.team_awesome.thefoxbox.provider;

public enum MsgType {
	SEARCH("search"), VOTE("vote"), LOGIN("login"), SUBMIT("submit"), MOODCHANGE(
			"moodchange"), SONGLIST("songlist");
	private String val;

	MsgType(String value) {
		val = value;
	}

	public String toString() {
		return val;
	}
}