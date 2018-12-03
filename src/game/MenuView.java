package game;

import engine.EnigView;
import engine.OpenGL.EnigWindow;
import engine.OpenGL.FBO;
import engine.OpenGL.Texture;
import engine.OpenGL.VAO;
import game.weapons.Scythe;
import game.weapons.Shield;
import game.weapons.Spear;
import game.weapons.Sword;
import org.joml.Matrix4f;
import org.joml.Vector2f;

import java.util.ArrayList;

import static game.Main.*;
import static game.Shaders.*;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.glDisable;

public class MenuView extends EnigView {
	public static MenuView main;
	
	public Texture titleTexture;
	public Texture tutorialButtonTexture;
	public Texture playButtonTexture;
	public Texture quitButtonTexture;
	
	public VAO tutorialButton;
	public VAO playButton;
	public VAO quitButton;
	
	public MenuView(EnigWindow window) {
		super(window);
		glDisable(GL_DEPTH_TEST);
		titleTexture = new Texture("res/textures/titleText.png");
		tutorialButtonTexture = new Texture("res/textures/tutorial.png");
		playButtonTexture = new Texture("res/textures/play.png");
		quitButtonTexture = new Texture("res/textures/quit.png");
		
		playButton = new VAO(-window.getAspectRatio() * 50f + 10f, -10f, 16, 8);
		tutorialButton = new VAO(-window.getAspectRatio() * 50f + 10f, -20f, 16, 8);
		quitButton = new VAO(-window.getAspectRatio() * 50f + 10, -30, 16, 8);
	}
	
	public boolean loop() {
		FBO.prepareDefaultRender();
		FBO.clearCurrentFrameBuffer();
		
		titleShader.enable();
		titleShader.setUniform(0, 0, getPerspectiveMatrix());
		titleShader.setUniform(2, 0, window.cursorXFloat * 50f * window.getAspectRatio(), window.cursorYFloat * 50f);
		titleShader.setUniform(2, 1, 1f);
		titleTexture.bind();
		titleObj.fullRender();
		
		buttonShader.enable();
		buttonShader.setUniform(0, 0, getPerspectiveMatrix());
		buttonShader.setUniform(2, 0, window.cursorXFloat * 50f * window.getAspectRatio(), window.cursorYFloat * 50f);
		buttonShader.setUniform(2, 1, 1f);
		buttonShader.setUniform(2, 2, 0f);
		
		float rat = window.getAspectRatio() * 50;
		if (window.cursorXFloat + 1 > 10 / rat) {
			if (window.cursorXFloat + 1 < 26 / rat) {
				if (window.cursorYFloat * 50 > -10) {
					if (window.cursorYFloat * 50 < -2) {
						if (window.mouseButtons[GLFW_MOUSE_BUTTON_LEFT] > 1) {
							MainView.main.tutorialFrame = -1;
							MainView.main.runLoop();
							return false;
						}else {
							buttonShader.setUniform(2, 2, 1f);
						}
					}
				}
			}
		}
		playButtonTexture.bind();
		playButton.fullRender();
		
		buttonShader.setUniform(2, 2, 0f);
		if (window.cursorXFloat + 1 > 10 / rat) {
			if (window.cursorXFloat + 1 < 26 / rat) {
				if (window.cursorYFloat * 50 > -20) {
					if (window.cursorYFloat * 50 < -12) {
						if (window.mouseButtons[GLFW_MOUSE_BUTTON_LEFT] > 1) {
							window.mouseButtons[GLFW_MOUSE_BUTTON_LEFT] = 0;
							MainView.main.tutorialFrame = 0;
							MainView.main.player.weapons.get(0).burning = false;
							MainView.main.runLoop();
							return false;
						}else {
							buttonShader.setUniform(2, 2, 1f);
						}
					}
				}
			}
		}
		tutorialButtonTexture.bind();
		tutorialButton.fullRender();
		
		buttonShader.setUniform(2, 2, 0f);
		if (window.cursorXFloat + 1 > 10 / rat) {
			if (window.cursorXFloat + 1 < 26 / rat) {
				if (window.cursorYFloat * 50 > -30) {
					if (window.cursorYFloat * 50 < -22) {
						if (window.mouseButtons[GLFW_MOUSE_BUTTON_LEFT] > 1) {
							return true;
						}else {
							buttonShader.setUniform(2, 2, 1f);
						}
					}
				}
			}
		}
		quitButtonTexture.bind();
		quitButton.fullRender();
		
		return false;
	}
	
	public Matrix4f getPerspectiveMatrix() {
		return new Matrix4f(MainView.main.perspectiveMatrix);
	}
}
