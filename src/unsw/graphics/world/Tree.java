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
        position = new Point3D(x, -y, z);
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
    							.scale(0.3f, 0.3f, 0.3f);
    	
    	Shader.setPenColor(gl, BROWN);

    	model.init(gl);
    	model.draw(gl, newFrame);
    }
    
    public void draw(GL3 gl) {
        draw(gl, CoordFrame3D.identity());
    }
}
