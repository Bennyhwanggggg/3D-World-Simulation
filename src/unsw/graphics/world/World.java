package unsw.graphics.world;

import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.KeyListener;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL3;

import unsw.graphics.Application3D;
import unsw.graphics.CoordFrame3D;
import unsw.graphics.Matrix4;
import unsw.graphics.Shader;
import unsw.graphics.Texture;
import unsw.graphics.Vector3;
import unsw.graphics.geometry.Point3D;



/**
 * COMMENT: Comment Game 
 *
 * @author malcolmr
 */
public class World extends Application3D implements KeyListener {

    private Terrain terrain;
    private Camera myCamera;

    public World(Terrain terrain) {
    	super("Assignment 2", 800, 600);
        this.terrain = terrain;
        
    }
    
    /**
     * Load a level file and display it.
     * 
     * @param args - The first argument is a level file in JSON format
     * @throws FileNotFoundException
     */
    public static void main(String[] args) throws FileNotFoundException {
    	
        //Terrain terrain = LevelIO.load(new File(args[0]));
    	Terrain terrain = LevelIO.load(new File("res/worlds/test9.json"));
        World world = new World(terrain);
        world.start();
    }

	@Override
	public void display(GL3 gl) {
		super.display(gl);

		// need to put camera onto terrain so when rotating, need to keep track of direction so you don't move away from terrain's coordinate frame
        Shader.setPenColor(gl, Color.WHITE);
        CoordFrame3D frame = CoordFrame3D.identity()
        		.rotateY(-myCamera.getAngle())
        		.translate(-myCamera.getPosition().getX(), -(myCamera.getPosition().getY()+1f), -myCamera.getPosition().getZ());

        Shader.setViewMatrix(gl, frame.getMatrix());
		terrain.draw(gl);
		if (!myCamera.isFirstPersonMode()) {
			myCamera.drawAvatar(gl);
		}
		if (myCamera.isTorchOn()) {
			myCamera.showTorchLight(gl, new Color(0f, 0f, 0f), 32.5f, 20f);
		} else {
			myCamera.showTorchLight(gl, new Color(0f, 0f, 0f), 0f, 0f);
		}
		if (terrain.isNightMode()) {
			setBackground(terrain.getDarkestColor());
		} else {
			setBackground(terrain.getSunlightColor());
		}
	}

	@Override
	public void destroy(GL3 gl) {
		super.destroy(gl);
		
	}

	@Override
	public void init(GL3 gl) {
		super.init(gl);
		getWindow().addKeyListener(this);
		myCamera = new Camera(0f, 0f, terrain);
		myCamera.init(gl);
		terrain.init(gl);
	}

	@Override
	public void reshape(GL3 gl, int width, int height) {
        super.reshape(gl, width, height);
        Shader.setProjMatrix(gl, Matrix4.perspective(60, width/(float)height, 1, 100));
	}

	@Override
	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
        
        case KeyEvent.VK_UP:
        	myCamera.move(-0.2f); // input value is the speed
            break;
        case KeyEvent.VK_DOWN:
        	myCamera.move(0.2f);
            break;
        case KeyEvent.VK_RIGHT:
            myCamera.turnRight(10f);
            break;
        case KeyEvent.VK_LEFT:
            myCamera.turnLeft(10f);
            break;
        case KeyEvent.VK_TAB:
        	myCamera.toggleView();
        	break;
        case KeyEvent.VK_S:
        	terrain.moving_sun_switch();
        	break;
        case KeyEvent.VK_N:
        	terrain.night_mode_switch();
        	myCamera.updateTorchStatus(terrain.isNightMode());
        	break;
        case KeyEvent.VK_SPACE:
        	myCamera.switchTorch();
        	break;
        default:
            break;
        }
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
	
    
}
