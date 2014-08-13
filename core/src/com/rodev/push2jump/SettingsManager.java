package com.rodev.push2jump;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

public class SettingsManager {

	public Preferences settings = Gdx.app.getPreferences("settings");
	public static final String SOUND = "sound";
	public static final String GPGS = "GPGS";
	
	public void toggleSound(){
		if(settings.getBoolean(SOUND)){
			settings.putBoolean(SOUND, false);
		}
		else{
			settings.putBoolean(SOUND, true);
		}
		settings.flush();
	}

	public boolean soundOn(){
		return settings.getBoolean(SOUND, false);
	}
	
	public void setGPGS(boolean bool){
		settings.putBoolean(GPGS, bool);
		settings.flush();
	}
	
	public boolean getGPGS(){
		return settings.getBoolean(GPGS, false);
	}
}
