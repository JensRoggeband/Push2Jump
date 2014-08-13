package com.rodev.push2jump.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public abstract class Entity {

	protected TextureRegion textureRegion;
	protected TextureRegion[][] textureRegions;
	protected Vector2 pos, direction;
	
	
	public Entity(TextureRegion textureRegion, Vector2 pos, Vector2 direction) {
		this.textureRegion = textureRegion;
		this.pos = pos;
		this.direction = direction;
	}
	
	public Entity(TextureRegion[][] textureRegions, Vector2 pos, Vector2 direction){
		this.textureRegions = textureRegions;
		this.textureRegion = textureRegions[0][0];
		this.pos = pos;
		this.direction = direction;
	}
	public Entity(TextureRegion textureRegion) {
		this.textureRegion = textureRegion;
	}
	
	public Entity(TextureRegion[][] textureRegions) {
		this.textureRegions = textureRegions;
		this.textureRegion = textureRegions[0][0];
	}
	
	public abstract void update();
	
	public void render(SpriteBatch sb) {
		sb.draw(textureRegion, pos.x, pos.y);
	}
	
	public Vector2 getPosition() {
		return pos;
	}
	
	public Rectangle getBounds() {
		return new Rectangle(pos.x, pos.y, textureRegion.getRegionWidth(), textureRegion.getRegionHeight());
	}
	
	public void setDirection(float x, float y) {
		direction.set(x, y);
		direction.scl(Gdx.graphics.getDeltaTime());
	}	
	
}
