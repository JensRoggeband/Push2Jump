package com.rodev.push2jump.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

public class Platform extends Entity {
	
	private int amount, orientation;
	public final int PORTRAIT = 0, LANDSCAPE = 1;

	public Platform() {
		super(TextureRegion.split(new Texture(Gdx.files.internal("textures/textures-platform.png")), 25, 20)[0][0]);
	}
	
	@Override
	public void render(SpriteBatch sb) {
		for(int i = 0 ; i < amount ; i++){
			if(orientation == PORTRAIT){
				int scoop = i * textureRegion.getRegionHeight();
				sb.draw(textureRegion, pos.x, pos.y + scoop);
			}
			else if(orientation == LANDSCAPE){
				int scoop = i * textureRegion.getRegionWidth();
				sb.draw(textureRegion, pos.x + scoop, pos.y);
			}
		}
	}

	@Override
	public void update() {
		pos.add(direction);
	}
	
	@Override
	public Rectangle getBounds() {
		if(orientation == PORTRAIT) return new Rectangle(pos.x, pos.y, textureRegion.getRegionWidth(), textureRegion.getRegionHeight() * amount);
		else if(orientation == LANDSCAPE) return new Rectangle(pos.x, pos.y, textureRegion.getRegionWidth() * amount, textureRegion.getRegionHeight());
		else return new Rectangle(pos.x, pos.y, textureRegion.getRegionWidth(), textureRegion.getRegionHeight());
	}	
}
