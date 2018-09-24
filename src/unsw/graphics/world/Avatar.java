package unsw.graphics.world;

import java.awt.Color;
import java.io.IOException;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL3;

import unsw.graphics.CoordFrame3D;
import unsw.graphics.Shader;
import unsw.graphics.Texture;
import unsw.graphics.geometry.Point3D;
import unsw.graphics.geometry.TriangleMesh;

public class Avatar {
	
	private Point3D position;
    private TriangleMesh model;
    private Texture texture;

	public Avatar(Point3D position) {
		this.position = position;
        try {
        	this.model = new TriangleMesh("res/models/chihuahua.ply", true, true);
        } catch (IOException e) {
        	this.model = null;
        }
	}
	
	public Avatar(float x, float y, float z) {
		this.position = new Point3D(x, y, z);
        try {
        	this.model = new TriangleMesh("res/models/chihuahua.ply", true, true);
        } catch (IOException e) {
        	this.model = null;
        }
	}
	
	public Point3D getPosition() {
		return this.position;
	}
	
	public void moveForward(float x, float y, float z, float speed) {
		this.position = new Point3D(this.position.getX() + speed*x, 
									this.position.getY() + speed*y, 
									this.position.getZ() + speed*z);
	}
	
	public void moveBackward(float x, float y, float z, float speed) {
		this.position = new Point3D(this.position.getX() - speed*x, 
									this.position.getY() - speed*y, 
									this.position.getZ() - speed*z);
	}
	
	public void init(GL3 gl) {
		texture = new Texture(gl, "res/textures/canTop.bmp", "bmp", false);
		model.init(gl);
	}
	
    public void draw(GL3 gl, CoordFrame3D frame) {
    	
    	// Bind Texture
    	Shader.setInt(gl, "tex", 0);
        gl.glActiveTexture(GL.GL_TEXTURE0);
        gl.glBindTexture(GL.GL_TEXTURE_2D, texture.getId());
        
        Shader.setModelMatrix(gl, frame.getMatrix());
        
        // Set the material properties
        Shader.setColor(gl, "ambientCoeff", Color.WHITE);
        Shader.setColor(gl, "diffuseCoeff", new Color(0.9f, 0.9f, 0.9f));
        Shader.setColor(gl, "specularCoeff", new Color(0.0f, 0.0f, 0.0f));
        Shader.setFloat(gl, "phongExp", 16f);
    	
        model.draw(gl, frame);

    }
    
    public void draw(GL3 gl) {
        draw(gl, CoordFrame3D.identity());
    }
}
