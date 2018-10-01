package unsw.graphics.world;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL3;

import unsw.graphics.CoordFrame3D;
import unsw.graphics.Shader;
import unsw.graphics.Texture;
import unsw.graphics.geometry.Point2D;
import unsw.graphics.geometry.Point3D;
import unsw.graphics.geometry.TriangleMesh;

public class Pond {
	
	private List<Texture> textures;
	private int time = 0;
	private float depth;
	private Terrain terrain;
	
	private TriangleMesh mesh;
	private float x;
	private float z;
	private float width;
	
	
	public Pond(float x, float z, float w, Terrain terrain) {
		this.x = x;
		this.z = z;
		this.width = w;
		this.terrain = terrain;
	}
	
	
	public void init(GL3 gl) {
		Texture texture1 = new Texture(gl, "res/textures/Water1.jpg", "jpg", true);
		Texture texture2 = new Texture(gl, "res/textures/Water2.jpg", "jpg", true);
//		Texture texture3 = new Texture(gl, "res/textures/Water3.jpg", "jpg", true);
//		textures = Arrays.asList(texture1, texture2, texture3);
		textures = Arrays.asList(texture1, texture2)
;		
		// Generate the names for the buffers.
        int index_vert, cnt;
        
        // vertex
        // ==========================================================
        List<Point3D> p_list = new ArrayList<Point3D>();
        
        p_list.add(new Point3D(x, terrain.altitude(x, z)+0.5f, z));
    	p_list.add(new Point3D(x+width, terrain.altitude(x, z)+0.5f, z-width));
    	p_list.add(new Point3D(x-width, terrain.altitude(x, z)+0.5f, z-width));
    	p_list.add(new Point3D(x, terrain.altitude(x, z)+0.5f, z));
    	p_list.add(new Point3D(x-width, terrain.altitude(x, z)+0.5f, z-width));
    	p_list.add(new Point3D(x-width, terrain.altitude(x, z)+0.5f, z+width));
    	p_list.add(new Point3D(x, terrain.altitude(x, z)+0.5f, z));
    	p_list.add(new Point3D(x-width, terrain.altitude(x, z)+0.5f, z+width));
    	p_list.add(new Point3D(x+width, terrain.altitude(x, z)+0.5f, z+width));
    	p_list.add(new Point3D(x, terrain.altitude(x, z)+0.5f, z));
    	p_list.add(new Point3D(x+width, terrain.altitude(x, z)+0.5f, z+width));
    	p_list.add(new Point3D(x+width, terrain.altitude(x, z)+0.5f, z-width));

            
        // texture
        // ==========================================================
        List<Point2D> t_list = new ArrayList<Point2D>();

        
        t_list.add(new Point2D(0.5f, 0.5f));
        t_list.add(new Point2D(1f, 0f));
		t_list.add(new Point2D(0f, 0f));
		
		t_list.add(new Point2D(0.5f, 0.5f));
		t_list.add(new Point2D(0f, 0f));
		t_list.add(new Point2D(0f, 1f));
		
		t_list.add(new Point2D(0.5f, 0.5f));
		t_list.add(new Point2D(0f, 1f));
		t_list.add(new Point2D(1f, 1f));
		
		t_list.add(new Point2D(0.5f, 0.5f));
		t_list.add(new Point2D(1f, 1f));
		t_list.add(new Point2D(1f, 0f));
		

        
        // indices
        // ==========================================================
//        int[] i_list = new int[3*2*(terrain.getWidth()-1)*(terrain.getDepth()-1)];
//        
//        index_vert=0;
//        cnt = 0;
//        
//        for(int j=0; j<terrain.getDepth()-1; j++){
//        	for(int i=0; i<terrain.getWidth()-1; i++){
//        		index_vert = (int) (dz.get(j)*terrain.getWidth()+dx.get(i));
//        		i_list[cnt++] = (index_vert);
//        		i_list[cnt++] = (index_vert+terrain.getWidth());
//        		i_list[cnt++] = (index_vert+1);
//        		
//        		i_list[cnt++] = (index_vert+1);
//        		i_list[cnt++] = (index_vert+terrain.getWidth());
//        		i_list[cnt++] = (index_vert+terrain.getWidth()+1);
//        	}
//        }
//        
//        Integer[] index_list = ArrayUtils.toObject(i_list);
//        List<Integer> indice_points = Arrays.asList(index_list);
        mesh = new TriangleMesh(p_list, true, t_list);
        mesh.init(gl);
	}
	
	public void draw(GL3 gl, CoordFrame3D frame) {
		Shader.setInt(gl, "tex", 0);
        gl.glActiveTexture(GL.GL_TEXTURE0);
        gl.glBindTexture(GL.GL_TEXTURE_2D, textures.get((time/20)%2).getId());

        time += 1;
        Shader.setPenColor(gl, Color.WHITE);
        // Set the material properties
        Shader.setColor(gl, "ambientCoeff", Color.WHITE);
        Shader.setColor(gl, "diffuseCoeff", new Color(0.7f, 0.7f, 0.7f));
        Shader.setColor(gl, "specularCoeff", new Color(0.9f, 0.9f, 0.9f));
        Shader.setFloat(gl, "phongExp", 50f);

        mesh.draw(gl, frame);
	}
	
	public void draw(GL3 gl) {
		draw(gl, CoordFrame3D.identity());
	}
}
