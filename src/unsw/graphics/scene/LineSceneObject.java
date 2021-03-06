package unsw.graphics.scene;

import java.awt.Color;

import com.jogamp.opengl.GL3;

import unsw.graphics.CoordFrame2D;
import unsw.graphics.Shader;
import unsw.graphics.geometry.Line2D;
import unsw.graphics.geometry.Point2D;
import unsw.graphics.geometry.Polygon2D;

/**
 * A game object that has a polygonal shape.
 * 
 * This class extend SceneObject to draw polygonal shapes.
 *
 * TODO: The methods you need to complete are at the bottom of the class
 *
 * @author malcolmr
 * @author Robert Clifton-Everest
 * 
 */
public class LineSceneObject extends SceneObject {

    private Polygon2D myPolygon;
    private Line2D line;
    private Color myLineColor;
    private Shader shader;
    private float x_0;
    private float x_1;
    private float y_0;
    private float y_1;
    private Point2D point_1;
    private Point2D point_2;

    /**
     * Create a polygonal scene object and add it to the scene tree
     * 
     * The line and fill colors can possibly be null, in which case that part of the object
     * should not be drawn.
     *
     * @param parent The parent in the scene tree
     * @param points A list of points defining the polygon
     * @param fillColor The fill color
     * @param lineColor The outline color
    */

    
    //Create a LineSceneObject from (0,0) to (1,0)
    public LineSceneObject(SceneObject parent, Color lineColor){
    	super(parent);
    	x_1 = 1.0f;
    	x_0 = y_0 = y_1 = 0;
    	line = new Line2D(x_0,y_0, x_1,y_1);
    	myLineColor = lineColor;
    }
    

    //Create a LineSceneObject from (x1,y1) to (x2,y2)
    public LineSceneObject(SceneObject parent, float x0, float y0, float x1, float y1, Color lineColor){
    	super(parent);
    	x_0=x0;
    	x_1=x1;
    	y_0=y0;
    	y_1=y1;
    	line = new Line2D(x_0,y_0, x_1,y_1);
    	myLineColor = lineColor;
    	
    }
    
    public void set_first_poinst(){
    	point_1 = new Point2D(x_0, y_0);
    }
    
    public void set_second_poinst(){
    	point_2 = new Point2D(x_1, y_1);
    }
    
    public Point2D get_first_point(){
		return point_1; 
    }
    
    public Point2D get_second_point(){
    	return point_2; 
    }
    


    /**
     * Get the outline color.
     * 
     * @return
     */
    public Color getLineColor() {
        return myLineColor;
    }

    /**
     * Set the outline color.
     * 
     * Setting the color to null means the outline should not be drawn
     * 
     * @param lineColor
     */
    public void setLineColor(Color lineColor) {
        myLineColor = lineColor;
    }

    // ===========================================
    // COMPLETE THE METHODS BELOW
    // ===========================================
    

    /**
     * Draw the polygon
     * 
     * if the fill color is non-null, draw the polygon filled with this color
     * if the line color is non-null, draw the outline with this color
     * 
     */
    @Override
    public void drawSelf(GL3 gl, CoordFrame2D frame) {

        // TODO: Write this method

    	if(myLineColor!=null){
    		shader.setPenColor(gl, myLineColor);
    		line.draw(gl, frame);
    		
    	}

    }


}
