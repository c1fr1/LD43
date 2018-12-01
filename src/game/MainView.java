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
	
	public MainView(EnigWindow window) {
		super(window);
		glDisable(GL_DEPTH_TEST);
		perspectiveMatrix = window.getSquarePerspectiveMatrix(100);
		
		Weapon.loadTextures();
		Sword.loadFrames();
		tile = new Texture("res/textures/tile.png");
		Player.playerTexture = new Texture("res/textures/player.png");
		Enemy.enemyTexture = new Texture("res/textures/ghost.png");
		
		player.weapons.add(new Sword(player));
		player.weapons.get(0).burning = true;
	}
	
	@Override
	public boolean loop() {
		FBO.prepareDefaultRender();
		FBO.clearCurrentFrameBuffer();
		
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
		
		if (player.weapons.size() > 0) {
			player.renderWeaponUI(window, getUnTranslatedPerspectiveMatrix());
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
		double rand = Math.random();
		if (Math.random() < 1f / ((float) weapons.size()) || weapons.size() == 0) {
			if (rand > 0.999) {
				weapons.add(new Scythe(player));
			} else if (rand > 0.995) {
				weapons.add(new Spear(player));
			} else if (rand > 0.992) {
				weapons.add(new Shield(player));
			} else if (rand > 0.98) {
				weapons.add(new Sword(player));
			}
		}
		for (int i = 0; i < weapons.size(); ++i) {
			Weapon w = weapons.get(i);
			float dist = w.distanceSquared(player);
			if (dist > player.torchStrength * 2f + 20f) {
				weapons.remove(i);
				--i;
			}else {
				if (dist < 25) {
					player.weapons.add(w);
					weapons.remove(i);
					--i;
				}
			}
		}
	}
	
	public void generateEnemies() {
		if (Math.random() < 0.02) {
			enemies.add(new Enemy(player));
		}
	}
	
	public void renderEnemies() {
		textureShader.enable();
		entityObj.prepareRender();
		Enemy.enemyTexture.bind();
		for (int i = 0; i < enemies.size(); ++i) {
			Enemy e = enemies.get(i);
			if (player.shouldKill(e)) {
				enemies.remove(e);
				--i;
				continue;
			}
			float enemyAngle = e.facingAngle(player);
			textureShader.setUniform(0, 0, getPerspectiveMatrix().translate(e.x, e.y, 0).rotateZ(enemyAngle));
			e.update(enemyAngle);
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
	
	public Matrix4f getPerspectiveMatrix() {
		return new Matrix4f(perspectiveMatrix).translate(-camPos.x, -camPos.y, 0);
	}
	public Matrix4f getUnTranslatedPerspectiveMatrix() {
		return new Matrix4f(perspectiveMatrix);
	}
}
