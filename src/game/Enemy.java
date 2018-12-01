package game;

import engine.OpenGL.EnigWindow;
import engine.OpenGL.Texture;
import org.joml.Matrix4f;
import org.joml.Vector2f;

import java.util.ArrayList;

import static game.Main.entityObj;
import static game.Shaders.textureShader;
import static java.lang.Math.*;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

public class Enemy extends Vector2f {
	public static Texture enemyTexture;
	
	public int hp;
	
	public Enemy(Player p) {
		double angle = random() * PI * 2;
		float distance = (float) (sqrt(p.torchStrength) + random() * 10f) + 5f;
		x = (float) cos(angle) * distance + p.x;
		y = (float) sin(angle) * distance + p.y;
	}
	
	public void update(float angle) {
		x += cos(angle) * 0.1f;
		y += sin(angle) * 0.1f;
	}
	
	public float facingAngle(Player p) {
		return (float) Math.atan2(p.y - y, p.x - x);
	}
}
