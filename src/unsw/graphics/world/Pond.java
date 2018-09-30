package unsw.graphics.world;

import java.util.Arrays;
import java.util.List;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL3;

import unsw.graphics.Shader;
import unsw.graphics.Texture;

public class Pond {
	
	private List<Texture> textures;
	private int time = 0;
	private float depth;
	
	public Pond() {
		
	}
	
	public void init(GL3 gl) {
		Texture texture1 = new Texture(gl, "res/textures/Water1.jpg", "jpg", true);
		Texture texture2 = new Texture(gl, "res/textures/Water2.jpg", "jpg", true);
		Texture texture3 = new Texture(gl, "res/textures/Water3.jpg", "jpg", true);
		textures = Arrays.asList(texture1, texture2, texture3);
	}
	
	public void draw(GL3 gl) {
		Shader.setInt(gl, "tex", 0);
        gl.glActiveTexture(GL.GL_TEXTURE0);
        gl.glBindTexture(GL.GL_TEXTURE_2D, textures.get(time/30).getId());
        time += 1;
	}
}
