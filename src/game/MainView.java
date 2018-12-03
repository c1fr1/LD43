package game;

import engine.*;
import engine.OpenAL.Sound;
import engine.OpenAL.SoundSource;
import engine.OpenGL.*;
import game.weapons.*;
import org.joml.Matrix4f;
import org.joml.Vector2f;

import java.util.ArrayList;

import static game.Main.entityObj;
import static game.Main.gameOverOpacity;
import static game.Main.titleObj;
import static game.Shaders.*;
import static java.lang.Math.random;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.glDisable;

public class MainView extends EnigView {
	public static MainView main;
	
	public Matrix4f perspectiveMatrix;
	
	public Vector2f camPos = new Vector2f();
	public Player player = new Player();
	
	public ArrayList<Weapon> weapons = new ArrayList<>();
	public ArrayList<Enemy> enemies = new ArrayList<>();
	public ArrayList<Enemy> dyingEnemies = new ArrayList<>();
	
	public Texture tile;
	public Texture gameoverTexture;
	public Texture[] tutorialFrames;
	
	public SoundSource sfxSource;
	public SoundSource[] deathSources;
	public Sound[] breakingSounds;
	public Sound[] deathSounds;
	
	public NumberRenderer scoreRenderer;
	int score = 0;
	
	public int deathIndex = 0;
	
	public int tutorialFrame = -1;
	
	public MainView(EnigWindow window) {
		super(window);
		glDisable(GL_DEPTH_TEST);
		perspectiveMatrix = window.getSquarePerspectiveMatrix(100);
		
		Weapon.loadTextures();
		Sword.loadFrames();
		Scythe.loadFrames();
		Spear.loadFrames();
		Shield.loadFrames();
		tile = new Texture("res/textures/tile2.png");
		gameoverTexture = new Texture("res/textures/gameOverText.png");
		Player.playerTexture = new Texture("res/textures/player0.png");
		Enemy.enemyTexture = new Texture("res/textures/ghost.png");
		
		tutorialFrames = new Texture[4];
		for (int i = 0; i < 4; ++i) {
			tutorialFrames[i] = new Texture("res/textures/tutorialFrames/" + i + ".png");
		}
		
		Player.particles = new ParticleRenderer(1f, 0.2f, 0f, 50);
		Player.weapUIObj = new VAO(-5, -5, 10, 10, 1);
		
		player.weapons.add(new Scythe(player));
		if (tutorialFrame == -1) {
			player.weapons.get(0).burning = true;
		}
		
		sfxSource = new SoundSource();
		breakingSounds = new Sound[2];
		for (int i = 0; i < breakingSounds.length; ++i) {
			breakingSounds[i] = new Sound("res/sounds/breaking/" + i + ".wav");
		}
		deathSources = new SoundSource[10];
		for (int i = 0; i < deathSources.length; ++i) {
			deathSources[i] = new SoundSource();
		}
		deathSounds = new Sound[5];
		for (int i = 0; i < deathSounds.length; ++i) {
			deathSounds[i] = new Sound("res/sounds/dying/" + i + ".wav");
		}
		
		scoreRenderer = new NumberRenderer(window.getWidth(), window.getHeight(), 50, -window.getWidth()/2 + 25, -window.getHeight()/2 + 50);
	}
	
	public boolean loop() {
		FBO.prepareDefaultRender();
		FBO.clearCurrentFrameBuffer();
		
		if (UserControls.restart(window)) {
			restart();
		}
		
		if (tutorialFrame == -1) {
			generateEnemies();
		}
		if (tutorialFrame != 0) {
			manageSpawns();
		}
		
		renderScene();
		scoreRenderer.renderNum(score);
		
		updateCamera();
		
		if (tutorialFrame >= 0) {
			textureShader.enable();
			textureShader.setUniform(0, 0, getUnTranslatedPerspectiveMatrix());
			tutorialFrames[tutorialFrame].bind();
			titleObj.fullRender();
		}
		
		if (UserControls.quit(window)) {
			restart();
			return true;
		}
		return false;
	}
	
	public void renderScene() {
		renderBackground();
		Weapon.renderWeapons(weapons, getPerspectiveMatrix());
		player.renderPlayer(getPerspectiveMatrix().translate(player.x, player.y, 0), window, camPos);
		renderEnemies();
		renderWall();
		
		renderUI();
		if (gameOverOpacity > 0) {
			float ratio = player.torchStrength / gameOverOpacity;
		}
	}
	
	public void updateCamera() {
		player.update(window, camPos);
		
		final float camFollowDist = 15f;
		if (player.x - camPos.x > camFollowDist) {
			camPos.x = player.x - camFollowDist;
		}else if (camPos.x - player.x > camFollowDist) {
			camPos.x = player.x + camFollowDist;
		}
		
		if (player.y - camPos.y > camFollowDist) {
			camPos.y = player.y - camFollowDist;
		}else if (camPos.y - player.y > camFollowDist) {
			camPos.y = player.y + camFollowDist;
		}
	}
	
	public void manageSpawns() {
		if (gameOverOpacity < 0) {
			double rand = random();
			if (random() < 1f / ((float) weapons.size()) || weapons.size() == 0) {
				if (rand > 0.999) {
					weapons.add(new Scythe(player));
				} else if (rand > 0.995) {
					weapons.add(new Shield(player));
				} else if (rand > 0.992) {
					weapons.add(new Sword(player));
				} else if (rand > 0.98) {
					weapons.add(new Spear(player));
				} else if (rand > 0.979) {
					weapons.add(new StyreneMonomer(player));
				}
			}
			for (int i = 0; i < weapons.size(); ++i) {
				Weapon w = weapons.get(i);
				float dist = w.distanceSquared(player);
				if (dist > player.torchStrength * 2f + 20f) {
					weapons.remove(i);
					--i;
				} else {
					if (dist < 25) {
						player.weapons.add(w);
						weapons.remove(i);
						--i;
						if (tutorialFrame == 2) {
							tutorialFrame = 3;
						}
					}
				}
			}
		}
	}
	
	public void generateEnemies() {
		if (gameOverOpacity > 0) {
			if (random() > 0.02) {
				enemies.add(new Enemy(player));
			}
			while (enemies.size() > 1000) {
				enemies.remove(0);
			}
		}else {
			if (random() < 0.02) {
				enemies.add(new Enemy(player));
			}
			if (random() < 0.0001f * enemies.size()) {
				int k = 0;
				while (enemies.get(k).eliteTimer > -1f) {
					if (k >= enemies.size()) {
						return;
					}else {
						++k;
					}
				}
				enemies.get(k).eliteTimer = -1;
			}
		}
	}
	
	public void renderWeaponUI() {
		if (player.weapons.size() > 0) {
			if (gameOverOpacity < 0) {
				player.renderWeaponUI(window, getUnTranslatedPerspectiveMatrix());
			}
		}
	}
	
	public void renderUI() {
		renderWeaponUI();
		if (gameOverOpacity > 0) {
			titleShader.enable();
			titleShader.setUniform(0, 0, getUnTranslatedPerspectiveMatrix());
			titleShader.setUniform(2, 0, player.x - camPos.x, player.y - camPos.y);
			titleShader.setUniform(2, 1, 1 - (player.torchStrength / gameOverOpacity));
			gameoverTexture.bind();
			titleObj.fullRender();
		}
	}
	
	public void renderEnemies() {
		textureShader.enable();
		entityObj.prepareRender();
		Enemy.enemyTexture.bind();
		for (int i = 0; i < enemies.size(); ++i) {
			Enemy e = enemies.get(i);
			float distsqrd = player.distanceSquared(e);
			if (gameOverOpacity < 0) {
				if (distsqrd < 36) {
					gameOverOpacity = player.torchStrength;
					if (gameOverOpacity < 1) {
						gameOverOpacity = 1;
					}
					for (int j = 0; j < player.weapons.size(); ++j) {
						if (!player.weapons.get(j).burning) {
							player.weapons.remove(j);
							--j;
						}
					}
					player.selectedWeaponIndex = 0;
				}
				if (player.shouldKill(e)) {
					++score;
					deathSources[deathIndex].setPitch(0.5f + (float) random()/3f);
					deathSources[deathIndex].playSound(deathSounds[(int)(random() * 5)]);
					++deathIndex;
					if (deathIndex >= deathSources.length) {
						deathIndex = 0;
					}
					dyingEnemies.add(e);
					e.eliteTimer = 0;
					e.particles.sustained = false;
					enemies.remove(i);
					--i;
					if (tutorialFrame == 1) {
						tutorialFrame = 2;
					}
					continue;
				}
			}
			float enemyAngle = e.facingAngle(player);
			textureShader.setUniform(0, 0, getPerspectiveMatrix().translate(e.x, e.y, 0).rotateZ(enemyAngle));
			if (distsqrd > 10) {
				e.update(enemyAngle);
			}
			entityObj.drawTriangles();
		}
		if (gameOverOpacity < 0) {
			entityObj.unbind();
			for (int i = 0; i < enemies.size(); ++i) {
				Enemy e = enemies.get(i);
				if (e.eliteTimer > -2f) {
					float enemyAngle = e.facingAngle(player);
					e.particles.updateAndRender(getPerspectiveMatrix().translate(e.x, e.y, 0).rotateZ(enemyAngle));
				}
			}
		}
		for (int i = 0; i < dyingEnemies.size(); ++ i) {
			Enemy e = dyingEnemies.get(i);
			float enemyAngle = e.facingAngle(player);
			e.particles.updateAndRender(getPerspectiveMatrix().translate(e.x, e.y, 0));
			e.eliteTimer += 0.01;
			if (e.eliteTimer > 1) {
				dyingEnemies.remove(i);
				--i;
			}
		}
	}
	
	public void renderBackground() {
		tileShader.enable();
		tileShader.setUniform(0, 0, window.getAspectRatio());
		tileShader.setUniform(0, 1, camPos);
		tile.bind();
		Main.screenObj.fullRender();
	}
	
	public void renderWall() {
		wallShader.enable();
		wallShader.setUniform(0, 0, window.getAspectRatio());
		wallShader.setUniform(0, 1, camPos);
		wallShader.setUniform(2, 0, player);
		wallShader.setUniform(2, 1, player.torchStrength);
		Main.screenObj.fullRender();
	}
	
	public void restart() {
		while (player.weapons.size() > 0) {
			player.weapons.remove(0);
		}
		while (weapons.size() > 0) {
			weapons.remove(0);
		}
		while (enemies.size() > 0) {
			enemies.remove(0);
		}
		camPos.x = 0;
		camPos.y = 0;
		gameOverOpacity = -1f;
		player.x = 0;
		player.y = 0;
		player.weapons.add(new Scythe(player));
		player.weapons.get(0).burning = true;
		score = 0;
	}
	
	public Matrix4f getPerspectiveMatrix() {
		return new Matrix4f(perspectiveMatrix).translate(-camPos.x, -camPos.y, 0);
	}
	public Matrix4f getUnTranslatedPerspectiveMatrix() {
		return new Matrix4f(perspectiveMatrix);
	}
}
