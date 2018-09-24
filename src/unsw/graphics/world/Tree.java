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

/**
 * COMMENT: Comment Tree 
 *
 * @author malcolmr
 */
public class Tree {
	
	private static final Color BROWN = new Color(151/255f, 84/255f, 69/255f);

    private Point3D position;
    private TriangleMesh model;
    private Texture texture;
    
    public Tree(float x, float y, float z) {
        position = new Point3D(x, y, z);
        try {
        	model = new TriangleMesh("res/models/tree.ply", true, true);
        } catch (IOException e) {
        	model = null;
        }
    }
    
    public Point3D getPosition() {
        return position;
    }
    
    
    public void draw(GL3 gl, CoordFrame3D frame) {

    	CoordFrame3D newFrame = frame.translate(position)
    			.translate(0f, 1.4f, 0f)
    			.scale(0.3f, 0.3f, 0.3f);
    	
    	Shader.setPenColor(gl, BROWN);
    	
    	// Set tree properites
        Shader.setColor(gl, "ambientCoeff", Color.WHITE);
        Shader.setColor(gl, "diffuseCoeff", new Color(0.8f, 0.8f, 0.8f));
        Shader.setColor(gl, "specularCoeff", new Color(0.0f, 0.0f, 0.0f));
        Shader.setFloat(gl, "phongExp", 16f);

    	model.init(gl);
    	model.draw(gl, newFrame);
    }
    
    public void draw(GL3 gl) {
        draw(gl, CoordFrame3D.identity());
    }
}
