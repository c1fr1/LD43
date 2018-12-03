package game.weapons;

import game.MainView;
import game.Player;
import game.Weapon;
import game.WeaponType;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

import static game.WeaponType.styreneMonomer;

public class StyreneMonomer extends Weapon {
	
	public StyreneMonomer(Player p) {
		super(p);
		type = styreneMonomer;
	}
	
	public void renderAttack(Matrix4f matrix) {}
	
	public void renderIdle(Matrix4f mat) {
		float proportion = 1 - durability / maxDurability();
		float target = 1 - proportion;
		Player.particles.color.x += 0.1 * ((1 - proportion) - Player.particles.color.x);
		Player.particles.color.y += 0.1 * ((0.2f - 0.2f * proportion) - Player.particles.color.y);
		Player.particles.color.z += 0.1 * ((proportion) - Player.particles.color.z);
	}
	
	public boolean attackHits(Vector2f position, Vector2f player) {
		if (player.distanceSquared(position) < 50) {
			return true;
		}
		return false;
	}
	
	public float maxDurability() {
		return 5;
	}
}
