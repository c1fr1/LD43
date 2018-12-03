package game;

import engine.OpenGL.EnigWindow;
import engine.OpenGL.Texture;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.ArrayList;

import static game.Main.entityObj;
import static game.Shaders.textureShader;
import static java.lang.Math.*;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

public class Enemy extends Vector2f {
	public static Texture enemyTexture;
	
	public ParticleRenderer particles;
	public float eliteTimer = -2;
	
	public int hp;
	
	public Enemy(Player p) {
		double angle = random() * PI * 2;
		float distance = (float) (sqrt(p.torchStrength) + random() * 10f) + 5f;
		x = (float) cos(angle) * distance + p.x;
		y = (float) sin(angle) * distance + p.y;
		
		particles = new ParticleRenderer(0, 0, 0, 25);
		particles.colorOffsetCoeff.x = 0;
		particles.colorOffsetCoeff.y = 0;
		particles.colorOffsetCoeff.z = 0;
	}
	
	public void update(float angle) {
		if (eliteTimer > 0) {
			x += cos(angle) * (0.1f + eliteTimer * 0.1 * eliteTimer);
			y += sin(angle) * (0.1f + eliteTimer * 0.1 * eliteTimer);
			particles.colorOffsetCoeff.x = 0.4f * eliteTimer;
			particles.colorOffsetCoeff.z = 0.8f * eliteTimer;
		}else {
			x += cos(angle) * 0.1f;
			y += sin(angle) * 0.1f;
		}
		if (eliteTimer > -2f && eliteTimer < 1f) {
			eliteTimer += 0.02f;
		}
	}
	
	public float facingAngle(Player p) {
		return (float) Math.atan2(p.y - y, p.x - x);
	}
}
