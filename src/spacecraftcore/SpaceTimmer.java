package spacecraftcore;

public class SpaceTimmer implements Runnable {

	@Override
	public void run() {
		while (true) {
			if (MainGame.bm.paused != true) {
				MainGame.bm.update();
			}
			try {
				Thread.sleep(19);
				MainGame.gametime = MainGame.gametime + 1;

			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
