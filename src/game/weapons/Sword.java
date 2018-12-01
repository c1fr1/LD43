package game.weapons;

import engine.OpenGL.Texture;
import engine.OpenGL.VAO;
import game.Player;
import game.Weapon;
import org.joml.Matrix4f;
import org.joml.Vector2f;

import static engine.EnigUtils.compareAngles;
import static game.Shaders.attackShader;
import static game.Shaders.textureShader;
import static game.WeaponType.*;
import static org.joml.Math.atan2;

public class Sword extends Weapon {
	public static Texture[] attackFrames;
	public static VAO attackVAO;
	public boolean bloodied;
	public static void loadFrames() {
		attackFrames = new Texture[4];
		for (int i = 0; i < 4; ++i) {
			attackFrames[i] = new Texture("res/textures/weaponAnims/sword/" + i + ".png");
		}
		attackVAO = new VAO(-5, -5, 20, 10);
	}
	
	public Sword(Player p) {
		super(p);
		type = sword;
		durability = 10;
	}
	
	public void renderAttack(Matrix4f matrix) {
		attackShader.enable();
		attackShader.setUniform(0, 0, matrix.rotateZ(attackAngle));
		if (burning) {
			attackShader.setUniform(2, 0, 1f, 0.6f, 0.22f);
		}else if (bloodied) {
			attackShader.setUniform(2, 0, 1f, 0.6f, 0.22f);
		}else {
			attackShader.setUniform(2, 0, 0.8f, 0f, 0f);
		}
		attackFrames[4-attackFrame/4].bind();
		attackVAO.fullRender();
	}
	
	public boolean attackHits(Vector2f position, Vector2f player) {
		float distance = position.distance(player);
		if (distance < 12 - attackFrame / 4) {
			float angleDiff = compareAngles(attackAngle, (float) atan2(position.y - player.y, position.x - player.x));
			if (angleDiff < 0.01 * distance * distance) {
				return true;
			}
		}
		return false;
	}
}