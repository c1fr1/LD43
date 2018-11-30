package game;

import engine.OpenGL.EnigWindow;
import engine.OpenGL.VAO;

import java.util.Scanner;

public class Main {
	public static VAO screenObj;
	public static void main(String[] args) {
		EnigWindow window = new EnigWindow("Enignets game", "res/textures/testex.png");
		Shaders.createMainShaders();
		screenObj = new VAO(-1, -1, 2, 2);
		window.fps = 60;
		MainView.main = new MainView(window);
		MainView.main.runLoop();
		window.terminate();
	}
}
