package com.rodev.push2jump.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

public class BadPlatform extends Entity {
	
	private int amount;

	public BadPlatform() {
		super(TextureRegion.split(new Texture(Gdx.files.internal("textures/textures-badplatform.png")), 25, 20)[0][0]);
	}
	
	@Override
	public void render(SpriteBatch sb) {
		for(int i = 0 ; i < amount ; i++){
			int scoop = i * textureRegion.getRegionWidth();
			sb.draw(textureRegion, pos.x + scoop, pos.y);
		}
	}

	@Override
	public void update() {
		pos.add(direction);
	}
	
	@Override
	public Rectangle getBounds() {
		return new Rectangle(pos.x, pos.y, textureRegion.getRegionWidth() * amount, textureRegion.getRegionHeight());
	}	
}
