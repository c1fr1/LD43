package game;

import engine.OpenGL.EnigWindow;
import engine.OpenGL.VAO;

import java.util.Scanner;

public class Main {
	public static VAO screenObj;
	public static VAO entityObj;
	public static void main(String[] args) {
		EnigWindow.runOpeningSequence = false;
		EnigWindow window = new EnigWindow("Enignets game", "res/textures/icon.png");
		Shaders.createMainShaders();
		screenObj = new VAO(-1, -1, 2, 2);
		entityObj = new VAO(-5, -5, 10, 10);
		window.fps = 60;
		MainView.main = new MainView(window);
		MainView.main.runLoop();
		window.terminate();
	}
}
