package game.weapons;

import game.Player;
import game.Weapon;
import org.joml.Matrix4f;
import org.joml.Vector2f;

import static game.WeaponType.spear;

public class Spear extends Weapon {
	
	public Spear(Player p) {
		super(p);
		type = spear;
		durability = 5;
	}
	
	public void renderAttack(Matrix4f matrix) {
	
	}
	
	public boolean attackHits(Vector2f position, Vector2f player) {
		return false;
	}
}
