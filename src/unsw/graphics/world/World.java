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



/**
 * COMMENT: Comment Game 
 *
 * @author malcolmr
 */
public class World extends Application3D implements KeyListener {

    private Terrain terrain;
    private Camera myCamera;
    private float rotationY;
    private Texture texture;
    private float cam_x;
    private float cam_y;
    

    public World(Terrain terrain) {
    	super("Assignment 2", 800, 600);
    	rotationY = 0;
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
    	Terrain terrain = LevelIO.load(new File("res/worlds/test1.json"));
        World world = new World(terrain);
        world.start();
    }

	@Override
	public void display(GL3 gl) {
		super.display(gl);
		
		// texture
		


		// need to put camera onto terrain so when rotating, need to keep track of direction so you don't move away from terrain's coordinate frame
        Shader.setPenColor(gl, Color.WHITE);
        CoordFrame3D frame = CoordFrame3D.identity()
        		.rotateY(-myCamera.getAngle())
                .translate(-myCamera.getPosition().getX(), -(myCamera.getPosition().getY()+1f), -myCamera.getPosition().getZ());
//                .rotateY(-myCamera.getAngle());
//                .scale(0.3f, 0.3f, 0.3f);
        //Shader.setViewMatrix(gl, frame.getMatrix());
//		myCamera.update();
		terrain.draw(gl, frame);
		//terrain.drawTrees(gl, frame);
	}

	@Override
	public void destroy(GL3 gl) {
		super.destroy(gl);
		
	}

	@Override
	public void init(GL3 gl) {
		super.init(gl);
		getWindow().addKeyListener(this);
		myCamera = new Camera(10f, 0f, 10f);
		terrain.init(gl);
	}

	@Override
	public void reshape(GL3 gl, int width, int height) {
        super.reshape(gl, width, height);
        Shader.setProjMatrix(gl, Matrix4.perspective(60, width/(float)height, 1, 100));
	}

	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		switch (e.getKeyCode()) {
        
        case KeyEvent.VK_UP:
        	myCamera.move(-0.2f, terrain); // integer is the speed
            break;
        case KeyEvent.VK_DOWN:
        	myCamera.move(0.2f, terrain);
            break;
        case KeyEvent.VK_RIGHT:
            myCamera.turnRight(10f);
            break;
        case KeyEvent.VK_LEFT:
            myCamera.turnLeft(10f);
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
