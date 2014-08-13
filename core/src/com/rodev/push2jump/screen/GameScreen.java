package com.rodev.push2jump.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.TimeUtils;
import com.rodev.push2jump.LevelManager;
import com.rodev.push2jump.MainGame;
import com.rodev.push2jump.SettingsManager;
import com.rodev.push2jump.camera.OrthoCamera;
import com.rodev.push2jump.entity.EntityManager;

public class GameScreen extends Screen {

	private OrthoCamera camera;
	private EntityManager entityManager;
	private ShapeRenderer shapeRenderer;
	public MainGame game;
	
	private Music bgm;
	private Sound winSound;
	private TextureAtlas buttonAtlas;
	private Skin buttonSkin;
	private ImageButtonStyle soundButtonStyle;
	private ImageButton pauseButton, playButton, menuButton, soundButton, retryButton, nextButton;
	private Table pauseTable, winTable;
	private Stage normalStage, pauseStage, winStage;
	private BitmapFont font;
	
	private int currentLevel;
	private long cooldownBackButton;
	private boolean createStageOnce;
	private int currentScore;
	private final int BUTTONSIZE = 65, BUTTONPADDING = 25;

	public enum State
	{
	    PAUSE,
	    RUN,
	    WIN
	}
	public static State gameState;
	
	public GameScreen(MainGame game, int level){
		this.game = game;
		this.currentLevel = level;
	}
	
	@Override
	public void create() {
		camera = new OrthoCamera();
		camera.resize();
		gameState = State.RUN;
		createStageOnce = true;
		currentScore = this.scoreManager.getAllScores().get(currentLevel-1).getScore();
		
		winSound = Gdx.audio.newSound(Gdx.files.internal("audio/win.ogg"));
		
		bgm = Gdx.audio.newMusic(Gdx.files.internal("audio/bgm.mp3"));
		bgm.setLooping(true);
		bgm.setVolume(0.5f);
		if(this.settingsManager.soundOn()){
			bgm.play();
		}
		
		entityManager = new EntityManager(this, this.settingsManager, currentLevel, scale);
		shapeRenderer = new ShapeRenderer();
		cooldownBackButton = TimeUtils.millis();
		
		buttonAtlas = new TextureAtlas("ui/icons.pack");
		buttonSkin = new Skin(buttonAtlas);

		font = new BitmapFont(Gdx.files.internal("fonts/showcardgothic.fnt"), false);
		font.setScale(scale * 0.25f);
		
		ImageButtonStyle imageButtonStyle = new ImageButtonStyle();
		imageButtonStyle.up = buttonSkin.getDrawable("pause-icon.up");
		imageButtonStyle.down = buttonSkin.getDrawable("pause-icon.down");
		
		pauseButton = new ImageButton(imageButtonStyle);
		pauseButton.setSize(scale * BUTTONSIZE, scale * BUTTONSIZE);
		pauseButton.setPosition(scale * BUTTONPADDING, Gdx.graphics.getHeight() - pauseButton.getHeight() - scale * BUTTONPADDING);
		pauseButton.addListener(new InputListener(){
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                return true;
			}
       
	        public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
	        	if(pauseButton.getOriginX() < x && x < pauseButton.getWidth()
	        			&& pauseButton.getOriginY() < y && y < pauseButton.getHeight()){
	        		togglePause();
	        	}
	        }
		});
		
		normalStage = new Stage();
		normalStage.clear();
		Gdx.input.setInputProcessor(normalStage);
		normalStage.addActor(pauseButton);
	}

	@Override
	public void update() {
		if(gameState == State.RUN){
			camera.update();
			entityManager.update();
		}
		else if(gameState == State.WIN && createStageOnce){
			createWinStage();
			createStageOnce = false;
			if(game.actionResolver != null) game.checkGPGS();
		}

		if (Gdx.input.isKeyPressed(Keys.BACK) || Gdx.input.isKeyPressed(Keys.ESCAPE)) {	
			togglePause();
		}
	}

	@Override
	public void render(SpriteBatch sb) {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        shapeRenderer.begin(ShapeType.Filled);
        shapeRenderer.rect(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), new Color(0, .75f, 1, 1), new Color(0, .75f, 1, 1), new Color(.2f, .5f, 1, 1), new Color(.2f, .5f, 1, 1));        
        shapeRenderer.end();
		sb.setProjectionMatrix(camera.combined);		
		sb.begin();
		entityManager.render(sb);
		sb.end();		
		
		if(gameState == State.PAUSE){
        	Gdx.gl.glEnable(GL20.GL_BLEND);
            Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
    		shapeRenderer.begin(ShapeType.Filled);
        	shapeRenderer.setColor(new Color(0, 0, 0, 0.5f));
            shapeRenderer.rect(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        	shapeRenderer.setColor(new Color(0.1f, 0.1f, 0.1f, 1));
            shapeRenderer.rect(0, 0, scale * BUTTONSIZE * 2, Gdx.graphics.getHeight());
            shapeRenderer.end();
            Gdx.gl.glDisable(GL20.GL_BLEND);
            
			pauseStage.act();
			pauseStage.draw();
		}
		else if(gameState == State.WIN && winStage != null){
			if(normalStage != null)
				normalStage.clear();
			
        	Gdx.gl.glEnable(GL20.GL_BLEND);
            Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
    		shapeRenderer.begin(ShapeType.Filled);
        	shapeRenderer.setColor(new Color(0, 0, 0, 0.5f));
            shapeRenderer.rect(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        	shapeRenderer.setColor(new Color(0.1f, 0.1f, 0.1f, 1));
        	shapeRenderer.rect(Gdx.app.getGraphics().getWidth()/4, Gdx.app.getGraphics().getHeight()/4, Gdx.app.getGraphics().getWidth()/2, Gdx.app.getGraphics().getHeight()/2);
            shapeRenderer.end();
            Gdx.gl.glDisable(GL20.GL_BLEND);
            
			winStage.act();
			winStage.draw();
		}
		else{
			if(winStage != null)
				winStage.clear();
			normalStage.act(Gdx.graphics.getDeltaTime());
			normalStage.draw();
		}
	}

	@Override
	public void resize(int width, int height) {
		camera.resize();
	}

	@Override
	public void dispose() {
		entityManager.dispose();
		if(normalStage != null)
			normalStage.dispose();
		if(pauseStage != null)
			pauseStage.dispose();
		if(winStage != null)
			winStage.dispose();
		buttonAtlas.dispose();
		buttonSkin.dispose();
		shapeRenderer.dispose();
		if(bgm.isPlaying()) 
			bgm.stop();
		winSound.stop();
		winSound.dispose();
		bgm.dispose();
	}
	
	private void togglePause(){
		if(gameState == State.PAUSE && TimeUtils.millis() - cooldownBackButton > 500){
			resume();
		}
		else if(gameState == State.RUN && TimeUtils.millis() - cooldownBackButton > 500){
			pause();
		}
	}

	@Override
	public void pause() {
		cooldownBackButton = TimeUtils.millis();
		gameState = State.PAUSE;
		createPauseStage();
	}

	@Override
	public void resume() {
		cooldownBackButton = TimeUtils.millis();
		gameState = State.RUN;
		if(pauseStage != null) pauseStage.clear();
		Gdx.input.setInputProcessor(normalStage);
	}
	
	private void createPauseStage(){	
		pauseStage = new Stage();
		pauseStage.clear();		
		Gdx.input.setInputProcessor(pauseStage);
		final ScreenManager scM = this.screenManager;
		final SettingsManager seM = this.settingsManager;
		
		ImageButtonStyle pauseButtonStyle = new ImageButtonStyle();
		pauseButtonStyle.up = buttonSkin.getDrawable("pause-icon.up");
		pauseButtonStyle.down = buttonSkin.getDrawable("pause-icon.down");
		
		ImageButtonStyle playButtonStyle = new ImageButtonStyle();
		playButtonStyle.up = buttonSkin.getDrawable("play-icon.up");
		playButtonStyle.down = buttonSkin.getDrawable("play-icon.down");
		
		ImageButtonStyle menuButtonStyle = new ImageButtonStyle();
		menuButtonStyle.up = buttonSkin.getDrawable("menu-icon.up");
		menuButtonStyle.down = buttonSkin.getDrawable("menu-icon.down");
		
		soundButtonStyle = new ImageButtonStyle();
		if(this.settingsManager.soundOn()){
			soundButtonStyle.up = buttonSkin.getDrawable("sound-on-icon.up");
			soundButtonStyle.down = buttonSkin.getDrawable("sound-on-icon.down");
		}
		else{
			soundButtonStyle.up = buttonSkin.getDrawable("sound-off-icon.up");
			soundButtonStyle.down = buttonSkin.getDrawable("sound-off-icon.down");
		}
				
		pauseTable = new Table(buttonSkin);
		pauseTable.setFillParent(true);
		pauseTable.left();
		pauseTable.pad(scale * BUTTONPADDING);
		pauseTable.top();
		
		pauseStage.addActor(pauseTable);
		
		playButton = new ImageButton(playButtonStyle);
		playButton.addListener(new InputListener(){
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                return true;
			}
       
	        public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
	        	if(playButton.getOriginX() < x && x < playButton.getWidth()
	        			&& playButton.getOriginY() < y && y < playButton.getHeight()){
	                togglePause();
	        	}
	        }
		});
		pauseTable.add(playButton).space(scale * BUTTONPADDING).width(scale * BUTTONSIZE).height(scale * BUTTONSIZE);
		pauseTable.row();
		
		menuButton = new ImageButton(menuButtonStyle);
		menuButton.addListener(new InputListener(){
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                return true;
			}
       
	        public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
	        	if(menuButton.getOriginX() < x && x < menuButton.getWidth()
	        			&& menuButton.getOriginY() < y && y < menuButton.getHeight()){
	        		scM.setScreen(new MenuScreen(game));
	        	}
	        }
		});		
		pauseTable.add(menuButton).space(scale * BUTTONPADDING).width(scale * BUTTONSIZE).height(scale * BUTTONSIZE);;
		pauseTable.row();
		
		soundButton = new ImageButton(soundButtonStyle);
		soundButton.addListener(new InputListener(){
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                return true;
			}
       
	        public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
	        	if(soundButton.getOriginX() < x && x < soundButton.getWidth()
	        			&& soundButton.getOriginY() < y && y < soundButton.getHeight()){
	                seM.toggleSound();
	                if(soundButtonStyle.up == buttonSkin.getDrawable("sound-off-icon.up")){
	                	soundButtonStyle.up = buttonSkin.getDrawable("sound-on-icon.up");
	                	soundButtonStyle.down = buttonSkin.getDrawable("sound-on-icon.down");
	                	if(!bgm.isPlaying()) bgm.play();
	                }
	                else{
	                	soundButtonStyle.up = buttonSkin.getDrawable("sound-off-icon.up");
	                	soundButtonStyle.down = buttonSkin.getDrawable("sound-off-icon.down");
	                	if(bgm.isPlaying()) bgm.stop();
	                }
	        	}
	        }
		});	
		pauseTable.add(soundButton).space(scale * BUTTONPADDING).width(scale * BUTTONSIZE).height(scale * BUTTONSIZE);;
		pauseTable.row();
	}
	
	private void createWinStage(){	
		winStage = new Stage();
		winStage.clear();		
		Gdx.input.setInputProcessor(winStage);	
		final ScreenManager scM = this.screenManager;
		if(this.settingsManager.soundOn()) winSound.play();
		
		ImageButtonStyle menuButtonStyle = new ImageButtonStyle();
		menuButtonStyle.up = buttonSkin.getDrawable("menu-icon.up");
		menuButtonStyle.down = buttonSkin.getDrawable("menu-icon.down");
		
		ImageButtonStyle retryButtonStyle = new ImageButtonStyle();
		retryButtonStyle.up = buttonSkin.getDrawable("retry-icon.up");
		retryButtonStyle.down = buttonSkin.getDrawable("retry-icon.down");
		
		ImageButtonStyle nextButtonStyle = new ImageButtonStyle();
		nextButtonStyle.up = buttonSkin.getDrawable("next-icon.up");
		nextButtonStyle.down = buttonSkin.getDrawable("next-icon.down");
						
		winTable = new Table(buttonSkin);
		winTable.debug();
		winTable.center();	
		winTable.setPosition(Gdx.app.getGraphics().getWidth()/4, Gdx.app.getGraphics().getHeight()/4);
		winTable.setSize(Gdx.app.getGraphics().getWidth()/2, Gdx.app.getGraphics().getHeight()/2);
		
		LabelStyle labelStyle = new LabelStyle();
		labelStyle.font = font;
		
		String s = "";
		if(entityManager.getAttempts() > 1) s = "s";
		Label winText = new Label("You have completed this level with " + entityManager.getAttempts() + " Attempt" + s + "!", labelStyle);
		winText.setWrap(true);
		winTable.add(winText).width(winTable.getWidth() - 20).colspan(3);
		winTable.row();
		
		if(currentScore > entityManager.getAttempts()){
			Label newRecord = new Label("New record!", labelStyle);
			newRecord.setWrap(true);
			winTable.add(newRecord).width(winTable.getWidth() - 20).colspan(3);
			winTable.row();
		}
		
		menuButton = new ImageButton(menuButtonStyle);
		menuButton.setSize(scale * BUTTONSIZE, scale * BUTTONSIZE);
		menuButton.addListener(new InputListener(){
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                return true;
			}
       
	        public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
	        	if(menuButton.getOriginX() < x && x < menuButton.getWidth()
	        			&& menuButton.getOriginY() < y && y < menuButton.getHeight()){
	        		scM.setScreen(new MenuScreen(game));
	        	}
	        }
		});
		winTable.add(menuButton).space(scale * BUTTONPADDING).width(scale * BUTTONSIZE).height(scale * BUTTONSIZE);
		
		retryButton = new ImageButton(retryButtonStyle);
		retryButton.setSize(scale * BUTTONSIZE, scale * BUTTONSIZE);
		retryButton.addListener(new InputListener(){
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                return true;
			}
       
	        public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
	        	if(retryButton.getOriginX() < x && x < retryButton.getWidth()
	        			&& retryButton.getOriginY() < y && y < retryButton.getHeight()){
	        		scM.setScreen(new GameScreen(game, currentLevel));
	        	}
	        }
		});		
		winTable.add(retryButton).space(scale * BUTTONPADDING).width(scale * BUTTONSIZE).height(scale * BUTTONSIZE);
		
		if(currentLevel < LevelManager.AmountLevels){
			nextButton = new ImageButton(nextButtonStyle);
			nextButton.setSize(scale * BUTTONSIZE, scale * BUTTONSIZE);
			nextButton.addListener(new InputListener(){
				public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
	                return true;
				}
	       
		        public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
		        	if(nextButton.getOriginX() < x && x < nextButton.getWidth()
		        			&& nextButton.getOriginY() < y && y < nextButton.getHeight()){
		        		scM.setScreen(new GameScreen(game, currentLevel + 1));
		        	}
		        }
			});	
			winTable.add(nextButton).space(scale * BUTTONPADDING).width(scale * BUTTONSIZE).height(scale * BUTTONSIZE);
		}
		winStage.addActor(winTable);
	}
}