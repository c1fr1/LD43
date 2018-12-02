package game;

import engine.*;
import engine.OpenGL.*;
import game.weapons.Scythe;
import game.weapons.Shield;
import game.weapons.Spear;
import game.weapons.Sword;
import org.joml.Matrix4f;
import org.joml.Vector2f;

import java.util.ArrayList;

import static game.Main.entityObj;
import static game.Main.gameOverOpacity;
import static game.Main.titleObj;
import static game.Shaders.*;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.glDisable;

public class MainView extends EnigView {
	public static MainView main;
	
	public Matrix4f perspectiveMatrix;
	
	public Vector2f camPos = new Vector2f();
	public Player player = new Player();
	
	public ArrayList<Weapon> weapons = new ArrayList<>();
	public ArrayList<Enemy> enemies = new ArrayList<>();
	
	public Texture tile;
	public Texture gameoverTexture;
	
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
		Player.playerTexture = new Texture("res/textures/player.png");
		Enemy.enemyTexture = new Texture("res/textures/ghost.png");
		
		Player.weapUIObj = new VAO(-5, -5, 10, 10, 1);
		
		player.weapons.add(new Scythe(player));
		player.weapons.get(0).burning = true;
	}
	
	@Override
	public boolean loop() {
		FBO.prepareDefaultRender();
		FBO.clearCurrentFrameBuffer();
		
		if (UserControls.restart(window)) {
			restart();
		}
		
		generateEnemies();
		manageSpawns();
		
		renderScene();
		
		updateCamera();
		
		if (UserControls.quit(window)) {
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
			double rand = Math.random();
			if (Math.random() < 1f / ((float) weapons.size()) || weapons.size() == 0) {
				if (rand > 0.999) {
					weapons.add(new Scythe(player));
				} else if (rand > 0.995) {
					weapons.add(new Shield(player));
				} else if (rand > 0.992) {
					weapons.add(new Sword(player));
				} else if (rand > 0.98) {
					weapons.add(new Spear(player));
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
					}
				}
			}
		}
	}
	
	public void generateEnemies() {
		if (gameOverOpacity > 0) {
			if (Math.random() > 0.02) {
				enemies.add(new Enemy(player));
			}
			while (enemies.size() > 1000) {
				enemies.remove(0);
			}
		}else {
			if (Math.random() < 0.02) {
				enemies.add(new Enemy(player));
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
					for (int j = 0; j < player.weapons.size(); ++j) {
						if (!player.weapons.get(j).burning) {
							player.weapons.remove(j);
							--j;
						}
					}
					player.selectedWeaponIndex = 0;
				}
				if (player.shouldKill(e)) {
					enemies.remove(e);
					--i;
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
		entityObj.unbind();
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
	}
	
	public Matrix4f getPerspectiveMatrix() {
		return new Matrix4f(perspectiveMatrix).translate(-camPos.x, -camPos.y, 0);
	}
	public Matrix4f getUnTranslatedPerspectiveMatrix() {
		return new Matrix4f(perspectiveMatrix);
	}
}
