package game;

import engine.OpenGL.EnigWindow;
import engine.OpenGL.Texture;
import engine.OpenGL.VAO;
import org.joml.Matrix4f;
import org.joml.Vector2f;

import java.util.ArrayList;

import static game.Shaders.textureShader;
import static game.WeaponType.*;
import static java.lang.Math.*;
import static java.lang.Math.sin;

public abstract class Weapon extends Vector2f {
	public static Texture swordTexture;
	public static Texture shieldTexture;
	public static Texture scytheTexture;
	public static Texture spearTexture;
	public static Texture styreneMonomerTexture;
	
	public static Texture flamingSwordTexture;
	public static Texture flamingShieldTexture;
	public static Texture flamingScytheTexture;
	public static Texture flamingSpearTexture;
	
	public static VAO weapOBJ;
	
	public int attackFrame = -10;
	public float attackAngle = 0;
	public float durability;
	public boolean burning;
	public boolean bloodied;
	
	public WeaponType type;
	
	public Weapon(Player p) {
		double angle = random() * PI * 2;
		float distance = (float) (sqrt(p.torchStrength) + random() * 10f) + 5f;
		x = (float) cos(angle) * distance + p.x;
		y = (float) sin(angle) * distance + p.y;
		durability = maxDurability();
	}
	
	public boolean startAttack(EnigWindow w, Player player, Vector2f camPos) {
		if (attackFrame <= -10) {
			if (durability == 0) {
				return false;
			}
			if (burning) {
				durability -= 1f;
			}
			--durability;
			attackAngle = player.facingAngle(w, camPos);
			attackFrame = 16;
		}
		return true;
	}
	
	public abstract void renderAttack(Matrix4f matrix);
	
	public abstract void renderIdle(Matrix4f mat);
	
	public abstract boolean attackHits(Vector2f position, Vector2f player);
	
	public abstract float maxDurability();
	
	public static void loadTextures() {
		swordTexture = new Texture("res/textures/weapons/sword.png");
		shieldTexture = new Texture("res/textures/weapons/shield.png");
		scytheTexture = new Texture("res/textures/weapons/scythe.png");
		spearTexture = new Texture("res/textures/weapons/spear.png");
		styreneMonomerTexture = new Texture("res/textures/weapons/styrene.png");
		
		flamingSwordTexture = new Texture("res/textures/weapons/flamingSword.png");
		flamingShieldTexture = new Texture("res/textures/weapons/flamingShield.png");
		flamingScytheTexture = new Texture("res/textures/weapons/flamingScythe.png");
		flamingSpearTexture = new Texture("res/textures/weapons/flamingSpear.png");
		
		weapOBJ = new VAO(-2, -2, 4, 4);
	}
	public static void renderWeapons(ArrayList<Weapon> weapons, Matrix4f perspectiveArray) {
		textureShader.enable();
		weapOBJ.prepareRender();
		for (Weapon w : weapons) {
			w.bindTexture();
			textureShader.setUniform(0, 0, perspectiveArray.translate(w.x, w.y, 0, new Matrix4f()));
			weapOBJ.drawTriangles();
		}
		weapOBJ.unbind();
	}
	
	public void bindTexture() {
		if (type == sword) {
			if (burning) {
				flamingSwordTexture.bind();
			}else {
				swordTexture.bind();
			}
		}else if (type == spear) {
			if (burning) {
				flamingSpearTexture.bind();
			}else {
				spearTexture.bind();
			}
		}else if (type == shield) {
			if (burning) {
				flamingShieldTexture.bind();
			}else {
				shieldTexture.bind();
			}
		}else if (type == scythe) {
			if (burning) {
				flamingScytheTexture.bind();
			}else {
				scytheTexture.bind();
			}
		}else if (type == styreneMonomer) {
			styreneMonomerTexture.bind();
		}
	}
}
