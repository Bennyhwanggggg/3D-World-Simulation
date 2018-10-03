package unsw.graphics.world;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL3;

import unsw.graphics.CoordFrame3D;
import unsw.graphics.Shader;
import unsw.graphics.Texture;
import unsw.graphics.Vector3;
import unsw.graphics.geometry.Point2D;
import unsw.graphics.geometry.Point3D;
import unsw.graphics.geometry.TriangleMesh;

/**
 * COMMENT: Comment Road 
 *
 * @author malcolmr
 */
public class Road {

    private List<Point2D> points;
    private float width;
    private Texture texture;
    
    private TriangleMesh meshes;
    
    /**
     * Create a new road with the specified spine 
     *
     * @param width
     * @param spine
     */
    public Road(float width, List<Point2D> spine) {
        this.width = width;
        this.points = spine;
    }

    /**
     * The width of the road.
     * 
     * @return
     */
    public double width() {
        return width;
    }
    
    /**
     * Get the number of segments in the curve
     * 
     * @return
     */
    public int size() {
        return points.size() / 3;
    }

    /**
     * Get the specified control point.
     * 
     * @param i
     * @return
     */
    public Point2D controlPoint(int i) {
        return points.get(i);
    }
    
    /**
     * Get a point on the spine. The parameter t may vary from 0 to size().
     * Points on the kth segment take have parameters in the range (k, k+1).
     * 
     * @param t
     * @return
     */
    public Point2D point(float t) {
        int i = (int)Math.floor(t);
        t = t - i;
        
        i *= 3;
        
        Point2D p0 = points.get(i++);
        Point2D p1 = points.get(i++);
        Point2D p2 = points.get(i++);
        Point2D p3 = points.get(i++);
        

        float x = b(0, t) * p0.getX() + b(1, t) * p1.getX() + b(2, t) * p2.getX() + b(3, t) * p3.getX();
        float y = b(0, t) * p0.getY() + b(1, t) * p1.getY() + b(2, t) * p2.getY() + b(3, t) * p3.getY();        
        
        return new Point2D(x, y);
    }
    
    /**
     * Calculate the Bezier coefficients
     * 
     * @param i
     * @param t
     * @return
     */
    private float b(int i, float t) {
        
        switch(i) {
        
        case 0:
            return (1-t) * (1-t) * (1-t);

        case 1:
            return 3 * (1-t) * (1-t) * t;
            
        case 2:
            return 3 * (1-t) * t * t;

        case 3:
            return t * t * t;
        }
        
        // this should never happen
        throw new IllegalArgumentException("" + i);
    }
    
    private float beizerDerivative(int i, float t) {
      switch(i) {
          case 0:
              return (1-t) * (1-t);
  
          case 1:
              return 2 * (1-t) * t;
              
          case 2:
              return t * t;
        }
        
        // this should never happen
        throw new IllegalArgumentException("" + i);
    }
    
    public Vector3 getTangent(float t) {
    	int m = 3;
    	int i = (int)Math.floor(t);
	    t = t - i;
	    
	    i *= m;
	    
	    Point2D p0 = points.get(i++);
	    Point2D p1 = points.get(i++);
	    Point2D p2 = points.get(i++);
	    Point2D p3 = points.get(i++);
	    
	    float x = beizerDerivative(0, t) * (p1.getX()-p0.getX())
	        + beizerDerivative(1, t) * (p2.getX()-p1.getX())
	        + beizerDerivative(2, t) * (p3.getX()-p2.getX());
	    
	    // use z as notation since we are gonna translate it to z when we go back to 3D
	    float z = beizerDerivative(0, t) * (p1.getY()-p0.getY())
	        + beizerDerivative(1, t) * (p2.getY()-p1.getY())
	        + beizerDerivative(2, t) * (p3.getY()-p2.getY());
	    
	    Vector3 tangent = new Vector3(x, 0, z);
	    return tangent.normalize();
    }
    
    public void init(GL3 gl) {
    texture = new Texture(gl, "res/textures/rock.bmp", "bmp", false);
  }

    public void draw(GL3 gl, CoordFrame3D frame, Terrain terrain) {
      // offset
      float y_offset = 0.0001f;
      float step_rate = 0.005f;
      
      // Bind Texture
      Shader.setInt(gl, "tex", 0);
        gl.glActiveTexture(GL.GL_TEXTURE0);
        gl.glBindTexture(GL.GL_TEXTURE_2D, texture.getId());
        
        // Set material properties
        Shader.setColor(gl, "ambientCoeff", Color.WHITE);
        Shader.setColor(gl, "diffuseCoeff", new Color(0.8f, 0.8f, 0.8f));
        Shader.setColor(gl, "specularCoeff", new Color(0.3f, 0.3f, 0.3f));
        Shader.setFloat(gl, "phongExp", 22f);
        
        List<Point3D> vertices = new ArrayList<Point3D>();
        List<Vector3> normals = new ArrayList<Vector3>();
        List<Integer> indices = new ArrayList<Integer>();
        // Get the road points
        for (float t=0; t<this.size(); t+= step_rate) {
          Point2D spinePoint = point(t);
          Vector3 tangent = getTangent(t);
          Vector3 normalVector = new Vector3(-tangent.getZ(), 0, tangent.getX()).normalize();
          
          float road_height = terrain.altitude(spinePoint.getX(), spinePoint.getY())+y_offset;
          
          Point2D normalPoint = new Point2D(-1*normalVector.getX()*(width/2), -1*normalVector.getZ()*(width/2));
          float width_offset_x = normalPoint.getX();
          float width_offset_y = normalPoint.getY();
          
          Point3D point1 = new Point3D(spinePoint.getX() + width_offset_x, 
        		  road_height,
                  spinePoint.getY() + width_offset_y);
                    
          Point3D point2 = new Point3D(spinePoint.getX() - width_offset_x, 
        		  road_height,
        		  spinePoint.getY() - width_offset_y);

          
          vertices.add(point1);
          vertices.add(point2);

        }
        
      /* indices order
       * 2 ----3
       * |     |
       * 0 --- 1
       * 
       * */
        
        int gap_slides = 2;
        for(int i=0; i<(vertices.size() /2 - 1); i++) {
          // for each slides
          normals.add(new Vector3(1, 0, 0));
          normals.add(new Vector3(1, 0, 0));
          // add indices
          int num_slides = i*gap_slides;
          // first triangle
          indices.add(num_slides);
          indices.add(num_slides + 1);
          indices.add(num_slides + 3);
          // 2nd triangle
          indices.add(num_slides + 2);
          indices.add(num_slides );
          indices.add(num_slides + 3);

          
        }
        
//        meshes = new TriangleMesh(vertices, true);
        meshes = new TriangleMesh(vertices, normals, indices);
        meshes.init(gl);
        meshes.draw(gl, frame);
    }

}
