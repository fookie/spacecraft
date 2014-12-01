package spacecraftelements.Weapon;

import spacecraftelements.Bullets.BasicBullet;
import spacecraftelements.Bullets.Bullet;

public class BasicWeapon extends Weapon {
	public BasicWeapon() {
		super.defaultbullet = 0;
		super.clip = 10;
		super.iv = 100;
		super.reload = 1;
		super.ID = 0;
	}

	public Bullet shoot(int x, int y, int cx, int cy) {
		double a, b, c;
		a = cx - x;
		b = cy - y;
		c = Math.sqrt(a*a+b*b);
		double sin = b/c;
		double cos = a/c;
		int vx = (int) (iv * cos);
		int vy = (int) (iv * sin);
		Bullet bullet = new BasicBullet(x, y, vx, vy);
		return bullet;
	}
}