package spacecraftcore;

import java.awt.Rectangle;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import displayConsole.Gamewindow;
import displayConsole.Mousehud;
import displayConsole.Scoreprinter;
import displayConsole.WeaponSlot;
import spacecraftelements.Bullets.Bullet;
import spacecraftelements.Enemy.Enemy;
import spacecraftelements.Items.H_bulletblast;
import spacecraftelements.Items.H_bulletrain;
import spacecraftelements.Items.S_repair;
import spacecraftelements.Items.S_speedup;
import spacecraftelements.Items.SpaceItem;
import spacecraftelements.Items.W_BasicWeapon;
import spacecraftelements.Items.W_Shotgun;
import spacecraftelements.Items.W_StarWeapon;
import spacecraftelements.Items.W_rocketlauncher;
import spacecraftelements.SpaceShip.SpaceShip;
import spacecraftelements.SpecialEffect.SpecialEffect;
import spacecraftevent.SpaceEvent;

/**
 * BattleFieldManager负责更新战场以及地图，并把信息传递给DisplayManager This class is in charge of
 * battlefield updating and pass data to DisplayManager.
 * 
 * @method 添加元素：add()<br/>
 *         更新战场数据及逻辑(Update battle data and logics) update();
 * 
 * @author Hale
 *
 */
public class BattleFieldManager {
	public List<Bullet> BulletList = new LinkedList<Bullet>();
	public List<Enemy> EnemyList = new LinkedList<Enemy>();
	private List<SpaceEvent> EventList = new LinkedList<SpaceEvent>();
	private List<SpaceItem> ItemList = new LinkedList<SpaceItem>();
	private List<SpecialEffect> OList = new LinkedList<SpecialEffect>();
	public SpaceShip ship = null;
	public SpaceShip p2_ship = null;
	public int mapx, mapy;// 地图实际大小//mapsize
	private String bgloc;// 背景地址//background image location
	private DataInputStream mapin;
	public int autotarx, autotary;// 按住自动发炮的变量//autoshoot target
	public int windowsizex, windowsizey;
	public boolean paused = false;
	public int score = 0;
	public int imgnum = 0;
	public boolean isshooting = false;
	public long cautiontime = 0;
	public String lan_buffer = "";
	public boolean lan_new = false;

	public SpaceShip getShip() {
		return ship;
	}

	public void SetShip(SpaceShip ship) {
		this.ship = ship;
	}

	/**
	 * 
	 * 在这里输入输入窗口大小</br>BattlefieldManager get the windowsize here
	 * 
	 * @param windowsizex
	 * @param windowsizey
	 */
	public BattleFieldManager(int windowsizex, int windowsizey) {
		this.windowsizex = windowsizex;
		this.windowsizey = windowsizey;

		// 预处理//load image to prevent lag
		// add(new Bigslime(100, 100, 2500, 1200));
		// add(new BigS(200, 200, (int) (windowsizex * 0.7),
		// (int) (windowsizey * 0.7)));
		// add(new Stigis(200, 200, (int) (windowsizex * 0.7),(int) (windowsizey
		// * 0.7)));
		// add(new Splinter(200,200,180));
	}

	/**
	 * input a map(*.smp),and it will return a frame,if the map has broken it
	 * will return null 输入一个地图，输出一个窗口。地图有问题return null
	 * 
	 * @param mapaddress
	 * @return
	 */
	public Gamewindow loadmap(String mapaddress) {
		try {
			mapin = new DataInputStream(new BufferedInputStream(
					new FileInputStream(mapaddress)));
		} catch (FileNotFoundException e) {
			System.out.println(mapaddress + " 不存在，故无法加载地图");// (chinese:XX not
															// exist)
			e.printStackTrace();// 会出现红字//print StackTrace
			return null;
		}
		try {
			bgloc = mapin.readUTF();
			mapx = mapin.readInt();
			mapy = mapin.readInt();
			System.out.println("正在处理" + mapaddress + "\n背景地址：" + bgloc
					+ "地图实际大小：" + mapx + "x" + mapy);// (Chinese Loading )

		} catch (IOException e1) {
			System.out.println("在读取地图:" + mapaddress + "时,无法获取基本信息，故无法加载地图");// (Chinese

			e1.printStackTrace();
			return null;
		}
		try {
			mapin.close();
		} catch (IOException e) {
			System.out.println("在读取地图:" + mapaddress + "时,无法关闭流，读取失败。");// (Chinese

			e.printStackTrace();
		}
		System.out.println("读取完成");// (finished)
		return new Gamewindow(bgloc, mapx, mapy, windowsizex, windowsizey);
	}

	public boolean add(SpaceEvent e) {
		return EventList.add(e);
	}

	/**
	 * 添加子弹
	 * 
	 * @param Bullet
	 * @return 是否成功添加(if it load successfully)
	 */
	public boolean add(Bullet b) {
		return BulletList.add(b);
	}

	/**
	 * 添加SpaceShip，只能添加一次
	 * 
	 * @param SpaceShip
	 * @return 是否成功添加(if it load successfully)
	 */
	public boolean add(SpaceShip s) {
		if (ship == null) {
			ship = s;
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 添加敌人
	 * 
	 * @param Enemy
	 * @return 是否成功添加(if it load successfully)
	 */
	public boolean add(Enemy e) {
		return EnemyList.add(e);
	}

	public boolean add(SpaceItem i) {
		return ItemList.add(i);
	}

	public boolean add(SpecialEffect o) {
		return OList.add(o);
	}

	public boolean update() {
		if (bgloc == null) {
			System.out.println("没有加载地图，故无法更新战场数据");// (no map ,can;t update)
			return false;
		}
		if (lan_new) {
			String[] r_array = lan_buffer.split(" ");
			kw = (r_array[0]).compareTo("W")==0;
			ka = (r_array[1]).compareTo("A")==0;
			ks = (r_array[2]).compareTo("S")==0;
			kd = (r_array[3]).compareTo("D")==0;
			lan_new = false;
		}
		// System.out.println(MainGame.ji.);

		// System.out.println("A");
		collisionupdate();
		// 基本计算
		autoshoot();// 自动射击//(Chinese is the translation of the method name)
		updateevent();// 事件
		updateItem();
		updatebullet();// 更新子弹
		updateship();// 更新飞船
		updateEnemy();// 更新敌人
		// 计算碰撞
		// 传递数据
		sendImage();
		if (MainGame.lan_game) {
			sendpacket();
		}
		return true;
	}

	private void updateevent() {
		for (int i = 0; i < EventList.size(); i++) {
			EventList.get(i).execute();
			if (EventList.get(i).over == true) {
				EventList.remove(i);
				i--;
			}
		}
	}

	public void sendImage() {
		if (MainGame.cansendimage) {// "can send image"means Repainter is ready
									// for painting
			// 清空//clear the image
			MainGame.test.repainter.le.clear();
			imgnum = 0;
			// 传递飞船并计算偏移量//compute offset
			MainGame.test.repainter.computeOffset(ship);

			// 传递子弹//send bullet to screen
			for (int i = 0; i < BulletList.size(); i++) {
				MainGame.test.repainter.add(BulletList.get(i).ImageID,
						BulletList.get(i).Imagesize, (int) BulletList.get(i).x,
						(int) BulletList.get(i).y,
						-getangle(BulletList.get(i).vx, BulletList.get(i).vy),
						2);
			}
			// 传递物品//send items to screen
			if (ItemList.size() != 0) {
				for (int i = 0; i < ItemList.size(); i++) {
					if ((ItemList.get(i).timeup < 30 && ItemList.get(i).timeup > 15)
							|| (ItemList.get(i).timeup < 60 && ItemList.get(i).timeup > 45)
							|| (ItemList.get(i).timeup < 90 && ItemList.get(i).timeup > 75)) {

					} else {
						MainGame.test.repainter.add(ItemList.get(i).imageID,
								ItemList.get(i).imagesize, ItemList.get(i).x,
								ItemList.get(i).y, 0, 2);
					}
				}
			}
			// 传递特技//send SpecialEffect to screen
			for (int i = 0; i < OList.size(); i++) {

				MainGame.test.repainter.add(OList.get(i).getImage(),
						OList.get(i).imagesize, OList.get(i).x, OList.get(i).y,
						0, 0);
				if (OList.get(i).over) {
					this.OList.remove(i);
				}

			}
			// 传递敌人//send enemy to screen
			for (int i = 0; i < EnemyList.size(); i++) {
				MainGame.test.repainter.add(EnemyList.get(i).imageID,
						EnemyList.get(i).imagesize, (int) EnemyList.get(i).x,
						(int) EnemyList.get(i).y,
						-getangle(EnemyList.get(i).vx, EnemyList.get(i).vy), 2);
			}

			// GameOver↓
			if (score > 10000) {
				MainGame.test.repainter.add_nooffset_element(
						"Images//UI//win.png", -200, 75, 0, 0);
				ship.health = -1;
			} else if (ship.health <= 0 && score < 10000) {
				MainGame.test.repainter.add_nooffset_element(
						"Images//UI//gameover.png", -200, 75, 0, 0);
			}

			// 生命值判断↓//Hp

			if (ship.health > 5) {
				MainGame.test.repainter.add_nooffset_element(
						"Images//InGameHUD//hpBar//hpover.png",
						MainGame.test.repainter.onscreenx(0),
						MainGame.test.repainter.onscreeny(0), 0, 2);
				cautiontime = 0;
			} else {
				MainGame.test.repainter.add_nooffset_element(
						"Images//InGameHUD//hpBar//hp" + ship.health + ".png",
						MainGame.test.repainter.onscreenx(0),
						MainGame.test.repainter.onscreeny(0), 0, 2);
				if (ship.health == 1) {
					if (cautiontime == 0) {
						cautiontime = MainGame.gametime;
					}
					if (Math.abs(MainGame.gametime - cautiontime) % 120 == 0) {
						Thread t = new Thread(new SoundController(
								"Sounds//alert.wav"));
						t.start();
						cautiontime = MainGame.gametime;
					}
				} else {
					cautiontime = 0;
				}
			}

			Mousehud.showHud(ML.mx, ML.my);

			// 显示武器槽↓//displayweapon
			WeaponSlot.displayweapon(ship);
			Scoreprinter.printscore(score);
			// 重新绘制↓//repaint
			MainGame.test.repainter.repaint();
		} else {
			// System.out.println("WARNING:can't paint image when bullet:"+BulletList.size());
		}
	}

	private void updateEnemy() {
		for (int i = 0; i < EnemyList.size(); i++) {
			if (EnemyList.get(i).health < 0) {
				add(EnemyList.get(i).deathwhisper());
				if (score < 10000) {
					score = score + EnemyList.get(i).getscore;
				}
				EnemyList.remove(i);
				i--;
			} else {
				EnemyList.get(i).update();
				// if (EnemyList.get(i).x > (mapx / 2)
				// || EnemyList.get(i).x < -(mapx / 2)
				// || EnemyList.get(i).y > (mapy / 2)
				// || EnemyList.get(i).y < -(mapy / 2)) {
				// EnemyList.remove(i);
				// i = i - 1;
				// }
			}// check Enemy in the list ,if still alive then update the status
				// of it
		}
	}

	private void updateItem() {
		if (ItemList.size() != 0) {
			for (int i = 0; i < ItemList.size(); i++) {
				ItemList.get(i).timeup--;
				if (ItemList.get(i).timeup <= 0) {
					ItemList.remove(i);
					i--;
				}// remove picakable item when time is out
			}
		}
	}

	/**
	 * 计算碰撞compute collision
	 */

	private void collisionupdate() {
		Rectangle Shiphitbox = new Rectangle(ship.x - ship.volume / 2, ship.y
				- ship.volume / 2, ship.volume / 2, ship.volume / 2);

		for (int i = 0; i < EnemyList.size(); i++) {// 这个循环基本处理了所有需要处理的东西//collision
													// between enemy and bullet
			Enemy tEnemy = EnemyList.get(i);
			Rectangle Enemyhitbox = new Rectangle((int) tEnemy.x
					- tEnemy.volume / 2, (int) tEnemy.y - tEnemy.volume / 2,
					tEnemy.volume / 2, tEnemy.volume / 2);
			if (Enemyhitbox.intersects(Shiphitbox)) {// 飞机碰上怪物怪物挂掉//dedete Enemy
														// ship then delete
				ship.health--;
				EnemyList.remove(i);
				i--;
			}
			for (int j = 0; j < BulletList.size(); j++) {
				Bullet tBullet = BulletList.get(j);
				Rectangle Bullethitbox = new Rectangle((int) tBullet.x
						- tBullet.volume / 2, (int) tBullet.y - tBullet.volume
						/ 2, tBullet.volume / 2, tBullet.volume / 2);
				// System.out.println(tBullet.faction);
				if (tBullet.faction == 0) {// 己方子弹our bullet
					if (Enemyhitbox.intersects(Bullethitbox)) {
						tEnemy.health = tEnemy.health - tBullet.damage;
						tEnemy.hit = true;
						BulletList.remove(j);
						j--;
					}
				} else if (tBullet.faction == 1)// 敌方子弹emeny bullet
				{
					if (Shiphitbox.intersects(Bullethitbox)) {
						ship.health--;
						BulletList.remove(j);
						j--;
					}

				}
			}
		}

		if (EnemyList.size() == 0) {// 没有敌人时，单独处理子弹和飞船//if no enemy ,we only
									// deal with bullet and ship.
			for (int j = 0; j < BulletList.size(); j++) {
				Bullet tBullet = BulletList.get(j);
				Rectangle Bullethitbox = new Rectangle((int) tBullet.x
						- tBullet.volume / 2, (int) tBullet.y - tBullet.volume
						/ 2, tBullet.volume / 2, tBullet.volume / 2);
				// System.out.println(tBullet.faction);
				if (tBullet.faction == 1)// 敌方子弹
				{
					if (Shiphitbox.intersects(Bullethitbox)) {
						ship.health--;
						BulletList.remove(j);
						j--;
					}
				}
			}
		}
		for (int i = 0; i < ItemList.size(); i++) {
			SpaceItem tItem = ItemList.get(i);
			Rectangle Itemhitbox = new Rectangle(tItem.x - tItem.imagesize,
					tItem.y - tItem.imagesize, 50, 50);
			if (Itemhitbox.intersects(Shiphitbox)) {
				tItem.getitem();
				if (ship.health >= 0) {
					Thread t = new Thread(new SoundController(
							"Sounds//bonus.wav"));
					t.start();
				}
				ItemList.remove(i);
				i--;
			}
		}
	}

	/**
	 * 移动子弹并且自动移除出界子弹 update bullet and remove the bullet which is out of map
	 */
	private void updatebullet() {
		for (int i = 0; i < BulletList.size(); i++) {
			BulletList.get(i).update();
			// 计算部分
			// 超出边界out of map
			if (BulletList.get(i).x > (mapx / 2)
					|| BulletList.get(i).x < -(mapx / 2)
					|| BulletList.get(i).y > (mapy / 2)
					|| BulletList.get(i).y < -(mapy / 2)) {
				BulletList.remove(i);
				i = i - 1;
			}
		}
	}

	private void updateship() {
		if (ship.health <= 0 || score > 10000) {
			ship.x = 0;
			ship.y = 0;
			ship.visx = 0;
			ship.visy = 0;
			ship.ImageID = "";

		}

		if (ship.vx > 0) {// 下面这几行保证飞船在不按键时能停住this lines will stop the ship when
							// player do not press any button
			ship.vx = ship.vx - ship.m;
		} else if (ship.vx < 0) {
			ship.vx = ship.vx + ship.m;
		}
		if (ship.vy > 0) {
			ship.vy = ship.vy - ship.m;
		} else if (ship.vy < 0) {
			ship.vy = ship.vy + ship.m;
		}
		// 下面处理按键//add a to the v
		if (kw && !ks) {
			ship.vy = ship.vy + ship.a;
		}
		if (ks && !kw) {
			ship.vy = ship.vy - ship.a;
		}
		if (kd && !ka) {
			ship.vx = ship.vx + ship.a;
		}
		if (ka && !kd) {
			ship.vx = ship.vx - ship.a;
		}

		if (Math.abs(ship.vx) > ship.maxv) {// ship can't be faster than its max
											// speed
			if (ship.vx > 0) {
				ship.vx = ship.maxv;
			} else {
				ship.vx = -ship.maxv;
			}
		}
		if (Math.abs(ship.vy) > ship.maxv) {
			if (ship.vy > 0) {
				ship.vy = ship.maxv;
			} else {
				ship.vy = -ship.maxv;
			}
		}

		// 滚屏的部分代码
		// This lines will change ship's position both on screen and in game.
		if (((ship.x + ship.vx) > (mapx / 2) || (ship.x + ship.vx) < -(mapx / 2)) != true) {
			ship.x = ship.x + ship.vx;
			ship.visx = ship.visx + ship.vx;
		}
		if (((ship.y + ship.vy) > (mapy / 2) || (ship.y + ship.vy) < -(mapy / 2)) != true) {
			ship.y = ship.y + ship.vy;
			ship.visy = ship.visy + ship.vy;
		}
		ship.angle = currentangle;

	}

	private void sendpacket() {
		// step1:prepare data
		String tosend = "";
		tosend = (kw ? "W" : "w") + " " + (ka ? "A" : "a") + " "
				+ (ks ? "S" : "s") + " " + (kd ? "D" : "d") + " ";
	//	System.out.println(tosend);
		// step2:send data
		byte[] buf = tosend.trim().getBytes();
		try {
			InetAddress address = InetAddress.getByName(MainGame.lan_IP);
			DatagramPacket dp = new DatagramPacket(buf, buf.length, address,
					Receiver.port);
			DatagramSocket ds = new DatagramSocket();
			ds.send(dp);
			ds.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private boolean kw = false;
	private boolean ka = false;
	private boolean ks = false;
	private boolean kd = false;
	private double currentangle;
	public boolean ml = false;
	public long pressedtime = 0;

	/**
	 * compute angle
	 * 
	 * @param x
	 * @param y
	 * @return
	 * @author EveLIN
	 */
	public static double getangle(double x, double y) {
		double ans;
		double temp = (double) y / x;
		if (y > 0) {
			if (x == 0) {
				return 90.00;
			}
			if (x > 0) {
				ans = (Math.atan(temp) * (180 / Math.PI));
				return ans;
			}
			if (x < 0) {
				ans = 180.00 - (Math.atan(-temp) * (180 / Math.PI));
				return ans;
			}
		}
		if (y < 0) {
			if (x == 0)
				return 270.00;
			if (x > 0) {
				ans = 360.00 - (Math.atan(-temp) * (180 / Math.PI));
				return ans;
			}
			if (x < 0) {
				ans = 180.00 + (Math.atan(temp) * (180 / Math.PI));
				return ans;
			}
		}
		if (y == 0) {
			if (x >= 0) {
				return 0.00;
			} else {
				return 180.00;
			}

		}
		return 0;
	}

	/**
	 * The "KL" use this to send imformation to this class
	 * 这个是管理按键的，注意：无论是按下还是松开都会触发这个!
	 * 
	 * @param i
	 * @param key
	 */
	public void Keyprocesser(Boolean i, char key) {
		if (key == 'w') {
			kw = i;
		} else if (key == 'a') {
			ka = i;
		} else if (key == 's') {
			ks = i;
		} else if (key == 'd') {
			kd = i;
		}
		int t1 = (ML.mx - windowsizex / 2 - 45) - (ship.visx);
		int t2 = (windowsizey / 2 - ML.my + 45) - (ship.visy);
		currentangle = -getangle(t1, t2);
	}

	/**
	 * this method will make the ship always face the cursor 这个负责船体旋转的视觉效果
	 * 
	 * @param x
	 * @param y
	 * @author EveLIN
	 */
	public void Mouseprocessor(int x, int y) {
		int t1 = (x - windowsizex / 2) - (ship.visx);
		int t2 = (windowsizey / 2 - y) - (ship.visy);
		currentangle = -getangle(t1, t2);

	}

	// hold the mouse button ,ship will auto shoot
	public void autoshoot() {

		if (ml && (MainGame.gametime - pressedtime) % ship.w1.cd == 0) {
			shootprocessor(autotarx, autotary);
		}

	}

	public void shootprocessor(int mx, int my) {
		if (!paused && ship.health >= 0) {
			int cx = mx - windowsizex / 2;
			int cy = windowsizey / 2 - my;
			Bullet[] tBullets = new Bullet[this.ship.w1.count()];
			tBullets = this.ship.w1.shoot(ship.x, ship.y, ship.visx, ship.visy,
					cx, cy, 0);
			for (int i = 0; i < this.ship.w1.count(); i++) {
				add(tBullets[i]);
			}
		}
	}

	/**
	 * Create a item on the map
	 * 
	 * 
	 * @param x
	 * @param y
	 * @param l
	 *            : level of item
	 */
	public static void createitem(double x, double y, double l) {
		double r = Math.random() * 100;
		if (r < 5) {
			MainGame.bm.add(new W_StarWeapon(x, y));
		} else if (r > 15 && r < 20) {
			MainGame.bm.add(new H_bulletrain(x, y));
		} else if (r > 20 && r < 25) {
			MainGame.bm.add(new S_speedup(x, y));
		} else if (r > 30 && r < 37) {
			MainGame.bm.add(new S_repair(x, y));
		} else if (r > 40 && r < 47) {
			MainGame.bm.add(new H_bulletblast(x, y));
		} else if (r > 55 && r < 60 + l) {
			MainGame.bm.add(new W_Shotgun(x, y));
		} else if (r > 60 + l && r < 61 + l) {
			MainGame.bm.add(new W_BasicWeapon(x, y));
		} else if (r > 61 + l && r < 64 + l) {
			MainGame.bm.add(new W_rocketlauncher(x, y));
		}
	}
}