package game;

import engine.OpenGL.EnigWindow;

import static org.lwjgl.glfw.GLFW.*;

public class UserControls {
	public static int[] left = new int[] {GLFW_KEY_A};
	public static int[] right = new int[] {GLFW_KEY_D};
	public static int[] down = new int[] {GLFW_KEY_S};
	public static int[] up = new int[] {GLFW_KEY_W};
	public static int[] weaponUp = new int[] {GLFW_KEY_SPACE, GLFW_KEY_UP};
	public static int[] weaponDown = new int[] {GLFW_KEY_LEFT_SHIFT, GLFW_KEY_DOWN};
	public static int[] burn = new int[] {GLFW_KEY_B};
	public static int[] quit = new int[] {GLFW_KEY_ESCAPE};
	public static float sensitivity = 1f/500f;
	
	public static boolean left(EnigWindow window) {
		for (int i:left) {
			if (window.keys[i] > 0) {
				return true;
			}
		}
		return false;
	}
	public static boolean right(EnigWindow window) {
		for (int i:right) {
			if (window.keys[i] > 0) {
				return true;
			}
		}
		return false;
	}
	public static boolean down(EnigWindow window) {
		for (int i:down) {
			if (window.keys[i] > 0) {
				return true;
			}
		}
		return false;
	}
	public static boolean up(EnigWindow window) {
		for (int i:up) {
			if (window.keys[i] > 0) {
				return true;
			}
		}
		return false;
	}
	public static boolean weaponUp(EnigWindow window) {
		for (int i: weaponUp) {
			if (window.keys[i] == 1) {
				return true;
			}
		}
		return false;
	}
	public static boolean weaponDown(EnigWindow window) {
		for (int i: weaponDown) {
			if (window.keys[i] == 1) {
				return true;
			}
		}
		return false;
	}
	public static boolean burn(EnigWindow window) {
		for (int i: burn) {
			if (window.keys[i] > 0) {
				return true;
			}
		}
		return false;
	}
	public static boolean quit(EnigWindow window) {
		for (int i:quit) {
			if (window.keys[i] > 0) {
				return true;
			}
		}
		return false;
	}
}
	
	
	
/*
	if commonCamera is true, the camera rotation for most 3d games will be used, if it is false, this alternate method is used:

	every frame the new rotation matrix is set to the rotation matrix from relative mouse position changes (from the last frame),
	will be multiplied by the old rotation matrix.
	R(xnew-xold)*R(ynew-yold)*R(znew-zold)*oldRotationMatrix.
	
	oh btw
	this variable has been moved from this file, to the Camera file, so you can have different cameras with different types of cameras
																																		 */