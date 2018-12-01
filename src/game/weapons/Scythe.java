package game.weapons;

import game.Player;
import game.Weapon;
import game.WeaponType;
import org.joml.Matrix4f;
import org.joml.Vector2f;

import static game.WeaponType.scythe;

public class Scythe extends Weapon {
	
	public Scythe(Player p) {
		super(p);
		type = scythe;
		durability = 30;
	}
	
	public void renderAttack(Matrix4f matrix) {
	
	}
	
	public boolean attackHits(Vector2f position, Vector2f player) {
		return false;
	}
}
