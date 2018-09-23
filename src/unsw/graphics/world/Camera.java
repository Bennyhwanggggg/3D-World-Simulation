package unsw.graphics.world;

import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.KeyListener;
import com.jogamp.opengl.GL3;
import com.jogamp.opengl.glu.GLU;

import unsw.graphics.CoordFrame2D;
import unsw.graphics.CoordFrame3D;
import unsw.graphics.Shader;
import unsw.graphics.Vector3;
import unsw.graphics.geometry.Line3D;
import unsw.graphics.geometry.LineStrip2D;
import unsw.graphics.geometry.Point3D;

public class Camera {
	
	private Point3D pos;
	private float angle; // degrees
	private Vector3 orientation;
	int camera_mode; 
	
	public Camera(float x, float y, float z) {
		pos = new Point3D(x, y, z);
		orientation = new Vector3(1, 0, 0); // set initial direction to face z axis
		angle = 0;
	}
	
	public Camera(Point3D position) {
		pos = position;
		orientation = new Vector3(1, 0, 0);
		angle = 0;
	}
	
	public Camera(float x, float z, Terrain terrain) {
		pos = new Point3D(x, terrain.altitude(x, z), z);
		orientation = new Vector3(1, 0, 0);
		angle = 0;
	}
	
	public Point3D getPosition() {
		return pos;
	}
	
	public float getAngle() {
		return angle;
	}
	
	private void changeOrientation() {
		float dx, dz;
		if (angle <= 90 || angle >= 270) {
			dz = 1;
		} else {
			dz = -1;
		}
		dx = (float) (Math.tan(Math.toRadians(angle))*dz);
		if (angle == 90) {
			orientation = new Vector3(1, 0, 0);
		} else if(angle == 270) {
			orientation = new Vector3(-1, 0, 0);
		} else {
			orientation = new Vector3(dx, 0, dz).normalize();
		}
		System.out.println("Orientation is " + orientation.getX() + " " + orientation.getZ());
		System.out.println("Angle is " + angle);
	}
	
	public void turnRight(float deg) {
		angle -= deg;
		// normalise angle
		while (angle < 0) {
			angle += 360f;
		}
		angle %= 360;
		changeOrientation();
	}
	
	public void turnLeft(float deg) {
		angle += deg;
		// normalise angle
		while (angle < 0) {
			angle += 360f;
		}
		angle %= 360;
		changeOrientation();
		
	}
	

	public void move(float d, Terrain terrain) { // input d is positive for moving forward and negative for moving backward
		pos = new Point3D(pos.getX() + orientation.getX()*d,  pos.getY(),  pos.getZ()+orientation.getZ()*d);
		pos = new Point3D(pos.getX(), terrain.altitude(pos.getX(), pos.getZ()), pos.getZ());
		System.out.println("position is: " + pos.getX() + " " + pos.getY() + " " + pos.getZ());
	}
	

}
