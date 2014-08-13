package com.rodev.push2jump.score;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Base64Coder;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter.OutputType;
import com.rodev.push2jump.LevelManager;

public class ScoreManager {

	private Json json;
	private FileHandle fileHandlePath;
	private int amountLevels;
	
	/**
	 * The ScoreManager will keep track of scores and is able to write new highscores.
	 * De scores are encoded, so that the user cannot easily edit the highscores.
	 * 
	 */
	public ScoreManager(){
		this.amountLevels = LevelManager.AmountLevels;
		json = new Json(OutputType.json);
		fileHandlePath = Gdx.files.local("data/highscores.json");
		if(!fileHandlePath.exists()) createHighScoreFile();	
	}
	
	/**
	 * The highscore.json will be generated, based on the amount of levels
	 */
	public void createHighScoreFile(){
		ArrayList<Score> scores = new ArrayList<Score>();
		for(int i = 1 ; i < amountLevels + 1 ; i++){
			Score score = new Score(i, false, 0);
			scores.add(score);
		}
		fileHandlePath.writeString(Base64Coder.encodeString(json.prettyPrint(scores)), true);
	}
	
	/**
	 * The new score will only be added when it is better than the previous/known score.
	 * The highscores.json file will be read and completely rewritten 
	 * 
	 * @param score The score which will be added to the highscores.json file
	 */
	public void addNewScore(Score score){	
		String s = Base64Coder.decodeString(json.fromJson(String.class, fileHandlePath));
		@SuppressWarnings("unchecked")
		ArrayList<Score> scores = json.fromJson(ArrayList.class, s);
		ArrayList<Score> newScores = new ArrayList<Score>();
		
		for(int i = 0 ; i < amountLevels ; i++){
			Score currentScore = scores.get(i);
			if(currentScore.getLevel() == score.getLevel()){
				if(score.isLevelCompleted() && !currentScore.isLevelCompleted()){
					currentScore.setLevelCompleted(true);
					currentScore.setScore(score.getScore());
				}
				else if((score.isLevelCompleted() && currentScore.isLevelCompleted()) || !score.isLevelCompleted() && !currentScore.isLevelCompleted()){
					if(score.getScore() < currentScore.getScore()){
						currentScore.setScore(score.getScore());
					}
				}
			}
			newScores.add(currentScore);
		}	
		fileHandlePath.writeString(Base64Coder.encodeString(json.prettyPrint(newScores)), false);
	}
	
	public ArrayList<Integer> getScores(){
		String s = Base64Coder.decodeString(json.fromJson(String.class, fileHandlePath));
		@SuppressWarnings("unchecked")
		ArrayList<Score> scores = json.fromJson(ArrayList.class, s);
		ArrayList<Integer> getScores = new ArrayList<Integer>();
		for(int i = 0 ; i < amountLevels ; i++){
			getScores.add(scores.get(i).getScore());
		}
		return getScores;		
	}
	
	@SuppressWarnings("unchecked")
	public ArrayList<Score> getAllScores(){
		String s = Base64Coder.decodeString(json.fromJson(String.class, fileHandlePath));
		return json.fromJson(ArrayList.class, s);
	}
}
