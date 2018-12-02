package game.weapons;

import engine.OpenGL.Texture;
import engine.OpenGL.VAO;
import game.Player;
import game.Weapon;
import game.WeaponType;
import org.joml.Matrix4f;
import org.joml.Vector2f;

import static engine.EnigUtils.compareAngles;
import static game.Shaders.attackShader;
import static game.WeaponType.scythe;
import static org.joml.Math.PI;
import static org.joml.Math.atan2;

public class Scythe extends Weapon {
	public static Texture attackFrameTex;
	public static VAO attackVAO;
	
	public Scythe(Player p) {
		super(p);
		type = scythe;
	}
	
	public void renderAttack(Matrix4f matrix) {
		attackShader.enable();
		float frameProgress = 1 - ((float) attackFrame - 3f) / 13f;
		attackShader.setUniform(0, 0, matrix.rotateZ(attackAngle - (float) (PI * frameProgress)));
		if (burning) {
			attackShader.setUniform(2, 0, 1f, 0.6f, 0.22f);
		}else if (bloodied) {
			attackShader.setUniform(2, 0, 1f, 0.6f, 0.22f);
		}else {
			attackShader.setUniform(2, 0, 0.8f, 0f, 0f);
		}
		attackFrameTex.bind();
		attackVAO.fullRender();
	}
	
	public boolean attackHits(Vector2f position, Vector2f player) {
		float distance = position.distance(player);
		if (distance < 15) {
			float frameProgress = 1 - ((float) attackFrame - 3f) / 13f;
			float angleDiff = compareAngles(attackAngle - (float) (-PI/2 + PI * frameProgress), (float) atan2(position.y - player.y, position.x - player.x));
			if (angleDiff < PI/10) {
				return true;
			}
		}
		return false;
	}
	
	public float maxDurability() {
		return 30;
	}
	
	public static void loadFrames() {
		attackFrameTex = new Texture("res/textures/weaponAnims/scythe/0.png");
		attackVAO = new VAO(-5, -15f, 20, 30);
	}
}
