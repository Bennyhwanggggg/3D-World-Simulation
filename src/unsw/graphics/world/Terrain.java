package unsw.graphics.world;



import java.awt.Color;
import java.io.IOException;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.smurn.jply.Element;
import org.smurn.jply.ElementReader;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL3;
import com.jogamp.opengl.util.GLBuffers;

import unsw.graphics.CoordFrame3D;
import unsw.graphics.Point2DBuffer;
import unsw.graphics.Point3DBuffer;
import unsw.graphics.Shader;
import unsw.graphics.Texture;
import unsw.graphics.Vector3;
import unsw.graphics.geometry.Point2D;
import unsw.graphics.geometry.Point3D;
import unsw.graphics.geometry.TriangleMesh;



/**
 * COMMENT: Comment HeightMap 
 *
 * @author malcolmr
 */
public class Terrain {

    private int width;
    private boolean moving_sun;
    private boolean night_mode;
    private Color sun_light_color;
    private int depth;
    private float[][] altitudes;
    private List<Tree> trees;
    private List<Road> roads;
    private Vector3 sunlight;
    private Point3DBuffer vertexBuffer;
    private IntBuffer indicesBuffer;
    private Texture texture;
    private TriangleMesh mesh;
    private float sun_moving_rate;
    private Shader shader;
    private float darkest_value;
    
    private List<Pond> ponds;
    
    // testing animated textures
//    private List<Texture> textures;
//    private int time = 0;
//    

    /**
     * Contains the normals for all vertices.
     */
    private Point3DBuffer normals;

    /**
     * Contains the texture coordinates for all vertices.
     */
    private Point2DBuffer texCoords;

    /**
     * Contains indices into the buffer of vertices and normals. Each set of 3
     * indices forms a triangle.
     */
    private IntBuffer indices;

    /**
     * The name of the vertex buffer according to OpenGL
     */
    private int verticesName;
    
    /**
     * The name of the normal buffer according to OpenGL
     */
    private int normalsName;
    
    /**
     * The name of the normal buffer according to OpenGL
     */
    private int texCoordsName;

    /**
     * The name of the indices buffer according to OpenGL
     */
    private int indicesName;

    /**
     * Create a new terrain
     *
     * @param width The number of vertices in the x-direction
     * @param depth The number of vertices in the z-direction
     */
    public Terrain(int width, int depth, Vector3 sunlight) {
        this.width = width;
        this.depth = depth;
        this.night_mode = false;
        this.moving_sun = false;
        this.sun_moving_rate = 0.005f;
        this.darkest_value = 30f;		// minimum value for dark, 30f/255;
        this.sun_light_color = new Color(1f,1f,1f);
        altitudes = new float[width][depth];
        trees = new ArrayList<Tree>();
        roads = new ArrayList<Road>();
        ponds = new ArrayList<Pond>();
        this.sunlight = sunlight;
    }
    
    public void moving_sun_switch() {
    	this.moving_sun = ! this.moving_sun;
    }

    public List<Tree> trees() {
        return trees;
    }

    public List<Road> roads() {
        return roads;
    }
    
    public Vector3 getSunlight() {
        return sunlight;
    }
    
    public Color getSunlightColor() {
    	return sun_light_color;
    }
    
    public Color getDarkestColor() {
    	float dv = this.darkest_value/255f;
    	return new Color(dv,dv,dv);
    }

    /**
     * Set the sunlight direction. 
     * 
     * Note: the sun should be treated as a directional light, without a position
     * 
     * @param dx
     * @param dy
     * @param dz
     */
    public void setSunlightDir(float dx, float dy, float dz) {
        sunlight = new Vector3(dx, dy, dz);      
    }

    /**
     * Get the altitude at a grid point
     * 
     * @param x
     * @param z
     * @return
     */
    public double getGridAltitude(int x, int z) {
        return altitudes[x][z];
    }

    /**
     * Set the altitude at a grid point
     * 
     * @param x
     * @param z
     * @return
     */
    public void setGridAltitude(int x, int z, float h) {
        altitudes[x][z] = h;
    }
    

    /**
     * Get the altitude at an arbitrary point. 
     * Non-integer points should be interpolated from neighbouring grid points
     * 
     * @param x
     * @param z
     * @return
     */
    public float altitude(float x, float z) {
        float altitude = 0;
        
        /**
         * 
         * (x0,0,z0)  (x1,0.5,z0)
			   +-----+
			   |    /|
			   |  /  |
			   |/    |
			   +-----+
			(x0,0,z1)  (x1,0.3,z1)
         */
        
        
        if(x<0 || x>=width ||z<0 || z>=depth) {
        	return altitude;
        }
        
        float x_remain = x % 1;
        float z_remain = z % 1;
        int x0 = (int) x;
        int z0 = (int) z;
        int x1 = x0<width-1?x0+1:x0;
        int z1 = z0<depth-1?z0+1:z0;
      
        float hypotenuseX = (x0 + z1) - z;

        if (x_remain == 0 && z_remain == 0) {
        	altitude = altitudes[x0][z0];
        } else if (x_remain == 0) {
        	altitude = z_remain * altitudes[x0][z1] + (1-z_remain) * altitudes[x0][z0];
        } else if (z_remain == 0) {
        	altitude = x_remain * altitudes[x1][z0] + (1-x_remain) * altitudes[x0][z0];
        } else if (x < hypotenuseX){
        	float a0 = x_remain/(hypotenuseX - x0);
        	float zAlt1  = (1-z_remain) * altitudes[x1][z0] + z_remain * altitudes[x0][z1];
    	  
        	float a1 = (hypotenuseX - x) / (hypotenuseX - x0);
        	float zAlt2 = (1-z_remain) * altitudes[x0][z0] + z_remain * altitudes[x0][z1];
    	  
        	altitude = a0*zAlt1 + a1*zAlt2;
        } else {
        	float a0 = -(1 - x_remain)/(hypotenuseX - x1);
        	float zAlt1  = (z_remain) * altitudes[x0][z1] + (1-z_remain) * altitudes[x1][z0];
    	  
        	float a1 = (hypotenuseX - x) / (hypotenuseX - x1);
        	float zAlt2  = (z_remain) * altitudes[x1][z1] + (1-z_remain) * altitudes[x1][z0];
    	  
        	altitude = a0*zAlt1 + a1*zAlt2;
        }
      
        return altitude;
    }
    
    /**
     * Add a tree at the specified (x,z) point. 
     * The tree's y coordinate is calculated from the altitude of the terrain at that point.
     * 
     * @param x
     * @param z
     * @throws IOException 
     */
    public void addTree(float x, float z) {
        float y = altitude(x, z);
        Tree tree = new Tree(x, y, z);
        trees.add(tree);
    }


    /**
     * Add a road. 
     * 
     * @param x
     * @param z
     */
    public void addRoad(float width, List<Point2D> spine) {
        Road road = new Road(width, spine);
        road.setTerrain(this);
        roads.add(road);        
    }
    
    public void addPonds(float x, float z, float w, float offset) {
    	Pond pond = new Pond(x, z, w, offset, this);
    	ponds.add(pond);
    }
  
    public int getWidth() {
    	return width;
    }
    
    public int getDepth() {
    	return depth;
    }
    
    public void init(GL3 gl) {
        // Generate the names for the buffers.
        int dx, dz, index_vert, cnt;
        
        // vertex
        // ==========================================================
        List<Point3D> p_list = new ArrayList<Point3D>();

        for(dz=0; dz<depth; dz++){
        	for(dx=0; dx<width; dx++){
        		p_list.add(new Point3D(dx, altitudes[dx][dz], dz));
        	}
        }
       
       
        // texture
        // ==========================================================
        List<Point2D> t_list = new ArrayList<Point2D>();
        for(dz=0; dz<depth; dz++){
        	for(dx=0; dx<width; dx++){
//        		t_list.add(new Point2D((float)dz/(depth-1), (float)dx/(width-1)));
        		t_list.add(new Point2D((float)dz, (float)dx));
        	}
        }
        
        
        // indices
        // ==========================================================
        int[] i_list = new int[3*2*(width-1)*(depth-1)];
        
        index_vert=0;
        cnt = 0;
        
        for(dz=0; dz<depth-1; dz++){
        	for(dx=0; dx<width-1; dx++){
        		index_vert = dz*width+dx;
        		i_list[cnt++] = (index_vert);
        		i_list[cnt++] = (index_vert+width);
        		i_list[cnt++] = (index_vert+1);
        		
        		i_list[cnt++] = (index_vert+1);
        		i_list[cnt++] = (index_vert+width);
        		i_list[cnt++] = (index_vert+width+1);
        	}
        }
        
        Integer[] index_list = ArrayUtils.toObject(i_list);
        List<Integer> indice_points = Arrays.asList(index_list);
        mesh = new TriangleMesh(p_list, indice_points, true, t_list);
        mesh.init(gl);
        
        
        // texture
        // ========================================
        texture = new Texture(gl, "res/textures/grass.bmp", "bmp", true);
        
        // lighting
        shader = new Shader(gl, "shaders/vertex_spotlight_phong.glsl", "shaders/fragment_spotlight_phong.glsl");
        shader.use(gl);
                
        for (Tree t: trees) {
        	t.init(gl);
        }
        
        for (Road road: roads) {
    		road.init(gl);
    	}
        
        for (Pond pond: ponds) {
        	pond.init(gl);
        }
        
    }
    
    public void drawTrees(GL3 gl, CoordFrame3D frame) {
    	for (Tree t: trees) {
    		t.draw(gl, frame);
    	}
    }
    
    public void drawRoads(GL3 gl, CoordFrame3D frame) {
    	for (Road road: roads) {
    		road.draw(gl, frame, this);
    	}
    }
    
    public void drawPonds(GL3 gl, CoordFrame3D frame) {
    	for (Pond pond: ponds) {
    		pond.draw(gl, frame);
    	}
    }
    
    public float get_sun_height(float x) {
    	// r = 1, y = sqrt( r^2 - x^2)
    	float y = (float) Math.sqrt(1- Math.pow(Math.abs(x), 2));
    	return y;
    }
    
    public void update_sun_height(float moving_rate) {
    	// assume Z remain the same
    	float new_sun_x = sunlight.getX() + moving_rate;
        sunlight.setX(new_sun_x);
        sunlight.setY(get_sun_height(new_sun_x));
        if(sunlight.getX() > 1) {
        	sunlight.setX(-1);
        }
    	
    }
    
    public boolean isNightMode() {
    	return this.night_mode;
    }
    
    public void night_mode_switch() {
    	this.night_mode = !this.night_mode;
    	if(this.night_mode) {
    		night_mode_on();
    	}else {
    		night_mode_off();
    	}
    }
    
    public void night_mode_on() {
    	this.moving_sun = false;	// stop moving sun
    	sun_light_color = new Color(0, 0, 0);
    	
    }
    
    public void night_mode_off() {
    	sun_light_color = new Color(1f, 1f, 1f);
    	
    }
    
    public float sunlight_value_map(float sunheight) {
    	// sunheight has range (0,1)
    	// map to darkestvalue
    	return ( (255- this.darkest_value)*sunheight + this.darkest_value )/255f;
    }
    
    public void draw(GL3 gl, CoordFrame3D frame) {
    	
    	// Bind Texture
    	Shader.setInt(gl, "tex", 0);
        gl.glActiveTexture(GL.GL_TEXTURE0);
        gl.glBindTexture(GL.GL_TEXTURE_2D, texture.getId());

        Shader.setPenColor(gl, Color.WHITE);
        
        Shader.setModelMatrix(gl, frame.getMatrix());
        
        // moving sun
        if(this.moving_sun) {
        	this.night_mode = false;		//disable night mode for moving sun
        	update_sun_height(sun_moving_rate);
        	float light_val = sunlight_value_map(sunlight.getY());
        	sun_light_color = new Color(light_val, light_val, light_val);
        }

        // Set light properties
        Shader.setPoint3D(gl, "lightPos", new Point3D(sunlight.getX(), sunlight.getY(), sunlight.getZ()));
        Shader.setColor(gl, "lightIntensity", sun_light_color);	// changing light color as sun goes down
        Shader.setColor(gl, "ambientIntensity", new Color(0.5f, 0.5f, 0.5f));
        
        // Set the material properties
        Shader.setColor(gl, "ambientCoeff", Color.WHITE);
        Shader.setColor(gl, "diffuseCoeff", new Color(0.9f, 0.9f, 0.9f));
        Shader.setColor(gl, "specularCoeff", new Color(0.0f, 0.0f, 0.0f));
        Shader.setFloat(gl, "phongExp", 50f);
    	
        mesh.draw(gl, frame);
        drawTrees(gl, frame);
        drawRoads(gl, frame);
        drawPonds(gl, frame);

    }
    
    public void draw(GL3 gl) {
        draw(gl, CoordFrame3D.identity());
    }
    
}
