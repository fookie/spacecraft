package spacecraftcore;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KL implements KeyListener
{

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		MainGame.bm.Keyprocesser(true, e.getKeyChar());
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		MainGame.bm.Keyprocesser(false, e.getKeyChar());
		
	}
	
}

