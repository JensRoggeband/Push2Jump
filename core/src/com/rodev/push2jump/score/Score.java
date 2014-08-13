package com.rodev.push2jump.score;


public class Score{
	
	private int level;
	private boolean completedLevel = false;
	private int score;	
	
	public Score(int level, boolean completedLevel, int score){
		this.level = level;
		this.completedLevel = completedLevel;
		this.score = score;
	}
	
	public Score(){
	}
	
	public boolean isLevelCompleted(){
		return completedLevel;
	}
	
	public void setLevelCompleted(boolean bool){
		this.completedLevel = bool;
	}
	
	public void setScore(int score){
		this.score = score;
	}
	
	public int getScore(){
		if(!completedLevel) return 999;
		return score;
	}
	
	public void setLevel(int level){
		this.level = level;
	}
	
	public int getLevel(){
		return level;
	}
}
