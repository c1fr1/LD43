package game;

import engine.OpenGL.EnigWindow;
import engine.OpenGL.Texture;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.lwjglx.debug.org.eclipse.jetty.websocket.api.SuspendToken;

import java.util.ArrayList;

import static game.Main.entityObj;
import static game.Shaders.textureShader;
import static game.WeaponType.*;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

public class Player extends Vector2f {
	public static Texture playerTexture;
	
	public float torchStrength = 1000f;
	
	public ArrayList<Weapon> weapons = new ArrayList<>();
	
	public int selectedWeaponIndex;
	
	public void update(EnigWindow w, Vector2f camPos) {
		Vector2f movement = new Vector2f();
		if (UserControls.up(w)) {
			movement.y += 1;
		}
		if (UserControls.down(w)) {
			movement.y -= 1;
		}
		if (UserControls.left(w)) {
			movement.x -= 1;
		}
		if (UserControls.right(w)) {
			movement.x += 1;
		}
		
		float mlength = movement.length();
		if (mlength > 0.01) {
			movement.mul(0.15f / movement.length());
			add(movement);
		}
		
		if (weapons.size() > 0) {
			if (UserControls.weaponUp(w)) {
				--selectedWeaponIndex;
			}
			if (UserControls.weaponDown(w)) {
				++selectedWeaponIndex;
			}
			if (selectedWeaponIndex < 0) {
				selectedWeaponIndex = 0;
			}else if (selectedWeaponIndex >= weapons.size()) {
				selectedWeaponIndex = weapons.size() - 1;
			}
			if (UserControls.burn(w)) {
				weapons.get(selectedWeaponIndex).burning = true;
			}
		}
		
		if (w.mouseButtons[GLFW_MOUSE_BUTTON_LEFT] > 1) {
			if (weapons.size() > 0) {
				if (selectedWeaponIndex < weapons.size()) {
					if (!weapons.get(selectedWeaponIndex).startAttack(w, this, camPos)) {
						weapons.remove(selectedWeaponIndex);
						if (weapons.size() == selectedWeaponIndex) {
							--selectedWeaponIndex;
							if (selectedWeaponIndex == -1) {
								selectedWeaponIndex = 0;
							}
						}
					}
				}
			}
		}
		
		torchStrength = 0;
		for (int i = 0; i < weapons.size(); ++i) {
			Weapon weap = weapons.get(i);
			if (weap.burning) {
				weap.durability -= 0.005f;
				if (weap.durability <= 0f) {
					weapons.remove(i);
					--i;
					if (weapons.size() > 0) {
						if (selectedWeaponIndex >= weapons.size()) {
							--selectedWeaponIndex;
						}
					}
				}else {
					torchStrength += weap.durability * 100;
					if (weap.attackFrame > 0) {
						torchStrength += weap.attackFrame * 25f;
					}
				}
			}
		}
		if (torchStrength < 1f) {
			torchStrength = 0.001f;
		}
	}
	
	public float facingAngle(EnigWindow w, Vector2f camPos) {
		float worldX = w.cursorXFloat * 50f * w.getAspectRatio();
		float worldY = w.cursorYFloat * 50f;
		return (float) Math.atan2(worldY - y + camPos.y, worldX - x + camPos.x);
	}
	
	public void renderPlayer(Matrix4f perspectiveMatrix, EnigWindow w, Vector2f camPos) {
		textureShader.enable();
		playerTexture.bind();
		textureShader.setUniform(0, 0, new Matrix4f(perspectiveMatrix).rotateZ(facingAngle(w, camPos)));
		entityObj.fullRender();
		if (weapons.size() > 0) {
			Weapon selectedWeapon = weapons.get(selectedWeaponIndex);
			if (selectedWeapon.attackFrame > 3) {
				selectedWeapon.renderAttack(perspectiveMatrix);
			}
			--selectedWeapon.attackFrame;
			if (selectedWeapon.attackFrame < -10) {
				selectedWeapon.attackFrame = -10;
			}
		}
	}
	
	public boolean shouldKill(Vector2f enemy) {
		if (weapons.size() > 0) {
			Weapon selectedWeapon = weapons.get(selectedWeaponIndex);
			if (selectedWeapon.attackFrame > 3) {
				return selectedWeapon.attackHits(enemy, this);
			}
		}
		return false;
	}
	
	public void renderWeaponUI(EnigWindow w, Matrix4f mat) {
		textureShader.enable();
		Weapon weap;
		entityObj.prepareRender();
		for (int i = 0; i < weapons.size(); ++i) {
			if (i != selectedWeaponIndex) {
				textureShader.setUniform(0, 0, mat.translate(w.getAspectRatio() * 50f - 2f, 6 * (selectedWeaponIndex - i), 0f, new Matrix4f()).scale(0.5f));
				weap = weapons.get(i);
				weap.bindTexture();
				entityObj.drawTriangles();
			}
		}
		textureShader.setUniform(0, 0, mat.translate(w.getAspectRatio() * 50f - 5, 0f, 0f, new Matrix4f()));
		weap = weapons.get(selectedWeaponIndex);
		weap.bindTexture();
		entityObj.drawTriangles();
		entityObj.unbind();
	}
}
