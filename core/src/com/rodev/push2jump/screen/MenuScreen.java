package com.rodev.push2jump.screen;

import java.util.ArrayList;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.utils.TimeUtils;
import com.rodev.push2jump.LevelManager;
import com.rodev.push2jump.MainGame;
import com.rodev.push2jump.SettingsManager;
import com.rodev.push2jump.camera.OrthoCamera;
import com.rodev.push2jump.entity.Background;
import com.rodev.push2jump.entity.Cloud;
import com.rodev.push2jump.entity.Entity;
import com.rodev.push2jump.score.Score;

public class MenuScreen extends Screen {

	private OrthoCamera camera;
	private ShapeRenderer shapeRenderer;
	private MainGame game;
	
	private Stage menuStage, levelStage;
	private TextureAtlas buttonsAtlas;
	private Skin buttonSkin;
	private TextButton playButton, exitButton, achievementButton, leaderboardButton, levelOneButton, levelTwoButton, levelThreeButton;
	private ImageButton soundButton, previousButton;
	private Table menuTable, levelTable;
	private BitmapFont font;
	private ImageButtonStyle imageButtonStyle;
	private ArrayList<Entity> entities;
	
	private boolean levelStageActive;
	private long cooldownBackButton;
	private final int SPACEBETWEENCOLS = 50;
	private final int BUTTONSIZE = 65, BUTTONPADDING = 25;
	private final int BUTTONWIDTH = 320, BUTTONHEIGHT = 65;
	
	public MenuScreen(MainGame game){
		this.game = game;
	}
	
	@Override
	public void create() {	
		camera = new OrthoCamera();
		camera.resize();
		shapeRenderer = new ShapeRenderer();
		menuStage = new Stage();
		levelStage = new Stage();
		
		entities = new ArrayList<Entity>();
		Background bg = new Background(TextureRegion.split(new Texture(Gdx.files.internal("textures/bg-mountains.png")), 480, 100), new Vector2(0, 0), new Vector2(-1f, 0));
		addEntity(bg);
		Background bg2 = new Background(TextureRegion.split(new Texture(Gdx.files.internal("textures/bg-mountains.png")), 480, 100), new Vector2(480, 0), new Vector2(-1f, 0));
		addEntity(bg2);
		Background bg3 = new Background(TextureRegion.split(new Texture(Gdx.files.internal("textures/bg-mountains.png")), 480, 100), new Vector2(960, 0), new Vector2(-1f, 0));
		addEntity(bg3);	
		
		Background bg4 = new Background(TextureRegion.split(new Texture(Gdx.files.internal("textures/bg-mountains2.png")), 480, 58), new Vector2(0, 0), new Vector2(-1.3f, 0));
		addEntity(bg4);
		Background bg5 = new Background(TextureRegion.split(new Texture(Gdx.files.internal("textures/bg-mountains2.png")), 480, 58), new Vector2(480, 0), new Vector2(-1.3f, 0));
		addEntity(bg5);
		Background bg6 = new Background(TextureRegion.split(new Texture(Gdx.files.internal("textures/bg-mountains2.png")), 480, 58), new Vector2(960, 0), new Vector2(-1.3f, 0));
		addEntity(bg6);	
		
		Random random = new Random();
		for(int i = 0 ; i < 15; i++){
			Cloud c1 = new Cloud(new Vector2(random.nextInt(MainGame.WIDTH), random.nextInt(MainGame.HEIGHT)), new Vector2(-1f + -random.nextFloat(), 0));
			addEntity(c1);
		}

		buttonsAtlas = new TextureAtlas("ui/icons.pack");
		buttonSkin = new Skin(buttonsAtlas);
		font = new BitmapFont(Gdx.files.internal("fonts/showcardgothic.fnt"), false);
		font.setScale(scale * 0.4f);
		
		createMenuStage();
	}
	
	private void createMenuStage(){
		if(levelStage != null) levelStage.clear();
		levelStageActive = false;
		
		final SettingsManager seM = this.settingsManager;
		
		menuTable = new Table(buttonSkin);
		menuTable.setBounds(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		
		LabelStyle labelStyle = new LabelStyle(font, Color.WHITE);
		labelStyle.font = font;
		Label heading = new Label("Push2Jump", labelStyle);
		heading.setScale(scale);
		menuTable.top();
		menuTable.add(heading).space(50).row();
		
		TextButtonStyle textButtonStyle = new TextButtonStyle();
		textButtonStyle.up = buttonSkin.getDrawable("completed-button.up");
		textButtonStyle.down = buttonSkin.getDrawable("completed-button.down");
		textButtonStyle.pressedOffsetX = 1;
		textButtonStyle.pressedOffsetY = -1;
		textButtonStyle.font = font;
		
		imageButtonStyle = new ImageButtonStyle();
		if(this.settingsManager.soundOn()){
			imageButtonStyle.up = buttonSkin.getDrawable("sound-on-icon.up");
			imageButtonStyle.down = buttonSkin.getDrawable("sound-on-icon.down");
		}
		else{
			imageButtonStyle.up = buttonSkin.getDrawable("sound-off-icon.up");
			imageButtonStyle.down = buttonSkin.getDrawable("sound-off-icon.down");
		}
		
		playButton = new TextButton("Play", textButtonStyle);
		playButton.addListener(new InputListener(){
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                return true;
			}
       
	        public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
	        	if(playButton.getOriginX() < x && x < playButton.getWidth()
	        			&& playButton.getOriginY() < y && y < playButton.getHeight()){
	        		createLevelStage();
	        	}
	        }
		});
		playButton.pad(5);
		
		achievementButton = new TextButton("Achievements", textButtonStyle);
		achievementButton.addListener(new InputListener(){
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                return true;
			}
       
	        public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
	        	if(achievementButton.getOriginX() < x && x < achievementButton.getWidth()
	        			&& achievementButton.getOriginY() < y && y < achievementButton.getHeight()){
	        		game.actionResolver.getAchievementsGPGS();   		
	        	}
	        }
		});
		achievementButton.pad(5);
		
		leaderboardButton = new TextButton("Leaderboard", textButtonStyle);
		leaderboardButton.addListener(new InputListener(){
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                return true;
			}
       
	        public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
	        	if(leaderboardButton.getOriginX() < x && x < leaderboardButton.getWidth()
	        			&& leaderboardButton.getOriginY() < y && y < leaderboardButton.getHeight()){
	        		game.actionResolver.getLeaderboardGPGS();
	        	}
	        }
		});
		leaderboardButton.pad(5);
		
		exitButton = new TextButton("Exit", textButtonStyle);
		exitButton.addListener(new InputListener(){
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                return true;
			}
       
	        public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
	        	if(playButton.getOriginX() < x && x < playButton.getWidth()
	        			&& playButton.getOriginY() < y && y < playButton.getHeight()){
	                Gdx.app.exit();
	        	}
	        }
		});
		exitButton.pad(5);
		
		soundButton = new ImageButton(imageButtonStyle);
		soundButton.addListener(new InputListener(){
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                return true;
			}

	        public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
	        	if(soundButton.getOriginX() < x && x < soundButton.getWidth()
	        			&& soundButton.getOriginY() < y && y < soundButton.getHeight()){
	        		seM.toggleSound();	 
	        		toggleSound();    
	        	}
	        }
		});

		menuTable.add(playButton).width(scale * BUTTONWIDTH).height(scale * BUTTONHEIGHT).spaceBottom(10);
		menuTable.row();
		menuTable.add(achievementButton).width(scale * BUTTONWIDTH).height(scale * BUTTONHEIGHT).spaceBottom(10);
		menuTable.row();
		menuTable.add(leaderboardButton).width(scale * BUTTONWIDTH).height(scale * BUTTONHEIGHT).spaceBottom(10);
		menuTable.row();
		menuTable.add(exitButton).width(scale * BUTTONWIDTH).height(scale * BUTTONHEIGHT).spaceBottom(10);
		menuTable.row();
		menuTable.add(soundButton).width(scale * BUTTONSIZE).height(scale * BUTTONSIZE);

		menuStage.clear();
		Gdx.input.setInputProcessor(menuStage);
		
		menuStage.addActor(menuTable);
	}
	
	private void createLevelStage(){
		if(menuStage != null) menuStage.clear();
		levelStageActive = true;
		
		final ScreenManager scM = this.screenManager;
		
		levelTable = new Table(buttonSkin);
		levelTable.setBounds(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		
		LabelStyle labelStyle = new LabelStyle(font, Color.WHITE);
		labelStyle.font = font;
		Label heading = new Label("Levelselect", labelStyle);
		heading.setScale(scale);
		levelTable.top();
		levelTable.add(heading).colspan(LevelManager.AmountLevels).space(50).row();
		
		TextButtonStyle completedButtonStyle = new TextButtonStyle();
		completedButtonStyle.up = buttonSkin.getDrawable("completed-button.up");
		completedButtonStyle.down = buttonSkin.getDrawable("completed-button.down");
		completedButtonStyle.pressedOffsetX = 1;
		completedButtonStyle.pressedOffsetY = -1;
		completedButtonStyle.font = font;
			
		TextButtonStyle uncompletedButtonStyle = new TextButtonStyle();
		uncompletedButtonStyle.up = buttonSkin.getDrawable("uncompleted-button.up");
		uncompletedButtonStyle.down = buttonSkin.getDrawable("uncompleted-button.down");
		uncompletedButtonStyle.pressedOffsetX = 1;
		uncompletedButtonStyle.pressedOffsetY = -1;
		uncompletedButtonStyle.font = font;
		
		ArrayList<Score> allScores = this.scoreManager.getAllScores();
		levelOneButton = null;
		if(allScores.get(0).isLevelCompleted()){
			levelOneButton = new TextButton("1", completedButtonStyle);
		}
		else{
			levelOneButton = new TextButton("1", uncompletedButtonStyle);
		}
		levelOneButton.addListener(new InputListener(){
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                return true;
			}
       
	        public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
	        	if(levelOneButton.getOriginX() < x && x < levelOneButton.getWidth()
	        			&& levelOneButton.getOriginY() < y && y < levelOneButton.getHeight()){
	        		scM.setScreen(new GameScreen(game, 1));
	        	}
	        }
		});
		
		levelTwoButton = null;
		if(allScores.get(1).isLevelCompleted()){
			levelTwoButton = new TextButton("2", completedButtonStyle);
		}
		else{
			levelTwoButton = new TextButton("2", uncompletedButtonStyle);
		}
		levelTwoButton.addListener(new InputListener(){
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                return true;
			}
       
	        public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
	        	if(levelTwoButton.getOriginX() < x && x < levelTwoButton.getWidth()
	        			&& levelTwoButton.getOriginY() < y && y < levelTwoButton.getHeight()){
	        		scM.setScreen(new GameScreen(game, 2));
	        	}
	        }
		});
		
		levelThreeButton = null;
		if(allScores.get(2).isLevelCompleted()){
			levelThreeButton = new TextButton("3", completedButtonStyle);
		}
		else{
			levelThreeButton = new TextButton("3", uncompletedButtonStyle);
		}
		levelThreeButton.addListener(new InputListener(){
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                return true;
			}
       
	        public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
	        	if(levelThreeButton.getOriginX() < x && x < levelThreeButton.getWidth()
	        			&& levelThreeButton.getOriginY() < y && y < levelThreeButton.getHeight()){
	        		scM.setScreen(new GameScreen(game, 3));
	        	}
	        }
		});

		levelTable.debug();
		
		levelTable.add(levelOneButton).width(scale * 75).height(scale * 75).spaceRight(scale * SPACEBETWEENCOLS);
		levelTable.add(levelTwoButton).width(scale * 75).height(scale * 75).spaceRight(scale * SPACEBETWEENCOLS);
		levelTable.add(levelThreeButton).width(scale * 75).height(scale * 75).spaceRight(scale * SPACEBETWEENCOLS);

		levelTable.row();
				
		Label scoreLevelOne = new Label("", labelStyle);
		if(allScores.get(0).isLevelCompleted()){
			scoreLevelOne.setText("Attempts: " + allScores.get(0).getScore());
			scoreLevelOne.setFontScale(scale * 0.3f);
		}
		levelTable.add(scoreLevelOne);
		levelTable.getCell(scoreLevelOne).spaceRight(scale * SPACEBETWEENCOLS);
		
		Label scoreLevelTwo = new Label("", labelStyle);
		if(allScores.get(1).isLevelCompleted()){
			scoreLevelTwo = new Label("Attempts: " + allScores.get(1).getScore(), labelStyle);
			scoreLevelTwo.setFontScale(scale * 0.3f);
		}
		levelTable.add(scoreLevelTwo);
		levelTable.getCell(scoreLevelTwo).spaceRight(scale * SPACEBETWEENCOLS);
		
		Label scoreLevelThree = new Label("", labelStyle);
		if(allScores.get(2).isLevelCompleted()){		
			scoreLevelThree = new Label("Attempts: " + allScores.get(2).getScore(), labelStyle);
			scoreLevelThree.setFontScale(scale * 0.3f);
		}
		levelTable.add(scoreLevelThree);
		levelTable.getCell(scoreLevelThree).spaceRight(scale * SPACEBETWEENCOLS);
		levelTable.row();
			
		ImageButtonStyle imageButtonStyle = new ImageButtonStyle();
		imageButtonStyle.up = buttonSkin.getDrawable("previous-icon.up");
		imageButtonStyle.down = buttonSkin.getDrawable("previous-icon.down");
		
		previousButton = new ImageButton(imageButtonStyle);
		previousButton.addListener(new InputListener(){
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                return true;
			}

	        public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
	        	if(previousButton.getOriginX() < x && x < previousButton.getWidth()
	        			&& previousButton.getOriginY() < y && y < previousButton.getHeight()){
	        		createMenuStage(); 
	        	}
	        }
		});
		//levelTable.add(previousButton).width(scale * BUTTONSIZE).height(scale * BUTTONSIZE).left().bottom().spaceTop(SPACEBETWEENCOLS);
		previousButton.setSize(scale * BUTTONSIZE, scale * BUTTONSIZE);
		previousButton.setPosition(scale * BUTTONPADDING, Gdx.graphics.getHeight() - previousButton.getHeight() - scale * BUTTONPADDING);
		
		levelStage.clear();
		Gdx.input.setInputProcessor(levelStage);	
		
		levelStage.addActor(levelTable);
		levelStage.addActor(previousButton);
	}
	
	private void addEntity(Entity entity) {
		entities.add(entity);
	}
	
	private void toggleSound(){
		if (imageButtonStyle.up == buttonSkin.getDrawable("sound-off-icon.up")){
			imageButtonStyle.up = buttonSkin.getDrawable("sound-on-icon.up");
			imageButtonStyle.down = buttonSkin.getDrawable("sound-on-icon.down");
		}
		else{
			imageButtonStyle.up = buttonSkin.getDrawable("sound-off-icon.up");
			imageButtonStyle.down = buttonSkin.getDrawable("sound-off-icon.down");
		}
	}

	@Override
	public void update() {
		camera.update();
		if ((Gdx.input.isKeyPressed(Keys.BACK) || Gdx.input.isKeyPressed(Keys.ESCAPE)) && !levelStageActive && TimeUtils.millis() - cooldownBackButton > 500) {
			 Gdx.app.exit();
			 cooldownBackButton = TimeUtils.millis();
		}
		else if(Gdx.input.isKeyPressed(Keys.BACK) || Gdx.input.isKeyPressed(Keys.ESCAPE) && TimeUtils.millis() - cooldownBackButton > 500){
			createMenuStage();
			cooldownBackButton = TimeUtils.millis();
		}
		for (Entity e : entities)
			e.update();
	}

	@Override
	public void render(SpriteBatch sb) {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        shapeRenderer.begin(ShapeType.Filled);
        shapeRenderer.rect(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), new Color(0, .75f, 1, 1), new Color(0, .75f, 1, 1), new Color(.2f, .5f, 1, 1), new Color(.2f, .5f, 1, 1));        
        shapeRenderer.end();
        
		sb.setProjectionMatrix(camera.combined);
		sb.begin();
		for (Entity e : entities)
			e.render(sb);
		sb.end();
		
        menuStage.act();
		menuStage.draw();
		levelStage.act();
		levelStage.draw();
	}

	@Override
	public void resize(int width, int height) {
		camera.resize();
	}

	@Override
	public void dispose() {
        buttonSkin.dispose();
        buttonsAtlas.dispose();
        font.dispose();
		menuStage.dispose();
		levelStage.dispose();
		shapeRenderer.dispose();		
	}

	@Override
	public void pause() {
		
	}

	@Override
	public void resume() {
		
	}
}
