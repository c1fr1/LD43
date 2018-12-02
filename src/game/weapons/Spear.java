package game.weapons;

import engine.OpenGL.Texture;
import engine.OpenGL.VAO;
import game.Player;
import game.Weapon;
import org.joml.Matrix4f;
import org.joml.Vector2f;

import static engine.EnigUtils.compareAngles;
import static game.Main.entityObj;
import static game.Shaders.attackShader;
import static game.Shaders.textureShader;
import static game.WeaponType.spear;
import static org.joml.Math.PI;
import static org.joml.Math.atan2;

public class Spear extends Weapon {
	public static Texture[] attackFrames;
	public static VAO attackVAO;
	
	public Spear(Player p) {
		super(p);
		type = spear;
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
		attackFrames[16 - attackFrame].bind();
		attackVAO.fullRender();
	}
	
	public void renderIdle(Matrix4f mat) {
		if (attackFrame > 3) {
			float frameProgress = 1 - ((float) attackFrame - 3f) / 13f;
			textureShader.setUniform(0, 0, mat.rotateZ((float) -PI/2).scale(0.5f).translate(0, 6f + 30 * frameProgress, 0));
		}else {
			float frameProgress = (attackFrame - 3f)/13f + 1f;
			textureShader.setUniform(0, 0, mat.rotateZ((float) -PI/2).scale(0.5f).translate(0, 6f + 30 * frameProgress, 0));
		}
		bindTexture();
		entityObj.drawTriangles();
	}
	
	public boolean attackHits(Vector2f position, Vector2f player) {
		float distance = position.distance(player);
		if (distance < 30 - attackFrame * 2) {
			float angleDiff = compareAngles(attackAngle, (float) atan2(position.y - player.y, position.x - player.x));
			if (angleDiff < 0.1) {
				return true;
			}
		}
		return false;
	}
	
	public float maxDurability() {
		return 5;
	}
	
	public static void loadFrames() {
		attackFrames = new Texture[15];
		for (int i = 0; i < 15; ++i) {
			attackFrames[i] = new Texture("res/textures/weaponAnims/spear/" + i + ".png");
		}
		attackVAO = new VAO(-5, -5f, 30, 10);
	}
}
