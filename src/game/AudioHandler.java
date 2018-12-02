package game;

import engine.OpenAL.Sound;

import java.util.Timer;
import java.util.TimerTask;

public class AudioHandler extends TimerTask {
	public static Sound[] randSounds;
	public static Sound fightSound;
	public boolean fighting = false;
	public static void setup() {
		randSounds = new Sound[6];
		for (int i = 0; i < randSounds.length; ++i) {
			randSounds[i] = new Sound("res/sounds/randoms/" + i + ".wav");
		}
		fightSound = new Sound("res/sounds/fighting.wav");
		Timer timer = new Timer();
		timer.schedule(new AudioHandler(), 0, 3000);
	}
	public void run() {
		if (!fighting) {
			Main.source.stop();
			if (MainView.main.enemies.size() > 50) {
				fighting = true;
				Main.source.playSound(fightSound);
			}else {
				if (Math.random() > 0.5) {
					Main.source.playSound(randSounds[0]);
				}else {
					Main.source.playSound(randSounds[(int) (Math.random() * 6)]);
				}
			}
		}else {
			if (Main.gameOverOpacity < 0) {
				fighting = false;
			}
		}
	}
}
