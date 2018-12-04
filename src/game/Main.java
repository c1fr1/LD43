package game;

import engine.OpenAL.SoundSource;
import engine.OpenGL.EnigWindow;
import engine.OpenGL.VAO;

import java.io.IOException;

public class Main {
	public static VAO screenObj;
	public static VAO entityObj;
	public static VAO titleObj;
	public static float gameOverOpacity = - 1;
	public static SoundSource source;
	public static void main(String[] args) {
		
		if (args.length == 0) {
			String os = System.getProperty("os.name");
			System.out.println("Operating System: " + os);
			if (os.indexOf("mac") >= 0) {
				System.out.println("in order to get a stack trace, run with\njava -jar 'Flaming Resistance.jar' noReRun -XstartOnFirstThread");
				try {
					Runtime.getRuntime().exec(new String[]{"java", "-XstartOnFirstThread", "-jar", "'Flaming Resistance.jar'", "noReRun"});
				} catch (IOException e) {
					e.printStackTrace();
				}
			}else {
				EnigWindow.runOpeningSequence = false;
				EnigWindow window = new EnigWindow("Flaming Resistance", "res/textures/icon.png");
				source = new SoundSource();
				Shaders.createMainShaders();
				screenObj = new VAO(-1, -1, 2, 2);
				entityObj = new VAO(-5, -5, 10, 10);
				titleObj = new VAO(-30, 10, 60, 30);
				window.fps = 60;
				MainView.main = new MainView(window);
				MenuView.main = new MenuView(window);
				AudioHandler.setup();
				MenuView.main.runLoop();
				AudioHandler.timer.cancel();
				window.terminate();
			}
		}else if (args[0].equals("noReRun")) {
			EnigWindow.runOpeningSequence = false;
			EnigWindow window = new EnigWindow("Flaming Resistance", "res/textures/icon.png");
			source = new SoundSource();
			Shaders.createMainShaders();
			screenObj = new VAO(-1, -1, 2, 2);
			entityObj = new VAO(-5, -5, 10, 10);
			titleObj = new VAO(-30, 10, 60, 30);
			window.fps = 60;
			MainView.main = new MainView(window);
			MenuView.main = new MenuView(window);
			AudioHandler.setup();
			MenuView.main.runLoop();
			AudioHandler.timer.cancel();
			window.terminate();
		}
	}
}
