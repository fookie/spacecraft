package spacecraftelements.Enemy;

import spacecraftelements.Updatable;
import spacecraftelements.SpecialEffect.SpecialEffect;

public abstract class Enemy implements Updatable{
	/**
	 * 敌人贴图 image path
	 */
	public String imageID;
	/**
	 * 敌人贴图大小 image size
	 */
	public int imagesize;
	/**
	 *  敌人生成位置 initial position
	 */
	public int x, y;
	/**
	 * 	敌人攻击力 damage
	 */
	public int damage;
	/**
	 *  敌人移动速度
	 */
	public int v;
	public int vx,vy;
	/**
	 * 	敌人血量
	 */
	public int health;
	/**
	 * 	敌人体积
	 */
	public int volume;
	/**
	 * 	敌人自更新
	 */
	public double rotation;
	public int getscore;
	public abstract boolean update();
	public boolean hit=false;
	public int centerx,centery;
	public int radius;
   
	
	public abstract SpecialEffect deathwhisper();

}
