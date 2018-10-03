package unsw.graphics.world;

import java.awt.Color;

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
	
	private Terrain terrain;
	
	private Point3D pos;
	private float angle; // degrees
	private Vector3 orientation;
	
	private boolean firstPerson;
	private Avatar avatar;
	private float thirdPersonDistance = 2.0f;
	
	private boolean torchSwitch = false;
	
	public Camera(float x, float y, float z) {
		pos = new Point3D(x, y, z);
		orientation = new Vector3(0, 0, 1); // set initial direction to face z axis
		angle = 0;
		firstPerson = true;
		avatar = new Avatar(pos);
	}
	
	public Camera(Point3D position) {
		pos = position;
		orientation = new Vector3(0, 0, 1);
		angle = 0;
		firstPerson = true;
		avatar = new Avatar(pos);
	}
	
	public Camera(float x, float z, Terrain terrain) {
		pos = new Point3D(x, terrain.altitude(x, z), z);
		orientation = new Vector3(0, 0, 1);
		this.terrain = terrain;
		angle = 0;
		firstPerson = true;
		avatar = new Avatar(pos);
	}
	
	public void init(GL3 gl) {
		avatar.init(gl);
	}
	 
	public Point3D getPosition() {
		return pos;
	}
	
	public float getAngle() {
		return angle;
	}
	
	public boolean isFirstPersonMode() {
		return this.firstPerson;
	}
	
	public boolean isTorchOn() {
		return this.torchSwitch;
	}
	
	public void switchTorch() {
		torchSwitch = !torchSwitch;
	}
	
	public void updateTorchStatus(boolean status) {
		this.torchSwitch = status;
	}
	
	public void showTorchLight(GL3 gl, Color lightIntensity, float attenuation, float coneAngle) {
		Shader.setPoint3D(gl, "lightPosSpot", new Point3D(pos.getX(), pos.getY()+0.7f, pos.getZ()));	// + 0.7f so for little offset
		Shader.setColor(gl, "lightIntensitySpot", lightIntensity);
		Shader.setPoint3D(gl, "coneDirection", new Point3D(0f, 0f, 1f));
		Shader.setFloat(gl, "attenuation", attenuation);
        Shader.setFloat(gl, "coneAngle", coneAngle);				
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
		avatar.setOrientation(orientation);
//		System.out.println("Camera orientation is " + orientation.getX() + " " + orientation.getZ());
//		System.out.println("Camera angle is " + angle);
	}
	
	public void turnRight(float deg) {
		angle -= deg;
		// normalise angle
		while (angle < 0) {
			angle += 360f;
		}
		angle %= 360;
		changeOrientation();
		updateThridPersonView();
	}
	
	public void turnLeft(float deg) {
		angle += deg;
		// normalise angle
		while (angle < 0) {
			angle += 360f;
		}
		angle %= 360;
		changeOrientation();
		updateThridPersonView();
	}
	
	private void updateThridPersonView() {
		float avatarX = pos.getX()-orientation.getX()*thirdPersonDistance;
		float avatarZ = pos.getZ()-orientation.getZ()*thirdPersonDistance;
		Point3D avatarPos = new Point3D(avatarX, terrain.altitude(avatarX, avatarZ), avatarZ);
//		System.out.println("Avatar position is at " + avatarPos.getX() + " " + avatarPos.getY() + " " + avatarPos.getZ());
		avatar.setPosition(avatarPos);
		if (!firstPerson) {
			// update camera height based on avatar pos instead
			pos = new Point3D(pos.getX(), terrain.altitude(avatarPos.getX(), avatarPos.getZ()), pos.getZ());
		}
	}

	public void move(float d) { // input d is positive for moving forward and negative for moving backward
		pos = new Point3D(pos.getX() + orientation.getX()*d,  pos.getY(),  pos.getZ()+orientation.getZ()*d);
		pos = new Point3D(pos.getX(), terrain.altitude(pos.getX(), pos.getZ()), pos.getZ());
		updateThridPersonView();
//		System.out.println("Camera position is: " + pos.getX() + " " + pos.getY() + " " + pos.getZ());
	}
	
	public void toggleView() {
		if (!firstPerson) {
			System.out.println("Switched to first person");
			pos = new Point3D(pos.getX()-orientation.getX()*thirdPersonDistance, 
					pos.getY()-orientation.getY()*thirdPersonDistance, 
					pos.getZ()-orientation.getZ()*thirdPersonDistance);
		} else {
			System.out.println("Switched to third person");
			pos = new Point3D(pos.getX()+orientation.getX()*thirdPersonDistance, 
					pos.getY()+orientation.getY()*thirdPersonDistance, 
					pos.getZ()+orientation.getZ()*thirdPersonDistance);	
			updateThridPersonView(); 
		}
//		System.out.println("Camera position is: " + pos.getX() + " " + pos.getY() + " " + pos.getZ());
		firstPerson = !firstPerson;
	}

	public void drawAvatar(GL3 gl) {
		if (!firstPerson) {
			avatar.draw(gl);
		}
	}
	
	public void drawAvatar(GL3 gl, CoordFrame3D frame) {
		if (!firstPerson) {
			avatar.draw(gl, frame);
		}
	}
}
