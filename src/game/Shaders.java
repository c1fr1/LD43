package game;

import engine.OpenGL.ShaderProgram;

public class Shaders {
	public static ShaderProgram flipShader;
	public static void createMainShaders() {
		flipShader = new ShaderProgram("flipShader");
	}
}
