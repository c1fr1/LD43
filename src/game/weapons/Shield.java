package game.weapons;

import game.Player;
import game.Weapon;
import org.joml.Matrix4f;
import org.joml.Vector2f;

import static game.WeaponType.shield;

public class Shield extends Weapon {
	
	public Shield(Player p) {
		super(p);
		type = shield;
		durability = 50;
	}
	
	public void renderAttack(Matrix4f matrix) {
	
	}
	
	public boolean attackHits(Vector2f position, Vector2f player) {
		return false;
	}
}
