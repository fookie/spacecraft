package displayConsole.menu;

import spacecraftcore.MainGame;

public class Button_exit extends SButton {
	int overnum = 3;
	int status = 0;
	int f;

	public Button_exit(int x, int y) {
		super.buttonhitbox.width = 150;
		super.buttonhitbox.height = 50;
		super.buttonhitbox.x = x;
		super.buttonhitbox.y = y;
		super.Default = "Images//UI//buttons//exit1,0.png";
		super.over = "Images//UI//buttons//exit2,";
		super.pressed = "Images//UI//buttons//exit3,0.png";
	}

	public void setstatus(int s) {
		this.status = s;

	}

	@Override
	public String getImage() {
		if (status == 0) {
			return Default;
		} else if (status == 1) {
			// if(MainGame.gametime%12==0)
			// {
			// f=0;
			// }
			// else if(MainGame.gametime%12==4)
			// {
			// f=1;
			// }else if(MainGame.gametime%12==8)
			// {
			// f=2;
			// }
			f = 0;
			return over + f + ".png";
		} else {
			return pressed;
		}
	}

	@Override
	public boolean click() {
		// 展开另外两个按钮
		// MainGame.mainmenu.addbutton(new Button_survival(-320,300));
		// MainGame.mainmenu.addbutton(new Button_story(-320,230));
		System.exit(0);
		return false;
	}

}
