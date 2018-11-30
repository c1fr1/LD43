package game;

import engine.*;
import engine.OpenGL.*;

import static org.lwjgl.opengl.GL11.*;

public class MainView extends EnigView {
	public static MainView main;
	
	//project variables

	public MainView(EnigWindow window) {
		super(window);
	}
	
	@Override
	public boolean loop() {
		FBO.prepareDefaultRender();
		FBO.clearCurrentFrameBuffer();
		if (UserControls.quit(window)) {
			return true;
		}
		return false;
	}
}
