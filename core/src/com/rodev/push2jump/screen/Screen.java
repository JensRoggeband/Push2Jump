package com.rodev.push2jump.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.rodev.push2jump.MainGame;
import com.rodev.push2jump.SettingsManager;
import com.rodev.push2jump.score.ScoreManager;

public abstract class Screen {
	protected ScoreManager scoreManager;
	protected ScreenManager screenManager;
	protected SettingsManager settingsManager;
	protected float scale = (float)Gdx.graphics.getWidth()/MainGame.WIDTH;

	public abstract void create();
	
	public abstract void update();
	
	public abstract void render(SpriteBatch sb);
	
	public abstract void resize(int width, int height);
	
	public abstract void dispose();
	
	public abstract void pause();
	
	public abstract void resume();

	public void setScoreManager(ScoreManager scoreManager){
		this.scoreManager = scoreManager;
	}
	
	public void setScreenManager(ScreenManager screenManager){
		this.screenManager = screenManager;
	}
	
	public void setSettingsManager(SettingsManager settingsManager){
		this.settingsManager = settingsManager;
	}
}