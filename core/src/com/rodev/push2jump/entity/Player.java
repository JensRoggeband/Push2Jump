package com.rodev.push2jump.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.rodev.push2jump.MainGame;
import com.rodev.push2jump.SettingsManager;

public class Player extends Entity {
	
	private SettingsManager settingsManager;
	public static final int STANDING = 0, JUMPING = 1, DEAD = 2;
	public static final int GRAVITY = 75;
	public static int state;
	private Sound jumpSound, deathSound;
	private int velocity_y;
	private float scale;
	
	private Animation deathAnimation;
	private float time = 0;
	
	public Player(Vector2 pos, Vector2 direction, SettingsManager settingsManager) {
		super(TextureRegion.split(new Texture(Gdx.files.internal("textures/textures-player.png")), 50, 50), pos, direction);
		this.settingsManager = settingsManager;
		spawn();
		scale = (float)Gdx.graphics.getWidth()/MainGame.WIDTH;
		
		deathAnimation = new Animation(1/35f, this.textureRegions[0]);
		
		jumpSound = Gdx.audio.newSound(Gdx.files.internal("audio/jump.ogg"));
		deathSound = Gdx.audio.newSound(Gdx.files.internal("audio/death.ogg"));
	}

	@Override
	public void update() {
		pos.add(direction);
		if (Gdx.input.isTouched() && !(Gdx.input.getX() < scale * 90 && Gdx.input.getY() < scale * 90 )) {
			jump();
		}		
		if (pos.y < -getBounds().height){
			die();
		}
		if(dead()){
			textureRegion = deathAnimation.getKeyFrame(time += Gdx.graphics.getDeltaTime());
		}
	}
	
	public void spawn(){
		state = JUMPING;
	}
	
	private void jump(){
		if(standing()){
			velocity_y = 1200;
			direction.add(0, velocity_y);
			direction.scl(Gdx.graphics.getDeltaTime());
			state = JUMPING;
			pos.y++;
			if(settingsManager.soundOn())jumpSound.play();
		}
	}
	
	public boolean jumping(){
		return state == JUMPING;
	}
	
	public void die(){
		if(!dead()){
			state = DEAD;
			if(settingsManager.soundOn())deathSound.play();
		}
	}
	
	public boolean dead(){
		return state == DEAD;
	}
	
	public void stand(){
		if(!standing() && !dead()){
			state = STANDING;
			direction.y = 0;
		}
	}
	
	public boolean standing(){
		return state == STANDING;
	}
	
	public void gravity(){
		velocity_y -= GRAVITY;
		direction.add(0, velocity_y);
		direction.scl(Gdx.graphics.getDeltaTime());
		if (state != DEAD) state = JUMPING;
	}
	
	public void resetVelocityY(){
		velocity_y = 0;
		direction.y = 0;
	}
	
	public void setPosition(float x, float y){
		this.pos.x = x;
		this.pos.y = y;
	}
	
	public void dispose(){
		if(jumpSound != null)jumpSound.dispose();
		if(deathSound != null)deathSound.dispose();
	}
}
