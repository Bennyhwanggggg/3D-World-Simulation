package unsw.graphics.scene;

import java.awt.Color;

import com.jogamp.opengl.GL3;

import unsw.graphics.CoordFrame2D;
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
public class CircularSceneObject extends SceneObject {

    private Polygon2D myPolygon;
    private Color myFillColor;
    private Color myLineColor;
    private float r;

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
    public CircularSceneObject(SceneObject parent, Polygon2D polygon,
            Color fillColor, Color lineColor) {
        super(parent);

        myPolygon = polygon;
        myFillColor = fillColor;
        myLineColor = lineColor;
    }
    
    //Create a CircularSceneObject with centre 0,0 and radius 1
    public CircularSceneObject(SceneObject parent, Color fillColor, Color lineColor){
        super(parent);

        r = 1.0f;
        myFillColor = fillColor;
        myLineColor = lineColor;
    }

    //Create a CircularSceneObject with centre 0,0 and a given radius
    public CircularSceneObject(SceneObject parent, float radius, Color fillColor, Color lineColor){
        super(parent);

        r = radius;
        myFillColor = fillColor;
        myLineColor = lineColor;
    }
    
    //setter for radius
    public void set_radius(float radius){
    	r = radius;
    }
    
    //get for radius
    public float get_radius(){
    	return r;
    }

    /**
     * Get the fill color
     * 
     * @return
     */
    public Color getFillColor() {
        return myFillColor;
    }

    /**
     * Set the fill color.
     * 
     * Setting the color to null means the object should not be filled.
     * 
     * @param fillColor The fill color
     */
    public void setFillColor(Color fillColor) {
        myFillColor = fillColor;
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
    	if(myFillColor!=null){
    		
    		
    	}
    	if(myLineColor!=null){
    		
    	}

    }


}