package spacecraftelements.Enemy.Survival;

import spacecraftcore.BattleFieldManager;
import spacecraftcore.MainGame;
import spacecraftelements.Bullets.BlueBullet;
import spacecraftelements.Enemy.Enemy;
import spacecraftelements.SpecialEffect.BigBlast;
import spacecraftelements.SpecialEffect.SpecialEffect;

public class Bigslime extends Enemy{
	int mapsizex,mapsizey;
	double tarx,tary;
	public Bigslime(double x, double y,int mapsizex,int mapsizey) {
		super.x = x;
		super.y = y;
		super.damage = 1;
		super.health = 250;
		super.v = 3;
		super.volume = 110;
		super.imageID = "Images//enemy//bigslime.png";
		super.imagesize = 50;
		this.mapsizex=mapsizex;
		this.mapsizey=mapsizey;
		tarx= (Math.random()*mapsizex*0.4)*((int)Math.pow(-1,(int)x));
		tary= (Math.random()*mapsizey*0.4)*((int)Math.pow(-1,(int)y));
		//int a=(int) Math.pow(-1,3);
		
		super.getscore=125;
	}
	@Override
	public SpecialEffect deathwhisper() {
		BattleFieldManager.createitem(x, y, 2);
		for(int i=-5;i<6;i++)
		{
			MainGame.bm.add(new BlueBullet(x, y, i,(int) Math.sqrt(20-i*i), 1));
			MainGame.bm.add(new BlueBullet(x, y, i,-(int) Math.sqrt(20-i*i), 1));
		}
		return new BigBlast(x,y);
	}
	@Override
	public boolean update() {
		
		if (Math.abs(x - tarx)<20 && Math.abs(y - tary)<20) {
			tarx=(Math.random()*mapsizex*0.4)*((int)Math.pow(-1,(int)y));
			tary= (Math.random()*mapsizey*0.4)*((int)Math.pow(-1,(int)x));
			//System.out.println(tarx+" "+tary);
			return false;
		}
		if(hit==true){this.imageID = "Images//enemy//bigslime1.png";hit=false;}
		else {this.imageID = "Images//enemy//bigslime.png";}//hint when hit
		double x1, y1;
		double ratiox, ratioy, third;
		x1 = tarx - x;
		y1 = tary - y;
		third = Math.sqrt( x1 * x1 + y1 * y1);
		ratiox = ( x1) / third;
		ratioy = ( y1) / third;
		vx =  (( v) * ratiox);
		vy =  (( v) * ratioy);
		x = x + vx;
		y = y + vy;
		
		if (MainGame.gametime % (400-(int)(Math.random()*100)) == 0) {
			MainGame.bm.add(new Slime(x+55,y+55));
			MainGame.bm.add(new Slime(x+55,y-55));
			MainGame.bm.add(new Slime(x-55,y+55));
			MainGame.bm.add(new Slime(x-55,y-55));
		}
		return true;
	}

	
}
