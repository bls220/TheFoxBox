package com.team_awesome.thefoxbox.data;

public enum EVote {
	DOWN(-1), NONE(0), UP(1);
	private final int id;

	EVote(int id) {
		this.id = id;
	}
	
	EVote(){
		this.id = 0;
	}
	
	public int value(){
		return id;
	}

	@Override
	public String toString(){
		return Integer.toString(id);
	}
}