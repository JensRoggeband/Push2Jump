package com.rodev.push2jump.screen;

import com.rodev.push2jump.SettingsManager;
import com.rodev.push2jump.score.ScoreManager;

public class ScreenManager {

	private Screen currentScreen;
	private ScoreManager scoreManager;
	private SettingsManager settingsManager;
	
	public ScreenManager(ScoreManager scoreManager, SettingsManager settingsManager){
		this.scoreManager = scoreManager;
		this.settingsManager = settingsManager;
	}
	
	public void setScreen(Screen screen) {
		if (currentScreen != null)
			currentScreen.dispose();
		currentScreen = screen;
		currentScreen.setScreenManager(this);
		currentScreen.setSettingsManager(settingsManager);
		currentScreen.setScoreManager(scoreManager);
		currentScreen.create();
	}
	
	public Screen getCurrentScreen() {
		return currentScreen;
	}
}