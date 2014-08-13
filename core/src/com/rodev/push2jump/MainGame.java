package com.rodev.push2jump;

import java.util.ArrayList;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.rodev.push2jump.score.Score;
import com.rodev.push2jump.score.ScoreManager;
import com.rodev.push2jump.screen.MenuScreen;
import com.rodev.push2jump.screen.ScreenManager;

public class MainGame extends ApplicationAdapter {
	private SpriteBatch batch;
	private ScreenManager sm;
	public static int WIDTH = 800, HEIGHT = 480;
	public ActionResolver actionResolver;
	
	public MainGame(ActionResolver actionResolver){
		this.actionResolver = actionResolver;
	}
	
	public MainGame(){}
	
	@Override
	public void create() {		
		batch = new SpriteBatch();
		sm = new ScreenManager(new ScoreManager(), new SettingsManager());
		sm.setScreen(new MenuScreen(this));
		Gdx.input.setCatchBackKey(true);
		this.checkGPGS();
	}

	@Override
	public void dispose() {
		if (sm.getCurrentScreen() != null)
			sm.getCurrentScreen().dispose();
		batch.dispose();
	}

	@Override
	public void render() {		
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		if (sm.getCurrentScreen() != null)
			sm.getCurrentScreen().update();
		
		if (sm.getCurrentScreen() != null)
			sm.getCurrentScreen().render(batch);
	}

	@Override
	public void resize(int width, int height) {
		if (sm.getCurrentScreen() != null)
			sm.getCurrentScreen().resize(width, height);
	}

	@Override
	public void pause() {
		if (sm.getCurrentScreen() != null)
			sm.getCurrentScreen().pause();
	}

	@Override
	public void resume() {
		if (sm.getCurrentScreen() != null)
			sm.getCurrentScreen().resume();
	}
	
	public void checkGPGS(){
		//check for already completed achievements and highscores for the leaderboard
		if(actionResolver.getSignedInGPGS()){
			ScoreManager sm = new ScoreManager();
			ArrayList<Score> allScores = sm.getAllScores();		
			int highscore = 0;
			boolean achievementOne = true, achievementFive = true, achievementTen = true, achievementCompleted = true;
			for(int i = 0 ; i < allScores.size() ; i++){
				if(allScores.get(i).isLevelCompleted() && allScores.get(i).getScore() > 0){
					//the total score will be determined and added to the leaderboard later
					highscore += allScores.get(i).getScore();
				}
				else{
					achievementCompleted = false;
				}
				if(!(allScores.get(i).getScore() == 1)){
					achievementOne = false;
				}
				else if(!(allScores.get(i).getScore() <= 5)){
					achievementFive = false;
				}
				else if( !(allScores.get(i).getScore() <= 10)){
					achievementTen = false;
				}
			}
			//achievement I wasn't even trying completed
			if(achievementOne) actionResolver.unlockAchievementGPGS("CgkI-riXl7wKEAIQAQ");
			//achievement This game is easy completed
			if(achievementFive) actionResolver.unlockAchievementGPGS("CgkI-riXl7wKEAIQAg");
			//achievement I finally did it! completed
			if(achievementTen) actionResolver.unlockAchievementGPGS("CgkI-riXl7wKEAIQAw");
			//achievement completed first 3 levels
			if(achievementCompleted) actionResolver.unlockAchievementGPGS("CgkI-riXl7wKEAIQBQ");
			//add the highscore to the leaderboard
			if(highscore > 0) actionResolver.submitScoreGPGS(highscore);
		}
	}
}
