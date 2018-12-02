package game;

import engine.OpenGL.EnigWindow;
import engine.OpenGL.VAO;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

import static game.Main.screenObj;
import static game.Shaders.particleShader;
import static java.lang.Math.*;
import static java.lang.Math.sin;

public class ParticleRenderer {
	public Vector3f[] particles;
	public Vector3f[] colorOffsets;
	public Vector3f color;
	
	public Vector3f colorOffsetCoeff = new Vector3f(0.25f, 0.25f, 0.25f);
	
	public ParticleRenderer(float r, float g, float b, int particleCount) {
		color = new Vector3f(r, g, b);
		particles = new Vector3f[particleCount];
		colorOffsets = new Vector3f[particleCount];
		for (int i = 0; i < particleCount; ++i) {
			particles[i] = new Vector3f();
			double angle = random() * PI * 2;
			float distance = (float) random() + 0.1f;
			particles[i].x = (float) cos(angle) * distance;
			particles[i].y = (float) sin(angle) * distance;
			particles[i].z = (float) random();
			colorOffsets[i] = new Vector3f();
			colorOffsets[i].x = (float) random();
			colorOffsets[i].y = (float) random();
			colorOffsets[i].z = (float) random();
		}
	}
	public void updateAndRender(Matrix4f mat) {
		particleShader.enable();
		screenObj.prepareRender();
		for (int i = 0; i < particles.length; ++i) {
			
			particleShader.setUniform(2, 0, color.x + colorOffsets[i].x * colorOffsetCoeff.x, color.y + colorOffsets[i].y * colorOffsetCoeff.y, color.z + colorOffsets[i].z * colorOffsetCoeff.z);
			particleShader.setUniform(0, 0, new Matrix4f(mat).translate(particles[i].x, particles[i].y, 0f));
			particleShader.setUniform(2, 1, particles[i].z);
			screenObj.drawTriangles();
			
			particles[i].x *= 1.02f;
			particles[i].y *= 1.02f;
			particles[i].z -= 0.01f;
			
			if (particles[i].z < 0) {
				double angle = random() * PI * 2;
				float distance = (float) random() + 0.1f;
				particles[i].x = (float) cos(angle) * distance;
				particles[i].y = (float) sin(angle) * distance;
				particles[i].z = (float) random();
			}
		}
		screenObj.unbind();
		EnigWindow.checkGLError();
	}
}
