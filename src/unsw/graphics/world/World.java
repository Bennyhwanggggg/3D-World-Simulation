package unsw.graphics.world;

import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;

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
public class World extends Application3D {

    private Terrain terrain;
    private float rotationY;
    private Texture texture;

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
		



        Shader.setPenColor(gl, Color.WHITE);
        CoordFrame3D frame = CoordFrame3D.identity()
                .translate(0, -1, -5)
                .scale(0.3f, 0.3f, 0.3f);
		terrain.draw(gl, frame.rotateY(rotationY));
		rotationY += 1;
	}

	@Override
	public void destroy(GL3 gl) {
		super.destroy(gl);
		
	}

	@Override
	public void init(GL3 gl) {
		super.init(gl);
		terrain.init(gl);
		
		
	}

	@Override
	public void reshape(GL3 gl, int width, int height) {
        super.reshape(gl, width, height);
        Shader.setProjMatrix(gl, Matrix4.perspective(60, width/(float)height, 1, 100));
	}
}
