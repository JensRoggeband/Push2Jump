package com.rodev.push2jump.entity;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Mountain extends Entity{
	
	private TextureRegion topSegment, rightTopSegment, leftTopSegment;
	private boolean start;
	private final int LENGTH = 5;

	public Mountain(TextureRegion[][] textureRegions, Vector2 pos, Vector2 direction, boolean start) {
		super(textureRegions, pos, direction);
		this.start = start;
		
		topSegment = textureRegions[0][0];	
		rightTopSegment = textureRegions[0][1];
		leftTopSegment = textureRegions[0][2];
	}

	@Override
	public void update() {
		pos.add(direction);			
	}
	
	@Override
	public void render(SpriteBatch sb) {
		for(int i = 0 ; i < LENGTH ; i++){
			int scoop = i * (topSegment.getRegionWidth());
			sb.draw(topSegment, pos.x + scoop, pos.y);		
			if(start){
				if(i == LENGTH -1){
					sb.draw(rightTopSegment, pos.x + LENGTH * (rightTopSegment.getRegionWidth()), pos.y);
				}
			}
			else{
				sb.draw(leftTopSegment, pos.x, pos.y);
			}
		}
	}
	
	@Override
	public Rectangle getBounds() {
		return new Rectangle(pos.x, pos.y, topSegment.getRegionWidth() * (LENGTH +1), topSegment.getRegionHeight());
	}
	
	public boolean getStart(){
		return this.start;
	}
}
