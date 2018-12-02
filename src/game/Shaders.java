package game;

import engine.OpenGL.ShaderProgram;

public class Shaders {
	public static ShaderProgram flipShader;
	public static ShaderProgram wallShader;
	public static ShaderProgram tileShader;
	public static ShaderProgram textureShader;
	public static ShaderProgram attackShader;
	public static ShaderProgram titleShader;
	public static ShaderProgram weapUIShader;
	public static ShaderProgram buttonShader;
	public static ShaderProgram particleShader;
	public static void createMainShaders() {
		flipShader = new ShaderProgram("flipShader");
		wallShader = new ShaderProgram("wallShader");
		tileShader = new ShaderProgram("tileShader");
		textureShader = new ShaderProgram("textureShader");
		attackShader = new ShaderProgram("attackShader");
		titleShader = new ShaderProgram("titleShader");
		weapUIShader = new ShaderProgram("weapUIShader");
		buttonShader = new ShaderProgram("buttonShader");
		particleShader = new ShaderProgram("particleShader");
	}
}
