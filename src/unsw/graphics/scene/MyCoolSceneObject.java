package unsw.graphics.scene;

import java.awt.Color;

import unsw.graphics.geometry.Polygon2D;

/**
 * A cool scene object
 *
 */
public class MyCoolSceneObject extends SceneObject {

	PolygonalSceneObject p;
	
	public MyCoolSceneObject(SceneObject parent) {
        super(parent);
        // TODO: Write your scene object

        Polygon2D poly = new Polygon2D(0,0, 1,1, 0,1);
        p = new PolygonalSceneObject(parent, poly, Color.GREEN, Color.YELLOW );
        p.rotate(45);
        
        //Create a line that is a child of polygon p
        Color lineCol2 = new Color(0.5f, 0.5f, 1);
        LineSceneObject l2 = new LineSceneObject(p, lineCol2);     
        l2.setPosition(-1, 0);
      
        
        //Create a circle that is a child of polygon p       
        CircularSceneObject c2 = new CircularSceneObject(p, 0.25f, Color.WHITE, Color.WHITE);     
        c2.translate(-1,0);
        
    }
    
	public void translate(float x, float y) {
		p.setPosition(x, y);
	}
	
	public void rotate(int r) {
		p.setRotation(r);;
	}
	
	public void scale(float s) {
		p.setScale(s);;
	}
    
	
}
