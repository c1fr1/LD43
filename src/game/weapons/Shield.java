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
import static game.WeaponType.shield;
import static java.lang.Math.sqrt;
import static org.joml.Math.PI;
import static org.joml.Math.atan2;

public class Shield extends Weapon {
	public static Texture[] attackFrames;
	public static VAO attackVAO;
	
	public Shield(Player p) {
		super(p);
		type = shield;
	}
	
	public void renderAttack(Matrix4f matrix) {
		attackShader.enable();
		attackShader.setUniform(0, 0, matrix.rotateZ(attackAngle));
		if (burning) {
			attackShader.setUniform(2, 0, 1f, 0.6f, 0.22f);
		}else {
			attackShader.setUniform(2, 0, 0.8f, 0f, 0f);
		}
		if (attackFrame == 16) {
			attackFrames[0].bind();
		}else {
			attackFrames[5 - attackFrame / 3].bind();
		}
		attackVAO.fullRender();
	}
	
	public void renderIdle(Matrix4f mat) {
		if (attackFrame > 3) {
			float frameProgress = 1 - ((float) attackFrame - 3f) / 13f;
			textureShader.setUniform(0, 0, mat.rotateZ((float) -PI/2).scale(0.5f + frameProgress * frameProgress).translate(0, 5f + 4 * frameProgress, 0));
		}else {
			float frameProgress = (attackFrame - 3f)/13f + 1f;
			textureShader.setUniform(0, 0, mat.rotateZ((float) -PI/2).scale(0.5f + frameProgress * 0.5f).translate(0, 5f + 4 * frameProgress, 0));
		}
		bindTexture();
		entityObj.drawTriangles();
	}
	
	public boolean attackHits(Vector2f position, Vector2f player) {
		Vector2f change = position.sub(player, new Vector2f());
		float distance = change.length();
		float progress = 20 * (1 - ((float) attackFrame - 3f) / 13f);
		if (distance < progress) {
			float angleDiff = compareAngles(attackAngle, (float) atan2(position.y - player.y, position.x - player.x));
			if (angleDiff < PI/2 + 0.01f) {
				if (burning) {
					durability *= 0.95f;
					return true;
				}else {
					Vector2f nPos = change.normalize(progress).add(player);
					position.x = nPos.x;
					position.y = nPos.y;
				}
			}
		}
		return false;
	}
	
	public float maxDurability() {
		return 30;
	}
	
	public static void loadFrames() {
		attackFrames = new Texture[5];
		for (int i = 0; i < 5; ++i) {
			attackFrames[i] = new Texture("res/textures/weaponAnims/shield/" + i + ".png");
		}
		attackVAO = new VAO(-5, -15f, 20, 30);
	}
}
