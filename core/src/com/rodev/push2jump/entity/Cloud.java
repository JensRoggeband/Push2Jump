package com.rodev.push2jump.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.rodev.push2jump.MainGame;

public class Cloud extends Entity{
	
	public Cloud() {
		super(TextureRegion.split(new Texture(Gdx.files.internal("textures/textures-cloud.png")), 47, 35), new Vector2(0,0), new Vector2( -0.3f, 0));
	}
	
	public Cloud(Vector2 pos, Vector2 direction){
		super(TextureRegion.split(new Texture(Gdx.files.internal("textures/textures-cloud.png")), 47, 35), pos, direction);
	}

	@Override
	public void update() {
		pos.add(direction);	
		if(pos.x <= -this.textureRegions[0][0].getTexture().getWidth()){
			//pos.x = Gdx.graphics.getWidth() + this.textureRegions[0][0].getTexture().getWidth();
			pos.x = MainGame.WIDTH + this.textureRegions[0][0].getTexture().getWidth();
		}
	}
}
