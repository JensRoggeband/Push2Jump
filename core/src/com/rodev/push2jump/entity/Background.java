package com.rodev.push2jump.entity;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class Background extends Entity{

	public Background(TextureRegion[][] textureRegions, Vector2 pos, Vector2 direction) {
		super(textureRegions, pos, direction);
	}

	@Override
	public void update() {
		pos.add(direction);	
		if(pos.x <= -this.textureRegions[0][0].getTexture().getWidth()){
			pos.x = 2*this.textureRegions[0][0].getTexture().getWidth();
		}		
	}

}
