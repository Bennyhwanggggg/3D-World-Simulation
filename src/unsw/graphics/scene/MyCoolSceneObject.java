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
        float s = 0.8f;
        
        // face shape
        Polygon2D poly = new Polygon2D(s,-s, s,s, -s,s, -s,-s);
        p = new PolygonalSceneObject(parent, poly, Color.GREEN, Color.YELLOW );
        

        // nose
        set_nose(p);

        // eyes
        set_eye(p, 1);		// right
        set_eye(p, -1);		// left
        
        // mouth
        set_mouth(p);
        
        // eyebrows
        set_eyebrows(p,1); 	// right
        set_eyebrows(p,-1);	// left
        
        
    }
    
	public void set_nose(SceneObject frame){
        Polygon2D nose = new Polygon2D(0.5f,0, 0,1, -0.5f,0);
        PolygonalSceneObject n = new PolygonalSceneObject(frame, nose, Color.YELLOW, null);
        n.translate(0, 0.1f);
        n.scale(0.1f);
		
	}
	
	
	public void set_eye(SceneObject frame, float side){
		CircularSceneObject e1 = new CircularSceneObject(frame, 0.25f, Color.WHITE, Color.WHITE);
        e1.translate(side*0.5f,0.1f);
		
        CircularSceneObject e2 = new CircularSceneObject(e1, 0.25f, Color.BLACK, Color.WHITE);
        e2.translate(0, 0.1f);
        e2.scale(0.5f);

	}
	
	public void set_mouth(SceneObject frame){
		float s = 0.6f;
		Polygon2D poly = new Polygon2D(s,0, s, -0.9f*s, 0,-s, -s,-0.9f*s, -s,0);
		PolygonalSceneObject p = new PolygonalSceneObject(frame, poly, Color.PINK, Color.YELLOW );
		p.translate(0, -0.1f);
		
		Color lineCol2 = new Color(0.5f, 0.5f, 1);
		LineSceneObject l1 = new LineSceneObject(p, lineCol2);     
        l1.setPosition(-s, 0.5f*-s);
	}
	
	public void set_eyebrows(SceneObject frame, float side){
		float s = 0.2f;
		float l = 0.4f;
		float h = 0.5f;

		Polygon2D poly = new Polygon2D(side*s,s+h, 	side*(s+l),s+h, 	side*(s+l), 0.9f*s+h, 	side*s,0.7f*s+h);
		PolygonalSceneObject p = new PolygonalSceneObject(frame, poly, Color.PINK, Color.YELLOW );

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
