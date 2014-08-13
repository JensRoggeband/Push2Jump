package com.rodev.push2jump.entity;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter.OutputType;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;
import com.rodev.push2jump.SettingsManager;
import com.rodev.push2jump.score.Score;
import com.rodev.push2jump.score.ScoreManager;
import com.rodev.push2jump.screen.GameScreen;

public class EntityManager implements Disposable {

	private GameScreen gameScreen;
	private Array<Entity> entities;
	private ArrayList<Entity> readEntities;
	private Player player;
	private Entity startMountain, endMountain;
	private Background bg, bg2, bg3, bg4, bg5, bg6;
	private Task initializeGame;
	private BitmapFont font;
	private ScoreManager scoreManager;
	private Json json;	
	private SettingsManager settingsManager;

	private boolean checkForDead;
	private int currentLevel;
	private int attempt = 1;
	private float attemptVel, attemptX;
	
	public EntityManager(GameScreen gameScreen, SettingsManager settingsManager, int currentLevel, float scale) {
		this.gameScreen = gameScreen;
		this.settingsManager = settingsManager;
		this.currentLevel = currentLevel;
		entities = new Array<Entity>();
		
		json = new Json(OutputType.json);		
		json.addClassTag("platform", Platform.class);
		json.addClassTag("badplatform", BadPlatform.class);
		json.addClassTag("cloud", Cloud.class);
		
		initializeGame = new Task(){
			@Override
		    public void run() {
		    	initialize();
		    }
		};
		initializeGame.run();
		attemptVel = readEntities.get(readEntities.size()-1).direction.x;
		
		font = new BitmapFont(Gdx.files.internal("fonts/showcardgothic.fnt"), false);
		font.setScale(scale * 0.3f);	
		
		scoreManager = new ScoreManager();
	}
	
	public void update() {
		if (!player.dead()){
			attemptX += attemptVel;
			for (Entity e : entities)
				e.update();
		}
		player.update();
		checkCollisions();
		checkPlayerStatus();
	}
	
	public void render(SpriteBatch sb) {
		for (Entity e : entities)
			if(e.pos.x > -e.getBounds().width && e.pos.x < Gdx.graphics.getWidth() + 100 && e.pos.y > -100 && e.pos.y < Gdx.graphics.getHeight())
				e.render(sb);
		font.draw(sb, "Attempt " + attempt, attemptX, 100);
	
		player.render(sb);
	}
	
	private void initialize(){
		entities = new Array<Entity>();
		//System.gc();
		attemptX = 100;
		if(attempt == 50) gameScreen.game.actionResolver.unlockAchievementGPGS("CgkI-riXl7wKEAIQBA");
		
		bg = new Background(TextureRegion.split(new Texture(Gdx.files.internal("textures/bg-mountains.png")), 480, 100), new Vector2(0, 0), new Vector2(-0.1f, 0));
		addEntity(bg);
		bg2 = new Background(TextureRegion.split(new Texture(Gdx.files.internal("textures/bg-mountains.png")), 480, 100), new Vector2(480, 0), new Vector2(-0.1f, 0));
		addEntity(bg2);
		bg3 = new Background(TextureRegion.split(new Texture(Gdx.files.internal("textures/bg-mountains.png")), 480, 100), new Vector2(960, 0), new Vector2(-0.1f, 0));
		addEntity(bg3);	
		
		bg4 = new Background(TextureRegion.split(new Texture(Gdx.files.internal("textures/bg-mountains2.png")), 480, 58), new Vector2(0, 0), new Vector2(-0.3f, 0));
		addEntity(bg4);
		bg5 = new Background(TextureRegion.split(new Texture(Gdx.files.internal("textures/bg-mountains2.png")), 480, 58), new Vector2(480, 0), new Vector2(-0.3f, 0));
		addEntity(bg5);
		bg6 = new Background(TextureRegion.split(new Texture(Gdx.files.internal("textures/bg-mountains2.png")), 480, 58), new Vector2(960, 0), new Vector2(-0.3f, 0));
		addEntity(bg6);	
		
		readEntities = json.fromJson(ArrayList.class, Gdx.files.internal("data/level" + currentLevel + ".json"));	
		for (Entity e : readEntities){
			addEntity(e);
		}
		
		Entity lastPlatform = readEntities.get(readEntities.size()-1);
		float distance = lastPlatform.getPosition().x + lastPlatform.getBounds().width + 80;
	
		startMountain = new Mountain(TextureRegion.split(new Texture(Gdx.files.internal("textures/textures-mountain.png")), 100, 50), new Vector2(0, 0), lastPlatform.direction, true);
		addEntity(startMountain);
		
		endMountain = new Mountain(TextureRegion.split(new Texture(Gdx.files.internal("textures/textures-mountain.png")), 100, 50), new Vector2(distance, 0), lastPlatform.direction, false);
		addEntity(endMountain);
		
		if(player != null) player.dispose();
		player = new Player(new Vector2(230, startMountain.getBounds().height), new Vector2(0, 0), settingsManager);
	}
	
	private void checkCollisions() {
		boolean onTop = false;
		Rectangle sideRect = new Rectangle(player.getPosition().x + player.getBounds().width - 5, player.getPosition().y, 5, player.getBounds().height);
		Rectangle topRect = new Rectangle(player.getPosition().x, player.getPosition().y + player.getBounds().height - 5, player.getBounds().width, 5);
		for (Entity p : getPlattformsMountains()) {
			if (sideRect.overlaps(new Rectangle(p.getPosition().x, p.getPosition().y, 1, p.getBounds().height))){
				player.die();
			}
			else if (topRect.overlaps(p.getBounds())){
				player.pos.y = p.pos.y - player.getBounds().height;
				player.resetVelocityY();
			}
			else if (player.getBounds().overlaps(p.getBounds())) {
				onTop = true;
				player.resetVelocityY();
				player.pos.y = p.getPosition().y + p.getBounds().height;
				if(p instanceof Mountain){
					Mountain m = (Mountain) p;
					if(!m.getStart()) GameScreen.gameState = GameScreen.State.WIN;
					else player.stand();
				}
				else if(p instanceof BadPlatform){
					player.die();
				}
				else{
					player.stand();
				}
			}
		}
		if (!onTop){
			player.gravity();
		}
	}
	
	private void checkPlayerStatus(){
		if (!player.dead())
			checkForDead = true;
		else if (player.dead() && !initializeGame.isScheduled() && checkForDead){
			Timer.schedule(initializeGame, 7/35f);
			attempt++;
			checkForDead = false;
		}		
		if(GameScreen.gameState == GameScreen.State.WIN){
			scoreManager.addNewScore(new Score(currentLevel, true, attempt));
		}
	}
	
	public void addEntity(Entity entity) {
		entities.add(entity);
	}
	
	private Array<Entity> getPlattformsMountains(){
		Array<Entity> plattformsmountains = new Array<Entity>();
		for (Entity e : entities)
			if (e instanceof Platform || e instanceof BadPlatform || e instanceof Mountain)
				plattformsmountains.add((Entity)e);
		return plattformsmountains;
	}
	
	public int getAttempts(){
		return attempt;
	}
	
	public void dispose(){
		font.dispose();
		if(player != null) player.dispose();
	}
}
