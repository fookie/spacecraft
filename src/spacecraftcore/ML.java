package spacecraftcore;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
/**
 * Mouselistener during the game
 * @author Hale
 *
 */
public class ML implements MouseMotionListener,MouseListener{
	
    public static int mx,my;
    
    
	public void mouseDragged(MouseEvent e) {//hole the mouse button to auto shoot
		mx=e.getX();
		my=e.getY();
		MainGame.bm.ml=true;
		MainGame.bm.autotarx=mx;
		MainGame.bm.autotary=my;
		MainGame.bm.Mouseprocessor(mx,my);
		MainGame.bm.isshooting = true;
	}

	public void mouseMoved(MouseEvent e) {
		mx=e.getX();
		my=e.getY();
		MainGame.bm.Mouseprocessor(mx,my);
	}

	public void mouseClicked(MouseEvent e) {
		
	}

	public void mousePressed(MouseEvent e) {
		mx=e.getX();
		my=e.getY();
		MainGame.bm.autotarx=mx;
		MainGame.bm.autotary=my;
		MainGame.bm.ml=true;
		MainGame.bm.isshooting = true;
		MainGame.bm.shootprocessor(mx, my);
		MainGame.bm.pressedtime=MainGame.gametime;
		
	}

	public void mouseReleased(MouseEvent e) {
		MainGame.bm.ml=false;
		MainGame.bm.isshooting = false;
		MainGame.bm.pressedtime=0;
	}

	public void mouseEntered(MouseEvent e) {
		
	}

	public void mouseExited(MouseEvent e) {
		
	}

}