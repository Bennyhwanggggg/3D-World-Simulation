package unsw.graphics.world;

import java.awt.Color;
import java.io.IOException;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL3;

import unsw.graphics.CoordFrame3D;
import unsw.graphics.Shader;
import unsw.graphics.Texture;
import unsw.graphics.Vector3;
import unsw.graphics.geometry.Point3D;
import unsw.graphics.geometry.TriangleMesh;

public class Avatar {
	
	private Point3D position;
    private TriangleMesh model;
    private Texture texture;
    private Vector3 orientation;
    private float angle;

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
	
	public void move(Vector3 direction, float speed) {
		this.position = new Point3D(this.position.getX()+direction.getX()*speed,
									this.position.getY()+direction.getY()*speed,
									this.position.getZ()+direction.getZ()*speed);
	}
	
	public void setOrientation(Vector3 newOrientation) {
		this.orientation = newOrientation;
		// convert back to angle for rotation
		double dx = this.orientation.getX();
		double dz = this.orientation.getZ();
		this.angle = (float) Math.toDegrees(Math.atan2(dx, dz));
	}
	
	public void setPosition(Point3D newPos) {
		this.position = newPos;
	}
	
	public void setAltitude(float y) {
		this.position = new Point3D(this.position.getX(), y, this.position.getZ());
	}
	
	public void init(GL3 gl) {
		texture = new Texture(gl, "res/textures/rock.bmp", "bmp", false);
		model.init(gl);
	}
	
    public void draw(GL3 gl, CoordFrame3D frame) {
    	
    	// Bind Texture
    	Shader.setInt(gl, "tex", 0);
        gl.glActiveTexture(GL.GL_TEXTURE0);
        gl.glBindTexture(GL.GL_TEXTURE_2D, texture.getId());
        
        
        // Set the material properties
        Shader.setColor(gl, "ambientCoeff", Color.WHITE);
        Shader.setColor(gl, "diffuseCoeff", new Color(0.9f, 0.9f, 0.9f));
        Shader.setColor(gl, "specularCoeff", new Color(0.0f, 0.0f, 0.0f));
        Shader.setFloat(gl, "phongExp", 16f);
    	
        CoordFrame3D scaledFrame = frame.translate(position).
        							translate(0f, 0.2f, 0f).
        							rotateY(this.angle).
        							rotateY(180f). // this one is because head is the other way
        							scale(0.1f, 0.1f, 0.1f);
        Shader.setModelMatrix(gl, scaledFrame.getMatrix());
        model.draw(gl, scaledFrame);

    }
    
    public void draw(GL3 gl) {
        draw(gl, CoordFrame3D.identity());
    }
}
