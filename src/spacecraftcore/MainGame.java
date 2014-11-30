package spacecraftcore;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import displayConsole.Test;
import spacecraftelements.Bullets.BasicBullet;

/**
 * 考虑在主程序里加上各个Manager的全局变量
 * 
 */

public class MainGame {
	public static BattleFieldManager bm;
	public static Test test;
	public static void main(String[] args) {
		test = new Test();
		   test.addWindowListener(new WindowAdapter(){
			   public void windowClosing(WindowEvent e){
				   System.exit(0);
			   }
		   });
		Thread mt=new Thread(new SpaceTimmer());
		bm=new BattleFieldManager();
		bm.loadmap("ditu1.cmp");
		for(int j=0;j<20;j++){
			bm.add(new BasicBullet( (int) (Math.random() * 100) % 10,(int) (Math.random() * 100) % 10,(int) (Math.random() * 100) % 10,(int) (Math.random() * 100) % 10));
			}
		mt.start();
	}
	
}
